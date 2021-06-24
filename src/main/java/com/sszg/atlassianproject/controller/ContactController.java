package com.sszg.atlassianproject.controller;

import com.amazonaws.services.kms.model.NotFoundException;
import com.sszg.atlassianproject.exception.InvalidRequestException;
import com.sszg.atlassianproject.model.Contact;
import com.sszg.atlassianproject.service.VendorAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class ContactController {

    private final VendorAccountService vendorAccountService;

    public ContactController(VendorAccountService vendorAccountService) {
        this.vendorAccountService = vendorAccountService;
    }

    // HTTP POST URL - http://localhost:9500/api/contact
    @PostMapping("/contact")
    public ResponseEntity<String> postContact(@RequestBody Contact contact) throws InvalidRequestException {
        log.info("Starting to post contact");
        log.info(contact.toString());
        vendorAccountService.postContact(contact);
        return new ResponseEntity<>(contact.getUid(), HttpStatus.OK);
    }

    // HTTP GET URL - http://localhost:9500/api/contact
    @GetMapping("/contact")
    public ResponseEntity<Contact> getContact(@RequestParam String uid) throws InvalidRequestException, NotFoundException {
        log.info("Starting to get contact");
        log.info(uid);
        Contact contact = vendorAccountService.getContact(uid);
        return new ResponseEntity<>(contact, HttpStatus.OK);
    }

    // HTTP GET URL - http://localhost:9500/api/contact
    @PutMapping("/contact")
    public ResponseEntity<Contact> deleteContact(@RequestParam String uid) throws InvalidRequestException, NotFoundException {
        log.info("Starting to delete contact");
        log.info(uid);
        Contact contact = vendorAccountService.deleteContact(uid);
        return new ResponseEntity<>(contact, HttpStatus.OK);
    }

    // TODO: CHange get urls to match paramaters
    // HTTP GET URL - http://localhost:9500/api/contact
    @GetMapping("/contact-by-email")
    public ResponseEntity<List<Contact>> getContactsByEmail(@RequestParam String email) throws InvalidRequestException, NotFoundException {
        log.info("Starting to query contact by email");
        log.info(email);
        List<Contact> contacts = vendorAccountService.getContactsByEmail(email);
        return new ResponseEntity<>(contacts, HttpStatus.OK);
    }
}