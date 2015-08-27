package org.dronix.hackathon_rome_interactv.dagger;
import android.app.Application;

public class InteracTvApplication extends Application {
    private InteracTvComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerInteracTvComponent.builder()
                .interacTvModule(new InteracTvModule(this))
                .build();
    }

    public InteracTvComponent component() {
        return component;
    }


}