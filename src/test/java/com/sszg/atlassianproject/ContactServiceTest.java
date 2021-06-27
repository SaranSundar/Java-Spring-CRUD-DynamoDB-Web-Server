package com.sszg.atlassianproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sszg.atlassianproject.dao.ContactStore;
import com.sszg.atlassianproject.exception.InvalidRequestException;
import com.sszg.atlassianproject.model.Contact;
import com.sszg.atlassianproject.service.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ContactServiceTest {

    @InjectMocks
    private ContactService contactService;
    @Mock
    private ContactStore contactStore;

    private ObjectMapper mapper;

    public Contact createContactFromFile(String fileName) {
        if (mapper == null) {
            mapper = new ObjectMapper();
        }
        // read JSON file and map/convert to java POJO
        try {
            String path = "src/test/resources/dummy_data/" + fileName;
            File file = new File(path);
            return mapper.readValue(file, Contact.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @BeforeEach
    public void setup() {
        contactStore = Mockito.mock(ContactStore.class);
        //method that sets something in the db
        doNothing().when(contactStore).saveContact(any(Contact.class));

        contactService = new ContactService(contactStore);
    }

    @Test
    void sanityTest() {
        //method that returns something from the db
        when(contactStore.getContact(anyString())).thenReturn(createContactFromFile("DummyContact1.json"));
        // Random string which will not be used
        var contact = contactService.getContact("ewfwefm");
        assertNotNull(contact);
        assertEquals("John Mulaney", contact.getName());
    }

    @Test
    public void getContact_success() {
        Contact contact1 = createContactFromFile("DummyContact1.json");
        // method that returns something from the db
        when(contactStore.getContact(anyString())).thenReturn(contact1);
        // Random string which will not be used
        Contact contact = contactService.getContact("324324");
        assertNotNull(contact);
        assertEquals("John Mulaney", contact.getName());
        assertEquals(contact.getEmailAddress(), "JohnMulaney@gmail.com");
        assertEquals(contact.getAddressLine1(), "77 E 4th St");
        assertEquals(contact.getPostalCode(), "10003");
    }


    @Test
    public void getContact_exception() {
        // When the contact UID is null or empty it should throw an exception
        Throwable throwable = assertThrows(Throwable.class, () -> contactService.getContact(""));
        assertEquals(InvalidRequestException.class, throwable.getClass());
        Throwable throwable2 = assertThrows(Throwable.class, () -> contactService.getContact(null));
        assertEquals(InvalidRequestException.class, throwable2.getClass());
    }
}

//    public void postContact(Contact contact) {
//        if (contact.getUid() != null) {
//            throw new InvalidRequestException("Contact for POST should not provide UID");
//        }
//        contactStore.saveContact(contact);
//    }
//
//    public void putContact(Contact contact) {
//        if (StringUtils.isNullOrEmpty(contact.getUid())) {
//            throw new InvalidRequestException("Contact for PUT should provide a UID");
//        }
//        getContact(contact.getUid());
//        // We try to get contact before saving new contact because, if it fails to get it will throw a ItemNotFoundException
//        contactStore.saveContact(contact);
//    }
//
//    public Contact deleteContact(String uid) {
//        if (StringUtils.isNullOrEmpty(uid)) {
//            throw new InvalidRequestException("Contact UID for GET should provide a UID");
//        }
//        return contactStore.deleteContact(uid);
//    }
//
//    public List<Contact> getContactsByEmail(String email) {
//        if (StringUtils.isNullOrEmpty(email)) {
//            throw new InvalidRequestException("Contact email for GET should provide a UID");
//        }
//        return contactStore.getAllContactsWithEmail(email);
//    }
