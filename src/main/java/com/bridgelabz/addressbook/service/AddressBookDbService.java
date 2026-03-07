package com.bridgelabz.addressbook.service;

import com.bridgelabz.addressbook.dto.ContactRequest;
import com.bridgelabz.addressbook.model.AddressBookEntry;
import com.bridgelabz.addressbook.model.Contact;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface AddressBookDbService {
    List<AddressBookEntry> getAllEntries();

    Optional<Contact> getContactByName(String firstName, String lastName);

    Optional<Contact> updateContactByName(String firstName, String lastName, ContactRequest request);

    List<Contact> getContactsAddedBetween(LocalDateTime start, LocalDateTime end);

    java.util.Map<String, Long> countContactsByCity();

    java.util.Map<String, Long> countContactsByState();

    Optional<Contact> addContactToDb(String addressBookName, ContactRequest request);

    List<Contact> addContactsToDb(String addressBookName, List<ContactRequest> requests);

    CompletableFuture<List<AddressBookEntry>> getAllEntriesAsync();

    CompletableFuture<Optional<Contact>> getContactByNameAsync(String firstName, String lastName);

    CompletableFuture<Optional<Contact>> updateContactByNameAsync(String firstName, String lastName, ContactRequest request);

    CompletableFuture<List<Contact>> getContactsAddedBetweenAsync(LocalDateTime start, LocalDateTime end);

    CompletableFuture<java.util.Map<String, Long>> countContactsByCityAsync();

    CompletableFuture<java.util.Map<String, Long>> countContactsByStateAsync();

    CompletableFuture<Optional<Contact>> addContactToDbAsync(String addressBookName, ContactRequest request);

    CompletableFuture<List<Contact>> addContactsToDbAsync(String addressBookName, List<ContactRequest> requests);
}
