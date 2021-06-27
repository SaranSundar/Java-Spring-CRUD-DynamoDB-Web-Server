package com.sszg.atlassianproject.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.sszg.atlassianproject.exception.ItemNotFoundException;
import com.sszg.atlassianproject.model.Contact;

import java.util.*;

public class DynamoStore<T> implements DataStore<T> {

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
//        eav.put(":val2", new AttributeValue().withS("frisco"));
//        Map<String, Condition> filters = new HashMap<>();
//        filters.put("city", new Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(Collections.singletonList(new AttributeValue(":val2"))));
        DynamoDBQueryExpression<T> queryExpression = new DynamoDBQueryExpression<T>()
                .withKeyConditionExpression(tableIndex + " = :val1")
//                .withQueryFilter(filters)
                .withConsistentRead(false)
                .withIndexName(globalSIN)
                .withExpressionAttributeValues(eav);

        PaginatedQueryList<T> results = dynamoDBMapper.query(itemClazz, queryExpression);
        List<T> queryList = new LinkedList<>(results); //--- Line1
        return queryList;
    }

    List<Contact> getContactsByUids(Set<String> contactUids) {
        List<Contact> contacts = new ArrayList<>();
        //The primary key for ArticleDao is "articleId"
        for (String uid : contactUids) {
            contacts.add(Contact.builder().uid(uid).build());
        }
        //Batch Load this list of objects
        Map<String, List<Object>> batchResult = dynamoDBMapper.batchLoad(contacts);
        if (batchResult.containsKey("contacts")
                && batchResult.get("contacts") != null) {
            List<Object> results = batchResult.get("contacts");
            //Convert the list of Objects into "ArticlesDao
            @SuppressWarnings("unchecked")
            List<Contact> contactResults = (List<Contact>) (List<?>) results;
            return contactResults;
        }
        return null;
    }

    public T getItem(String key) {
        T item = dynamoDBMapper.load(itemClazz, key);
        if (item == null) {
            throw new ItemNotFoundException("Item not found in Dynamo DB with key " + key);
        }
        return item;
    }

    public void deleteItem(T item) {
        dynamoDBMapper.delete(item);
    }
}
