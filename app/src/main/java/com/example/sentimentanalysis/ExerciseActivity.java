package com.example.sentimentanalysis;
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

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

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

            org.jsoup.select.Elements cards = document2.select("div.cell, div.small-12, div.bp600-6");
            System.out.println(cards);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    LinearLayout item = (LinearLayout) findViewById(R.id.WorkoutCardHolder);
                    for (int i = 0; i < cards.size(); i++) {
                        View child = getLayoutInflater().inflate(R.layout.activity_workout_card, null);

                        WorkoutImage = child.findViewById(R.id.WorkoutImage);
                        Picasso.get().load(cards.get(i).child(0).child(0).attr("data-src")).into(WorkoutImage);

                        WorkoutTitle = child.findViewById(R.id.WorkoutTitle);
                        WorkoutTitle.setText(cards.get(i).child(1).getElementsByAttributeValue("class", "node-title").text());

                        WorkoutButton = child.findViewById(R.id.WorkoutButton);
                        String link = cards.get(i).attr("href");
                        System.out.println(link);
//                        RecipeButton.setOnClickListener(view ->{
//                            Uri uri = Uri.parse(temp);
//                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                            startActivity(intent);
//                        });
//

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
