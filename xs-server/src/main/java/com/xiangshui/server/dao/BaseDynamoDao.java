package com.xiangshui.server.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.*;
import com.amazonaws.services.dynamodbv2.model.ReturnConsumedCapacity;
import com.xiangshui.util.CallBack;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

abstract public class BaseDynamoDao<T> {


    private final Logger log = LoggerFactory.getLogger(this.getClass());
    protected static AmazonDynamoDB client;
    protected static DynamoDB dynamoDB;
    protected static boolean inited;

    public static final int maxResultSize = 1000;
    public static final int maxDownloadSize = 100000;

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


    public T getItem(PrimaryKey primaryKey) {
        Table table = getTable();
        Item item = table.getItem(primaryKey);
        if (item != null) {
            return JSON.parseObject(item.toJSON(), tClass);
        }
        return null;
    }

    public T getItem(GetItemSpec getItemSpec) {
        Table table = getTable();
        Item item = table.getItem(getItemSpec);
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


    public List<T> scan() {
        ScanSpec scanSpec = new ScanSpec();
        scanSpec.withMaxResultSize(maxResultSize);
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

    public List<T> scan(ScanSpec scanSpec) {
        if (scanSpec.getMaxResultSize() == null || scanSpec.getMaxResultSize() <= 0) {
            scanSpec.setMaxResultSize(maxResultSize);
        }
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
        if (scanSpec.getMaxResultSize() == null || scanSpec.getMaxResultSize() <= 0 || scanSpec.getMaxResultSize() > maxResultSize) {
            scanSpec.setMaxResultSize(maxResultSize);
        }
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

    public void updateItem(PrimaryKey primaryKey, T example, String[] fields) throws Exception {
        List<AttributeUpdate> updateList = new ArrayList<AttributeUpdate>();
        for (String fieldName : fields) {
            Field field = tClass.getDeclaredField(fieldName);
            if (field != null) {
                field.setAccessible(true);
                Class<?> type = field.getType();
                if (type == String.class) {
                    if (StringUtils.isNotBlank((CharSequence) field.get(example))) {
                        updateList.add(new AttributeUpdate(fieldName).put(field.get(example)));
                    } else {
                        updateList.add(new AttributeUpdate(fieldName).delete());
                    }
                } else if (type == byte.class || type == Byte.class
                        || type == short.class || type == Short.class
                        || type == int.class || type == Integer.class
                        || type == long.class || type == Long.class
                        || type == float.class || type == Float.class
                        || type == double.class || type == Double.class
                        || type == boolean.class || type == Boolean.class
                        || type == char.class || type == Character.class) {
                    if (field.get(example) == null) {
                        updateList.add(new AttributeUpdate(fieldName).delete());
                    } else {
                        updateList.add(new AttributeUpdate(fieldName).put(field.get(example)));
                    }
                } else if (type == Date.class) {
                    if (field.get(example) == null) {
                        updateList.add(new AttributeUpdate(fieldName).delete());
                    } else {
                        updateList.add(new AttributeUpdate(fieldName).put(((Date) (field.get(example))).getTime()));
                    }
                } else {
                    if (field.get(example) == null) {
                        updateList.add(new AttributeUpdate(fieldName).delete());
                    } else {
                        updateList.add(new AttributeUpdate(fieldName).put(JSON.toJSON(field.get(example), new SerializeConfig())));
                    }
                }
            }
        }
        Table table = getTable();
        table.updateItem(primaryKey, updateList.toArray(new AttributeUpdate[0]));
    }

    public List<T> batchGetItem(String hashKeyName, Object[] hashKeyValues, String[] attributeNames) {
        TableKeysAndAttributes tableKeysAndAttributes = new TableKeysAndAttributes(getFullTableName());
        tableKeysAndAttributes.addHashOnlyPrimaryKeys(hashKeyName, hashKeyValues);
        if (attributeNames != null) {
            tableKeysAndAttributes.withAttributeNames(attributeNames);
        }
        BatchGetItemOutcome outcome = dynamoDB.batchGetItem(ReturnConsumedCapacity.TOTAL, tableKeysAndAttributes);
        Map<String, List<Item>> tableItems = outcome.getTableItems();
        for (Map.Entry<String, List<Item>> entry : tableItems.entrySet()) {
            if (getFullTableName().equals(entry.getKey())) {
                List<T> list = new ArrayList();
                for (Item item : entry.getValue()) {
                    list.add(JSON.parseObject(item.toJSON(), tClass));
                }
                return list;
            }
        }
        return null;
    }

    public List<T> batchGetItem(String hashKeyName, Object[] hashKeyValues) {
        return batchGetItem(hashKeyName, hashKeyValues, null);
    }


    public List<ScanFilter> makeScanFilterList(T t, String... fields) throws NoSuchFieldException, IllegalAccessException {
        Set<String> fieldSet = new HashSet<String>();
        if (fields == null || fields.length == 0) {
            for (Field field : tClass.getDeclaredFields()) {
                fieldSet.add(field.getName());
            }
        } else {
            for (String fieldName : fields) {
                fieldSet.add(fieldName);
            }
        }
        List<ScanFilter> filterList = new ArrayList<ScanFilter>();
        for (String fieldName : fieldSet) {
            Field field = tClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            Class<?> type = field.getType();
            if (type == String.class) {
                if (StringUtils.isNotBlank((CharSequence) field.get(t))) {
                    filterList.add(new ScanFilter(fieldName).eq(field.get(t)));
                }
            } else {
                if (field.get(t) != null) {
                    filterList.add(new ScanFilter(fieldName).eq(field.get(t)));
                }
            }
        }
        return filterList;
    }


    public ScanFilter makeDateRangeFilter(String fieldName, Date start, Date end) {
        if (start != null && end != null) {
            return new ScanFilter(fieldName).between(
                    start.getTime() / 1000, (end.getTime() + 1000 * 60 * 60 * 24) / 1000
            );
        } else if (start != null && end == null) {
            return new ScanFilter(fieldName).gt(start.getTime() / 1000);
        } else if (start == null && end != null) {
            return new ScanFilter(fieldName).lt((end.getTime() + 1000 * 60 * 60 * 24) / 1000);
        }
        return null;
    }


    public void appendDateRangeFilter(List<ScanFilter> scanFilterList, String fieldName, Date start, Date end) {
        ScanFilter scanFilter = makeDateRangeFilter(fieldName, start, end);
        if (scanFilter != null) {
            scanFilterList.add(scanFilter);
        }
    }
}
