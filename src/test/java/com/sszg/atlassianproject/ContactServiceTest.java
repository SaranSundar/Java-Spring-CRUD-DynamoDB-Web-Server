package com.sszg.atlassianproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sszg.atlassianproject.dao.ContactStore;
import com.sszg.atlassianproject.model.Contact;
import com.sszg.atlassianproject.service.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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


}
