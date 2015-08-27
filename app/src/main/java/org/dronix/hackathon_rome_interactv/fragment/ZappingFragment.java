package org.dronix.hackathon_rome_interactv.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.dronix.hackathon_rome_interactv.MainActivity;
import org.dronix.hackathon_rome_interactv.R;
import org.dronix.hackathon_rome_interactv.dagger.InteracTvApplication;
import org.dronix.hackathon_rome_interactv.interfaces.ISpeakEngine;
import org.dronix.hackathon_rome_interactv.model.SmartChannel;
import org.dronix.hackathon_rome_interactv.model.SmartChannels;
import org.dronix.hackathon_rome_interactv.request.ReqManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ZappingFragment extends Fragment {
    private View mRootView;
    private View mLL_main;
    private ViewPager mViewPager;

    private static final int REQUEST_OK_REC_VOICE = 3;
    private AtomicBoolean mShakeDetection = new AtomicBoolean(false);
    public List<SmartChannel> mItems;
    private String[] mCommands;
    private boolean enableSpeech = true;
    public final String[] ZAPPING_Commands = {"vai al canale"};
    public final String[] Channels_Commands = {"info", "descrizione", " o vai al canale"};

    @Inject
    ReqManager mReqManager;

    ISpeakEngine mSpeakEngine;

    public void setCommamds(String[] commands) {
        this.mCommands = commands;
    }

    public ZappingFragment() {

    }

    public interface OnShakeDetected {
        void onShakeDetected();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((InteracTvApplication) getActivity().getApplication()).component().inject(this);
        mSpeakEngine = ((MainActivity)getActivity()).getSpeakEngine();
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(
                R.layout.fragment_main, container, false);
        mLL_main = mRootView.findViewById(R.id.ll_main);

        mRootView.setOnClickListener(onClickListener);
        ((TextView) mRootView.findViewById(R.id.title_id)).setText("Zapping");
        setCommamds(ZAPPING_Commands);

        mReqManager.getChannels()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SmartChannels>() {
                    @Override
                    public void onNext(SmartChannels smartChannels) {
                        mItems = smartChannels.getResponse();
                    }

                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {}

                });

        return mRootView;
    }


    private OnShakeDetected onShakeDetected = new OnShakeDetected() {
        @Override
        public synchronized void onShakeDetected() {
            if (mShakeDetection.compareAndSet(false, true)) {
                String commands = "";
                for (String s : mCommands) {
                    commands += s + " ,";
                }

                mSpeakEngine.speak(commands, new Subscriber<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Void aVoid) {
                        Intent recVoiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        recVoiceIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
                        recVoiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        recVoiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ITALIAN);
                        try {
                            startActivityForResult(recVoiceIntent, REQUEST_OK_REC_VOICE);
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

//                mSpeakEngine.speak(commands, "tes", new TextToSpeech.OnUtteranceCompletedListener() {
//                    @Override
//                    public void onUtteranceCompleted(String utteranceId) {
//
//
//                    }
//                });
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OK_REC_VOICE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            boolean find = false;
            for (String s : thingsYouSaid) {
                if (s.equalsIgnoreCase("info")) {
                    mSpeakEngine.speak(mItems.get(mViewPager.getCurrentItem()).getInfo());
                    break;
                } else if (s.equalsIgnoreCase("descrizione")) {
                    mSpeakEngine.speak(mItems.get(mViewPager.getCurrentItem()).getDescription());
                    break;
                } else {
                    if (mItems != null && mItems.size() > 0) {
                        for (int i = 0; i < mItems.size(); i++) {
                            if (s.equalsIgnoreCase(mItems.get(i).getName())) {
                                goTo(i, mItems.get(i).getId());
                                find = true;
                                break;
                            }
                        }
                    }
                }
                if (find)
                    break;
            }
        }
        mShakeDetection.set(false);
    }


    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity


    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter

            if (mAccel > 12) {
                onShakeDetected.onShakeDetected();
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Observable.create(new Observable.OnSubscribe<SmartChannels>() {
                @Override
                public void call(Subscriber<? super SmartChannels> subscriber) {
                    if (mItems != null && mItems.size() > 0) {
                        SmartChannels s = new SmartChannels();
                        s.setResponse(mItems);
                        subscriber.onNext(s);
                    } else {
                        mReqManager.getChannels()
                                .subscribeOn(Schedulers.io()).
                                observeOn(AndroidSchedulers.mainThread()).
                                subscribe(new Subscriber<SmartChannels>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onNext(SmartChannels smartChannels) {
                                        mRootView.findViewById(R.id.subContainer).setVisibility(View.VISIBLE);
                                        mLL_main.setVisibility(View.GONE);
                                        mRootView.setOnClickListener(null);
                                        NavigationPagerAdapter mNavigationPagerAdapter =
                                                new NavigationPagerAdapter(getChildFragmentManager(), smartChannels.getResponse());
                                        mViewPager = (ViewPager) mRootView.findViewById(R.id.subContainer);
                                        mViewPager.setOffscreenPageLimit(1);
                                        mViewPager.setAdapter(mNavigationPagerAdapter);
                                        mViewPager.setOnPageChangeListener(mNavigationPagerAdapter);
                                        setCommamds(Channels_Commands);
                                    }
                                });
                    }
                }
            });
        }
    };

    public void goTo(int i, String id) {
        if (mViewPager == null) {
            mRootView.findViewById(R.id.subContainer).setVisibility(View.VISIBLE);
            mLL_main.setVisibility(View.GONE);
            mRootView.setOnClickListener(null);
            NavigationPagerAdapter mNavigationPagerAdapter =
                    new NavigationPagerAdapter(getChildFragmentManager(), mItems);
            mViewPager = (ViewPager) mRootView.findViewById(R.id.subContainer);
            mViewPager.setOffscreenPageLimit(1);
            mViewPager.setAdapter(mNavigationPagerAdapter);
            mViewPager.setOnPageChangeListener(mNavigationPagerAdapter);
            setCommamds(Channels_Commands);
        }
        mViewPager.setCurrentItem(i);

        mReqManager.setChannel(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        enableSpeech = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

    }

    public class NavigationPagerAdapter extends FragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {

        public NavigationPagerAdapter(FragmentManager fm, List<SmartChannel> items) {
            super(fm);
            mItems = items;
        }


        @Override
        public Fragment getItem(int i) {
            if (mViewPager.getCurrentItem() == i && enableSpeech) {
                mSpeakEngine.speak(mItems.get(i).getName());
                enableSpeech = false;
            }

            return ChannelFragment.newInstance(mItems.get(i));
        }


        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mSpeakEngine.speak(mItems.get(position).getName());
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

}

