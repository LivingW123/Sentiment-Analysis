package com.example.sentimentanalysis;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sentimentanalysis.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

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
        LinearLayout ChatCard = this.findViewById(R.id.ChatSelection);
        Intent cintent = getIntent();
        int cargs = cintent.getIntExtra("cargs",0);
        if (cargs==1){
            ChatCard.setBackgroundColor(getResources().getColor(R.color.light_green));
            Intent cloop;
            int cloopnum = 1;
            cloop = new Intent(MainActivity.this, ChatActivity.class);
            cloop.putExtra("cargs",cloopnum);
            startActivity(cloop);
        }
        else{
            ChatCard.setBackgroundColor(getResources().getColor(R.color.light_pink));
        }

        Button ToDiet = this.findViewById(R.id.ButtonDiet);
        ToDiet.setOnClickListener(this::onDietClicked);
        Intent dintent = getIntent();
        int dargs = dintent.getIntExtra("DietColor",0);
        LinearLayout DietCard = this.findViewById(R.id.DietSelection);
        if (dargs==1){
            DietCard.setBackgroundColor(getResources().getColor(R.color.light_green));
        }
        else{
            DietCard.setBackgroundColor(getResources().getColor(R.color.light_pink));
        }

        Button ToExercise = this.findViewById(R.id.SubmitCal);
        ToExercise.setOnClickListener(this::onExerciseClicked);
        LinearLayout ExerciseCard = this.findViewById(R.id.ExerciseSelection);

        GoogleSignInOptions gso;
        GoogleSignInClient gsc;
        FirebaseDatabase database;
        DatabaseReference mDatabaseUser, mDatabaseEmail;
        User user;

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount act = GoogleSignIn.getLastSignedInAccount(this);

        //if there's already a signed in user in the session fill the activity with its info
//        if(act != null){
//            String email = act.getEmail();
//            database=FirebaseDatabase.getInstance();
//            mDatabaseUser=database.getReference(getString(R.string.USER_DATA));
//            mDatabaseEmail=database.getReference(getString(R.string.USER_MAP));
//            mDatabaseEmail.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    HashMap hm = (HashMap) snapshot.getValue();
//                    String id = (String)(hm.get(email.replaceAll("[.#$]" , ",")));
//                    mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            HashMap hm2 = (HashMap) snapshot.getValue();
//                            ArrayList<Long> hist = ((ArrayList<Long>)((HashMap) hm2.get(id)).get("sentiment"));
//                            String s = "Most recent score is : " + hist.get(hist.size() - 1);
//                            System.out.println(s);
//                            hist.add( hist.get(hist.size() - 1) + 1);
//                            mDatabaseUser.child(id).child("sentiment").setValue(hist);
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//        }

        LinearLayout MainList = this.findViewById(R.id.MainList);

        final KonfettiView konfettiView = findViewById(R.id.konfettiView);


        MainList.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    float x = (int) event.getX();
                    float y = (int) event.getY()+100;
                    System.out.println(x);
                    System.out.println(y);
                    konfettiView.build()
                            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                            .setDirection(0.0,359.0)
                            .setSpeed(1f, 5f)
                            .setFadeOutEnabled(false)
                            .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                            .addSizes(new Size(8,4f))
                            .setPosition(x-0,x-0,y-0,y-0)
                            .streamFor(100,500L);
                }
                return true;
            }
        });


        MainList.setOnClickListener((view) -> {

            konfettiView.build()
                    .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                    .setDirection(0.0,359.0)
                    .setSpeed(1f, 5f)
                    .setFadeOutEnabled(true)
                    .setTimeToLive(1000L)
                    .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                    .addSizes(new Size(8,4f))
                    .setPosition(-50f,konfettiView.getWidth()+50f,-50f,-50f)
                    .streamFor(300,1000L);
        });



        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        Animation animSlideInLeft = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in_left);
        Animation animSlideInRight = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_in_right);
        ChatCard.startAnimation(animFadeIn);
        ChatCard.startAnimation(animSlideInLeft);
        ExerciseCard.startAnimation(animFadeIn);
        ExerciseCard.startAnimation(animSlideInRight);
        DietCard.startAnimation(animFadeIn);
        DietCard.startAnimation(animSlideInLeft);
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

}