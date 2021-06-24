package com.sszg.atlassianproject.controller;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;

@RestController
@RequestMapping("/api")
@Slf4j
public class InitializeTablesController {

    private final DynamoDB dynamoDB;

    public InitializeTablesController(DynamoDB dynamoDB) {
        this.dynamoDB = dynamoDB;
    }

    // HTTP POST URL - http://localhost:9500/api/create-contacts-table
    @PostMapping("/contacts-table")
    public ResponseEntity<String> postContactsTable() {
        log.info("Starting to create contacts table");
        createContactsTable();
        return new ResponseEntity<>("", HttpStatus.OK);
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

        try {
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

            CreateTableRequest createTableRequest = new CreateTableRequest()
                    .withTableName("contacts")
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits((long) 5)
                            .withWriteCapacityUnits((long) 1))
                    .withAttributeDefinitions(attributeDefinitions)
                    .withKeySchema(tableKeySchema)
                    .withGlobalSecondaryIndexes(emailAddressIndex);

            System.out.println("Attempting to create table; please wait...");
            Table table = dynamoDB.createTable(createTableRequest);
            table.waitForActive();
            System.out.println(table.getDescription());
            System.out.println("Success.  Table status: " + table.getDescription().getTableStatus());

        } catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
        }
    }
}