package com.bridgelabz.addressbook.service;

import com.bridgelabz.addressbook.dto.ContactRequest;
import com.bridgelabz.addressbook.model.AddressBookEntry;
import com.bridgelabz.addressbook.model.Contact;
import com.bridgelabz.addressbook.repository.AddressBookJdbcRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
 
@Service
public class AddressBookDbServiceImpl implements AddressBookDbService {
    private final AddressBookJdbcRepository repository;
    private final AddressBookService addressBookService;
    private final TransactionTemplate transactionTemplate;

    public AddressBookDbServiceImpl(AddressBookJdbcRepository repository,
                                    AddressBookService addressBookService,
                                    TransactionTemplate transactionTemplate) {
        this.repository = repository;
        this.addressBookService = addressBookService;
        this.transactionTemplate = transactionTemplate;
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

    @Override
    public List<Contact> addContactsToDb(String addressBookName, List<ContactRequest> requests) {
        if (addressBookName == null || addressBookName.isBlank() || requests == null || requests.isEmpty()) {
            return List.of();
        }

        long addressBookId = transactionTemplate.execute(status ->
                repository.findAddressBookIdByName(addressBookName)
                        .orElseGet(() -> repository.insertAddressBook(addressBookName))
        );
        if (addressBookId <= 0) {
            return List.of();
        }

        int poolSize = Math.min(requests.size(), 4);
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        List<Future<Contact>> futures = new ArrayList<>();

        for (ContactRequest request : requests) {
            futures.add(executor.submit(() -> insertContactTransactional(addressBookId, request)));
        }

        List<Contact> created = new ArrayList<>();
        for (Future<Contact> future : futures) {
            try {
                Contact contact = future.get();
                if (contact != null) {
                    created.add(contact);
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException ex) {
                // skip failed insert
            }
        }

        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        return created;
    }

    @Override
    public List<Contact> getContactsByAddressBookName(String addressBookName) {
        if (addressBookName == null || addressBookName.isBlank()) {
            return List.of();
        }
        return repository.findContactsByAddressBookName(addressBookName);
    }

    private Contact insertContactTransactional(long addressBookId, ContactRequest request) {
        return transactionTemplate.execute(status -> {
            Contact contact = new Contact();
            contact.setFirstName(request.getFirstName());
            contact.setLastName(request.getLastName());
            contact.setAddress(request.getAddress());
            contact.setCity(request.getCity());
            contact.setState(request.getState());
            contact.setZip(request.getZip());
            contact.setPhoneNumber(request.getPhoneNumber());
            contact.setEmail(request.getEmail());

            LocalDateTime now = LocalDateTime.now();
            long contactId = repository.insertContact(addressBookId, contact, now);
            if (contactId <= 0) {
                return null;
            }
            contact.setId(contactId);
            contact.setDateAdded(now);
            return contact;
        });
    }
}
