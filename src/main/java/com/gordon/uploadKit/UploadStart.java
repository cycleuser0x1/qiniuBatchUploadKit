package com.gordon.uploadKit;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created by wwz on 2016/8/24.
 */
public class UploadStart {

    public static void main(String[] args) throws IOException {
        System.out.println(PropertiesUtil.getValue("msg.tips"));
        Scanner scanner = new Scanner(System.in);
        System.out.print(PropertiesUtil.getValue("msg.path"));
        String path = scanner.next();
        while (!dirCheck(path, false)) {
            System.out.print(PropertiesUtil.getValue("msg.path.illegal"));
            path = scanner.next();
        }
        System.out.println(path);

        System.out.print(PropertiesUtil.getValue("msg.bucket.name"));
        String product = scanner.next();

        System.out.print(PropertiesUtil.getValue("msg.key.path"));
        String secretKeyPath = scanner.next();
        while (!dirCheck(secretKeyPath, true)) {
            System.out.print(PropertiesUtil.getValue("msg.key.path.illegal"));
            secretKeyPath = scanner.next();
        }
        Properties properties = new Properties();
        FileInputStream in = new FileInputStream(secretKeyPath);
        try {
            properties.load(in);
        } catch (IOException e) {
            System.exit(1);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                System.exit(1);
            }
        }
        String accessKey = (String) properties.get("accessKey");
        String secretKey = (String) properties.get("secretKey");
        System.out.println(PropertiesUtil.getValue("msg.upload.confirm"));
        String cmd = scanner.next();
        cmd = cmd.toUpperCase();
        if (cmd.equals("N")) {
            return;
        } else if (cmd.equals("Y")) {
            new QiniuAuth(accessKey, secretKey);
            Upload upload = new Upload(path, product);
            path = path.replace("/", File.separator);
            upload.up(path);
            upload.fixedThreadPool.shutdown();
        }
    }

    /**
     * @param path
     * @param isFile
     * @return
     */
    public static boolean dirCheck(String path, boolean isFile) {
        path = path.replace("/", File.separator);
        if (!StringUtils.isNotBlank(path)) {
            return false;
        }
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        if (isFile) {
            if (file.isDirectory()) {
                return false;
            }
        } else {
            if (path.lastIndexOf(File.separator) != path.length() - 1) {
                return false;
            }
            if (file.isFile()) {
                return false;
            }
        }
        return true;
    }

}
