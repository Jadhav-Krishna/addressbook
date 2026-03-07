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

    AddContactResult addContact(String name, ContactRequest request);

    Optional<Contact> updateContact(String name, long id, ContactRequest request);

    boolean deleteContact(String name, long id);
}
