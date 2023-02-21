package com.example.sentimentanalysis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class DietActivity extends AppCompatActivity {
    PieChart pieChart;
    PieData pieData;
    PieDataSet pieDataSet;
    ArrayList pieEntries;
    ArrayList PieEntryLabels;
    TextView RecipeTitle;
    AppCompatButton RecipeButton;
    TextView CookTime;
    TextView Serving;
    TextView Fat;
    TextView Carbs;
    TextView Protein;
    TextView Calories;
    ImageView RecipeImage;
    double rec_calories=2000;
    int weight;
    int height;
    int age;
    int rec_rounded;
    int prop2;
    String gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);
        Button Submit = this.findViewById(R.id.SubmitCal);
        Submit.setOnClickListener(this::onSubClicked);

        food_webscrape dw = new food_webscrape();
        dw.execute();

        pieChart = findViewById(R.id.pieChart);
        getEntries(1,1);
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setOrientation(Legend.LegendOrientation.VERTICAL);
//        l.setDrawInside(false);

        //https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartLib/src/main/java/com/github/mikephil/charting/charts/PieChart.java

        //pieDataSet.setSelectionShift(30f);

        Intent intent = getIntent();
        String message = intent.getStringExtra(ProfileActivity.EXTRA_MESSAGE);
        TextView textView = findViewById(R.id.agetext);
        textView.setText(message);

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
                            System.out.println(hm3.get(id));
                            weight = ((Long)((HashMap) hm3.get(id)).get("weight")).intValue();
                            height = ((Long)((HashMap) hm3.get(id)).get("height")).intValue();
                            System.out.println(height);
                            age=Integer.parseInt((String)((HashMap) hm3.get(id)).get("age"));

                            TextView smth =findViewById(R.id.red_value);

                            gender=((String)((HashMap) hm3.get(id)).get("gender"));
                            System.out.println(gender);
                            if (gender.equals("m")){
                                rec_calories=(13.75*weight)+(5.003*height)-(6.75*age)+66.5;
                            }
                            else{
                                rec_calories=9.563*weight+1.85*height-4.676*age+655.1;
                            }
                            rec_rounded = (int)Math.round(rec_calories);
                            smth.setText(""+rec_rounded);
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








        int act_calories = 0;

        EditText real_calories = findViewById(R.id.real_calories);
        real_calories.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }


            @Override
            public void afterTextChanged(Editable s) {
                //get calories rec for every person
                int numCals;
                if (s.toString().equals("")){
                    numCals = 0;
                    getEntries(numCals,rec_rounded);
                }
                else{
                    numCals = Integer.parseInt(s.toString());
                    if (numCals>rec_rounded){
                        numCals = rec_rounded;
                        getEntries(numCals, rec_rounded);
                    }
                    else{
                        getEntries(numCals,rec_rounded);
                    }
                }
            }
        });


    }
    private void getEntries(int one, int two) {
        int prop1 = one;
        prop2 = (two-one);



        pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(prop1, "My Calories"));
        pieEntries.add(new PieEntry(prop2, "Recommended Calories"));

        pieDataSet = new PieDataSet(pieEntries, "");
        pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);

        pieChart.setHoleRadius(20);
        pieChart.setTransparentCircleAlpha(20);

        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieDataSet.setValueTextSize(20f);

        pieChart.getDescription().setEnabled(false);
        Legend l = pieChart.getLegend();
        l.setEnabled(false);
    }


    private void onSubClicked(View view){
        Intent i;
        i = new Intent(DietActivity.this, MainActivity.class);
        int dargs;

        if (prop2==0){
            dargs = 1;
        }
        else{
            dargs = 0;
        }

        i.putExtra("DietColor",dargs);
        startActivity(i);
    }

    private class food_webscrape extends AsyncTask<Void, Void, Void> {

        @Override
        protected  void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void...voids) {
            org.jsoup.nodes.Document document = null;
            try {
                document = Jsoup.connect("https://www.allrecipes.com/recipes/17561/lunch/").get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            org.jsoup.select.Elements cards = document.select("a.mntl-card, a.card");
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    LinearLayout item = (LinearLayout)findViewById(R.id.DietCardHolder);
                    for(int i = 0; i < cards.size(); i++)
                    {
                        View child = getLayoutInflater().inflate(R.layout.activity_recipe_card, null);

                        RecipeImage=child.findViewById(R.id.RecipeImage);
                        Picasso.get().load(cards.get(i).child(0).child(0).child(0).child(0).attr("data-src")).into(RecipeImage);

                        RecipeTitle=child.findViewById(R.id.RecipeTitle);
                        RecipeTitle.setText(cards.get(i).child(1).getElementsByAttributeValue("class", "card__title").text());

                        RecipeButton=child.findViewById(R.id.RecipeButton);
                        String link = cards.get(i).attr("href");
                        //System.out.println(link);
                        RecipeButton.setOnClickListener(view ->{
                            Uri uri = Uri.parse(link);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        });
                        Thread thread = new Thread(new Runnable(){
                            @Override
                            public void run() {
                                try {
                                    org.jsoup.nodes.Document dish = null;
                                    //System.out.println(link);
                                    org.jsoup.nodes.Document document = Jsoup.connect(link).get();
                                    CookTime=child.findViewById(R.id.CookTime);
                                    String s = document.getElementsByAttributeValue("class","mntl-recipe-details__content").text();
                                    String m="N/A";
                                    String n="N/A";
                                    System.out.println("s is"+s);
                                    if(s.contains("Total Time:"))
                                    {
                                        //System.out.println("contains!");

                                        m = s.substring(s.indexOf("Total Time:") + 11);
                                        if(m.contains("mins")) {
                                            int minsIndex = m.indexOf("mins") + 4;
                                            m = m.substring(0, minsIndex);
                                        }
                                        else{
                                                int hrsIndex = m.indexOf("hrs")+ 3;
                                                m = m.substring(0, hrsIndex);}
                                        System.out.println("m is" + m);
                                    }
                                    CookTime.setText(m);
                                    Serving=child.findViewById(R.id.Serving);
                                    if (s.contains("Servings:")){
                                        n = s.substring(s.indexOf("Servings:") + 9);
                                        if(n.contains("Yield:")) {
                                            int minsIndex = n.indexOf("Yield:");
                                            n = n.substring(0, minsIndex);
                                        }
                                    }
                                    Serving.setText(n);
                                    String x = document.getElementsByAttributeValue("class","mntl-nutrition-facts-summary__table-row").text();
                                    System.out.println("x is"+x);
                                    Calories=child.findViewById(R.id.Calories);
                                    Calories.setText(x.substring(0,x.indexOf("Calories")));
                                    Fat=child.findViewById(R.id.Fat);
                                    Fat.setText(x.substring(x.indexOf("Calories")+8,x.indexOf("Fat")));
                                    Carbs=child.findViewById(R.id.Carbs);
                                    Carbs.setText(x.substring(x.indexOf("Fat")+3,x.indexOf("Carbs")));
                                    Protein=child.findViewById(R.id.Protein);
                                    Protein.setText(x.substring(x.indexOf("Carbs")+5,x.indexOf("Protein")));
                                } catch (Exception e) {
                                }
                            }
                        });
                        thread.start();
                        item.addView(child);
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


        }
    }
}