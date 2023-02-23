package com.example.sentimentanalysis;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileActivity extends AppCompatActivity {

    private static final int DEFAULT = 50;
    public static final String EXTRA_MESSAGE = "com.example.Sentiment-Analysis.MESSAGE";

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    FirebaseDatabase database;
    DatabaseReference mDatabaseUser, mDatabaseEmail;
    User user;

    Button googleBtn;
    Button signOutBtn;
    Button profileSaveBtn;
    EditText emailEditText;
    EditText nameEditText;
    EditText ageEditText;
    EditText phone_numberEditText;
    EditText heightfeetEditText;
    EditText heightinchesEditText;
    EditText weightEditText;
    Switch genderEditSwitch;
    ArrayList<Long> currentHist = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageButton ToHome = this.findViewById(R.id.HomeButton);
        ToHome.setOnClickListener(this::onHomeClicked);

        Button ToProgress = this.findViewById(R.id.SentimentIndex);
        ToProgress.setOnClickListener(this::onProgressClicked);

        //get firebase database + necessary refs
        database=FirebaseDatabase.getInstance();
        mDatabaseUser=database.getReference(getString(R.string.USER_DATA));
        mDatabaseEmail=database.getReference(getString(R.string.USER_MAP));

        //connect to ui elems
//        EditProfileButton = this.findViewById(R.id.EditProfileButton);


        googleBtn = this.findViewById(R.id.google_button);
        signOutBtn = findViewById(R.id.signOutBtn);
        profileSaveBtn = this.findViewById(R.id.profilesave);
        emailEditText=findViewById(R.id.editTextEmailAddress);
        nameEditText=findViewById(R.id.editTextTextPersonName);
        ageEditText=findViewById(R.id.editTextTextPersonAge);
        phone_numberEditText=findViewById(R.id.editTextTextPersonPhone);
        heightfeetEditText=findViewById(R.id.editTextHeightFeet);
        heightinchesEditText=findViewById(R.id.editTextHeightInches);
        weightEditText=findViewById(R.id.editTextWeight);
        genderEditSwitch=findViewById(R.id.genderswitch);

        //googlesignin/authentication setup
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount act = GoogleSignIn.getLastSignedInAccount(this);

        //if there's already a signed in user in the session fill the activity with its info
        if(act != null){
            //formerly doCoolStuff/updateUI
            fillUserInfo(act);
        }

        //appropriate clicklisteners
        googleBtn.setOnClickListener(view -> signIn());
        signOutBtn.setOnClickListener(view -> signOut());

        profileSaveBtn.setOnClickListener(view -> {

            //register data from all edittext fields
            //TODO: establish acceptable range of data in each field, write conditionals to ensure data integrity
                String email = emailEditText.getText().toString();
                String password = email;
                String name = nameEditText.getText().toString();
                String age = ageEditText.getText().toString();
                String phoneNumber = phone_numberEditText.getText().toString();
                int heightFeet = Integer.parseInt(heightfeetEditText.getText().toString());
                int heightInches = Integer.parseInt(heightinchesEditText.getText().toString());
                int weight = Integer.parseInt(weightEditText.getText().toString());
                boolean switchState = genderEditSwitch.isChecked();
                String gender = "f";
                if (switchState){
                    gender = "m";
                }
            ArrayList<Integer> cars = new ArrayList<Integer>();
            cars.add(0);

                //generate appropriate User obj
                User u = new User(email, password, name, age, phoneNumber, gender, heightFeet, heightInches, weight);
                if(currentHist != null)
                {
                    for(Long l: currentHist)
                    {
                        u.addScore(l.intValue());
                    }
                }
                else {
                    u.addScore(DEFAULT);
                }

                register(email, password);

                //push new data to firebase
                String keyId=mDatabaseUser.push().getKey();
                //new obj added into firebase
                mDatabaseUser.child(keyId).setValue(u);
                //key added to email/id hashmap
                mDatabaseEmail.child(email.replaceAll("[.#$]" , ",")).setValue(keyId);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

//        EditProfileButton.setOnClickListener(view ->{
//            emailEditText.setEnabled(true);
//            emailEditText.setFocusable(true);
//            nameEditText.setEnabled(true);
//            nameEditText.setFocusable(true);
//            ageEditText.setEnabled(true);
//            ageEditText.setFocusable(true);
//            phone_numberEditText.setEnabled(true);
//            phone_numberEditText.setFocusable(true);
//            heightfeetEditText.setEnabled(true);
//            heightfeetEditText.setFocusable(true);
//            heightinchesEditText.setEnabled(true);
//            heightinchesEditText.setFocusable(true);
//            weightEditText.setEnabled(true);
//            weightEditText.setFocusable(true);
//            genderEditSwitch.setChecked(true);
//        });
    }

    void fillUserInfo(GoogleSignInAccount act){

        //get needed ui elements
        ImageView profimage = findViewById(R.id.profile_image);
        EditText PersonNameChange=findViewById(R.id.editTextTextPersonName);
        EditText PersonMailChange=findViewById(R.id.editTextEmailAddress);

        //get data associated with google acct (NOT object)
        String UserDname = act.getDisplayName();
        String UserEmail = act.getEmail();
        Uri profilePic = act.getPhotoUrl();

        String email =  UserEmail.replaceAll("[.#$]" , ",");
        mDatabaseEmail.child(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null)
                {
                    String UserId = snapshot.getValue().toString();
                    DatabaseReference userRef= mDatabaseUser.child(UserId);
                    //second add value event listener tries to retrieve the actual properties of the original user object.
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            HashMap hm = (HashMap) snapshot.getValue();
                            ageEditText.setText("" + hm.get("age"));
                            heightfeetEditText.setText("" + ((long)hm.get("height")/12));
                            heightinchesEditText.setText("" + ((long)hm.get("height")%12));
                            phone_numberEditText.setText("" + hm.get("phonenumber"));
                            weightEditText.setText("" + hm.get("weight"));
                            genderEditSwitch.setChecked(false);
                            if(hm.get("gender").equals("m"))
                            {
                                genderEditSwitch.setChecked(true);
                            }
                            currentHist = (ArrayList<Long>) hm.get("sentiment");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            //error msg here
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //load google acct data into interface
        Toast.makeText(getApplicationContext(), "Welcome, " + UserDname + "!", Toast.LENGTH_LONG).show();
        if(profilePic != null)
        {
            Picasso.get().load(profilePic.toString()).into(profimage);
        }
        PersonNameChange.setText(UserDname);
        PersonMailChange.setText(UserEmail);

        googleBtn.setText("Logged in as " + UserDname);
        googleBtn.setAlpha(.5f);
        googleBtn.setEnabled(false);
    }

    //generates the google signin page/activity
    void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    //does what it says it does...
    void signOut(){
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                finish();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);

                //at this point, there's a google acct signed into the app. so this line is safe to call
                GoogleSignInAccount act = GoogleSignIn.getLastSignedInAccount(this);
                //fill ui with act's data
                fillUserInfo(act);
            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "oops", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void register(String email, String password){
        //access authentication data of app
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        //creates user here, totally independent from realtime database
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    }
                });
    }

    public void updateUI(FirebaseUser Fuser){
        String keyId=mDatabaseUser.push().getKey();
        mDatabaseUser.child(keyId).setValue(user);
    }


    private void onHomeClicked(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void onProgressClicked(View view){
        Intent intent = new Intent(this, ProgressActivity.class);
        startActivity(intent);
    }

}
