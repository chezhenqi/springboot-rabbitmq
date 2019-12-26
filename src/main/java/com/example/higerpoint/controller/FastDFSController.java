package com.example.higerpoint.controller;

import com.example.higerpoint.fastdfs.FastDFSClient;
import com.example.higerpoint.fastdfs.FastDFSConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chezhenqi
 * @date 16:30 2019/12/26
 * @description: 文件上传
 * @params: No such property: code for class: Script1
 * @return:
 */
@Controller
@RequestMapping("/fastDFS")
public class FastDFSController {
    private final static Logger logger = (Logger) LoggerFactory.getLogger(FastDFSController.class);
    @Autowired
    private FastDFSConfig fastDFSConfig;
    /**
     * @author chezhenqi
     * @date 16:26 2019/12/26
     * @description: 上传文件
     * @params: No such property: code for class: Script1
     * @return:
     */
    @PostMapping(value = "/saveFile")
    @ResponseBody
    public Map<String, Object> saveFile(@RequestParam("file") MultipartFile multipartFile) {
        String s = null;
        String code = null;
        String message = null;

        Map map = new HashMap();
        String fileName = multipartFile.getOriginalFilename();
        try {
            boolean flag = FastDFSClient.testFile(multipartFile);
            if (flag) {
                s = FastDFSClient.saveFile(multipartFile);
                message = "上传成功！";
                code = "200";
            } else {
                s = "";
                message = "上传异常，请检查图片后缀名是否做过修改！";
                code = "500";
            }
        } catch (IOException e) {
            logger.error("图片上传失败，请上传正确的图片格式或者图片大小");
            s = "";
            code = "500";
            message = "图片上传失败，请稍后重试或联系管理员！";
        }

        map.put("code", code);
        map.put("msg", message);
        map.put("fileUrl", s);
        map.put("fileName", fileName);
        return map;
    }

    /**
     * @author chezhenqi
     * @date 16:27 2019/12/26
     * @description: 获取文件前缀
     * @params: No such property: code for class: Script1
     * @return:
     */
    @GetMapping(value = "/getTrackerUrl")
    @ResponseBody
    public String getTrackerUrl() {
        return fastDFSConfig.getTracker_server_host();
    }

}
