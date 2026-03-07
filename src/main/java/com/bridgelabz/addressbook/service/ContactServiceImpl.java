package com.bridgelabz.addressbook.service;

import com.bridgelabz.addressbook.dto.ContactRequest;
import com.bridgelabz.addressbook.model.Contact;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ContactServiceImpl implements ContactService {
    private final List<Contact> contacts = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public List<Contact> getAllContacts() {
        return new ArrayList<>(contacts);
    }

    @Override
    public Optional<Contact> getContactById(long id) {
        return contacts.stream()
                .filter(contact -> contact.getId() != null && contact.getId() == id)
                .findFirst();
    }

    @Override
    public Contact createContact(ContactRequest request) {
        long id = idGenerator.incrementAndGet();
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
        contacts.add(contact);
        return contact;
    }

    @Override
    public Optional<Contact> updateContact(long id, ContactRequest request) {
        for (int index = 0; index < contacts.size(); index++) {
            Contact existing = contacts.get(index);
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
                contacts.set(index, updated);
                return Optional.of(updated);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteContact(long id) {
        Iterator<Contact> iterator = contacts.iterator();
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
