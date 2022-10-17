package com.example.sentimentanalysis;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class DietActivity extends AppCompatActivity {
    PieChart pieChart;
    PieData pieData;
    PieDataSet pieDataSet;
    ArrayList pieEntries;
    ArrayList PieEntryLabels;
    double rec_calories=2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);
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


        TextView smth =findViewById(R.id.red_value);
        int weight = 100;
        int height = 150;
        int age = 12;

        String gender = "m";
        if (gender=="m"){
            rec_calories=13.75*weight+5*height-6.76*age+66;
        }
        else{
            rec_calories=9.56*weight+1.85*height-4.68*age+655;
        }
        int rec_rounded = (int)Math.round(rec_calories);
        smth.setText(""+rec_rounded);
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
        int prop2 = (two-one);


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
}

