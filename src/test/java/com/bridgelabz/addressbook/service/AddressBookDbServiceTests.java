package com.bridgelabz.addressbook.service;

import com.bridgelabz.addressbook.dto.ContactRequest;
import com.bridgelabz.addressbook.model.AddressBookEntry;
import com.bridgelabz.addressbook.model.Contact;
import com.bridgelabz.addressbook.repository.AddressBookJdbcRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({AddressBookDbServiceImpl.class, AddressBookJdbcRepository.class, AddressBookServiceImpl.class})
class AddressBookDbServiceTests {

    private final AddressBookDbService dbService;

    AddressBookDbServiceTests(AddressBookDbService dbService) {
        this.dbService = dbService;
    }

    @Test
    void getAllEntries_returnsSeededRows() {
        List<AddressBookEntry> entries = dbService.getAllEntries();
        assertThat(entries).isNotEmpty();
        assertThat(entries).anyMatch(entry -> "Personal".equals(entry.getAddressBookName()));
        assertThat(entries).anyMatch(entry -> "Ada".equals(entry.getFirstName()));
    }

    @Test
    void getContactByName_returnsPerson() {
        Optional<Contact> contact = dbService.getContactByName("Ada", "Lovelace");

        assertThat(contact).isPresent();
        assertThat(contact.get().getCity()).isEqualTo("Pune");
    }

    @Test
    void updateContactByName_updatesDbAndReturnsContact() {
        ContactRequest update = new ContactRequest();
        update.setAddress("99 New St");
        update.setCity("Delhi");
        update.setState("DL");
        update.setZip("110001");
        update.setPhoneNumber("7777777777");
        update.setEmail("ada.new@example.com");

        Optional<Contact> updated = dbService.updateContactByName("Ada", "Lovelace", update);

        assertThat(updated).isPresent();
        assertThat(updated.get().getCity()).isEqualTo("Delhi");
        assertThat(updated.get().getEmail()).isEqualTo("ada.new@example.com");
    }

    @Test
    void getContactsAddedBetween_returnsMatchingRows() {
        LocalDateTime start = LocalDateTime.parse("2026-03-01T00:00:00");
        LocalDateTime end = LocalDateTime.parse("2026-03-03T23:59:59");

        List<Contact> contacts = dbService.getContactsAddedBetween(start, end);

        assertThat(contacts).isNotEmpty();
        assertThat(contacts).allMatch(contact -> "Ada".equals(contact.getFirstName()));
    }

    @Test
    void countContactsByCity_returnsCounts() {
        Map<String, Long> counts = dbService.countContactsByCity();

        assertThat(counts).containsEntry("Pune", 1L);
        assertThat(counts).containsEntry("Mumbai", 1L);
    }

    @Test
    void countContactsByState_returnsCounts() {
        Map<String, Long> counts = dbService.countContactsByState();

        assertThat(counts).containsEntry("MH", 2L);
    }

    @Test
    void addContactToDb_createsContactAndReturnsId() {
        ContactRequest request = new ContactRequest();
        request.setFirstName("Alan");
        request.setLastName("Turing");
        request.setAddress("3 Main St");
        request.setCity("Pune");
        request.setState("MH");
        request.setZip("411002");
        request.setPhoneNumber("6666666666");
        request.setEmail("alan@example.com");

        Optional<Contact> created = dbService.addContactToDb("Personal", request);

        assertThat(created).isPresent();
        assertThat(created.get().getId()).isNotNull();
        assertThat(created.get().getDateAdded()).isNotNull();
    }

    @Test
    void addContactsToDb_insertsMultipleContacts() {
        List<ContactRequest> requests = new ArrayList<>();

        ContactRequest first = new ContactRequest();
        first.setFirstName("Katherine");
        first.setLastName("Johnson");
        first.setAddress("4 Main St");
        first.setCity("Pune");
        first.setState("MH");
        first.setZip("411003");
        first.setPhoneNumber("5555555555");
        first.setEmail("kj@example.com");

        ContactRequest second = new ContactRequest();
        second.setFirstName("Tim");
        second.setLastName("Berners-Lee");
        second.setAddress("5 Main St");
        second.setCity("Pune");
        second.setState("MH");
        second.setZip("411004");
        second.setPhoneNumber("4444444444");
        second.setEmail("tbl@example.com");

        requests.add(first);
        requests.add(second);

        List<Contact> created = dbService.addContactsToDb("Personal", requests);

        assertThat(created).hasSize(2);
        assertThat(created).allMatch(contact -> contact.getId() != null);
    }
}