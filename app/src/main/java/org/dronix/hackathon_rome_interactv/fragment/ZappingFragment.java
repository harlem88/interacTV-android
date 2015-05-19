package org.dronix.hackathon_rome_interactv.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.dronix.hackathon_rome_interactv.R;
import org.dronix.hackathon_rome_interactv.request.ReqManager;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

public class ZappingFragment extends Fragment {
    private View mRootView;
    private View mLL_main;
    private ViewPager mViewPager;

    private AtomicBoolean mShakeDetection = new AtomicBoolean(false);
    private String[] mCommands;
    private boolean enableSpeech = true;
    public final String[] ZAPPING_Commands = {"vai al canale"};
    public final String[] Channels_Commands = {"info", "descrizione", " o vai al canale"};

    @Inject
    ReqManager mReqManager;

    public void setCommamds(String[] commands) {
        this.mCommands = commands;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(
                R.layout.fragment_main, container, false);
        mLL_main = mRootView.findViewById(R.id.ll_main);


        return mRootView;

    }
}

