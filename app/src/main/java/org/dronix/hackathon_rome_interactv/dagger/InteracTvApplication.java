package org.dronix.hackathon_rome_interactv.dagger;


import android.app.Application;

import org.dronix.hackathon_rome_interactv.DaggerInteracTvComponent;

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