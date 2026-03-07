package com.bridgelabz.addressbook.service;

import com.bridgelabz.addressbook.dto.ContactRequest;
import com.bridgelabz.addressbook.model.Contact;

import java.util.List;
import java.util.Optional;

public interface ContactService {
    List<Contact> getAllContacts();

    Optional<Contact> getContactById(long id);

    Contact createContact(ContactRequest request);

    Optional<Contact> updateContact(long id, ContactRequest request);

    Optional<Contact> updateContactByFirstName(String firstName, ContactRequest request);

    boolean deleteContact(long id);

    boolean deleteContactByFirstName(String firstName);
}
