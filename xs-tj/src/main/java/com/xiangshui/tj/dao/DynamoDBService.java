package com.xiangshui.tj.dao;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import org.springframework.stereotype.Component;

@Component
public class DynamoDBService {

    private AmazonDynamoDB client;
    private DynamoDB dynamoDB;

    public DynamoDBService() {
        client = AmazonDynamoDBClientBuilder.standard().build();
        dynamoDB = new DynamoDB(client);
    }


    public static void main(String[] args) {
        new DynamoDBService().test();
    }

    private void test() {

        Table table = dynamoDB.getTable("dev_boo");


    }

}
