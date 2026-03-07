package com.bridgelabz.addressbook.service;

import com.bridgelabz.addressbook.model.AddressBookEntry;
import com.bridgelabz.addressbook.repository.AddressBookJdbcRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import({AddressBookDbServiceImpl.class, AddressBookJdbcRepository.class})
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
}