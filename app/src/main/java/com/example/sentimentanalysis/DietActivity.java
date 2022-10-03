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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);
        pieChart = findViewById(R.id.pieChart);
        getEntries(1,1);


        //https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartLib/src/main/java/com/github/mikephil/charting/charts/PieChart.java

        //pieDataSet.setSelectionShift(30f);

        Intent intent = getIntent();
        String message = intent.getStringExtra(ProfileActivity.EXTRA_MESSAGE);
        TextView textView = findViewById(R.id.agetext);
        textView.setText(message);


        int rec_calories = 2022;
        TextView smth =findViewById(R.id.red_value);
        smth.setText(""+rec_calories);
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
                    getEntries(numCals,rec_calories);
                }
                else{
                    numCals = Integer.parseInt(s.toString());
                    getEntries(numCals,rec_calories);
                }
            }
        });


    }
    private void getEntries(int one, int two) {
        float prop1 = (float) one / (two);
        float prop2 = (float) two / (one+two);


        pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(prop1, 0));
        pieEntries.add(new PieEntry(prop2, 1));

        pieDataSet = new PieDataSet(pieEntries, "");
        pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);

        pieChart.setHoleRadius(20);
        pieChart.setTransparentCircleAlpha(20);

        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieDataSet.setValueTextSize(20f);
    }
}

