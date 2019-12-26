package com.example.higerpoint.fastdfs;

import lombok.Cleanup;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * @author chezhenqi
 * @date 2019/12/26 星期四
 * @time 16:15
 * @description springboot-rabbitmq
 */
@Component("fastDFSClient")
public class FastDFSClient {
    private static final Logger logger = LoggerFactory.getLogger(FastDFSClient.class);

    private static Properties properties = null;

    private static FastDFSConfig fastDFSConfig;

    /**
     * @author chezhenqi
     * @date 17:56 2019/12/26
     * @description: 将静态变量 fastDFSConfig 注入进来
     * @params: No such property: code for class: Script1
     * @return:
     */
    @Autowired
    public FastDFSClient(FastDFSConfig fastDFSConfig) {
        this.fastDFSConfig = fastDFSConfig;
    }

    private static void init() {
        try {
            properties = new Properties();
            properties.setProperty("fastdfs.connect_timeout_in_seconds", fastDFSConfig.getConnect_timeout_in_seconds());
            properties.setProperty("fastdfs.network_timeout_in_seconds", fastDFSConfig.getNetwork_timeout_in_seconds());
            properties.setProperty("fastdfs.http_tracker_http_port", fastDFSConfig.getHttp_tracker_http_port());
            properties.setProperty("fastdfs.tracker_servers", fastDFSConfig.getTracker_servers());
            properties.setProperty("fastdfs.tracker_server_host", fastDFSConfig.getTracker_server_host());
            ClientGlobal.initByProperties(properties);
            logger.info("ClientGlobal.configInfo(): " + ClientGlobal.configInfo());
        } catch (IOException e) {
            logger.info("FastDFS Client Init Fail!", e);
        } catch (MyException e) {
            logger.info("FastDFS Client Init Fail!", e);
        }
    }

    /**
     * @param multipartFile
     * @return
     * @throws IOException
     */
    public static String saveFile(MultipartFile multipartFile) throws IOException {
        init();
        String[] typeFile = {"pdf", "doc", "ppt", "docx", "pptx", "jpeg", "png", "jpg"};
        String fileName = multipartFile.getOriginalFilename();
        String ext = null;// 文件扩展名
        if (multipartFile != null) {
            fileName = multipartFile.getOriginalFilename();
            ext = fileName.indexOf(".") != -1 ? fileName.substring(fileName.lastIndexOf(".") + 1) : null;
        }
        if (ext != null) {
            boolean booIsType = false;
            for (int i = 0; i < typeFile.length; i++) {
                if (typeFile[i].equals(ext.toLowerCase(Locale.ENGLISH))) {
                    booIsType = true;
                }
            }
            // 类型正确时上传
            if (booIsType) {
                @Cleanup
                InputStream inputStream = multipartFile.getInputStream();
                int len1 = inputStream.available();
                final int tooBig = 0x6400000; // 100MB
                if (len1 > tooBig) {
                    throw new IllegalStateException(
                            "http request inputStream is too large.");
                }
                byte[] file_buff = new byte[len1];
                try {
                    int read = inputStream.read(file_buff);
                    if (read == -1) {
                        logger.info("输入流传输不能为空");
                    }
                } catch (IOException e) {
                    logger.error("读取图片流异常");
                } finally {
                    inputStream.close();
                }
                FastDFSFile file = new FastDFSFile(fileName, file_buff, ext);
                String[] fileAbsolutePath = FastDFSClient.upload(file);
                // upload to fastdfs
                if (fileAbsolutePath.length == 0) {
                    logger.error("upload file failed,please upload again!");
                }
                String path = fileAbsolutePath[0] + "/" + fileAbsolutePath[1];
                return path;
            } else {
                logger.info("文件类型校验失败：");
                return null;
            }
        } else {
            logger.info("文件扩展名不能为空");
            return null;
        }

    }

