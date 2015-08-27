package org.dronix.hackathon_rome_interactv.model;

import java.io.Serializable;
import java.util.List;

public class SmartChannels implements Serializable {

    List<SmartChannel> response;

    public List<SmartChannel> getResponse() {
        return response;
    }

    public void setResponse(List<SmartChannel> response) {
        this.response = response;
    }
}


