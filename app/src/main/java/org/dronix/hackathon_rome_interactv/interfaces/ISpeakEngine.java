package org.dronix.hackathon_rome_interactv.interfaces;

import rx.Subscriber;

public interface ISpeakEngine {
    void speak(String message);
    void speak(String message, Subscriber<Void> utteranceProgressListener);
}
