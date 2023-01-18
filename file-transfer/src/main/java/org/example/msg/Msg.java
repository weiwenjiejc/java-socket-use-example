package org.example.msg;

import java.io.Serializable;

public class Msg implements Serializable {


    private static final long serialVersionUID = 8586989183160410293L;

    /**
     * 消息类型是文本
     */
    public static final int TYPE_STR = 0;
    /**
     * 消息类型是文件
     */
    public static final int TYPE_FILE = 1;

    /**
     * 系统消息
     */
    public static final int TYPE_SYSTEM = 2;

    /**
     * 消息类型
     * 0，文本
     * 1，文件
     * 2, 系统消息
     */
    private int type;

    public Msg(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
