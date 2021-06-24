package com.sszg.atlassianproject.service;

import com.amazonaws.util.StringUtils;
import com.sszg.atlassianproject.dao.ContactStore;
import com.sszg.atlassianproject.exception.InvalidRequestException;
import com.sszg.atlassianproject.model.Contact;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VendorAccountService {

    private final ContactStore contactStore;

    public VendorAccountService(ContactStore contactStore) {
        this.contactStore = contactStore;
    }

    public void postContact(Contact contact) {
        if (contact.getUid() != null) {
            throw new InvalidRequestException("Contact for post should not provide UID");
        }
        contactStore.saveContact(contact);
    }

    public Contact getContact(String uid) {
        if (StringUtils.isNullOrEmpty(uid)) {
            throw new InvalidRequestException("Contact UID for get should is null or empty");
        }
        return contactStore.getContact(uid);
    }

    public Contact deleteContact(String uid) {
        if (StringUtils.isNullOrEmpty(uid)) {
            throw new InvalidRequestException("Contact UID for get should is null or empty");
        }
        return contactStore.deleteContact(uid);
    }

    public List<Contact> getContactsByEmail(String email) {
        if (StringUtils.isNullOrEmpty(email)) {
            throw new InvalidRequestException("Contact email for get is null or empty");
        }
        return contactStore.getAllContactsWithEmail(email);
    }
}
