package com.bridgelabz.addressbook.service;

import com.bridgelabz.addressbook.dto.ContactRequest;
import com.bridgelabz.addressbook.model.AddressBookEntry;
import com.bridgelabz.addressbook.model.Contact;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AddressBookDbService {
    List<AddressBookEntry> getAllEntries();

    Optional<Contact> getContactByName(String firstName, String lastName);

    Optional<Contact> updateContactByName(String firstName, String lastName, ContactRequest request);

    List<Contact> getContactsAddedBetween(LocalDateTime start, LocalDateTime end);

    java.util.Map<String, Long> countContactsByCity();

    java.util.Map<String, Long> countContactsByState();

    Optional<Contact> addContactToDb(String addressBookName, ContactRequest request);
}
