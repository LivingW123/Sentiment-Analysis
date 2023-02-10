package com.example.sentimentanalysis;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
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

    int exercise_count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        int DailyReset=0;
        Date currentTime = Calendar.getInstance().getTime();

        ExerciseActivity.workout_webscrape dw = new ExerciseActivity.workout_webscrape();
        dw.execute();

        Spinner spinner = findViewById(R.id.exercise_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.excercise, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(this);
//        CheckBox checked = findViewById(R.id.exercise_check);
//        if(checked.isChecked()){
//            exercise_count+=1;
//        }
    }

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
