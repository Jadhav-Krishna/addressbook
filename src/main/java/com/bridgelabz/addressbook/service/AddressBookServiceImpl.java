package com.bridgelabz.addressbook.service;

import com.bridgelabz.addressbook.dto.ContactRequest;
import com.bridgelabz.addressbook.model.Contact;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AddressBookServiceImpl implements AddressBookService {
    private static final class AddressBookStore {
        private final List<Contact> contacts = new ArrayList<>();
        private final AtomicLong idGenerator = new AtomicLong(0);
    }

    private final Map<String, AddressBookStore> addressBooks = new ConcurrentHashMap<>();

    @Override
    public boolean createAddressBook(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }
        return addressBooks.putIfAbsent(name, new AddressBookStore()) == null;
    }

    @Override
    public List<String> getAllAddressBookNames() {
        List<String> names = new ArrayList<>(addressBooks.keySet());
        Collections.sort(names);
        return names;
    }

    @Override
    public Optional<List<Contact>> getContacts(String name) {
        AddressBookStore store = addressBooks.get(name);
        if (store == null) {
            return Optional.empty();
        }
        return Optional.of(new ArrayList<>(store.contacts));
    }

    @Override
    public Optional<Contact> addContact(String name, ContactRequest request) {
        AddressBookStore store = addressBooks.get(name);
        if (store == null) {
            return Optional.empty();
        }
        long id = store.idGenerator.incrementAndGet();
        Contact contact = new Contact(
                id,
                request.getFirstName(),
                request.getLastName(),
                request.getAddress(),
                request.getCity(),
                request.getState(),
                request.getZip(),
                request.getPhoneNumber(),
                request.getEmail()
        );
        store.contacts.add(contact);
        return Optional.of(contact);
    }

    @Override
    public Optional<Contact> updateContact(String name, long id, ContactRequest request) {
        AddressBookStore store = addressBooks.get(name);
        if (store == null) {
            return Optional.empty();
        }
        for (int index = 0; index < store.contacts.size(); index++) {
            Contact existing = store.contacts.get(index);
            if (existing.getId() != null && existing.getId() == id) {
                Contact updated = new Contact(
                        id,
                        request.getFirstName(),
                        request.getLastName(),
                        request.getAddress(),
                        request.getCity(),
                        request.getState(),
                        request.getZip(),
                        request.getPhoneNumber(),
                        request.getEmail()
                );
                store.contacts.set(index, updated);
                return Optional.of(updated);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteContact(String name, long id) {
        AddressBookStore store = addressBooks.get(name);
        if (store == null) {
            return false;
        }
        Iterator<Contact> iterator = store.contacts.iterator();
        while (iterator.hasNext()) {
            Contact contact = iterator.next();
            if (contact.getId() != null && contact.getId() == id) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }
}
