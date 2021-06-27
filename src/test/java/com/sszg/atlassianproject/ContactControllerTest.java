package com.sszg.atlassianproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sszg.atlassianproject.controller.ContactController;
import com.sszg.atlassianproject.dao.ContactStore;
import com.sszg.atlassianproject.model.Contact;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.io.IOException;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactController.class)
public class ContactControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ContactStore contactStore;

    public Contact createContactFromFile() {
        // read JSON file and map/convert to java POJO
        try {
            String clsPath = new ClassPathResource("dummy_data/DummyContact1.json").getPath();
            Contact contact = mapper.readValue(new File(clsPath), Contact.class);
            System.out.println(contact);
            return contact;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Test
    public void getContact_success() throws Exception {
        Contact contact = createContactFromFile();

        Mockito.when(contactStore.getContact(Mockito.anyString())).thenReturn(contact);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/contact")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
