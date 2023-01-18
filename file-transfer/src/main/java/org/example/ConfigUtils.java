package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtils {


    private static Properties properties = new Properties();

    static {
        InputStream inputStream = ConfigUtils.class.getResourceAsStream("/config.properties");

        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


    public static String getServerHost() {
        String localhost = properties.getProperty("client.host", "localhost");
        return localhost;
    }

    public static int getSErverPort() {
        String property = properties.getProperty("client.port", "9002");
        return Integer.parseInt(property);
    }

    public static int getMyPort() {
        String property = properties.getProperty("server.port", "9002");
        return Integer.parseInt(property);
    }

    public static String getReceiveFilePath() {
//        String localhost = properties.getProperty("file.receive.path", "localhost");
        String property = System.getProperty("user.dir");
        return property;
    }
}
