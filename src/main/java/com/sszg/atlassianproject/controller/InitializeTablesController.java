package com.sszg.atlassianproject.controller;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.sszg.atlassianproject.exception.InvalidTableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class InitializeTablesController {

    private final DynamoDB dynamoDB;

    public InitializeTablesController(DynamoDB dynamoDB) {
        this.dynamoDB = dynamoDB;
    }

    // HTTP POST URL - http://localhost:9500/api/contacts-table
    // TODO: Make exception show exception message in postman
    @PostMapping("/contacts-table")
    public ResponseEntity<String> postContactsTable() {
        createContactsTable();
        return new ResponseEntity<>("Successfully Created Contacts Table", HttpStatus.OK);
    }

    // HTTP POST URL - http://localhost:9500/api/accounts-table
    @PostMapping("/accounts-table")
    public ResponseEntity<String> postAccountsTable() {
        createAccountsTable();
        return new ResponseEntity<>("Successfully Created Accounts Table", HttpStatus.OK);
    }

    public void createAccountsTable() {
        String tableName = "accounts";
        // Attribute definitions
        ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(new AttributeDefinition("uid", ScalarAttributeType.S));

        // Table key schema
        ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<>();
        tableKeySchema.add(new KeySchemaElement()
                .withAttributeName("uid")
                .withKeyType(KeyType.HASH));  //Partition key

        ArrayList<KeySchemaElement> indexKeySchema = new ArrayList<>();

        indexKeySchema.add(new KeySchemaElement()
                .withAttributeName("emailAddress")
                .withKeyType(KeyType.HASH));  //Partition key

        createTable(tableName, attributeDefinitions, tableKeySchema,
                Collections.emptyList(), indexKeySchema);
    }

    public void createTable(
            String tableName,
            List<AttributeDefinition> attributeDefinitions,
            List<KeySchemaElement> tableKeySchema,
            List<GlobalSecondaryIndex> globalSecondaryIndices,
            ArrayList<KeySchemaElement> indexKeySchema) {
        try {
            CreateTableRequest createTableRequest = new CreateTableRequest()
                    .withTableName(tableName)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits((long) 5)
                            .withWriteCapacityUnits((long) 1))
                    .withAttributeDefinitions(attributeDefinitions)
                    .withKeySchema(tableKeySchema);

            if (!globalSecondaryIndices.isEmpty()) {
                createTableRequest.withGlobalSecondaryIndexes(globalSecondaryIndices);
            }

            System.out.println("Attempting to create table " + tableName + "; please wait...");
            Table table = dynamoDB.createTable(createTableRequest);
            table.waitForActive();
            System.out.println(table.getDescription());
            System.out.println("Success. Table status: " + table.getDescription().getTableStatus());
        } catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
            throw new InvalidTableException(tableName + " already exists, so we can not create a new table.");
        }
    }

    public void createContactsTable() {
        String tableName = "contacts";
        // Attribute definitions
        ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(new AttributeDefinition("uid", ScalarAttributeType.S));
//        attributeDefinitions.add(new AttributeDefinition()
//                .withAttributeName("name")
//                .withAttributeType("S"));
        attributeDefinitions.add(new AttributeDefinition()
                .withAttributeName("emailAddress")
                .withAttributeType("S"));
//        attributeDefinitions.add(new AttributeDefinition()
//                .withAttributeName("addressLine1")
//                .withAttributeType("S"));
//        attributeDefinitions.add(new AttributeDefinition()
//                .withAttributeName("addressLine2")
//                .withAttributeType("S"));
//        attributeDefinitions.add(new AttributeDefinition()
//                .withAttributeName("city")
//                .withAttributeType("S"));
//        attributeDefinitions.add(new AttributeDefinition()
//                .withAttributeName("state")
//                .withAttributeType("S"));
//        attributeDefinitions.add(new AttributeDefinition()
//                .withAttributeName("postalCode")
//                .withAttributeType("S"));
//        attributeDefinitions.add(new AttributeDefinition()
//                .withAttributeName("country")
//                .withAttributeType("S"));

        // Table key schema
        ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<>();
        tableKeySchema.add(new KeySchemaElement()
                .withAttributeName("uid")
                .withKeyType(KeyType.HASH));  //Partition key

        GlobalSecondaryIndex emailAddressIndex = new GlobalSecondaryIndex()
                .withIndexName("EmailAddressIndex")
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits((long) 10)
                        .withWriteCapacityUnits((long) 1))
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL));

        ArrayList<KeySchemaElement> indexKeySchema = new ArrayList<>();

        indexKeySchema.add(new KeySchemaElement()
                .withAttributeName("emailAddress")
                .withKeyType(KeyType.HASH));  //Partition key

        emailAddressIndex.setKeySchema(indexKeySchema);
        createTable(tableName, attributeDefinitions, tableKeySchema,
                Collections.singletonList(emailAddressIndex), indexKeySchema);
    }
}