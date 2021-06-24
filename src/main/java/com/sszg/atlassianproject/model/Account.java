package com.sszg.atlassianproject.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

// TODO: Explain why all these annotations are needed
@DynamoDBTable(tableName = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Component
public class Account {
    // Each contact will have its own unique Id
    @DynamoDBHashKey(attributeName = "uid")
    @DynamoDBAutoGeneratedKey
    private String uid;
    @DynamoDBAttribute
    private String companyName;
    @DynamoDBAttribute
    private String addressLine1;
    @DynamoDBAttribute
    private String addressLine2;
    @DynamoDBAttribute
    private String city;
    @DynamoDBAttribute
    private String state;
    @DynamoDBAttribute
    private String postalCode;
    @DynamoDBAttribute
    private String country;
    @DynamoDBAttribute
    private List<String> contactIds;
}