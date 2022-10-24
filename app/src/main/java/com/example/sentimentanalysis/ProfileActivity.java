package com.example.sentimentanalysis;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;


public class ProfileActivity extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    Button googleBtn;
    Button signOutBtn;
    Button profileSaveBtn;
    TextView textview;
    EditText ageinput;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseUser, mDatabaseEmail;
    private FirebaseAuth mAuth;
    private User user;
    private static final String USER="user";

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
            changeUI();
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

        EditText emailEditText=findViewById(R.id.editTextEmailAddress);
        EditText nameEditText=findViewById(R.id.editTextTextPersonName);
        EditText ageEditText=findViewById(R.id.editTextTextPersonAge);
        EditText phone_numberEditText=findViewById(R.id.editTextTextPersonPhone);
        EditText heightfeetEditText=findViewById(R.id.editTextHeightFeet);
        EditText heightinchesEditText=findViewById(R.id.editTextHeightInches);
        EditText weightEditText=findViewById(R.id.editTextWeight);
        Switch genderEditSwitch=findViewById(R.id.genderswitch);

//        EditText passwordEditText=findViewById(R.id.passcode);

        database=FirebaseDatabase.getInstance();
        mDatabaseUser=database.getReference(USER);
        mDatabaseEmail=database.getReference("emailtoUid");
        mAuth = FirebaseAuth.getInstance();

        profileSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    String email = emailEditText.getText().toString();
                    String name = nameEditText.getText().toString();
                    String age = ageEditText.getText().toString();
                    String phoneNumber = phone_numberEditText.getText().toString();
                    int heightFeet = Integer.parseInt(heightfeetEditText.getText().toString());
                    int heightInches = Integer.parseInt(heightinchesEditText.getText().toString());
                    int weight = Integer.parseInt(weightEditText.getText().toString());
                    Boolean switchState = genderEditSwitch.isChecked();
                    String gender;
                    if (switchState==true){
                        gender = "m";
                    }
                    else{
                        gender = "f";
                    }
                    System.out.println(gender);
                String password = email;
                    User u = new User(email, password, name, age, phoneNumber, gender, heightFeet, heightInches, weight);
                    register(email,password);
                    String keyId=mDatabaseUser.push().getKey();

                    mDatabaseUser.child(keyId).setValue(u);
                    mDatabaseEmail.child(email.replaceAll("[.#$]" , ",")).setValue(keyId);
            }
        });
    }

    public void register(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    public void updateUI(FirebaseUser Fuser){
        String keyId=mDatabaseUser.push().getKey();
        mDatabaseUser.child(keyId).setValue(user);
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
                changeUI();
            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "oops", Toast.LENGTH_SHORT).show();
            }
        }
    }

    String UserEmail;
    void changeUI(){
        database=FirebaseDatabase.getInstance();
        mDatabaseUser=database.getReference(USER);
        mDatabaseEmail=database.getReference("emailtoUid");

        GoogleSignInAccount act = GoogleSignIn.getLastSignedInAccount(this);
        String UserDname = act.getDisplayName();
        UserEmail = act.getEmail();

        mDatabaseEmail.child(UserEmail.replaceAll("[.#$]" , ",")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("test lol"+dataSnapshot.getValue().toString());
                String UserId=dataSnapshot.getValue().toString();

                mDatabaseUser.child(UserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        User u = (User) snapshot.getKey();
//                        System.out.println(u.getAge());
                        HashMap hm = (HashMap) snapshot.getValue();
                        System.out.println(hm.get("password"));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });





        String profilePic = act.getPhotoUrl().toString();
        /**System.out.println("url is:" + profilePic);
        System.out.println(UserDname);
        System.out.println(UserEmail);
        System.out.println(UserGname);*/
        Toast.makeText(getApplicationContext(), UserDname, Toast.LENGTH_LONG).show();
        ImageView profimage = findViewById(R.id.profile_image);
        Picasso.get().load(profilePic).into(profimage);
        EditText PersonNameChange=findViewById(R.id.editTextTextPersonName);
        PersonNameChange.setText(UserDname);
        EditText PersonMailChange=findViewById(R.id.editTextEmailAddress);
        PersonMailChange.setText(UserEmail);
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


    public void retrieve(){
        System.out.println(mDatabaseEmail+UserEmail);

        EditText ageChange=findViewById(R.id.editTextTextPersonAge);
        EditText phone_numberChange=findViewById(R.id.editTextTextPersonPhone);
        EditText heightfeetChange=findViewById(R.id.editTextHeightFeet);
        EditText heightinchesChange=findViewById(R.id.editTextHeightInches);
        EditText weightChange=findViewById(R.id.editTextWeight);
        Switch genderChange=findViewById(R.id.genderswitch);

        ageChange.setText("lol");
        phone_numberChange.setText("lol");
        heightfeetChange.setText("lol");
        heightinchesChange.setText("lol");
        weightChange.setText("lol");
    }


//
//    public void editProfile(){
//
//    }
//
//    public void accountCheck(){
//        if ((database.getReference("emailtoUid")).child(UserEmail.replaceAll("[.#$]" , ","))==null){
//            ;
//        }
//        else{
//            System.out.println("account already made");
//        }
//    }


}
