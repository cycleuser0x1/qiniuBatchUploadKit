package com.gordon.uploadKit;

import com.qiniu.util.Auth;

/**
 * Created by wwz on 2016/8/24.
 */
public class QiniuAuth {

    public QiniuAuth(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    private static String accessKey;
    private static String secretKey;

    public static String getUploadToken(String bucketName, String fileName) {
        Auth auth = Auth.create(accessKey, secretKey);
        return auth.uploadToken(bucketName, fileName);
    }
}
