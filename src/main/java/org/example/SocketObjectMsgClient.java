package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 接收客户单发送的消息对象
 */
public class SocketObjectMsgClient {
    private static final Logger logger = LoggerFactory.getLogger(SocketObjectMsgClient.class);

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        long start = System.currentTimeMillis();
        System.out.println(start);

        String host = "172.16.4.77";
        int port = 9001;
        Socket socket = null;
        int soTimeout = 0;
        boolean connect = true;
        try {

            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 1000 * 1);
//            socket.setSoTimeout(1000 * 2);  // 设置读取时间
            soTimeout = socket.getSoTimeout();

        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("连接{}:{}失败，请求时间{}", host, port, soTimeout);
            connect = false;
        }
        long end = System.currentTimeMillis();
        System.out.println(end);
        logger.info("耗时:{}", end - start);
        if (!connect) {
            logger.info("结束连接");
            return;
        }

        InputStream inputStream = socket.getInputStream();

        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(inputStream);
        } catch (Exception e) {
            connect = false;
            logger.error(e.getMessage());
            logger.error("服务器关闭，结束运行");
        }
        if (!connect) {
            logger.info("结束连接");
            return;
        }
        while (true) {
            Msg readObject = (Msg) objectInputStream.readObject();
            logger.info("消息内容\n{}",readObject.getValue());
        }

    }
}