    public static String[] upload(FastDFSFile file) {
        init();
        NameValuePair[] meta_list = new NameValuePair[1];
        meta_list[0] = new NameValuePair("author", file.getAuthor());

        long startTime = System.currentTimeMillis();
        String[] uploadResults = null;
        StorageClient storageClient = null;
        try {
            storageClient = getTrackerClient();
            if (storageClient != null) {
                uploadResults = storageClient.upload_file(file.getContent(), file.getExt(), meta_list);
            }
        } catch (IOException e) {
            logger.error("IO Exception when uploadind the file:");
        } catch (MyException e) {
            logger.error("Non IO Exception when uploadind the file:");
        }
        logger.info("upload_file time used:" + (System.currentTimeMillis() - startTime) + " ms");

        if (uploadResults == null && storageClient != null) {
            logger.error("upload file fail, error code:" + storageClient.getErrorCode());
        }

        return uploadResults;
    }

    private static StorageClient getTrackerClient() throws IOException {
        init();
        TrackerServer trackerServer = getTrackerServer();
        StorageClient storageClient = new StorageClient(trackerServer, null);
        return storageClient;
    }

    private static TrackerServer getTrackerServer() throws IOException {
        init();
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        return trackerServer;
    }

    /**
     * @author chezhenqi
     * @date 16:29 2019/12/26
     * @description: 上传文件类型判断
     * @params: No such property: code for class: Script1
     * @return:
     */
    public static boolean testFile(MultipartFile multipartFile) throws IOException {
        boolean flag = false;
        Map<String, String> FILE_TYPE_MAP = new HashMap();
        FILE_TYPE_MAP.put("jpg", "FFD8FF"); //JPEG
        FILE_TYPE_MAP.put("png", "89504E47"); //PNG
        FILE_TYPE_MAP.put("gif", "47494638"); //GIF
        FILE_TYPE_MAP.put("tif", "49492A00"); //tif
        FILE_TYPE_MAP.put("bmp", "424D"); //Windows Bitmap
        FILE_TYPE_MAP.put("pdf", "25504446");//pdf
        FILE_TYPE_MAP.put("docx", "504B0304");//docx
        FILE_TYPE_MAP.put("doc", "D0CF11E0");//doc
        FILE_TYPE_MAP.put("ppt", "D0CF11E0");//doc
        FILE_TYPE_MAP.put("pptx", "504B0304");//doc
        FILE_TYPE_MAP.put("xlsx", "504B0304");//xlsx
        FILE_TYPE_MAP.put("xls", "D0CF11E0");//xls
        FILE_TYPE_MAP.put("txt", "BBB9BFEE");//txt
        File convert = convert(multipartFile);
        InputStream in = multipartFile.getInputStream();
        String prefix = convert.getName().substring(convert.getName().lastIndexOf(".") + 1);
        BufferedImage bufferedImage = ImageIO.read(in);
        @Cleanup InputStream inputStream = new FileInputStream(convert);
        byte[] bytes = new byte[8];
        inputStream.read(bytes);
        String result = bytesToHexString(bytes).toUpperCase();
        //规定图片类型
        String imageStr = "jpg,jpeg,png";
        if (bufferedImage != null) {//图片校验
            if (bufferedImage.getHeight(null) > 0 && bufferedImage.getWidth(null) > 0) {
                if (result.contains(FILE_TYPE_MAP.get(prefix))) {
                    flag = true;
                }
            }
        } else if (!imageStr.contains(prefix)) {//非图片校验
            if (result.contains(FILE_TYPE_MAP.get(prefix))) {
                flag = true;
            }
        }
        /**
         * 关闭流，删除临时文件
         */
        inputStream.close();
        convert.delete();
        return flag;
    }

    /**
     * @author chezhenqi
     * @date 16:29 2019/12/26
     * @description: 文件类型转换
     * @params: No such property: code for class: Script1
     * @return:
     */
    public static File convert(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
        } catch (IOException e) {
            logger.info("file类型转换失败：" + e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ext) {
                    logger.info("输出流释放异常：" + ext);
                }
            }
        }
        return convFile;
    }

    /**
     * @author chezhenqi
     * @date 16:30 2019/12/26
     * @description: 字节流转换为字符串
     * @params: No such property: code for class: Script1
     * @return:
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

}
