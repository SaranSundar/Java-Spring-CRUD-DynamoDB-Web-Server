package com.sszg.atlassianproject.dao;

import com.sszg.atlassianproject.exception.ItemNotFoundException;
import com.sszg.atlassianproject.model.Contact;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContactStore {

    private final DynamoStore<Contact> contactDynamoStore;

    public ContactStore(DynamoStore<Contact> contactDynamoStore) {
        this.contactDynamoStore = contactDynamoStore;
    }

    public void saveContact(Contact contact) {
        contactDynamoStore.saveItem(contact);
    }

    public Contact getContact(String uid) {
        return contactDynamoStore.getItem(uid);
    }

    public List<Contact> getAllContactsWithEmail(String email) {
        return contactDynamoStore.queryForItem("EmailAddressIndex", "emailAddress", email);
    }

    public Contact deleteContact(String uid) throws ItemNotFoundException {
        Contact contact = getContact(uid);
        contactDynamoStore.deleteItem(contact);
        return contact;
    }
}
