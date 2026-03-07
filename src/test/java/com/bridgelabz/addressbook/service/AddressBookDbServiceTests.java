package com.bridgelabz.addressbook.service;

import com.bridgelabz.addressbook.dto.ContactRequest;
import com.bridgelabz.addressbook.model.AddressBookEntry;
import com.bridgelabz.addressbook.model.Contact;
import com.bridgelabz.addressbook.repository.AddressBookJdbcRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

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
}