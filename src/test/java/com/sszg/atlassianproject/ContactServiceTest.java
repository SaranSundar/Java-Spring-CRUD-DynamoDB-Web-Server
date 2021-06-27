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
import static org.mockito.Mockito.*;

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
        verify(contactStore, times(1)).getContact("ewfwefm");
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
        verify(contactStore, times(1)).getContact("324324");
    }


    @Test
    public void getContact_exception() {
        // When the contact UID is null or empty it should throw an exception
        Throwable throwable = assertThrows(Throwable.class, () -> contactService.getContact(""));
        assertEquals(InvalidRequestException.class, throwable.getClass());
        Throwable throwable2 = assertThrows(Throwable.class, () -> contactService.getContact(null));
        assertEquals(InvalidRequestException.class, throwable2.getClass());
        verify(contactStore, times(0)).getContact("");
    }

    @Test
    public void postContact_success() {
        Contact trevorNoah = createContactFromFile("DummyContact2.json");
        contactService.postContact(trevorNoah);
        assertEquals(trevorNoah.getName(), "Trevor Noah");
        verify(contactStore, times(1)).saveContact(trevorNoah);
    }

    @Test
    public void postContact_exception() {
        Contact trevorNoah = createContactFromFile("DummyContact2.json");
        trevorNoah.setUid("weewvewv");
        //Should not provide UID on post
        Throwable throwable = assertThrows(Throwable.class, () -> contactService.postContact(trevorNoah));
        assertEquals(InvalidRequestException.class, throwable.getClass());
        verify(contactStore, times(0)).saveContact(trevorNoah);
    }

    @Test
    public void putContact_success() {
        Contact trevorNoah = createContactFromFile("DummyContact2.json");
        trevorNoah.setUid("34gt45t");
        contactService.putContact(trevorNoah);
        verify(contactStore, times(1)).getContact(trevorNoah.getUid());
        assertEquals(trevorNoah.getName(), "Trevor Noah");
        trevorNoah.setName("#TheRealTrevorNoah");
        contactService.putContact(trevorNoah);
        verify(contactStore, times(2)).getContact(trevorNoah.getUid());
        assertEquals(trevorNoah.getName(), "#TheRealTrevorNoah");
    }

    @Test
    public void putContact_exception() {
        Contact trevorNoah = createContactFromFile("DummyContact2.json");
        assertThrows(InvalidRequestException.class, () -> contactService.putContact(trevorNoah));
        verify(contactStore, times(0)).getContact(trevorNoah.getUid());
        assertEquals(trevorNoah.getName(), "Trevor Noah");
    }
}
