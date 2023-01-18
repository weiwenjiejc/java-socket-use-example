package org.example.msg;

import java.io.Serializable;

public class SystemMsg extends Msg {


    /**
     * 文件保存完成消息
     * 接收完文件，需要返回一个消息，另一方在没有收到这个消息之前，不能再次发送消息
     * 或者，用户新开一个线程，用来保存文件，但是还是需要返回一个消息，表示接收完成
     */
    public static final int SYSTEM_MSG_STATUS_FILE_SAVE = 0;

    /**
     *
     */
    private int status;

    private static final long serialVersionUID = -8786820391637645113L;

    public SystemMsg() {
        super(2);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
