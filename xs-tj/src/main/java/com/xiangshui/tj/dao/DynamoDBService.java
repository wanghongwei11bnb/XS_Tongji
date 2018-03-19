package com.xiangshui.tj.dao;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.xiangshui.tj.bean.Area;
import com.xiangshui.tj.bean.Booking;
import com.xiangshui.tj.bean.Capsule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

public class DynamoDBService<T, K> {
    private static final Logger log = LoggerFactory.getLogger(DynamoDBService.class);

    private static AmazonDynamoDB client;
    private static DynamoDB dynamoDB;

    static {
        client = AmazonDynamoDBClientBuilder.standard().build();
        dynamoDB = new DynamoDB(client);
    }

    private  Class<?> tClass;
    private  Class<?> kClass;
    private  String tableName;
    private  String keyField;



    public T getById() {

    }


    private void test() {
        Table table = dynamoDB.getTable("booking");
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("booking_id", 1997548720);
        Item item = table.getItem(spec);
        log.debug(item.toJSON());
    }


    public static void main(String[] args) {
//        new DynamoDBService().test();
    }


}
