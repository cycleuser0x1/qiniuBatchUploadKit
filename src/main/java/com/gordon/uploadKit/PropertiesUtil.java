package com.gordon.uploadKit;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by wwz on 2016/8/25.
 */
public class PropertiesUtil {

    private static Properties properties;

    static {
        properties = new Properties();
        FileInputStream in = null;
        try {
            properties.load(PropertiesUtil.class.getClassLoader().getResourceAsStream("msg.properties"));
        } catch (IOException e) {
            System.exit(1);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.exit(1);

                }
            }
        }
    }

    public static synchronized String getValue(String key) {
        return (String) properties.get(key);
    }

    public static String decodeUnicode(final String dataStr) {
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }
}
