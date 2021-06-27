package com.sszg.atlassianproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sszg.atlassianproject.dao.AccountStore;
import com.sszg.atlassianproject.dao.ContactStore;
import com.sszg.atlassianproject.exception.InvalidRequestException;
import com.sszg.atlassianproject.model.Account;
import com.sszg.atlassianproject.model.Contact;
import com.sszg.atlassianproject.service.AccountService;
import com.sszg.atlassianproject.service.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;
    @Mock
    private AccountStore accountStore;

    private ObjectMapper mapper;

    public Account createAccountFromFile(String fileName) {
        if (mapper == null) {
            mapper = new ObjectMapper();
        }
        // read JSON file and map/convert to java POJO
        try {
            String path = "src/test/resources/dummy_data/" + fileName;
            File file = new File(path);
            return mapper.readValue(file, Account.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @BeforeEach
    public void setup() {
        accountStore = Mockito.mock(AccountStore.class);
        //method that sets something in the db
        doNothing().when(accountStore).saveAccount(any(Account.class));

        accountService = new AccountService(accountStore);
    }

    @Test
    void sanityTest() {
        //method that returns something from the db
        when(accountStore.getAccount(anyString())).thenReturn(createAccountFromFile("DummyAccount1.json"));
        // Random string which will not be used
        var account = accountService.getAccount("rgyyuhu");
        assertNotNull(account);
        assertEquals("Cisco", account.getCompanyName());
        verify(accountStore, times(1)).getAccount("rgyyuhu");
    }

    @DisplayName("Tests getting a valid account")
    @Test
    public void getAccount_success() {
        Account account1 = createAccountFromFile("DummyAccount1.json");
        // method that returns something from the db
        when(accountStore.getAccount(anyString())).thenReturn(account1);
        // Random string which will not be used
        Account account = accountService.getAccount("ewgrk");
        assertNotNull(account);
        assertEquals("Cisco", account.getCompanyName());
        assertEquals("San Jose", account.getCity());
        assertEquals("CA", account.getState());
        assertEquals("95134", account.getPostalCode());
        verify(accountStore, times(1)).getAccount("ewgrk");
    }

    @DisplayName("Tests getting an invalid account and should throw exception")
    @Test
    public void getAccount_exception() {
        // When the contact UID is null or empty it should throw an exception
        assertThrows(InvalidRequestException.class, () -> accountService.getAccount(""));
        assertThrows(InvalidRequestException.class, () -> accountService.getAccount(null));
        verify(accountStore, times(0)).getAccount("");
    }
}
