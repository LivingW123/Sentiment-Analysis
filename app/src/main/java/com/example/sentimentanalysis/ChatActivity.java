package com.example.sentimentanalysis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class ChatActivity extends AppCompatActivity {

    TextView UserInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        UserInput=findViewById(R.id.MessageTextView);

    }
}

