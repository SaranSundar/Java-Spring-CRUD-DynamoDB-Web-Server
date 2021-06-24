package com.sszg.atlassianproject.controller;

import com.amazonaws.services.kms.model.NotFoundException;
import com.sszg.atlassianproject.exception.InvalidRequestException;
import com.sszg.atlassianproject.model.Contact;
import com.sszg.atlassianproject.service.ContactService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/contact")
    public ResponseEntity<String> postContact(@RequestBody Contact contact) throws InvalidRequestException {
        log.info("Starting to post contact");
        log.info(contact.toString());
        contactService.postContact(contact);
        return new ResponseEntity<>(contact.getUid(), HttpStatus.OK);
    }

    // HTTP POST URL - http://localhost:9500/api/contact
    @PutMapping("/contact")
    public ResponseEntity<String> putContact(@RequestBody Contact contact) throws InvalidRequestException {
        log.info("Starting to put contact");
        log.info(contact.toString());
        contactService.putContact(contact);
        return new ResponseEntity<>(contact.getUid(), HttpStatus.OK);
    }

    // HTTP GET URL - http://localhost:9500/api/contact?uid=
    @GetMapping("/contact")
    public ResponseEntity<Contact> getContact(@RequestParam String uid) throws InvalidRequestException, NotFoundException {
        log.info("Starting to get contact");
        log.info(uid);
        Contact contact = contactService.getContact(uid);
        return new ResponseEntity<>(contact, HttpStatus.OK);
    }

    // HTTP DELETE URL - http://localhost:9500/api/contact?uid=
    @DeleteMapping("/contact")
    public ResponseEntity<Contact> deleteContact(@RequestParam String uid) throws InvalidRequestException, NotFoundException {
        log.info("Starting to delete contact");
        log.info(uid);
        Contact contact = contactService.deleteContact(uid);
        return new ResponseEntity<>(contact, HttpStatus.OK);
    }

    // HTTP GET URL - http://localhost:9500/api/contact-by-email?email=
    @GetMapping("/contact-by-email")
    public ResponseEntity<List<Contact>> getContactsByEmail(@RequestParam String email) throws InvalidRequestException, NotFoundException {
        log.info("Starting to query contact by email");
        log.info(email);
        List<Contact> contacts = contactService.getContactsByEmail(email);
        return new ResponseEntity<>(contacts, HttpStatus.OK);
    }
}