package com.bridgelabz.addressbook.service;

import com.bridgelabz.addressbook.dto.AddContactResult;
import com.bridgelabz.addressbook.dto.ContactRequest;
import com.bridgelabz.addressbook.model.Contact;

import java.util.List;
import java.util.Optional;

public interface AddressBookService {
    boolean createAddressBook(String name);

    List<String> getAllAddressBookNames();

    Optional<List<Contact>> getContacts(String name);

    Optional<List<Contact>> getContactsSortedByName(String name);

    Optional<List<Contact>> getContactsSortedByCity(String name);

    Optional<List<Contact>> getContactsSortedByState(String name);

    Optional<List<Contact>> getContactsSortedByZip(String name);

    AddContactResult addContact(String name, ContactRequest request);

    Optional<Contact> updateContact(String name, long id, ContactRequest request);

    boolean deleteContact(String name, long id);

    List<Contact> searchByCity(String city);

    List<Contact> searchByState(String state);

    java.util.Map<String, Long> countByCity();

    java.util.Map<String, Long> countByState();

    boolean exportAddressBook(String name, String filePath);

    int importAddressBook(String name, String filePath);

    boolean exportAddressBookJson(String name, String filePath);

    int importAddressBookJson(String name, String filePath);

    boolean syncContactByName(Contact contact);
}
