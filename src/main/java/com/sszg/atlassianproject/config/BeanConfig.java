package com.sszg.atlassianproject.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.sszg.atlassianproject.dao.DynamoStore;
import com.sszg.atlassianproject.model.Account;
import com.sszg.atlassianproject.model.Contact;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// TODO: Add java docs and comments explaining the code

@Configuration
public class BeanConfig {
    @Value("${amazon.dynamodb.endpoint}")
    String endpoint = "http://localhost:8000/";

    @Value("${amazon.aws.accesskey}")
    String dynamoDBAccessKey = "key";

    @Value("${amazon.aws.secretkey}")
    String dynamoDBSecretKey = "";

    String region = "us-east-1";

    public AwsClientBuilder.EndpointConfiguration endpointConfiguration() {
        return new AwsClientBuilder.EndpointConfiguration(endpoint, region);
    }

    public AWSCredentialsProvider awsCredentialsProvider() {
        return new AWSStaticCredentialsProvider(new BasicAWSCredentials(dynamoDBAccessKey, dynamoDBSecretKey));
    }

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        return AmazonDynamoDBClientBuilder
                .standard()
                .withEndpointConfiguration(endpointConfiguration())
                .withCredentials(awsCredentialsProvider())
                .build();
    }

    @Bean
    public DynamoDB dynamoDB(AmazonDynamoDB amazonDynamoDB) {
        return new DynamoDB(amazonDynamoDB);
    }

    @Bean
    public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB amazonDynamoDB) {
        return new DynamoDBMapper(amazonDynamoDB);
    }

    @Bean
    public DynamoStore<Contact> contactDynamoStore(DynamoDBMapper dynamoDBMapper) {
        return new DynamoStore<>(dynamoDBMapper, Contact.class);
    }

    @Bean
    public DynamoStore<Account> accountDynamoStore(DynamoDBMapper dynamoDBMapper) {
        return new DynamoStore<>(dynamoDBMapper, Account.class);
    }
}
