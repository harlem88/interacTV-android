package org.dronix.hackathon_rome_interactv.request;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.dronix.hackathon_rome_interactv.BuildConfig;
import org.dronix.hackathon_rome_interactv.model.SmartChannels;
import org.json.JSONException;
import org.json.JSONObject;
import rx.Observable;
import rx.Subscriber;

public class ReqManager {

    private RequestQueue mRequestQueue;
    private static Context mCtx;
    private static final String CHANNEL_PATH = "channels";
    private static final String ZAPPING_PATH = "zapping";
    private static final String VOLUME_PATH = "volume";

    public ReqManager(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }


    public Observable<SmartChannels> getChannels() {
        return Observable.create(new Observable.OnSubscribe<SmartChannels>() {
            @Override
            public void call(Subscriber<? super SmartChannels> subscriber) {
                addToRequestQueue(new GsonRequest<>(Request.Method.GET, BuildConfig.SERVER_URL + CHANNEL_PATH,
                        SmartChannels.class,
                        null,
                        subscriber::onNext,
                        Throwable::printStackTrace));
            }
        });
    }

    public Observable<SmartChannels> setChannel(String id) {
        return Observable.create(subscriber -> {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", id);
            } catch (JSONException e1) {}

            addToRequestQueue(
                    new JsonObjectRequest(Request.Method.POST, BuildConfig.SERVER_URL + ZAPPING_PATH, jsonObject,
                            response -> {}, error -> {}));
        });
    }

    public Observable<SmartChannels> setVolume(String value) {
        return Observable.create(subscriber -> {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("volume", value);
            } catch (JSONException e1) {}

            addToRequestQueue(
                    new JsonObjectRequest(Request.Method.POST, BuildConfig.SERVER_URL + VOLUME_PATH, jsonObject,
                            response -> {}, error -> {}));
        });
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
