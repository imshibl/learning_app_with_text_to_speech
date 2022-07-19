package com.bilcodes.learningapp;

import static android.view.View.GONE;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ImageView micIcon;
    TextView qsTextView,ansTextView, userAnsTextView;
    Button resetButton;

    TextToSpeech speech;

    int num1;
    int num2;

    int ans;


    private static final int REQUEST_CODE_SPEECH_INPUT = 1;

    private void generateRandomNumber(){
        num1 = new Random().nextInt(10)+1;
        num2 = new Random().nextInt(10)+1;
    }

    @Override
    protected void onStart() {
        super.onStart();
        generateRandomNumber();
        ans = num1 * num2;
        String question = num1 + " x " + num2;
        String toSpeechQs = num1 + "  " + num2 + " " + "are";

        qsTextView.setText(question);
        speech.speak(toSpeechQs, TextToSpeech.QUEUE_FLUSH, null, null);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        micIcon = findViewById(R.id.micIcon);
        qsTextView = findViewById(R.id.qsTextView);
        ansTextView = findViewById(R.id.ansTextView);
        userAnsTextView = findViewById(R.id.userAnsTextView);
        resetButton = findViewById(R.id.resetButton);

        speech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR){
                    speech.setLanguage(Locale.ENGLISH);
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateRandomNumber();
                ans = num1 * num2;
                String question = num1 + " x " + num2;
                String toSpeechQs = num1 + "  " + num2 + " " + "are";

                qsTextView.setText(question);
                speech.speak(toSpeechQs, TextToSpeech.QUEUE_FLUSH, null, null);

                userAnsTextView.setVisibility(GONE);
                ansTextView.setVisibility(GONE);
            }
        });

        micIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "speech to text");

                    try{
                        startActivityIfNeeded(intent, REQUEST_CODE_SPEECH_INPUT);
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, ' ' + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    Toast.makeText(MainActivity.this, "First reset the question" , Toast.LENGTH_SHORT).show();
                }



            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SPEECH_INPUT){
            if(resultCode == RESULT_OK && data != null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                userAnsTextView.setVisibility(View.VISIBLE);
                userAnsTextView.setText(Objects.requireNonNull(result).get(0));
                String toSpeakResult;

                try{
                    if(Integer.parseInt(userAnsTextView.getText().toString()) == ans){
                        toSpeakResult = "Correct";

                    }else{
                        toSpeakResult = "Incorrect";
                    }
                    speech.speak(toSpeakResult, TextToSpeech.QUEUE_FLUSH, null, null);
                    ansTextView.setVisibility(View.VISIBLE);
                    ansTextView.setText(String.valueOf(ans));

                }catch (Exception e){
                    Toast.makeText(MainActivity.this, ' ' + e.getMessage(), Toast.LENGTH_SHORT).show();
                    toSpeakResult = "Something went wrong";
                    speech.speak(toSpeakResult, TextToSpeech.QUEUE_FLUSH, null, null);

                }


            }
        }
    }
}