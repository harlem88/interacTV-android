package org.dronix.hackathon_rome_interactv.dagger;

import org.dronix.hackathon_rome_interactv.request.ReqManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class InteracTvModule {
    private final InteracTvApplication application;

    public InteracTvModule(InteracTvApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    ReqManager provideReqManager() {
        return new ReqManager(application.getApplicationContext());
    }

}
