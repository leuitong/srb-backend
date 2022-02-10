package com.example.srb.oss.service;

import java.io.InputStream;

public interface OssService {
    /**
     * 上传文件至阿里云
     */
    String upload(InputStream inputStream, String module, String fileName);

    /**
     * 从阿里云删除文件
     */
    void removeFile(String url);
}
