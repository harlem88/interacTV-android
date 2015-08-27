package org.dronix.hackathon_rome_interactv.dagger;

import android.app.Activity;

import org.dronix.hackathon_rome_interactv.fragment.ChannelFragment;
import org.dronix.hackathon_rome_interactv.fragment.ZappingFragment;
import org.dronix.hackathon_rome_interactv.interfaces.ISpeakEngine;
import org.dronix.hackathon_rome_interactv.request.ReqManager;
import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component(modules = {InteracTvModule.class})
public interface InteracTvComponent {
    void inject(ZappingFragment zappingFragment);
    void inject(ChannelFragment zappingFragment);

    ReqManager provideReqManager();
}
