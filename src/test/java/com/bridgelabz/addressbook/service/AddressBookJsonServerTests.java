package com.bridgelabz.addressbook.service;

import com.bridgelabz.addressbook.dto.ContactRequest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AddressBookJsonServerTests {

    private final AddressBookService addressBookService;

    AddressBookJsonServerTests(AddressBookService addressBookService) {
        this.addressBookService = addressBookService;
    }

    @Test
    void loadFromJsonServer_updatesInMemoryAddressBook() {
        String baseUrl = System.getProperty("json.server.url", "http://localhost:3000");
        String endpoint = System.getProperty("json.server.endpoint", "/contacts");

        ContactRequest[] contacts;
        try {
            contacts = RestAssured.given()
                    .baseUri(baseUrl)
                    .when()
                    .get(endpoint)
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(ContactRequest[].class);
        } catch (Exception ex) {
            Assumptions.assumeTrue(false, "JSON server not reachable: " + baseUrl + endpoint);
            return;
        }

        Assumptions.assumeTrue(contacts != null && contacts.length > 0, "No contacts returned from JSON server");

        List<ContactRequest> contactList = Arrays.asList(contacts);
        int loaded = addressBookService.loadAddressBook("JsonServer", contactList);

        assertThat(loaded).isEqualTo(contactList.size());
        assertThat(addressBookService.getContacts("JsonServer"))
                .isPresent()
                .get()
                .hasSize(loaded);
    }
}
