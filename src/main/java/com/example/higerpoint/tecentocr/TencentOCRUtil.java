package com.example.higerpoint.tecentocr;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.higerpoint.util.HttpUtil;
import com.example.higerpoint.util.JsonUtil;
import com.example.higerpoint.util.MD5;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author chezhenqi
 * @date 2019/4/12 16:06
 * @description 腾讯证件照识别工具类
 */
@Component
@SuppressWarnings("all")
public class TencentOCRUtil {
    private static final Logger log = LoggerFactory.getLogger(TencentOCRUtil.class);

    // 腾讯开发平台账号appId
    private static int appId = 2114987937;
    // 腾讯开发平台呢账号appkey
    private static String appkey = "7hroeYa76DvB8FY7";

    public static long getTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    public static String getNonceStr() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    /**
     * 识别营业执照
     *
     * @param newImageName
     * @return
     */
    public String getOCRLicence(String newImageName, MultipartFile file) {
        try {
            log.info("营业执照识别开始--》" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            Map<String, String> param = new TreeMap();
            param.put("app_id", appId + "");
            param.put("time_stamp", String.valueOf(getTimeStamp()));
            param.put("nonce_str", getNonceStr());
            String image = getImageBase64Str(newImageName, file);
            param.put("image", image);
            String sign = getReqSign(param, appkey);
            param.put("sign", sign);
            byte[] resBytes = HttpUtil.doPost("https://api.ai.qq.com/fcgi-bin/ocr/ocr_bizlicenseocr", param, "UTF-8");
            if (resBytes.length >= 0x640000) {
                return "";
            }
            String result = new String(resBytes);
            int ret = Integer.valueOf(JSONObject.parseObject(result).get("ret").toString());
            if (ret == 0) {
                log.info("营业执照识别结束--》" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                return result;
            } else {
                log.error("识别营业执照失败：{}", JSONObject.parseObject(result).get("msg").toString());
                return "";
            }
        } catch (Exception e) {
            log.error("出错了");
        }
        return "";
    }

    /**
     * 识别身份证
     *
     * @param newImageName
     * @param cardType     身份证正反面：0正面，1反面
     * @return
     */
    public String getOCRIDCard(String newImageName, Integer cardType, MultipartFile file) {
        try {
            log.info("身份证识别开始--》" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            Map<String, String> param = new TreeMap();
            param.put("app_id", URLEncoder.encode(String.valueOf(appId), "UTF-8"));
            String image = getImageBase64Str(newImageName, file);
//            param.put("image", URLEncoder.encode(image.replace("\r\n", ""), "UTF-8"));
            param.put("image", new String(image.getBytes("ISO-8859-1"), "UTF-8"));
            param.put("card_type", URLEncoder.encode(cardType + "", "UTF-8"));
            param.put("time_stamp", URLEncoder.encode(String.valueOf(getTimeStamp()), "UTF-8"));
            param.put("nonce_str", URLEncoder.encode(getNonceStr(), "UTF-8"));
            appkey = URLEncoder.encode(appkey, "UTF-8");
            String sign = getReqSign(param, appkey);
            param.put("sign", sign);
            byte[] resBytes = HttpUtil.doPost("https://api.ai.qq.com/fcgi-bin/ocr/ocr_idcardocr", param);
            String result = new String(resBytes);
            int ret = Integer.valueOf(JSONObject.parseObject(result).get("ret").toString());
            if (ret == 0) {
                log.info("身份证识别结束--》" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                return result;
            } else {
                log.error("**************识别身份证{}失败,错误信息：{}", cardType == 0 ? "正面" : "反面", JSONObject.parseObject(result).get("msg").toString());
                return "";
            }
        } catch (Exception e) {
            log.error("出错了");
        }
        return "";
    }

    /**
     * 识别行驶证/驾驶证
     *
     * @param newImageName
     * @param cardType     0:行驶证识别;1:驾驶证识别
     * @return
     */
    public String getOCRDriverLicense(String newImageName, Integer cardType, MultipartFile file) {
        try {
            Map<String, String> param = new TreeMap();
            param.put("app_id", URLEncoder.encode(String.valueOf(appId), "UTF-8"));
            String image = getImageBase64Str(newImageName, file);
            param.put("image", new String(image.getBytes("ISO-8859-1"), "UTF-8"));
            param.put("type", URLEncoder.encode(cardType + "", "UTF-8"));
            param.put("time_stamp", URLEncoder.encode(String.valueOf(getTimeStamp()), "UTF-8"));
            param.put("nonce_str", URLEncoder.encode(getNonceStr(), "UTF-8"));
            appkey = URLEncoder.encode(appkey, "UTF-8");
            String sign = getReqSign(param, appkey);
            param.put("sign", sign);
            byte[] resBytes = HttpUtil.doPost("https://api.ai.qq.com/fcgi-bin/ocr/ocr_driverlicenseocr", param);
            String result = new String(resBytes);
            int ret = Integer.valueOf(JSONObject.parseObject(result).get("ret").toString());
            if (ret == 0) {
                return result;
            } else {
                if (0 == cardType) {
                    log.error("**************识别行驶证{}失败,错误信息：{}", JSONObject.parseObject(result).get("msg").toString());
                } else {
                    log.error("**************识别驾驶证{}失败,错误信息：{}", JSONObject.parseObject(result).get("msg").toString());
                }
                return "";
            }
        } catch (Exception e) {
            log.error("出错了");
        }
        return "";
    }

    /**
     * 识别银行卡
     *
     * @param newImageName
     * @return
     */
    public String getOCRCreditCard(String newImageName, MultipartFile file) {
        try {
            Map<String, String> param = new TreeMap();
            param.put("app_id", URLEncoder.encode(String.valueOf(appId), "UTF-8"));
            String image = getImageBase64Str(newImageName, file);
            param.put("image", new String(image.getBytes("ISO-8859-1"), "UTF-8"));
            param.put("time_stamp", URLEncoder.encode(String.valueOf(getTimeStamp()), "UTF-8"));
            param.put("nonce_str", URLEncoder.encode(getNonceStr(), "UTF-8"));
            appkey = URLEncoder.encode(appkey, "UTF-8");
            String sign = getReqSign(param, appkey);
            param.put("sign", sign);
            byte[] resBytes = HttpUtil.doPost("https://api.ai.qq.com/fcgi-bin/ocr/ocr_creditcardocr", param);
            String result = new String(resBytes);
            int ret = Integer.valueOf(JSONObject.parseObject(result).get("ret").toString());
            if (ret == 0) {
                return result;
            } else {
                log.error("**************识别银行卡{}失败,错误信息：{}", JSONObject.parseObject(result).get("msg").toString());
                return "";
            }
        } catch (Exception e) {
            log.error("出错了");
        }
        return "";
    }

    /**
     * 获取签名
     *
     * @param params
     * @param appkey
     * @return
     * @throws Exception
     */
    public static String getReqSign(Map<String, String> params, String appkey) throws Exception {
        /**
         * 1,key字典排序-升序
         * 2,非空value url编码,大写
         * 3,拼接所有非空 key=value&.. ,最后再拼接app_key= ,生成字符串s
         * 4,md5加密s, 转大写,生成sign
         */
        Map<String, String> sortedParams = new TreeMap(params);
        Set<Map.Entry<String, String>> entrys = sortedParams.entrySet();
        StringBuilder baseString = new StringBuilder();
        for (Map.Entry<String, String> param : entrys) {
            if (param.getValue() != null && !"".equals(param.getKey().trim()) &&
                    !"sign".equals(param.getKey().trim()) && !"".equals(param.getValue())) {
                baseString.append(param.getKey().trim()).append("=")
                        .append(URLEncoder.encode(param.getValue().toString(), "UTF-8")).append("&");
            }
        }
        if (baseString.length() > 0) {
            StringBuilder append = baseString.deleteCharAt(baseString.length() - 1).append("&app_key=")
                    .append(appkey);
        }
        try {
            String sign = MD5.getMD5(baseString.toString());
            return sign.toUpperCase();
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    /**
     * 将图片转为base64编码
     *
     * @param imgFile
     * @param file
     * @return
     * @description imgFile和file有一个不为空
     */
    public static String getImageBase64Str(String imgFile, MultipartFile file) {
        InputStream inputStream = null;
        byte[] data = null;
        try {
            if (StringUtils.isNotBlank(imgFile)) {
                inputStream = new FileInputStream(imgFile);
            } else {
                inputStream = file.getInputStream();
            }
            data = new byte[inputStream.available()];
            int num = inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            log.error("出错了");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.info("出错了");
                }

            }
        }
        byte[] encode = Base64.getEncoder().encode(data);
        if (encode.length > 0x640000) {
            return "";
        }
        return new String(encode);
    }

    /**
     * base64解密
     *
     * @param imageStr base64加密后的字符串
     * @return
     */
    private static String base64Decoder(String imageStr) {
        byte[] decode = Base64.getDecoder().decode(imageStr);
        if (decode.length >= 0x64000) {
            return "";
        }
        return new String(decode);
    }

    /**
     * 获取营业执照识别数据
     *
     * @param result
     * @return
     */
    public Map<String, String> getLicenceInfo(String result) {
        String data = JSONObject.parseObject(result).get("data").toString();
        String msg = JSONObject.parseObject(result).get("msg").toString();
        String itemList = JSONObject.parseObject(data).get("item_list").toString();
        JSONArray array = JSONArray.parseArray(itemList);
        Map<String, String> ocrInfo = new HashMap();
        ocrInfo.put("msg", msg);
        for (int i = 0; i < array.size(); i++) {
            JSONObject item = JSONObject.parseObject(array.get(i).toString());
            String itemKey = item.get("item").toString();
            if ("注册号".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("registerNumber", itemValue);
            } else if ("法定代表人".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("legalPerson", itemValue);
            } else if ("公司名称".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("companyName", itemValue);
            } else if ("地址".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("companyAddress", itemValue);

            } else if ("营业期限".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("businessTerm", itemValue);

            }
        }
        return ocrInfo;
    }

    /**
     * 获取身份证识别数据
     *
     * @param result
     * @param cardType 0:正面，1：反面
     * @return
     */
    public Map<String, String> getIDCardInfo(String result, int cardType) {
        String msg = JSONObject.parseObject(result).get("msg").toString();
        Map<String, String> ocrInfo = new HashMap();
        ocrInfo.put("msg", msg);
        if (cardType == 0) { //正面
            String data = JSONObject.parseObject(result).get("data").toString();
            JSONObject dataObject = JSONObject.parseObject(data);
            ocrInfo = (Map) dataObject;
        } else if (cardType == 1) { //反面
            String data = JSONObject.parseObject(result).get("data").toString();
            JSONObject dataObject = JSONObject.parseObject(data);
            ocrInfo = (Map) dataObject;
        }
        return ocrInfo;
    }

    /**
     * 获取行驶证/驾驶证信息
     *
     * @param result
     * @return
     */
    public Map<String, String> getDriverLicenceInfo(String result) {
        String data = JSONObject.parseObject(result).get("data").toString();
        String msg = JSONObject.parseObject(result).get("msg").toString();
        String itemList = JSONObject.parseObject(data).get("item_list").toString();
        JSONArray array = JSONArray.parseArray(itemList);
        Map<String, String> ocrInfo = new HashMap();
        ocrInfo.put("msg", msg);
        for (int i = 0; i < array.size(); i++) {
            JSONObject item = JSONObject.parseObject(array.get(i).toString());
            String itemKey = item.get("item").toString();
            if ("车牌号码".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("plateNumber", itemValue);
            } else if ("车辆类型".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("vehicleType", itemValue);
            } else if ("所有人".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("owner", itemValue);
            } else if ("住址".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("liveAddress", itemValue);
            } else if ("使用性质".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("useCharacter", itemValue);
            } else if ("品牌型号".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("brandModel", itemValue);
            } else if ("识别代码".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("idCode", itemValue);
            } else if ("发动机号".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("engineNumber", itemValue);
            } else if ("注册日期".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("regdate", itemValue);
            } else if ("发证日期".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("dateOfIssue", itemValue);
            } else if ("证号".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("cardNumber", itemValue);
            } else if ("姓名".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("name", itemValue);
            } else if ("性别".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("gender", itemValue);
            } else if ("国籍".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("nationality", itemValue);
            } else if ("出生日期".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("birthday", itemValue);
            } else if ("领证日期".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("dateOfCertification", itemValue);
            } else if ("准驾车型".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("quasiDrivingVehicle", itemValue);
            } else if ("起始日期".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("startDate", itemValue);
            } else if ("有效日期".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("effectiveDate", itemValue);
            } else if ("红章".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("hongzhang", itemValue);
            }
        }
        return ocrInfo;
    }

    /**
     * 获取银行卡识别数据
     *
     * @param result
     * @return
     */
    public Map<String, String> getCreditCardInfo(String result) {
        String data = JSONObject.parseObject(result).get("data").toString();
        String msg = JSONObject.parseObject(result).get("msg").toString();
        String itemList = JSONObject.parseObject(data).get("item_list").toString();
        JSONArray array = JSONArray.parseArray(itemList);
        Map<String, String> ocrInfo = new HashMap();
        ocrInfo.put("msg", msg);
        for (int i = 0; i < array.size(); i++) {
            JSONObject item = JSONObject.parseObject(array.get(i).toString());
            String itemKey = item.get("item").toString();
            if ("卡号".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("creditCardNumber", itemValue);
            } else if ("有效期".equals(itemKey)) {
                String itemValue = item.get("itemstring").toString();
                ocrInfo.put("effectiveDate", itemValue);
            }
        }
        return ocrInfo;
    }

    /*public static void main(String[] args) {
        *//**
         * 1、识别身份证正面或者反面--》0:front;1:back
         * 2、识别驾驶证/行驶证--》0:行驶证;1驾驶证:
         *//*
        Integer code = 0;//
        TencentOCRUtil tencentOCRUtil = new TencentOCRUtil();
        String imgPath = "C:/Users/xuewanli/Pictures/证件照/front.jpg";//身份证正面
//        imgPath = "C:/Users/xuewanli/Pictures/证件照/back.jpg";//身份证反面
//        imgPath = "C:/Users/xuewanli/Pictures/证件照/yyzz.jpg";//营业执照
//        imgPath = "C:/Users/xuewanli/Pictures/证件照/驾驶证.jpg";//驾驶证
        imgPath = "C:/Users/xuewanli/Pictures/证件照/行驶证.jpg";//行驶证
//        imgPath = "C:/Users/xuewanli/Pictures/证件照/银行卡.jpg";//银行卡

//        String result = tencentOCRUtil.getOCRLicence(imgPath, null);//识别营业执照
//        String result = tencentOCRUtil.getOCRIDCard(imgPath, code, null);//识别身份证
        String result = tencentOCRUtil.getOCRDriverLicense(imgPath, code, null);//识别行驶证/驾驶证
//        Map map = tencentOCRUtil.getLicenceInfo(result);//获取营业执照信息
//        Map map = tencentOCRUtil.getIDCardInfo(result, code);//获取身份证信息
        Map map = tencentOCRUtil.getDriverLicenceInfo(result);//获取行驶证/驾驶证信息
//        Map map = tencentOCRUtil.getCreditCardInfo(result);//获取银行卡信息
        log.info("图片识别结果为--》" + JsonUtil.object2json(map));
    }*/
}