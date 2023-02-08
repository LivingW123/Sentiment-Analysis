package com.example.sentimentanalysis;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sentimentanalysis.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Button ToChat = this.findViewById(R.id.ButtonChat);
        ToChat.setOnClickListener(this::onChatClicked);

        Button ToDiet = this.findViewById(R.id.ButtonDiet);
        ToDiet.setOnClickListener(this::onDietClicked);

        Button ToExercise = this.findViewById(R.id.ButtonExercise);
        ToExercise.setOnClickListener(this::onExerciseClicked);

        GoogleSignInOptions gso;
        GoogleSignInClient gsc;
        FirebaseDatabase database;
        DatabaseReference mDatabaseUser, mDatabaseEmail;
        User user;

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount act = GoogleSignIn.getLastSignedInAccount(this);

        //if there's already a signed in user in the session fill the activity with its info
        if(act != null){
            String email = act.getEmail();
            database=FirebaseDatabase.getInstance();
            mDatabaseUser=database.getReference(getString(R.string.USER_DATA));
            mDatabaseEmail=database.getReference(getString(R.string.USER_MAP));
            mDatabaseEmail.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    HashMap hm = (HashMap) snapshot.getValue();
                    String id = (String)(hm.get(email.replaceAll("[.#$]" , ",")));
                    mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            HashMap hm2 = (HashMap) snapshot.getValue();
                            ArrayList<Long> hist = ((ArrayList<Long>)((HashMap) hm2.get(id)).get("sentiment"));
                            String s = "Most recent score is : " + hist.get(hist.size() - 1);
                            System.out.println(s);
                            hist.add( hist.get(hist.size() - 1) + 1);
                            mDatabaseUser.child(id).child("sentiment").setValue(hist);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

//        Button ToMusic = this.findViewById(R.id.ButtonMusic);
//        ToMusic.setOnClickListener(this::onMusicClicked);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void onChatClicked(View view){
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }

    private void onDietClicked(View view){
        Intent intent = new Intent(this, DietActivity.class);
        startActivity(intent);
    }

    private void onExerciseClicked(View view){
        Intent intent = new Intent(this, ExerciseActivity.class);
        startActivity(intent);
    }

//    private void onMusicClicked(View view){
//        Intent intent = new Intent(this, MusicActivity.class);
//        startActivity(intent);
//    }
}