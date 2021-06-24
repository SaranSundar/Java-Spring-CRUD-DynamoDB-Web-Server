package com.sszg.atlassianproject.service;

import com.amazonaws.util.StringUtils;
import com.sszg.atlassianproject.dao.AccountStore;
import com.sszg.atlassianproject.exception.InvalidRequestException;
import com.sszg.atlassianproject.model.Account;
import com.sszg.atlassianproject.model.Contact;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountStore accountStore;

    public AccountService(AccountStore accountStore) {
        this.accountStore = accountStore;
    }

    public void postAccount(Account account) {
        if (account.getUid() != null) {
            throw new InvalidRequestException("Account for POST should not provide UID");
        }
        accountStore.saveAccount(account);
    }

    public void putAccount(Account account) {
        if (StringUtils.isNullOrEmpty(account.getUid())) {
            throw new InvalidRequestException("Account for PUT should provide a UID");
        }
        // We try to get Account before saving new Account because, if it fails to get it will throw a ItemNotFoundException
        getAccount(account.getUid());
        accountStore.saveAccount(account);
    }

    public Account getAccount(String uid) {
        if (StringUtils.isNullOrEmpty(uid)) {
            throw new InvalidRequestException("Account UID for GET should provide a UID");
        }
        return accountStore.getAccount(uid);
    }

    public List<Contact> getContacts(String uid){
        if (StringUtils.isNullOrEmpty(uid)) {
            throw new InvalidRequestException("Account UID for GET Contacts should provide a UID");
        }
        return accountStore.getContacts(uid);
    }

    public Account deleteAccount(String uid) {
        if (StringUtils.isNullOrEmpty(uid)) {
            throw new InvalidRequestException("Account UID for GET should provide a UID");
        }
        return accountStore.deleteAccount(uid);
    }
}
