package com.example.sentimentanalysis;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sentimentanalysis.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Button ToChat = this.findViewById(R.id.ButtonChat);
        ToChat.setOnClickListener(this::onChatClicked);

        Button ToDiet = this.findViewById(R.id.ButtonDiet);
        ToDiet.setOnClickListener(this::onDietClicked);

        Button ToExercise = this.findViewById(R.id.ButtonExercise);
        ToExercise.setOnClickListener(this::onExerciseClicked);

        Button ToMusic = this.findViewById(R.id.ButtonMusic);
        ToMusic.setOnClickListener(this::onMusicClicked);
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
        if (id == R.id.action_settings) {
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

    private void onMusicClicked(View view){
        Intent intent = new Intent(this, MusicActivity.class);
        startActivity(intent);
    }
}