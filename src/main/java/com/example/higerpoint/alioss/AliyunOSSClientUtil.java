package com.example.higerpoint.alioss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;

import net.coobird.thumbnailator.Thumbnails;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chezhenqi
 * @date 2019/12/30 星期一
 * @time 10:55
 * @description springboot-rabbitmq
 */
public class AliyunOSSClientUtil {
    public static Logger logger = LoggerFactory.getLogger(AliyunOSSClientUtil.class);
    //阿里云API的内或外网域名
    public static String ENDPOINT;
    //阿里云API的密钥Access Key ID
    public static String ACCESS_KEY_ID;
    //阿里云API的密钥Access Key Secret
    public static String ACCESS_KEY_SECRET;
    //阿里云API的bucket名称
    public static String BACKET_NAME;
    //阿里云API的文件夹名称
    public static String FOLDER;

    //初始化属性
    static {
        ENDPOINT = "http://oss-cn-beijing.aliyuncs.com";
        ACCESS_KEY_ID = "*******";
        ACCESS_KEY_SECRET = "*******";
        BACKET_NAME = "test";
        FOLDER = "test/";
    }

    /**
     * 获取阿里云OSS客户端对象
     *
     * @return ossClient
     */
    public static OSSClient getOSSClient() {
        return new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
    }

    /**
     * 创建存储空间
     *
     * @param ossClient  OSS连接
     * @param bucketName 存储空间
     * @return
     */
    public static String createBucketName(OSSClient ossClient, String bucketName) {
        //存储空间
        final String bucketNames = bucketName;
        if (!ossClient.doesBucketExist(bucketName)) {
            //创建存储空间
            Bucket bucket = ossClient.createBucket(bucketName);
            logger.info("创建存储空间成功");
            return bucket.getName();
        }
        return bucketNames;
    }

    /**
     * 删除存储空间buckName
     *
     * @param ossClient  oss对象
     * @param bucketName 存储空间
     */
    public static void deleteBucket(OSSClient ossClient, String bucketName) {
        ossClient.deleteBucket(bucketName);
        logger.info("删除" + bucketName + "Bucket成功");
    }

    /**
     * 创建模拟文件夹
     *
     * @param ossClient  oss连接
     * @param bucketName 存储空间
     * @param folder     模拟文件夹名如"folder/"
     * @return 文件夹名
     */
    public static String createFolder(OSSClient ossClient, String bucketName, String folder) {
        //文件夹名
        final String keySuffixWithSlash = folder;
        //判断文件夹是否存在，不存在则创建
        if (!ossClient.doesObjectExist(bucketName, keySuffixWithSlash)) {
            //创建文件夹
            ossClient.putObject(bucketName, keySuffixWithSlash, new ByteArrayInputStream(new byte[0]));
            logger.info("创建文件夹成功");
            //得到文件夹名
            OSSObject object = ossClient.getObject(bucketName, keySuffixWithSlash);
            String fileDir = object.getKey();
            return fileDir;
        }
        return keySuffixWithSlash;
    }

    /**
     * 根据key删除OSS服务器上的文件
     *
     * @param ossClient  oss连接
     * @param bucketName 存储空间
     * @param folder     模拟文件夹名 如"folder/"
     * @param key        Bucket下的文件的路径名+文件名 如："folder/1.jpg"
     */
    public static void deleteFile(OSSClient ossClient, String bucketName, String folder, String key) {
        ossClient.deleteObject(bucketName, folder + key);
        logger.info("删除" + bucketName + "下的文件" + folder + key + "成功");
    }

    /**
     * 上传图片至OSS
     *
     * @param ossClient      oss连接
     * @param beforeFileName 文件名
     * @param bufImg         图片文件
     * @param bucketName     存储空间
     * @param folder         模拟文件夹名 如"test-kaka/headImage//"
     * @param afterFileName  上传后的文件名
     * @return String 返回的唯一MD5数字签名（在获取图片链接时会追加在链接后面）
     */
    public static String uploadObject2OSS(OSSClient ossClient, BufferedImage bufImg, String bucketName, String folder, String beforeFileName, String afterFileName) {
        String resultStr = null;
        try {
            // 压缩代码
            bufImg = Thumbnails.of(bufImg).width(1024).keepAspectRatio(true).outputQuality(0.9).asBufferedImage();
            // 存储图片文件byte数组
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            // 图片写入到 ImageOutputStream
            ImageIO.write(bufImg, "jpg", bos);
            //以输入流的形式上传文件
            InputStream is = new ByteArrayInputStream(bos.toByteArray());
            //文件名
            String fileName = beforeFileName;
            //创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            //上传的文件的长度
            metadata.setContentLength(is.available());
            //指定该Object被下载时的网页的缓存行为
            metadata.setCacheControl("no-cache");
            //指定该Object下设置Header
            metadata.setHeader("Pragma", "no-cache");
            //指定该Object被下载时的内容编码格式
            metadata.setContentEncoding("utf-8");
            //文件的MIME，定义文件的类型及网页编码，决定浏览器将以什么形式、什么编码读取文件。如果用户没有指定则根据Key或文件名的扩展名生成，
            //如果没有扩展名则填默认值application/octet-stream
            metadata.setContentType(getContentType(fileName));
            //指定该Object被下载时的名称（指示MINME用户代理如何显示附加的文件，打开或下载，及文件名称）
//            metadata.setContentDisposition("filename/filesize=" + fileName + "/" + fileSize + "Byte.");
            //上传文件   (上传文件流的形式)
            PutObjectResult putResult = ossClient.putObject(bucketName, folder + afterFileName, is, metadata);
            //解析结果
            resultStr = putResult.getETag();
            logger.error("上传阿里云OSS成功 {}", resultStr);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
        }
        return resultStr;
    }

