package org.example.msg;

public class StrMsg extends Msg {

    private static final long serialVersionUID = 2803288535439607637L;
    private String value;

    public StrMsg() {
        super(0);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
