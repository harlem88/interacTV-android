package org.dronix.hackathon_rome_interactv;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.dronix.hackathon_rome_interactv.fragment.ZappingFragment;
import org.dronix.hackathon_rome_interactv.interfaces.ISpeakEngine;

import java.util.HashMap;
import java.util.Locale;

import rx.Subscriber;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private TextToSpeech mTts;
    private ProgressDialog mProgressDialog;
    private Subscriber<Void> utteranceProgressListenerSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mProgressDialog = ProgressDialog.show(this, "", "");
        mTts = new TextToSpeech(getBaseContext(), this);
        mTts.setOnUtteranceProgressListener(utteranceProgressListener);
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

    private Handler attachZappingFragment = new Handler(msg -> {
        ZappingFragment zappingFragment = new ZappingFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, zappingFragment).commit();
        return true;
    });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTts.stop();
        mTts.shutdown();

    }

    private Handler speakEngineHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Object objMessage = msg.obj;
            if (objMessage != null && objMessage instanceof String) {
                mTts.speak((String) objMessage, TextToSpeech.QUEUE_ADD, null);
            }
            return true;
        }
    });


    private UtteranceProgressListener utteranceProgressListener = new UtteranceProgressListener() {
        @Override
        public void onStart(String utteranceId) {

        }

        @Override
        public void onDone(String utteranceId) {
            if (utteranceId.equalsIgnoreCase("INIT")){
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                attachZappingFragment.sendEmptyMessageDelayed(-1, 100);
            }
            if(utteranceProgressListenerSubscriber != null){
                utteranceProgressListenerSubscriber.onNext(null);
                utteranceProgressListenerSubscriber.onCompleted();
                utteranceProgressListenerSubscriber = null;
            }
        }

        @Override
        public void onError(String utteranceId) {

        }
    };



//    public void speak(String s){
//        mTts.speak(s, TextToSpeech.QUEUE_ADD, null);
//    }
//
//    public void speak(String s, String utterance, TextToSpeech.OnUtteranceCompletedListener onUtteranceCompletedListener){
//
//        mTts.setOnUtteranceCompletedListener(onUtteranceCompletedListener);
//        HashMap<String, String> myHashAlarm = new HashMap<>();
//        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
//                String.valueOf(AudioManager.STREAM_ALARM));
//
//        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
//                utterance);
//        mTts.speak(s, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
//    }

    private ISpeakEngine speakEngine = new ISpeakEngine() {
        @Override
        public void speak(String message) {
            Message message1 = Message.obtain();
            message1.obj = message;
            speakEngineHandler.sendMessage(message1);
        }

        @Override
        public void speak(String message, Subscriber<Void> utteranceProgressListener) {
            utteranceProgressListenerSubscriber = utteranceProgressListener;
            Message message1 = Message.obtain();
            message1.obj = message;
            speakEngineHandler.sendMessage(message1);
        }

    };

    public ISpeakEngine getSpeakEngine(){
        return speakEngine;
    }
}