    /**
     * 通过文件名判断并获取OSS服务文件上传时文件的contentType
     *
     * @param fileName 文件名
     * @return 文件的contentType
     */
    public static String getContentType(String fileName) {
        //文件的后缀名
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        //后缀名转小写
        String fileType = fileExtension.toLowerCase();
        //返回类型
        String contentType = "";
        switch (fileType) {
            case ".bmp":
                contentType = "image/bmp";
                break;
            case ".gif":
                contentType = "image/gif";
                break;
            case ".png":
            case ".jpeg":
            case ".jpg":
                contentType = "image/jpeg";
                break;
            case ".html":
                contentType = "text/html";
                break;
            case ".txt":
                contentType = "text/plain";
                break;
            case ".vsd":
                contentType = "application/vnd.visio";
                break;
            case ".ppt":
            case ".pptx":
                contentType = "application/vnd.ms-powerpoint";
                break;
            case ".doc":
            case ".docx":
                contentType = "application/msword";
                break;
            case ".xml":
                contentType = "text/xml";
                break;
            case ".mp4":
                contentType = "video/mp4";
                break;
            default:
                contentType = "application/octet-stream";
                break;
        }
        return contentType;
    }

    /**
     * 获得url链接
     *
     * @param key 上传图片的路径+名称（如：test-kaka/headImage/1546404670068899.jpg）
     * @return 图片链接：http://xxxxx.oss-cn-beijing.aliyuncs.com/test/headImage/1546404670068899.jpg?Expires=1861774699&OSSAccessKeyId=****=p%2BuzEEp%2F3JzcHzm%2FtAYA9U5JM4I%3D
     * （Expires=1861774699&OSSAccessKeyId=LTAISWCu15mkrjRw&Signature=p%2BuzEEp%2F3JzcHzm%2FtAYA9U5JM4I%3D 分别为：有前期、keyID、签名）
     */
    public static String getUrl(String key) {
        // 设置URL过期时间为10年  3600l* 1000*24*365*10
        Date expiration = new Date(new Date().getTime() + 3600l * 1000 * 24 * 365 * 10);
        OSSClient ossClient = AliyunOSSClientUtil.getOSSClient();
        // 生成URL
        URL url = ossClient.generatePresignedUrl(AliyunOSSClientUtil.BACKET_NAME, key, expiration);

        return url.toString().substring(0, url.toString().lastIndexOf("?"));
    }

    /**
     * 获得指定文件的byte数组
     *
     * @param filePath 文件绝对路径
     * @return
     */
    public static byte[] imageToByteArray(String filePath) {
        byte imgs[] = null;
        try {
            OSSClient ossClient = AliyunOSSClientUtil.getOSSClient();
            OSSObject in = ossClient.getObject(BACKET_NAME, filePath.replace("VISIT_URL", ""));
            InputStream input = in.getObjectContent();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte tmp[] = new byte[1024];
            int i;
            while ((i = input.read(tmp, 0, 1024)) > 0) {
                baos.write(tmp, 0, i);
            }
            imgs = baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgs;
    }

    /*public static void main(String[] args) throws IOException {
        String files = "C:\\Users\\admin\\Desktop\\20131011135225_vUPw5.jpeg";
        File file = new File(files);
        InputStream input = new FileInputStream(file);
        // 把图片读入到内存中
        BufferedImage bufImg = ImageIO.read(input);
        String beforeFileName = "20131011135225_vUPw5.jpeg";
        String afterFileName = "1546404670068899.jpg";
        String floder = AliyunOSSClientUtil.FOLDER + "article/";
        OSSClient ossClient = AliyunOSSClientUtil.getOSSClient();

        //上传图片
        System.out.println(uploadObject2OSS(ossClient, bufImg, AliyunOSSClientUtil.BACKET_NAME, floder, beforeFileName, afterFileName));
        //获取图片路径
        System.out.println(getUrl(floder + afterFileName));
//    	String filePath = "http://test-kaka.oss-cn-beijing.aliyuncs.com/test-kaka/article/1546404670068899.jpg";
//    	System.out.println(filePath.substring(filePath.lastIndexOf("/")+1));
    }*/
}
