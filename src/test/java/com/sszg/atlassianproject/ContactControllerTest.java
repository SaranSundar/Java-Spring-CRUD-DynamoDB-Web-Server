package com.sszg.atlassianproject;

import com.sszg.atlassianproject.controller.ContactController;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(ContactController.class)
public class ContactControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper mapper;
//
//    @MockBean
//    private ContactStore contactStore;
//
//    @InjectMocks
//    ContactController contactController;
//
//    @Before
//    public void before() {
//        mockMvc = MockMvcBuilders.standaloneSetup(contactController)
//                .build();
//    }
//
//    @Test
//    public void getContact_success() throws Exception {
//        Contact contact = createContactFromFile();
//
//        Mockito.when(contactStore.getContact(Mockito.anyString())).thenReturn(contact);
//
//        mockMvc.perform(MockMvcRequestBuilders
//                .get("/contact")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }

}
