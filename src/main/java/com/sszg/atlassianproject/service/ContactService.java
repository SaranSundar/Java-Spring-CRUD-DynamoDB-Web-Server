package com.sszg.atlassianproject.service;

import com.amazonaws.util.StringUtils;
import com.sszg.atlassianproject.dao.ContactStore;
import com.sszg.atlassianproject.exception.InvalidRequestException;
import com.sszg.atlassianproject.model.Contact;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {

    private final ContactStore contactStore;

    public ContactService(ContactStore contactStore) {
        this.contactStore = contactStore;
    }

    public void postContact(Contact contact) {
        if (contact.getUid() != null) {
            throw new InvalidRequestException("Contact for POST should not provide UID");
        }
        contactStore.saveContact(contact);
    }

    public void putContact(Contact contact) {
        if (StringUtils.isNullOrEmpty(contact.getUid())) {
            throw new InvalidRequestException("Contact for PUT should provide a UID");
        }
        getContact(contact.getUid());
        // We try to get contact before saving new contact because, if it fails to get it will throw a ItemNotFoundException
        contactStore.saveContact(contact);
    }

    public Contact getContact(String uid) {
        if (StringUtils.isNullOrEmpty(uid)) {
            throw new InvalidRequestException("Contact UID for GET should is null or empty");
        }
        return contactStore.getContact(uid);
    }

    public Contact deleteContact(String uid) {
        if (StringUtils.isNullOrEmpty(uid)) {
            throw new InvalidRequestException("Contact UID for GET should is null or empty");
        }
        return contactStore.deleteContact(uid);
    }

    public List<Contact> getContactsByEmail(String email) {
        if (StringUtils.isNullOrEmpty(email)) {
            throw new InvalidRequestException("Contact email for GET is null or empty");
        }
        return contactStore.getAllContactsWithEmail(email);
    }
}
