package com.example.higerpoint.mongo;

import com.mongodb.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chezhenqi
 * @date 2019/12/27 星期五
 * @time 15:33
 * @description springboot-rabbitmq
 */
@Component
public class MongoDbUtil {
    @Value("${spring.data.mongodb.host}")
    private String privateHost;
    @Value("${spring.data.mongodb.port}")
    private int privatePort;
    @Value("${spring.data.mongodb.database}")
    private String privateDataBase;
    @Value("${spring.data.mongodb.username}")
    private String privateUserName;
    @Value("${spring.data.mongodb.password}")
    private String privatePassWord;

    private Mongo mongo;
    private DB db;

    private DB getDB() {
        mongo = new Mongo(privateHost + ":" + privatePort);
        return mongo.getDB(privateDataBase);
    }

    public MongoDbUtil() {
    }

    /**
     * 添加操作
     *
     * @param map
     * @param collectionName
     */
    public void add(Map<String, Object> map, String collectionName) {
        DBObject dbObject = new BasicDBObject(map);
        getCollection(collectionName).insert(dbObject);
    }

    /**
     * 添加操作
     *
     * @param list
     * @param collectionName
     */
    public void add(List<Map<String, Object>> list, String collectionName) {
        for (Map<String, Object> map : list) {
            add(map, collectionName);
        }
    }

    /**
     * 删除操作
     *
     * @param map
     * @param collectionName
     */
    public void delete(Map<String, Object> map, String collectionName) {
        DBObject dbObject = new BasicDBObject(map);
        getCollection(collectionName).remove(dbObject);
    }

    /**
     * 删除操作,根据主键
     *
     * @param id
     * @param collectionName
     */
    public void delete(String id, String collectionName) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("_id", new ObjectId(id));
        delete(map, collectionName);
    }

    /**
     * 删除全部
     *
     * @param collectionName
     */
    public void deleteAll(String collectionName) {
        getCollection(collectionName).drop();
    }

    /**
     * 修改操作
     * 会用一个新文档替换现有文档,文档key结构会发生改变
     * 比如原文档{"_id":"123","name":"zhangsan","age":12}当根据_id修改age
     * value为{"age":12}新建的文档name值会没有,结构发生了改变
     *
     * @param whereMap
     * @param valueMap
     * @param collectionName
     */
    public void update(Map<String, Object> whereMap, Map<String, Object> valueMap, String collectionName) {
        executeUpdate(collectionName, whereMap, valueMap, new UpdateCallback() {
            @Override
            public DBObject doCallback(DBObject valueDBObject) {
                return valueDBObject;
            }
        });
    }

    /**
     * 修改操作,使用$set修改器
     * 用来指定一个键值,如果键不存在,则自动创建,会更新原来文档, 不会生成新的, 结构不会发生改变
     *
     * @param whereMap
     * @param valueMap
     * @param collectionName
     */
    public void updateSet(Map<String, Object> whereMap, Map<String, Object> valueMap, String collectionName) {
        executeUpdate(collectionName, whereMap, valueMap, new UpdateCallback() {
            @Override
            public DBObject doCallback(DBObject valueDBObject) {
                return new BasicDBObject("$set", valueDBObject);
            }
        });
    }

    /**
     * 修改操作,使用$inc修改器
     * 修改器键的值必须为数字
     * 如果键存在增加或减少键的值, 如果不存在创建键
     *
     * @param whereMap
     * @param valueMap
     * @param collectionName
     */
    public void updateInc(Map<String, Object> whereMap, Map<String, Integer> valueMap, String collectionName) {
        executeUpdate(collectionName, whereMap, valueMap, new UpdateCallback() {
            @Override
            public DBObject doCallback(DBObject valueDBObject) {
                return new BasicDBObject("$inc", valueDBObject);
            }
        });
    }

    /**
     * 修改
     *
     * @param collectionName
     * @param whereMap
     * @param valueMap
     * @param updateCallback
     */
    private void executeUpdate(String collectionName, Map whereMap, Map valueMap, UpdateCallback updateCallback) {
        DBObject whereDBObject = new BasicDBObject(whereMap);
        DBObject valueDBObject = new BasicDBObject(valueMap);
        valueDBObject = updateCallback.doCallback(valueDBObject);
        getCollection(collectionName).update(whereDBObject, valueDBObject);
    }

    interface UpdateCallback {

        DBObject doCallback(DBObject valueDBObject);
    }

    /**
     * 获取集合(表)
     *
     * @param collectionName
     * @return
     */
    public DBCollection getCollection(String collectionName) {
        DB db = getDB();
        return db.getCollection(collectionName);
    }
}
