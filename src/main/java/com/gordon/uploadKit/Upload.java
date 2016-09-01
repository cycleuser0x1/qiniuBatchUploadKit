package com.gordon.uploadKit;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.persistent.FileRecorder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wwz on 2016/8/24.
 */
public class Upload {

    private String rootDir;
    private String fileRecorderDir = "recorder" + File.separator;
    private String product;
    private String prefix;


    public Upload(String rootDir, String product, String prefix) {
        this.rootDir = rootDir;
        this.rootDir = this.rootDir.replace("/", File.separator);
        this.product = product;
        this.prefix = prefix;
    }

    public static final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);

    public void up(String directory) {
        File file = new File(directory);
        String[] subDirs = file.list();
        if (subDirs == null || subDirs.length == 0) {
            return;
        }
        for (String dir : subDirs) {
            final String subDirectory = directory + dir;
            File subFile = new File(subDirectory);
            if (subFile.isFile()) {
                final String path;
                if (prefix.equals("null")) {
                    path = subDirectory.substring(rootDir.length());
                } else {
                    path = prefix + subDirectory.substring(rootDir.length());
                }
                fixedThreadPool.execute(new Runnable() {
                    public void run() {
                        qiniuUp(subDirectory, path, product);
                    }
                });
            } else {
                up(subDirectory + File.separator);
            }
        }
    }

    private void qiniuUp(String subDirectory, String path, String product) {
        path = path.replace(File.separator, "/");
        //获取token
        String uploadToken = QiniuAuth.getUploadToken(product, path);
        //创建FileRecorder
        FileRecorder fileRecorder = null;
        try {
            fileRecorder = new FileRecorder(fileRecorderDir);
        } catch (IOException e) {
            System.out.println("fileRecorder path is invalid");
            System.exit(1);
        }
        UploadManager uploadManager = new UploadManager(fileRecorder);
        //开始上传
        try {
            Response response = uploadManager.put(subDirectory, path, uploadToken);
            if (response.statusCode == 200) {
                System.out.println("FILE:" + subDirectory + " upload success");
            }
        } catch (QiniuException e) {
            try {
                System.out.println(e.response.bodyString());
            } catch (QiniuException e1) {
                e1.printStackTrace();
            }
            System.out.println("FILE:" + subDirectory + " upload fail");
        }
    }

}
