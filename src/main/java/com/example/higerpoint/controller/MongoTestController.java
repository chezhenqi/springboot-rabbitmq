package com.example.higerpoint.controller;

import com.example.higerpoint.mongo.MongoDbUtil;
import com.example.higerpoint.util.HttpUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author chezhenqi
 * @description mongo控制类
 * @date 2019-03-06 08:45:33
 */
@Controller
@RequestMapping("/mongo")
public class MongoTestController {
    @Autowired
    private MongoDbUtil mongoDbUtil;

    @GetMapping("/add")
    public void add(HttpServletRequest request){
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("id", UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
        map.put("createDate",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        map.put("createBy","test");
        map.put("options","backstage");
        map.put("method",request.getRequestURL().toString());
        map.put("ipAddr", HttpUtil.getIpAddr(request));
        mongoDbUtil.add(map,"sys_log");
    }

    @GetMapping("/sout")
    @ResponseBody
    public List test(HttpServletRequest request) {
        List result = new ArrayList();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("id", UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
        map.put("createDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        map.put("createBy", "test");
        map.put("options", "backstage");
        map.put("method", request.getRequestURL().toString());
        map.put("ipAddr", HttpUtil.getIpAddr(request));
        mongoDbUtil.add(map, "sys_log");
//        mongoDbUtil.deleteAll("sys_log");
//        DBCollection name = mongoDbUtil.getCollection("sys_log");
//        BasicDBObject queryObject = new BasicDBObject("createBy","a4f3762146e54696968b9d64a5bac26a");
//        DBCursor dbObjects = name.find(queryObject);
//        Iterator<DBObject> iterator = dbObjects.iterator();
//        while (iterator.hasNext()){
//            result.add(iterator.next());
//        }
        return result;
    }

    /**
     * 1.查询某个人的操作日志
     * 2.查询某一天的操作日志(日期出入格式为"yyyy-MM-dd"则为一天)
     * 3.查询某个人某一天的操作日志
     *
     * @param createBy
     * @param createDate
     * @return
     */
    @GetMapping(value = "/queryLogByCreateBy")
    @ResponseBody
    public List queryLogByCreateBy(String createBy, String createDate) {
        List result = new ArrayList();
        DBCollection collection = mongoDbUtil.getCollection("sys_log");
        BasicDBObject queryObject = new BasicDBObject();
        if (StringUtils.isNotBlank(createBy)) {
            queryObject.append("createBy", createBy);
        }
        if (StringUtils.isNotBlank(createDate)) {
            queryObject.append("createDate", Pattern.compile("^" + createDate + ".*$", Pattern.CASE_INSENSITIVE));
        }
        //根据创建时间(createDate)降序(-1,升序为1)查询
        DBCursor dbObjects = collection.find(queryObject).sort(new BasicDBObject("createDate", -1));
        ;
        Iterator<DBObject> iterator = dbObjects.iterator();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }
}
