package com.example.sentimentanalysis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    Button googleBtn;
    Button signOutBtn;
    Button profileSaveBtn;
    TextView textview;
    EditText ageinput;

    public static final String EXTRA_MESSAGE = "com.example.Sentiment-Analysis.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        googleBtn = this.findViewById(R.id.google_button);
        signOutBtn   = findViewById(R.id.signOutBtn);
        profileSaveBtn = this.findViewById(R.id.profilesave);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount act = GoogleSignIn.getLastSignedInAccount(this);

        if(act != null){
            doCoolStuff();
        }
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        profileSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    void signIn() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    void signOut(){
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                finish();
                startActivity(new Intent(ProfileActivity.this,MainActivity.class));
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
                doCoolStuff();
            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "oops", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void doCoolStuff(){
        GoogleSignInAccount act = GoogleSignIn.getLastSignedInAccount(this);
        String UserDname = act.getDisplayName();
        String UserEmail = act.getEmail();
        String UserGname = act.getGivenName();
        System.out.println(UserDname);
        System.out.println(UserEmail);
        System.out.println(UserGname);
        Toast.makeText(getApplicationContext(), UserDname, Toast.LENGTH_LONG).show();
        ImageView profimage = findViewById(R.id.profile_image);
        Picasso.get().load("https://i.pinimg.com/736x/78/fe/99/78fe99b1ea00a5b81729ad7d8933ada2.jpg").into(profimage);
        googleBtn.setText("Logged in as " + UserDname);
        googleBtn.setAlpha(.5f);
        googleBtn.setEnabled(false);















    }

    public void sendMessage () {
        Intent intent = new Intent(this,DietActivity.class);
        EditText ageinput = (EditText) findViewById(R.id.editTextTextPersonAge);
        String message = ageinput.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }





}