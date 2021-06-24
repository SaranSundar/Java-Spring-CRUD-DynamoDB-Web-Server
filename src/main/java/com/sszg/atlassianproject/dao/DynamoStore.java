package com.sszg.atlassianproject.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.sszg.atlassianproject.exception.ItemNotFoundException;

import java.util.*;

public class DynamoStore<T> {

    private final DynamoDBMapper dynamoDBMapper;
    private final Class<T> itemClazz;

    public DynamoStore(DynamoDBMapper dynamoDBMapper, Class<T> itemClazz) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.itemClazz = itemClazz;
    }

    public void saveItem(T item) {
        dynamoDBMapper.save(item);
    }


    public List<T> queryForItem(String globalSIN, String tableIndex, String valueToFind) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":val1", new AttributeValue().withS(valueToFind));
        eav.put(":val2", new AttributeValue().withS("frisco"));
        Map<String, Condition> filters = new HashMap<>();
        filters.put("city", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(Collections.singletonList(new AttributeValue(":val2"))));
        DynamoDBQueryExpression<T> queryExpression = new DynamoDBQueryExpression<T>()
                .withKeyConditionExpression(tableIndex + " = :val1")
                .withQueryFilter(filters)
                .withConsistentRead(false)
                .withIndexName(globalSIN)
                .withExpressionAttributeValues(eav);

        PaginatedQueryList<T> results = dynamoDBMapper.query(itemClazz, queryExpression);
        List<T> queryList = new LinkedList<>(results); //--- Line1
        return queryList;
    }

    public T getItem(String key) {
        T item = dynamoDBMapper.load(itemClazz, key);
        if (item == null) {
            // TODO: Think about if you want to throw table name as well, and make error more generic
            throw new ItemNotFoundException("Item not found in Dynamo DB with key " + key);
        }
        return item;
    }

    public void deleteItem(T item) {
        dynamoDBMapper.delete(item);
    }
}
