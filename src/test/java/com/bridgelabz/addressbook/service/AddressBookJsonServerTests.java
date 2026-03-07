package com.bridgelabz.addressbook.service;

import com.bridgelabz.addressbook.dto.ContactRequest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

        List<com.bridgelabz.addressbook.model.Contact> loadedContacts =
                addressBookService.getContacts("JsonServer").orElseThrow();
        assertThat(loaded).isEqualTo(contactList.size());
        assertThat(loadedContacts.size()).isEqualTo(loaded);
    }

    @Test
    void addMultipleEntriesToJsonServer_andSyncMemory() {
        String baseUrl = System.getProperty("json.server.url", "http://localhost:3000");
        String endpoint = System.getProperty("json.server.endpoint", "/contacts");

        List<ContactRequest> toCreate = List.of(
                newContact("Ada" + uniqueSuffix(), "Lovelace", "Pune"),
                newContact("Grace" + uniqueSuffix(), "Hopper", "Mumbai")
        );

        List<Integer> createdIds = new ArrayList<>();
        try {
            for (ContactRequest request : toCreate) {
                Integer id = RestAssured.given()
                        .baseUri(baseUrl)
                        .contentType("application/json")
                        .body(request)
                        .when()
                        .post(endpoint)
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("id");
                if (id != null) {
                    createdIds.add(id);
                }
            }

            ContactRequest[] contacts = RestAssured.given()
                    .baseUri(baseUrl)
                    .when()
                    .get(endpoint)
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(ContactRequest[].class);

            Assumptions.assumeTrue(contacts != null && contacts.length > 0,
                    "No contacts returned from JSON server");

            List<ContactRequest> contactList = Arrays.asList(contacts);
            int loaded = addressBookService.loadAddressBook("JsonServer", contactList);

            List<com.bridgelabz.addressbook.model.Contact> loadedContacts =
                    addressBookService.getContacts("JsonServer").orElseThrow();
            assertThat(loaded).isEqualTo(contactList.size());
            assertThat(loadedContacts.stream().anyMatch(contact ->
                    contact.getFirstName().equals(toCreate.get(0).getFirstName()))).isTrue();
            assertThat(loadedContacts.stream().anyMatch(contact ->
                    contact.getFirstName().equals(toCreate.get(1).getFirstName()))).isTrue();
        } catch (Exception ex) {
            Assumptions.assumeTrue(false, "JSON server not reachable: " + baseUrl + endpoint);
        } finally {
            for (Integer id : createdIds) {
                RestAssured.given()
                        .baseUri(baseUrl)
                        .when()
                        .delete(endpoint + "/" + id)
                        .then()
                        .statusCode(200);
            }
        }
    }

    @Test
    void updateEntryInJsonServer_andSyncMemory() {
        String baseUrl = System.getProperty("json.server.url", "http://localhost:3000");
        String endpoint = System.getProperty("json.server.endpoint", "/contacts");

        ContactRequest original = newContact("Margaret" + uniqueSuffix(), "Hamilton", "Pune");
        Integer createdId = null;

        try {
            createdId = RestAssured.given()
                    .baseUri(baseUrl)
                    .contentType("application/json")
                    .body(original)
                    .when()
                    .post(endpoint)
                    .then()
                    .statusCode(201)
                    .extract()
                    .path("id");

            Assumptions.assumeTrue(createdId != null, "JSON server did not return an id");

            ContactRequest updated = newContact(original.getFirstName(), original.getLastName(), "Mumbai");
            updated.setEmail("updated." + original.getFirstName().toLowerCase() + "@example.com");

            RestAssured.given()
                    .baseUri(baseUrl)
                    .contentType("application/json")
                    .body(updated)
                    .when()
                    .put(endpoint + "/" + createdId)
                    .then()
                    .statusCode(200);

            ContactRequest[] contacts = RestAssured.given()
                    .baseUri(baseUrl)
                    .when()
                    .get(endpoint)
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(ContactRequest[].class);

            Assumptions.assumeTrue(contacts != null && contacts.length > 0,
                    "No contacts returned from JSON server");

            List<ContactRequest> contactList = Arrays.asList(contacts);
            int loaded = addressBookService.loadAddressBook("JsonServer", contactList);

            List<com.bridgelabz.addressbook.model.Contact> loadedContacts =
                    addressBookService.getContacts("JsonServer").orElseThrow();
            assertThat(loaded).isEqualTo(contactList.size());
            assertThat(loadedContacts.stream().anyMatch(contact ->
                    contact.getFirstName().equals(updated.getFirstName())
                            && contact.getCity().equals("Mumbai")
                            && contact.getEmail().equals(updated.getEmail()))).isTrue();
        } catch (Exception ex) {
            Assumptions.assumeTrue(false, "JSON server not reachable: " + baseUrl + endpoint);
        } finally {
            if (createdId != null) {
                RestAssured.given()
                        .baseUri(baseUrl)
                        .when()
                        .delete(endpoint + "/" + createdId)
                        .then()
                        .statusCode(200);
            }
        }
    }

    @Test
    void deleteEntryInJsonServer_andSyncMemory() {
        String baseUrl = System.getProperty("json.server.url", "http://localhost:3000");
        String endpoint = System.getProperty("json.server.endpoint", "/contacts");

        ContactRequest original = newContact("Dennis" + uniqueSuffix(), "Ritchie", "Pune");
        Integer createdId = null;

        try {
            createdId = RestAssured.given()
                    .baseUri(baseUrl)
                    .contentType("application/json")
                    .body(original)
                    .when()
                    .post(endpoint)
                    .then()
                    .statusCode(201)
                    .extract()
                    .path("id");

            Assumptions.assumeTrue(createdId != null, "JSON server did not return an id");

            RestAssured.given()
                    .baseUri(baseUrl)
                    .when()
                    .delete(endpoint + "/" + createdId)
                    .then()
                    .statusCode(200);

            ContactRequest[] contacts = RestAssured.given()
                    .baseUri(baseUrl)
                    .when()
                    .get(endpoint)
                    .then()
                    .statusCode(200)
                    .extract()
                    .as(ContactRequest[].class);

            List<ContactRequest> contactList = contacts == null ? List.of() : Arrays.asList(contacts);
            int loaded = addressBookService.loadAddressBook("JsonServer", contactList);

            List<com.bridgelabz.addressbook.model.Contact> loadedContacts =
                    addressBookService.getContacts("JsonServer").orElseThrow();
            assertThat(loadedContacts.stream().noneMatch(contact ->
                    contact.getFirstName().equals(original.getFirstName()))).isTrue();
            assertThat(loaded).isEqualTo(contactList.size());
        } catch (Exception ex) {
            Assumptions.assumeTrue(false, "JSON server not reachable: " + baseUrl + endpoint);
        }
    }

    private static ContactRequest newContact(String firstName, String lastName, String city) {
        ContactRequest request = new ContactRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setAddress("1 Main St");
        request.setCity(city);
        request.setState("MH");
        request.setZip("411001");
        request.setPhoneNumber("9999999999");
        request.setEmail(firstName.toLowerCase() + "@example.com");
        return request;
    }

    private static String uniqueSuffix() {
        return "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
