package com.example.sentimentanalysis;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ibm.cloud.sdk.core.http.Response;
import com.ibm.cloud.sdk.core.http.ServiceCall;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.assistant.v2.model.DialogNodeOutputOptionsElement;
import com.ibm.watson.assistant.v2.model.RuntimeResponseGeneric;
import com.ibm.watson.assistant.v2.Assistant;
import com.ibm.watson.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.assistant.v2.model.MessageInput;
import com.ibm.watson.assistant.v2.model.MessageOptions;
import com.ibm.watson.assistant.v2.model.MessageResponse;
import com.ibm.watson.assistant.v2.model.SessionResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter mAdapter;
    private ArrayList messageArrayList;
    private EditText inputMessage;
    private Button btnSend;
    private boolean initialRequest;
    private static String TAG = "MainActivity";
    private Context mContext;

    private Boolean chatInitiated = false;
    private int localSentimentScore=0;

    private static final String USER_DATA="user";
    private static final String USER_MAP="emailtoUid";

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    FirebaseDatabase database;
    DatabaseReference mDatabaseUser, mDatabaseEmail;
    User user;

    private Assistant watsonAssistant;
    private Response<SessionResponse> watsonAssistantSession;



    private void createServices() {
        watsonAssistant = new Assistant("2019-02-28", new IamAuthenticator(mContext.getString(R.string.assistant_apikey)));
        watsonAssistant.setServiceUrl(mContext.getString(R.string.assistant_url));
    }


    private void SentimentIndex(String Index) {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount act = GoogleSignIn.getLastSignedInAccount(this);

        String UserEmail = act.getEmail();
        String email = UserEmail.replaceAll("[.#$]", ",");
        mDatabaseEmail.child(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    String UserId = snapshot.getValue().toString();
                    DatabaseReference userRef = mDatabaseUser.child(UserId);
                    //second add value event listener tries to retrieve the actual properties of the original user object.
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            HashMap hm = (HashMap) snapshot.getValue();
                            String ScoreStart=Index.trim();
                            if (!chatInitiated && ScoreStart.contains("Let's get started. Answer the questions with a range")){
                                chatInitiated=true;
                                localSentimentScore=0;
                            }
                            else{
                                localSentimentScore+=score(ScoreStart);
                                System.out.println("SCORE"+localSentimentScore);
                            }
//                            ageEditText.setText("" + hm.get("age"));

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //error msg here
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //error msg here
            }
        });
    }



    private int score(String msg){
        if (msg.contains("Fantastic job!")){
            return 2;
        }
        else if (msg.contains("That's great!")){
            return 1;
        }
        else if (msg.contains("That's alright.")){
            return 0;
        }
        else if (msg.contains("Oh no :(")){
            return -1;
        }
        else if (msg.contains("I'm so sorry to hear that :(")){
            return -2;
        }
        else if (msg.contains("Thanks for the evaluation! For more information")){
            System.out.println("done");
            Class activityClass = ChatActivity.class;  // This could be passed in as a variable.

            Intent i;
            i = new Intent(ChatActivity.this, activityClass);
            i.putExtra("var1", localSentimentScore);
            startActivity(i);
        }
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database=FirebaseDatabase.getInstance();
        mDatabaseUser=database.getReference(USER_DATA);
        mDatabaseEmail=database.getReference(USER_MAP);

        mContext = getApplicationContext();

        inputMessage = findViewById(R.id.message);
        btnSend = findViewById(R.id.btn_send);
        String customFont = "Montserrat-Regular.ttf";
        Typeface typeface = Typeface.createFromAsset(getAssets(), customFont);
        inputMessage.setTypeface(typeface);
        recyclerView = findViewById(R.id.recycler_view);

        messageArrayList = new ArrayList<>();
        mAdapter = new ChatAdapter(messageArrayList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        this.inputMessage.setText("");
        this.initialRequest = true;

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection()) {
                    sendMessage();
                }
            }
        });

        createServices();
        sendMessage();
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.refresh:
            finish();
            startActivity(getIntent());

    }
        return(super.onOptionsItemSelected(item));
    }



    // Sending a message to Watson Assistant Service
    private void sendMessage() {

        final String inputmessage = this.inputMessage.getText().toString().trim();
        if (!this.initialRequest) {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputmessage);
            inputMessage.setId("1");
            messageArrayList.add(inputMessage);
        } else {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputmessage);
            inputMessage.setId("100");
            this.initialRequest = false;
        }

        this.inputMessage.setText("");
        mAdapter.notifyDataSetChanged();

        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    if (watsonAssistantSession == null) {
                        ServiceCall<SessionResponse> call = watsonAssistant.createSession(new CreateSessionOptions.Builder().assistantId(mContext.getString(R.string.assistant_id)).build());
                        watsonAssistantSession = call.execute();
                    }

                    MessageInput input = new MessageInput.Builder()
                            .text(inputmessage)
                            .build();
                    MessageOptions options = new MessageOptions.Builder()
                            .assistantId(mContext.getString(R.string.assistant_id))
                            .input(input)
                            .sessionId(watsonAssistantSession.getResult().getSessionId())
                            .build();
                    Response<MessageResponse> response = watsonAssistant.message(options).execute();
                    Log.i(TAG, "run: " + response.getResult());
                    if (response != null &&
                            response.getResult().getOutput() != null &&
                            !response.getResult().getOutput().getGeneric().isEmpty()) {

                        List<RuntimeResponseGeneric> responses = response.getResult().getOutput().getGeneric();

                        for (RuntimeResponseGeneric r : responses) {
                            //bot message
                            Message outMessage;
                            switch (r.responseType()) {
                                case "text":
                                    outMessage = new Message();
                                    outMessage.setMessage(r.text());
                                    outMessage.setId("2");
                                    SentimentIndex(outMessage.getMessage());
                                    messageArrayList.add(outMessage);
                                    break;

                                case "option":
                                    outMessage =new Message();
                                    String title = r.title();
                                    String OptionsOutput = "";
                                    for (int i = 0; i < r.options().size(); i++) {
                                        DialogNodeOutputOptionsElement option = r.options().get(i);
                                        OptionsOutput = OptionsOutput + option.getLabel() +"\n";

                                    }
                                    outMessage.setMessage(title + "\n" + OptionsOutput);
                                    outMessage.setId("2");

                                    messageArrayList.add(outMessage);
                                    break;

                                case "image":
                                    outMessage = new Message(r);
                                    messageArrayList.add(outMessage);
                                    break;
                                default:
                                    Log.e("Error", "Unhandled message type");
                            }
                        }

                        runOnUiThread(new Runnable() {
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                if (mAdapter.getItemCount() > 1) {
                                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() - 1);

                                }

                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }


    /**
     * Check Internet Connection
     *
     * @return
     */
    private boolean checkInternetConnection() {
        // get Connectivity Manager object to check connection
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        // Check for network connections
        if (isConnected) {
            return true;
        } else {
            Toast.makeText(this, " No Internet Connection available ", Toast.LENGTH_LONG).show();
            return false;
        }

    }



    private void showError(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }


}



