package org.example.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public static void saveFile(String basePath, String fileName, byte[] fileBytes) {
        File file = new File(basePath + File.separator + fileName);
        logger.info("开始保存文件:{}" + file.getAbsolutePath());
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(fileBytes);
        } catch (Exception e) {
            logger.info("文件保存出错:{}" + e.getMessage());
            return;
        }
        logger.info("文件保存完成");
    }

    public static byte[] readFile(File file) {
        logger.info("开始读取文件:{}" + file.getAbsolutePath());

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            byte[] buff = new byte[1024];
            int read = fileInputStream.read(buff);
            while (read != -1) {
                stream.write(buff, 0, read);
                read = fileInputStream.read(buff);
            }
            fileInputStream.close();
            logger.info("文件读取完成");
            return stream.toByteArray();
        } catch (Exception e) {
            logger.info("文件读取出错:{}" + e.getMessage());
            return null;
        }
    }
}
