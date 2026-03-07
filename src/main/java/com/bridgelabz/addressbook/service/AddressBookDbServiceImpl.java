package com.bridgelabz.addressbook.service;

import com.bridgelabz.addressbook.dto.ContactRequest;
import com.bridgelabz.addressbook.model.AddressBookEntry;
import com.bridgelabz.addressbook.model.Contact;
import com.bridgelabz.addressbook.repository.AddressBookJdbcRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
 
@Service
public class AddressBookDbServiceImpl implements AddressBookDbService {
    private final AddressBookJdbcRepository repository;
    private final AddressBookService addressBookService;

    public AddressBookDbServiceImpl(AddressBookJdbcRepository repository,
                                    AddressBookService addressBookService) {
        this.repository = repository;
        this.addressBookService = addressBookService;
    }

    @Override
    public List<AddressBookEntry> getAllEntries() {
        return repository.findAllEntries();
    }

    @Override
    public Optional<Contact> getContactByName(String firstName, String lastName) {
        return repository.findContactByName(firstName, lastName);
    }

    @Override
    public Optional<Contact> updateContactByName(String firstName, String lastName, ContactRequest request) {
        Optional<Contact> existing = repository.findContactByName(firstName, lastName);
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        Contact toUpdate = existing.get();
        toUpdate.setAddress(request.getAddress());
        toUpdate.setCity(request.getCity());
        toUpdate.setState(request.getState());
        toUpdate.setZip(request.getZip());
        toUpdate.setPhoneNumber(request.getPhoneNumber());
        toUpdate.setEmail(request.getEmail());

        boolean updated = repository.updateContactByName(firstName, lastName, toUpdate);
        if (!updated) {
            return Optional.empty();
        }
        Optional<Contact> refreshed = repository.findContactByName(firstName, lastName);
        refreshed.ifPresent(addressBookService::syncContactByName);
        return refreshed;
    }

    @Override
    public List<Contact> getContactsAddedBetween(LocalDateTime start, LocalDateTime end) {
        return repository.findContactsAddedBetween(start, end);
    }

    @Override
    public java.util.Map<String, Long> countContactsByCity() {
        return repository.countByCity();
    }

    @Override
    public java.util.Map<String, Long> countContactsByState() {
        return repository.countByState();
    }

    @Override
    @Transactional
    public Optional<Contact> addContactToDb(String addressBookName, ContactRequest request) {
        if (addressBookName == null || addressBookName.isBlank()) {
            return Optional.empty();
        }
        Contact contact = new Contact();
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setAddress(request.getAddress());
        contact.setCity(request.getCity());
        contact.setState(request.getState());
        contact.setZip(request.getZip());
        contact.setPhoneNumber(request.getPhoneNumber());
        contact.setEmail(request.getEmail());

        long addressBookId = repository.findAddressBookIdByName(addressBookName)
                .orElseGet(() -> repository.insertAddressBook(addressBookName));
        if (addressBookId <= 0) {
            return Optional.empty();
        }
        LocalDateTime now = LocalDateTime.now();
        long contactId = repository.insertContact(addressBookId, contact, now);
        if (contactId <= 0) {
            return Optional.empty();
        }
        contact.setId(contactId);
        contact.setDateAdded(now);
        return Optional.of(contact);
    }
}
