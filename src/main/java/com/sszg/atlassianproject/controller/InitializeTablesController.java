package com.sszg.atlassianproject.controller;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sszg.atlassianproject.exception.InvalidTableException;
import com.sszg.atlassianproject.model.Account;
import com.sszg.atlassianproject.model.Contact;
import com.sszg.atlassianproject.service.AccountService;
import com.sszg.atlassianproject.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
@Slf4j
public class InitializeTablesController {

    private final DynamoDB dynamoDB;
    private ObjectMapper mapper;

    private Table contactsTable;
    private final ContactService contactService;
    private final AccountService accountService;

    private List<Contact> contacts;

    public InitializeTablesController(DynamoDB dynamoDB, ContactService contactService, AccountService accountService) {
        this.dynamoDB = dynamoDB;
        this.contactService = contactService;
        this.accountService = accountService;
        contacts = new ArrayList<>();
        mapper = new ObjectMapper();
    }

    // HTTP POST URL - http://localhost:{{port}}/api/tables
    @Operation(summary = "Create tables for account and contact")
    @PostMapping("/tables")
    public ResponseEntity<String> postTables() {
        createContactsTable();
        createAccountsTable();
        return new ResponseEntity<>("Successfully Created Contacts And Accounts Table", HttpStatus.OK);
    }

    // HTTP POST URL - http://localhost:{{port}}/api/populate-tables
    @Operation(summary = "Populate tables for account and contact")
    @PostMapping("/populate-tables")
    public ResponseEntity<String> postPopulateTables() {
        populateContactsTable();
        populateAccountsTable();
        return new ResponseEntity<>("Successfully Populated Contacts And Accounts Table", HttpStatus.OK);
    }

    public void populateContactsTable() {
        // Creating 2 contacts for our tables to be able to see in the local ui for dynamo db
        Contact contact1 = createContactFromFile("DummyContact1.json");
        Contact contact2 = createContactFromFile("DummyContact2.json");
        contacts = new ArrayList<>();
        contacts.add(contact1);
        contacts.add(contact2);

        contactService.postContact(contact1);
        contactService.postContact(contact2);
    }

    public void populateAccountsTable() {
        // Creating 2 accounts for our tables to be able to see in the local ui for dynamo db
        Account account1 = createAccountFromFile("DummyAccount1.json");
        Account account2 = createAccountFromFile("DummyAccount2.json");

        // Account 1 will have both contacts associated to it
        account1.setContactIds(Set.of(contacts.get(0).getUid(), contacts.get(1).getUid()));

        // Account 2 will only have 1 contact associated to it
        account2.setContactIds(Set.of(contacts.get(0).getUid()));

        accountService.postAccount(account1);
        accountService.postAccount(account2);
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

    public Table createTable(
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
            return table;
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
        // Can be used to query on other fields
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
        contactsTable = createTable(tableName, attributeDefinitions, tableKeySchema,
                Collections.singletonList(emailAddressIndex), indexKeySchema);
    }

    public Contact createContactFromFile(String fileName) {
        // read JSON file and map/convert to java POJO
        try {
            String path = "src/test/resources/dummy_data/" + fileName;
            File file = new File(path);
            return mapper.readValue(file, Contact.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Account createAccountFromFile(String fileName) {
        // read JSON file and map/convert to java POJO
        try {
            String path = "src/test/resources/dummy_data/" + fileName;
            File file = new File(path);
            return mapper.readValue(file, Account.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}