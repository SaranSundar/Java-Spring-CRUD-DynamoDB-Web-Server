package com.sszg.atlassianproject.controller;

import com.amazonaws.services.kms.model.NotFoundException;
import com.sszg.atlassianproject.exception.InvalidRequestException;
import com.sszg.atlassianproject.model.Account;
import com.sszg.atlassianproject.model.Contact;
import com.sszg.atlassianproject.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // TODO: Throw error if request body has more parameters then wat it should

    // HTTP POST URL - http://localhost:9500/api/account
    @PostMapping("/account")
    public ResponseEntity<String> postAccount(@Valid @RequestBody Account account) throws InvalidRequestException {
        log.info("Starting to post account");
        log.info(account.toString());
        accountService.postAccount(account);
        return new ResponseEntity<>(account.getUid(), HttpStatus.OK);
    }

    // HTTP POST URL - http://localhost:9500/api/account
    @PutMapping("/account")
    // TODO: Do i need to throw error if they provide same contact uid multiple times even tho spring takes care of that
    public ResponseEntity<String> putAccount(@Valid @RequestBody Account account) throws InvalidRequestException {
        log.info("Starting to put account");
        log.info(account.toString());
        accountService.putAccount(account);
        return new ResponseEntity<>(account.getUid(), HttpStatus.OK);
    }

    // HTTP GET URL - http://localhost:9500/api/{{accountUid}}
    @GetMapping("/account/{accountUid}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountUid) throws InvalidRequestException, NotFoundException {
        log.info("Starting to get account");
        log.info(accountUid);
        Account account = accountService.getAccount(accountUid);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    // HTTP GET URL - http://localhost:9500/api/{{accountUid}}/contacts
    @GetMapping("/account/{accountUid}/contacts")
    public ResponseEntity<List<Contact>> getAccountContacts(@PathVariable String accountUid) throws InvalidRequestException, NotFoundException {
        log.info("Starting to get account");
        log.info(accountUid);
        Account account = accountService.getAccount(accountUid);
        List<Contact> contacts = accountService.getContacts(account.getContactIds());
        return new ResponseEntity<>(contacts, HttpStatus.OK);
    }

    // HTTP DELETE URL - http://localhost:9500/api/{{accountUid}}
    @DeleteMapping("/account/{accountUid}")
    public ResponseEntity<Account> deleteAccount(@PathVariable String accountUid) throws InvalidRequestException, NotFoundException {
        log.info("Starting to delete account");
        log.info(accountUid);
        Account account = accountService.deleteAccount(accountUid);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }
}