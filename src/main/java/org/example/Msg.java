package org.example;

import java.io.Serializable;

public class Msg implements Serializable {


    private static final long serialVersionUID = 8586989183160410293L;
    private String value;


    public String getValue() {



        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
