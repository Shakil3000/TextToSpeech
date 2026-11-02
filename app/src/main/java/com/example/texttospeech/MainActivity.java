package com.example.texttospeech;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextToSpeech textToSpeech;
    private EditText editText;
    private SeekBar seekBarPitch;
    private SeekBar seekBarSpeed;
    private Button buttonForSay;
    private Button buttonForSave;
    private String mAudioFileName = "";
    @SuppressLint("MissingInflatedId")
    boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        buttonForSay = findViewById(R.id.buttonSay);
        buttonForSave = findViewById(R.id.buttonSave);
        editText = findViewById(R.id.editText);
        seekBarPitch = findViewById(R.id.seekBarPitch);
        seekBarSpeed = findViewById(R.id.seekBarSpeed);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int result = textToSpeech.setLanguage(Locale.ENGLISH);
                    if(result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED)
                        Log.e("TTS", "Language not supported");
                    else {
                        buttonForSay.setEnabled(true);
                        buttonForSave.setEnabled(true);
                    }
                }
                else {
                    Log.e("TTS","Initialization failed");
                }
                buttonForSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveAudioFile();
                    }
                });
            }

                @SuppressLint("ObsoleteSdkInt")
                private void saveAudioFile() {
                    File shakil = new File(Environment.getExternalStorageDirectory() + "my file");
                    shakil.mkdir();
                    mAudioFileName = shakil.getAbsolutePath() + "/" + "totts" + ".wav";

                    HashMap<String, String> hm = new HashMap();
                    hm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"totts");
                    textToSpeech.synthesizeToFile(String.valueOf(editText.getText()), hm, mAudioFileName);
                    Toast.makeText(MainActivity.this, "Saved to " + mAudioFileName, Toast.LENGTH_LONG).show();
                }
            });


        buttonForSay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }

        });



    }
    private void speak() {
        String myText = editText.getText().toString();

        float pitch = (float) seekBarPitch.getProgress()/50;
        if (pitch < 0.1f)
            pitch = 0.1f;
        float speed = (float) seekBarSpeed.getProgress()/50;
        if(speed < 0.1f)
            speed = 0.1f;

        textToSpeech.setPitch(pitch);
        textToSpeech.setSpeechRate(speed);
        textToSpeech.speak(myText,TextToSpeech.QUEUE_FLUSH,null);
    }


    @Override
    protected void onDestroy() {
        if (textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}