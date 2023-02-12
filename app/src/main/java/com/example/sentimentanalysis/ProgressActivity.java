package com.example.sentimentanalysis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.HashMap;

public class ProgressActivity extends AppCompatActivity {

    GraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        // on below line we are initializing our graph view.
        graphView = findViewById(R.id.GraphView);

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
//                            HashMap hm3 = (HashMap) snapshot.getValue();
//                            ArrayList<Long> hist = ((ArrayList<Long>)((HashMap) hm3.get(id)).get("sentiment"));
//                            String s = "Most recent score is : " + hist.get(hist.size() - 1);
//                            System.out.println(s);
//                            hist.add(hist.get(hist.size() - 1) + 1);
//                            String value=getIntent().getExtras().getString("key");
//                            System.out.println(hist);
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




        // on below line we are adding data to our graph view.



        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                // on below line we are adding
                // each point on our x and y axis.
                new DataPoint(0, 1),
                new DataPoint(1, 3),
                new DataPoint(2, 4),
                new DataPoint(3, 9),
                new DataPoint(4, 6),
                new DataPoint(5, 3),
                new DataPoint(6, 6),
                new DataPoint(7, 1),
                new DataPoint(8, 2)
        });

        // after adding data to our line graph series.
        // on below line we are setting
        // title for our graph view.
        graphView.setTitle("Sentiment Score");

        // on below line we are setting
        // text color to our graph view.
        graphView.setTitleColor(R.color.teal);

        // on below line we are setting
        // our title text size.
        graphView.setTitleTextSize(25);

        //on below line we are adding
        //data series to our graph view.
        graphView.addSeries(series);
    }
}