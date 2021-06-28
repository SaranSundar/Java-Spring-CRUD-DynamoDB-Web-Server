<h1 align="center"> Java Spring CRUD DynamoDB Web Server </h1> <br>

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Pre-Reqs](#pre-reqs)
- [Installation](#installation)
- [Testing](#testing)
- [Final Remarks](#final-remarks)

## Introduction

As the long title suggest this is an application using Java Spring and DynamoDB to allow for a web server to read and write contacts and account info into a locally running instance of DynamoDB through the use of a docker file.

## Features

A few of the things you can do with this web server:

* Create, Update, and Retrieve Accounts and Contacts
* Query for contacts by email
* Get all contacts associated to an account
* View the documentation through the use of swagger-ui and execute requests
* Import the postman json collection to execute requests as well
* Easily pre-populate accounts and contacts table with dummy data
* Delete contact by uid - Test feature, not fully supported since deleting contact doesn't remove it from accounts, so not fully implemented yet

## Pre-Reqs
- Steps below only tested for MacOS Big Sur 11.4
- Install Docker Desktop for Mac
- Install Java 11 using Homebrew
- Make sure Maven is installed as well
- Postman Version 8.7.0 (8.7.0) that has support for the Version2.1 import of it's exported collection

## Installation

- Clone and navigate to repo using a terminal
- We need to first setup the database using the docker-compose file, and some helper scripts that I have created
- Run `sh startContainer.sh` in the terminal to get the local instance of dynamo db which we can access at http://localhost:8000 as defined in the docker-compose.yml file
- Once you can see the database loaded on that port next we need to start the java spring app
- Open up project with IntelliJ if possible, then you can run the maven commands from the maven tab on the right side of IntelliJ, else use terminal to run maven commands below
- run `mvn clean compile install` to setup project and if possible press the maven reload button in IntelliJ
- Execute main method in com.sszg.atlassianproject.AtlassianProjectApplication from your IDE to start the app
- This will start the project on http://localhost:8367 as defined in the application.properties file
- It is important to initialize the database with the tables it needs on the first run, this would traditionally be setup once in the console but for this example I have created a RestController that exposes end points that need to be called to setup the database tables
- Import local file AtlassianProject.postman_collection.json into scratch pad in postman. Calls below for initialization will be found under Tables folder
- Using postman make a POST call to http://localhost:{{port}}/api/tables, this will setup the tables for the Contact and Account. {{port}} is defined in an environment in Postman which you can set by clicking the eyeball on the top right ore just replace with 8367.
- Optionally, next make a POST call to http://localhost:{{port}}/api/populate-tables, this will setup the database with 2 accounts and 2 contacts, where one account has 2 contacts associated to it, and the other just has 1 contact associated to it
- Now the database and spring app are ready to create contacts and accounts and for you to make queries
- You can import the 


## Testing

- Once you have the database tables initialized with the spring app running we can start testing our application
- Generated swagger docs in json format can be accessed at http://localhost:8367/api-docs
- YAML file for docs at http://localhost:8367/api-docs.yaml
- Swagger UI can be found at http://localhost:8367/swagger-ui.html
- The Swagger UI or postman can be used to interact with the Rest API to create, update, delete, and query contact and account objects to and from the database
- The end points that can be inserted into postman are placed in comments in the controller classes where the example request bodies can be found under the dummy_data folder
- You can import the exported postman collection of requests I made into postmans scratch pad to use for testing. It also requires creating an environment in postman to define port and accountUid and caontactUid or they can be hardcoded in the postman request
- Note, for associating a contact with an account, you need to first create a contact, and store the contacts UID in the accounts contactIds list before making a POST or PUT
- You can run `sh stopContainer.sh` to stop the container before resuming later with `sh startContainer.sh`
- You can also delete the container and wipe all the database info by running `sh deleteContainer.sh`


## Final Remarks

In a production environment and given more time there any many choices I would have done differently that I would like to discuss and note down here. For 
one, I could have potentially used AWS Gateway + Lambda to interact with the DynamoDB vs the way it is now that requires the spring app to probably be in an EC2 instance since it's talking directly to the DynamoDB with the client sdk.

I'm new to using DynamoDB, so a lot of this is relatively experimental, one important thing to point out is that even though the input for the post and put controllers are all final objects, probably through a mix of springs reflection in java, and the way DynamoDB uses its @DynamoDBAutoGeneratedKey it is modifying the final object with the uid which is the path
I return for the post and put calls. This is not really a good way to do this as I don't want to be relying on reflection to give back the path of the newly posted object but for now this is the only way I could find.

Another one is the way I'm using swagger right now, normally you would create the spec for the api first, use the swagger or OpenAPI file to generate the request/response model, which would then get translated into the database model before being used. In this case I just directly made the models and then added annotations to generate the swagger documentation for them.

Also, at Capital One we use an internal tool called Chamber of Secrets to be able to securely hide and retrieve the values in application.properties to connect to different database for the different environments like local, dev, qa, and prod. Here I only set up the file for local dev, but normally we would have separate application.yml files for each environment with their properties defined.

We would also traditionally have separate branches in the repo such as develop, master, and release. Where you would fork the project and make PR's into develop which then end up going to master and release. For this project though I just used one master branch. I would also add a linter of some sort like maybe checkstyle to ensure some standard of appearance.

For testing to be more comprehensive I would use karate tests for the controllers, for the sake of time I only tested the Contact and Account services.

Lastly, I am new to some things used in this project such as DynamoDB, so the way I created the tables to show my work, and the classes annotations for attribute definitions and querying will probably have some more optimal ways of usage in production vs how it is showcased here. All in all I had fun working on this project, and it helped me learn a lot and grow as a developer for sure.