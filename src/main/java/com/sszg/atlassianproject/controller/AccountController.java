package com.sszg.atlassianproject.controller;

import com.amazonaws.services.kms.model.NotFoundException;
import com.sszg.atlassianproject.exception.InvalidRequestException;
import com.sszg.atlassianproject.model.Account;
import com.sszg.atlassianproject.model.Contact;
import com.sszg.atlassianproject.service.AccountService;
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
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // HTTP POST URL - http://localhost:9500/api/account
    @Operation(summary = "Create a new account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Created a new account, gets back account UID",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid account object supplied",
                    content = @Content)})
    @PostMapping("/account")
    public ResponseEntity<String> postAccount(@Parameter(description = "account object that will be created") @Valid @RequestBody final Account account) throws InvalidRequestException {
        log.info("Starting to post account");
        log.info(account.toString());
        accountService.postAccount(account);
        return new ResponseEntity<>(account.getUid(), HttpStatus.OK);
    }

    // HTTP POST URL - http://localhost:9500/api/account
    @Operation(summary = "Update existing account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated existing account, gets back account UID",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid account object supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found for given UID",
                    content = @Content)})
    @PutMapping("/account")
    public ResponseEntity<String> putAccount(@Parameter(description = "account object that will be created") @Valid @RequestBody final Account account) throws InvalidRequestException {
        log.info("Starting to put account");
        log.info(account.toString());
        accountService.putAccount(account);
        return new ResponseEntity<>(account.getUid(), HttpStatus.OK);
    }

    // HTTP GET URL - http://localhost:9500/api/{{accountUid}}
    @Operation(summary = "Get existing account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found existing account, returns back account",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Account.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid account UID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found for given UID",
                    content = @Content)})
    @GetMapping("/account/{accountUid}")
    public ResponseEntity<Account> getAccount(@Parameter(description = "UID of existing account") @PathVariable final String accountUid) throws InvalidRequestException, NotFoundException {
        log.info("Starting to get account");
        log.info(accountUid);
        Account account = accountService.getAccount(accountUid);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @Operation(summary = "Get contacts for an existing account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found existing account, gets back account contacts",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Contact.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid account UID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found for given UID",
                    content = @Content)})
    // HTTP GET URL - http://localhost:9500/api/{{accountUid}}/contacts
    @GetMapping("/account/{accountUid}/contacts")
    public ResponseEntity<List<Contact>> getAccountContacts(@Parameter(description = "UID of existing account") @PathVariable final String accountUid) throws InvalidRequestException, NotFoundException {
        log.info("Starting to get account");
        log.info(accountUid);
        Account account = accountService.getAccount(accountUid);
        List<Contact> contacts = accountService.getContacts(account.getContactIds());
        return new ResponseEntity<>(contacts, HttpStatus.OK);
    }

    @Operation(summary = "Deletes account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deletes existing account, returns back account UID that was deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Account.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid account UID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found for given UID",
                    content = @Content)})
    // HTTP DELETE URL - http://localhost:9500/api/{{accountUid}}
    @DeleteMapping("/account/{accountUid}")
    public ResponseEntity<Account> deleteAccount(@Parameter(description = "UID of existing account") @PathVariable final String accountUid) throws InvalidRequestException, NotFoundException {
        log.info("Starting to delete account");
        log.info(accountUid);
        Account account = accountService.deleteAccount(accountUid);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }
}