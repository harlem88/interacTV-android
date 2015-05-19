package org.dronix.hackathon_rome_interactv.dagger;

import org.dronix.hackathon_rome_interactv.request.ReqManager;
import javax.inject.Singleton;
import dagger.Component;

@Singleton
@Component(modules = {InteracTvModule.class})
public interface InteracTvComponent {
    ReqManager provideReqManager();

}
