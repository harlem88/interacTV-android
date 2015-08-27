package org.dronix.hackathon_rome_interactv.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.dronix.hackathon_rome_interactv.R;
import org.dronix.hackathon_rome_interactv.dagger.InteracTvApplication;
import org.dronix.hackathon_rome_interactv.model.SmartChannel;
import org.dronix.hackathon_rome_interactv.request.ReqManager;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ChannelFragment extends Fragment implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener, View.OnTouchListener {

    private GestureDetectorCompat mDetector;
    private SmartChannel mSmartChannel;

    public static ChannelFragment newInstance(SmartChannel channel) {
        Bundle bundle = new Bundle();
        ChannelFragment channelFragment = new ChannelFragment();
        channelFragment.setChannel(channel);
        return channelFragment;
    }

    @Inject
    ReqManager mReqManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((InteracTvApplication) getActivity().getApplication()).component().inject(this);
    }

    public void setChannel(SmartChannel channel) {
        mSmartChannel = channel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_channel, container, false);
        mDetector = new GestureDetectorCompat(getActivity(), this);
        mDetector.setOnDoubleTapListener(this);
        rootView.setOnTouchListener(this);
        ((TextView) rootView.findViewById(R.id.title_id)).setText(mSmartChannel.getName());

        return rootView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.onTouchEvent(event);
        return mDetector.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {

        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }


    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        mReqManager.setChannel(mSmartChannel.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        return true;
    }

    private int mVolumeValue;

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        final String value;
        if (distanceY > 5) {
            mVolumeValue += 10;
            if (mVolumeValue > 100)
                mVolumeValue = 100;
            value = mVolumeValue + "";


        } else if (distanceY < -5) {
            mVolumeValue -= 10;
            if (mVolumeValue < 0)
                mVolumeValue = 0;
            value = mVolumeValue + "";
        } else
            value = null;

        if (value != null){
            mReqManager.setVolume(value)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe();
        }


        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }
}
