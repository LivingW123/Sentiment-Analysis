package com.example.sentimentanalysis;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;

public class ExerciseActivity extends AppCompatActivity {

    TextView WorkoutTitle;
    AppCompatButton WorkoutButton;
    TextView WorkoutLevel;
    TextView WorkoutGoal;
    TextView WorkoutType;
    TextView WorkoutEquipment;
    TextView WorkoutLength;
    ImageView WorkoutImage;
    int weight;
    int n;
    AppCompatTextView cal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        int DailyReset=0;
        Date currentTime = Calendar.getInstance().getTime();

        ExerciseActivity.workout_webscrape dw = new ExerciseActivity.workout_webscrape();
        dw.execute();

        EditText Minutes=(EditText) findViewById(R.id.duration_minutes);
        cal=findViewById(R.id.cal);

        Spinner spinner = (Spinner) findViewById(R.id.exercise_type_spinner);
//        String size = spinner.getSelectedItem().toString();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.excercise, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Double time = Double.valueOf(Minutes.getText().toString());
                int spinner_pos = spinner.getSelectedItemPosition();
                String[] size_values = getResources().getStringArray(R.array.excercise_val);
                double size = Double.valueOf(size_values[spinner_pos]);
                double MET=time*size*3.5*weight*0.00226796185;
                int RMET =(int)Math.round(MET);
                System.out.println(RMET);
                cal.setText("Calories: "+String.valueOf(RMET));
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


//        spinner.setOnItemSelectedListener(this);
//        CheckBox checked = findViewById(R.id.exercise_check);
//        if(checked.isChecked()){
//            exercise_count+=1;
//        }\

        GoogleSignInOptions gso;
        GoogleSignInClient gsc;
        FirebaseDatabase database;
        DatabaseReference mDatabaseUser, mDatabaseEmail;
        User user;

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount act = GoogleSignIn.getLastSignedInAccount(this);

//        Intent i;
//        i = new Intent(DietActivity.this, MainActivity.class);
//        int args = 0;
//        i.putExtra("DietColor",args);
//        startActivity(i);

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
                            HashMap hm3 = (HashMap) snapshot.getValue();
                            weight = ((Long)((HashMap) hm3.get(id)).get("weight")).intValue();
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




    }



//    private void onSpinnerClicked(View view){
//        //n=...
//        double MET=n*3.5*weight*0.00226796185;
//        int RMET =(int)Math.round(MET);
//    }

    private class workout_webscrape extends AsyncTask<Void, Void, Void> {

        @Override
        protected  void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void...voids) {
            org.jsoup.nodes.Document document2 = null;
            try {
                document2 = Jsoup.connect("https://www.muscleandstrength.com/workouts/home").get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Elements cards = document2.select("div.cell.small-12.bp600-6");
            //System.out.println(cards);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    LinearLayout item = (LinearLayout) findViewById(R.id.WorkoutCardHolder);
                    for (int i = 0; i < cards.size(); i++) {
                        View child = getLayoutInflater().inflate(R.layout.activity_workout_card, null);

                        WorkoutImage = child.findViewById(R.id.WorkoutImage);
                        //Node e =cards.get(i).childNode(1);
                        Picasso.get().load(cards.get(i).childNode(1).childNode(1).childNode(0).attr("data-src")).into(WorkoutImage);

                        WorkoutTitle = child.findViewById(R.id.WorkoutTitle);
                        WorkoutTitle.setText(cards.get(i).child(1).getElementsByAttributeValue("class", "node-title").text());

                        WorkoutButton = child.findViewById(R.id.WorkoutButton);
                        String link = cards.get(i).childNode(3).childNode(1).attr("href");
                        WorkoutButton.setOnClickListener(view ->{
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://www.muscleandstrength.com" + link));
                            startActivity(intent);
                        });
//
                        Thread thread = new Thread(new Runnable(){
                            @Override
                            public void run() {
                                try {
                                    org.jsoup.nodes.Document dish = null;
                                    //.println(link);
                                    org.jsoup.nodes.Document document = Jsoup.connect("https://www.muscleandstrength.com"+link).get();
                                    String p=document.getElementsByAttributeValue("class","node-stats-block").text();
                                    System.out.println("p is"+p);
                                    WorkoutGoal=child.findViewById(R.id.WorkoutGoal);
                                    WorkoutGoal.setText(p.substring(p.indexOf("Goal")+4,p.indexOf("Workout Type")));
                                    WorkoutType=child.findViewById(R.id.WorkoutType);
                                    WorkoutType.setText(p.substring(p.indexOf("Type")+4,p.indexOf("Training Level")));
                                    WorkoutLevel =child.findViewById(R.id.WorkoutLevel);
                                    WorkoutLevel.setText(p.substring(p.indexOf("Training Level")+14,p.indexOf("Program")));
                                    WorkoutLength=child.findViewById(R.id.WorkoutLength);
                                    WorkoutLength.setText(p.substring(p.indexOf("Time Per Workout")+16,p.indexOf("Equipment")));
                                    WorkoutEquipment=child.findViewById(R.id.WorkoutEquipment);
                                    WorkoutEquipment.setText(p.substring(p.indexOf("Required")+8,p.indexOf("Target Gender")));
                                    //Your code goes here
                                } catch (Exception e) {
                                }
                            }
                        });
                        thread.start();
                        item.addView(child);
                    }

//                    for (Element link : links) {
//                        View child = getLayoutInflater().inflate(R.layout.activity_recipe_card, null);
//                        RecipeButton=child.findViewById(R.id.RecipeButton);
//
//
//                        item.addView(child);
//                        System.out.println(link.ownText());
//                    }

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


        }
    }
}


//    @Override
//    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        String text = adapterView.getItemAtPosition(i).toString();
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> adapterView) {
//
//    }
