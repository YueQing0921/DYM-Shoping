package com.xxx.proj.controller;

import com.xxx.proj.dto.Result;
import com.xxx.proj.util.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Properties;

@RestController
public class UploadController {
    @Value("${fdfs.tracker_server}")
    private String trackerServer;
    @Value("${fdfs.profix}")
    private String profix;

    @RequestMapping("/upload_file")
    public Result upload_file(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        Properties properties = new Properties();
        properties.setProperty("fastdfs.tracker_servers", trackerServer);
        try {
            FastDFSClient client = new FastDFSClient(properties);
            String groupM00 = client.uploadFile(file.getBytes(), extName);
            return new Result(true, profix + groupM00);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "上传失败！！！");
        }

    }
}
