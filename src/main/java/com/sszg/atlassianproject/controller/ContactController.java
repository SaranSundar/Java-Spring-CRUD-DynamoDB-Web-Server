package com.sszg.atlassianproject.controller;

import com.amazonaws.services.kms.model.NotFoundException;
import com.sszg.atlassianproject.exception.InvalidRequestException;
import com.sszg.atlassianproject.model.Contact;
import com.sszg.atlassianproject.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    // HTTP POST URL - http://localhost:9500/api/contact
    @Operation(summary = "Create a new contact")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created a new contact, returns back contact UID",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid contact object supplied",
                    content = @Content)})
    @PostMapping("/contact")
    public ResponseEntity<String> postContact(@Parameter(description = "contact object that will be created") @Valid @RequestBody final Contact contact) throws InvalidRequestException {
        log.info("Starting to post contact");
        log.info(contact.toString());
        contactService.postContact(contact);
        return new ResponseEntity<>(contact.getUid(), HttpStatus.OK);
    }

    // HTTP POST URL - http://localhost:9500/api/contact
    @Operation(summary = "Updates existing contact")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated existing contact, returns back contact UID",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid contact object supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "contact not found for given UID",
                    content = @Content)})
    @PutMapping("/contact")
    public ResponseEntity<String> putContact(@Parameter(description = "contact object that will be created") @Valid @RequestBody final Contact contact) throws InvalidRequestException {
        log.info("Starting to put contact");
        log.info(contact.toString());
        contactService.putContact(contact);
        return new ResponseEntity<>(contact.getUid(), HttpStatus.OK);
    }

    // HTTP GET URL - http://localhost:9500/api/contact/{{contactUid}}
    @Operation(summary = "Gets existing contact")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found existing contact, returns back contact",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Contact.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid contact object supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "contact not found for given UID",
                    content = @Content)})
    @GetMapping("/contact/{contactUid}")
    public ResponseEntity<Contact> getContact(@Parameter(description = "UID of existing contact") @PathVariable final String contactUid) throws InvalidRequestException, NotFoundException {
        log.info("Starting to get contact");
        log.info(contactUid);
        Contact contact = contactService.getContact(contactUid);
        return new ResponseEntity<>(contact, HttpStatus.OK);
    }

    // HTTP DELETE URL - http://localhost:9500/api/contact/{{contactUid}}
    @Operation(summary = "Deletes existing contact")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deletes existing contact, returns back deleted contact uid",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Contact.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid contact object supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "contact not found for given UID",
                    content = @Content)})
    @DeleteMapping("/contact/{contactUid}")
    public ResponseEntity<Contact> deleteContact(@Parameter(description = "UID of existing contact") @PathVariable final String contactUid) throws InvalidRequestException, NotFoundException {
        log.info("Starting to delete contact");
        log.info(contactUid);
        Contact contact = contactService.deleteContact(contactUid);
        return new ResponseEntity<>(contact, HttpStatus.OK);
    }

    // HTTP GET URL - http://localhost:9500/api/contact?email=
    @Operation(summary = "Finds all contacts with given email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Finds all contacts with given email, then returns back list of found contacts",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Contact.class))})})
    @GetMapping("/contact")
    public ResponseEntity<List<Contact>> getContactsByEmail(@Parameter(description = "email to query contacts by") @RequestParam final String email) throws InvalidRequestException, NotFoundException {
        log.info("Starting to query contact by email");
        log.info(email);
        List<Contact> contacts = contactService.getContactsByEmail(email);
        return new ResponseEntity<>(contacts, HttpStatus.OK);
    }
}