package com.sszg.atlassianproject.dao;

import com.sszg.atlassianproject.exception.ItemNotFoundException;
import com.sszg.atlassianproject.model.Account;
import com.sszg.atlassianproject.model.Contact;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class AccountStore {

    private final DynamoStore<Account> accountDynamoStore;
    private final DynamoStore<Contact> contactDynamoStore;

    public AccountStore(DynamoStore<Account> accountDynamoStore, DynamoStore<Contact> contactDynamoStore) {
        this.accountDynamoStore = accountDynamoStore;
        this.contactDynamoStore = contactDynamoStore;
    }

    public void saveAccount(Account account) {
        accountDynamoStore.saveItem(account);
    }

    public Account getAccount(String uid) {
        return accountDynamoStore.getItem(uid);
    }

    public List<Contact> getContacts(Set<String> uids){
        // TODO: Implement this with some query in dynamo db
        return contactDynamoStore.getContactsByUids(uids);
    }


    public Account deleteAccount(String uid) throws ItemNotFoundException {
        Account account = getAccount(uid);
        accountDynamoStore.deleteItem(account);
        return account;
    }
}
