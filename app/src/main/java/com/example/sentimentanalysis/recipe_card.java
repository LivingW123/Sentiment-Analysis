package com.example.sentimentanalysis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class recipe_card extends AppCompatActivity {

    TextView RecipeTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_card);
        RecipeTitle=findViewById(R.id.RecipeTitle);
    }

    private class title_webscrape extends AsyncTask<Void, Void, Void> {

        @Override
        protected  void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackgound(Void...voids) {


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {


        }
    }
}