package com.xiangshui.server.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.xiangshui.util.CallBack;
import org.apache.lucene.search.highlight.QueryScorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

abstract public class BaseDynamoDao<T> {


    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static AmazonDynamoDB client;
    private static DynamoDB dynamoDB;

    private static boolean inited;

    static {
        if (!inited) {
            client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.CN_NORTH_1).build();
            dynamoDB = new DynamoDB(client);
            inited = true;
        }
    }


    @Value("${isdebug}")
    protected boolean debug;
    private Class<T> tClass;


    public BaseDynamoDao() {
        tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    }

    abstract public String getTableName();

    public String getFullTableName() {
        return debug ? "dev_" + getTableName() : getTableName();
    }

    public Table getTable() {
        return dynamoDB.getTable(getFullTableName());
    }


    public void putItem(T t) {
        Table table = getTable();
        Item item = Item.fromJSON(JSON.toJSONString(t));
        PutItemOutcome outcome = table.putItem(item);
    }


    public boolean deleteItem(PrimaryKey primaryKey) {
        Table table = getTable();
        DeleteItemOutcome outcome = table.deleteItem(primaryKey);
        return true;
    }

    public boolean updateItem(PrimaryKey primaryKey, AttributeUpdate... attributeUpdates) {
        Table table = getTable();
        UpdateItemOutcome outcome = table.updateItem(primaryKey, attributeUpdates);
        return true;
    }

    public boolean updateItem(UpdateItemSpec updateItemSpec) {
        Table table = getTable();
        UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
        return true;
    }


    public T getItem(PrimaryKey primaryKey) {
        Table table = getTable();
        Item item = table.getItem(primaryKey);
        if (item != null) {
            return JSON.parseObject(item.toJSON(), tClass);
        }
        return null;
    }


    public T getItem(KeyAttribute... primaryKeyComponents) {
        Table table = getTable();
        Item item = table.getItem(primaryKeyComponents);
        if (item != null) {
            return JSON.parseObject(item.toJSON(), tClass);
        }
        return null;
    }


    public List<T> scan(ScanSpec scanSpec) {
        Table table = getTable();
        ItemCollection<ScanOutcome> items = table.scan(scanSpec);
        List<T> list = new ArrayList();
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            Item item = iter.next();
            list.add(JSON.parseObject(item.toJSON(), tClass));
        }
        return list;
    }

    public void scan(ScanSpec scanSpec, CallBack<T> callback) {
        Table table = getTable();
        ItemCollection<ScanOutcome> items = table.scan(scanSpec);
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            Item item = iter.next();
            callback.run(JSON.parseObject(item.toJSON(), tClass));
        }
    }


    public List<T> query(QuerySpec querySpec) {
        Table table = getTable();
        ItemCollection<QueryOutcome> items = table.query(querySpec);
        List<T> list = new ArrayList();
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            Item item = iter.next();
            list.add(JSON.parseObject(item.toJSON(), tClass));
        }
        return list;
    }


    public void query(QuerySpec querySpec, CallBack<T> callBack) {
        Table table = getTable();
        ItemCollection<QueryOutcome> items = table.query(querySpec);
        Iterator<Item> iter = items.iterator();
        while (iter.hasNext()) {
            Item item = iter.next();
            callBack.run(JSON.parseObject(item.toJSON(), tClass));
        }
    }

}
