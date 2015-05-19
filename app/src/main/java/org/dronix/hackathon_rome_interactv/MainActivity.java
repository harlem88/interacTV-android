package org.dronix.hackathon_rome_interactv;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener , TextToSpeech.OnUtteranceCompletedListener {
    private TextToSpeech mTts;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressDialog = ProgressDialog.show(this, "", "");
        mTts = new TextToSpeech(getBaseContext(), this);
        mTts.setOnUtteranceCompletedListener(this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            mTts.setLanguage(Locale.ITALIAN);
            HashMap<String, String> myHashAlarm = new HashMap<>();
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                    String.valueOf(AudioManager.STREAM_ALARM));
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "INIT");
            mTts.speak(getString(R.string.start_step), TextToSpeech.QUEUE_FLUSH, myHashAlarm);

        } else {
            Toast.makeText(this, "TTS Initialization failed", Toast.LENGTH_SHORT).show();
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
        }

    }

    private void initFragment(){

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTts.stop();
        mTts.shutdown();

    }


    @Override
    public void onUtteranceCompleted(String utteranceId) {
        if (utteranceId.equalsIgnoreCase("INIT")){
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();

        }
    }
}
