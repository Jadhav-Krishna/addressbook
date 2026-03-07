package com.bridgelabz.addressbook.service;

import com.bridgelabz.addressbook.dto.AddContactResult;
import com.bridgelabz.addressbook.dto.AddContactStatus;
import com.bridgelabz.addressbook.dto.ContactRequest;
import com.bridgelabz.addressbook.model.Contact;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class AddressBookServiceImpl implements AddressBookService {
    private static final Gson GSON = new Gson();
    private static final class AddressBookStore {
        private final List<Contact> contacts = new ArrayList<>();
        private final AtomicLong idGenerator = new AtomicLong(0);
    }

    private final Map<String, AddressBookStore> addressBooks = new ConcurrentHashMap<>();
    private final Map<String, List<Contact>> cityIndex = new ConcurrentHashMap<>();
    private final Map<String, List<Contact>> stateIndex = new ConcurrentHashMap<>();

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
    public Optional<List<Contact>> getContactsSortedByName(String name) {
        AddressBookStore store = addressBooks.get(name);
        if (store == null) {
            return Optional.empty();
        }
        List<Contact> sorted = new ArrayList<>(store.contacts);
        Comparator<Contact> nameComparator = Comparator
                .comparing(Contact::getFirstName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                .thenComparing(Contact::getLastName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
        Collections.sort(sorted, nameComparator);
        return Optional.of(sorted);
    }

    @Override
    public Optional<List<Contact>> getContactsSortedByCity(String name) {
        AddressBookStore store = addressBooks.get(name);
        if (store == null) {
            return Optional.empty();
        }
        List<Contact> sorted = store.contacts.stream()
                .sorted(Comparator.comparing(Contact::getCity, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
        return Optional.of(new ArrayList<>(sorted));
    }

    @Override
    public Optional<List<Contact>> getContactsSortedByState(String name) {
        AddressBookStore store = addressBooks.get(name);
        if (store == null) {
            return Optional.empty();
        }
        List<Contact> sorted = store.contacts.stream()
                .sorted(Comparator.comparing(Contact::getState, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
        return Optional.of(new ArrayList<>(sorted));
    }

    @Override
    public Optional<List<Contact>> getContactsSortedByZip(String name) {
        AddressBookStore store = addressBooks.get(name);
        if (store == null) {
            return Optional.empty();
        }
        List<Contact> sorted = store.contacts.stream()
                .sorted(Comparator.comparing(Contact::getZip, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
        return Optional.of(new ArrayList<>(sorted));
    }

    @Override
    public AddContactResult addContact(String name, ContactRequest request) {
        AddressBookStore store = addressBooks.get(name);
        if (store == null) {
            return new AddContactResult(AddContactStatus.ADDRESS_BOOK_NOT_FOUND, null);
        }
        Contact candidate = new Contact(
                null,
                request.getFirstName(),
                request.getLastName(),
                request.getAddress(),
                request.getCity(),
                request.getState(),
                request.getZip(),
                request.getPhoneNumber(),
                request.getEmail()
        );
        boolean duplicate = store.contacts.stream().anyMatch(candidate::equals);
        if (duplicate) {
            return new AddContactResult(AddContactStatus.DUPLICATE_NAME, null);
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
        addToIndex(cityIndex, contact.getCity(), contact);
        addToIndex(stateIndex, contact.getState(), contact);
        return new AddContactResult(AddContactStatus.CREATED, contact);
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
                removeFromIndex(cityIndex, existing.getCity(), existing);
                removeFromIndex(stateIndex, existing.getState(), existing);
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
                addToIndex(cityIndex, updated.getCity(), updated);
                addToIndex(stateIndex, updated.getState(), updated);
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
                removeFromIndex(cityIndex, contact.getCity(), contact);
                removeFromIndex(stateIndex, contact.getState(), contact);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Contact> searchByCity(String city) {
        List<Contact> matches = cityIndex.get(city);
        if (matches == null) {
            return List.of();
        }
        return new ArrayList<>(matches);
    }

    @Override
    public List<Contact> searchByState(String state) {
        List<Contact> matches = stateIndex.get(state);
        if (matches == null) {
            return List.of();
        }
        return new ArrayList<>(matches);
    }

    @Override
    public Map<String, Long> countByCity() {
        return addressBooks.values().stream()
                .flatMap(store -> store.contacts.stream())
                .filter(contact -> contact.getCity() != null && !contact.getCity().isBlank())
                .collect(Collectors.groupingBy(Contact::getCity, Collectors.counting()));
    }

    @Override
    public Map<String, Long> countByState() {
        return addressBooks.values().stream()
                .flatMap(store -> store.contacts.stream())
                .filter(contact -> contact.getState() != null && !contact.getState().isBlank())
                .collect(Collectors.groupingBy(Contact::getState, Collectors.counting()));
    }

    @Override
    public boolean exportAddressBook(String name, String filePath) {
        AddressBookStore store = addressBooks.get(name);
        if (store == null || filePath == null || filePath.isBlank()) {
            return false;
        }
        Path path = Path.of(filePath);
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
             CSVWriter csvWriter = new CSVWriter(writer)) {
            csvWriter.writeNext(new String[] {
                    "firstName",
                    "lastName",
                    "address",
                    "city",
                    "state",
                    "zip",
                    "phoneNumber",
                    "email"
            });
            for (Contact contact : store.contacts) {
                csvWriter.writeNext(toCsvRow(contact));
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public int importAddressBook(String name, String filePath) {
        AddressBookStore store = addressBooks.get(name);
        if (store == null || filePath == null || filePath.isBlank()) {
            return 0;
        }
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return 0;
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReader(reader)) {
            String[] row = csvReader.readNext();
            if (row == null) {
                return 0;
            }
            int added = 0;
            while ((row = csvReader.readNext()) != null) {
                ContactRequest request = parseCsvRow(row);
                if (request == null) {
                    continue;
                }
                AddContactResult result = addContact(name, request);
                if (result.getStatus() == AddContactStatus.CREATED) {
                    added++;
                }
            }
            return added;
        } catch (Exception ex) {
            return 0;
        }
    }

    @Override
    public boolean exportAddressBookJson(String name, String filePath) {
        AddressBookStore store = addressBooks.get(name);
        if (store == null || filePath == null || filePath.isBlank()) {
            return false;
        }
        Path path = Path.of(filePath);
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            GSON.toJson(store.contacts, writer);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public int importAddressBookJson(String name, String filePath) {
        AddressBookStore store = addressBooks.get(name);
        if (store == null || filePath == null || filePath.isBlank()) {
            return 0;
        }
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return 0;
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            ContactRequest[] requests = GSON.fromJson(reader, ContactRequest[].class);
            if (requests == null || requests.length == 0) {
                return 0;
            }
            int added = 0;
            for (ContactRequest request : requests) {
                if (request == null) {
                    continue;
                }
                AddContactResult result = addContact(name, request);
                if (result.getStatus() == AddContactStatus.CREATED) {
                    added++;
                }
            }
            return added;
        } catch (JsonSyntaxException ex) {
            return 0;
        } catch (Exception ex) {
            return 0;
        }
    }

    @Override
    public boolean syncContactByName(Contact contact) {
        if (contact == null) {
            return false;
        }
        for (AddressBookStore store : addressBooks.values()) {
            for (int index = 0; index < store.contacts.size(); index++) {
                Contact existing = store.contacts.get(index);
                if (contact.equals(existing)) {
                    removeFromIndex(cityIndex, existing.getCity(), existing);
                    removeFromIndex(stateIndex, existing.getState(), existing);
                    Contact updated = new Contact(
                            existing.getId(),
                            contact.getFirstName(),
                            contact.getLastName(),
                            contact.getAddress(),
                            contact.getCity(),
                            contact.getState(),
                            contact.getZip(),
                            contact.getPhoneNumber(),
                            contact.getEmail()
                    );
                    store.contacts.set(index, updated);
                    addToIndex(cityIndex, updated.getCity(), updated);
                    addToIndex(stateIndex, updated.getState(), updated);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int loadAddressBook(String name, List<ContactRequest> requests) {
        if (name == null || name.isBlank() || requests == null) {
            return 0;
        }
        AddressBookStore store = addressBooks.computeIfAbsent(name, value -> new AddressBookStore());
        for (Contact existing : store.contacts) {
            removeFromIndex(cityIndex, existing.getCity(), existing);
            removeFromIndex(stateIndex, existing.getState(), existing);
        }
        store.contacts.clear();
        store.idGenerator.set(0);

        int added = 0;
        for (ContactRequest request : requests) {
            if (request == null) {
                continue;
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
            addToIndex(cityIndex, contact.getCity(), contact);
            addToIndex(stateIndex, contact.getState(), contact);
            added++;
        }
        return added;
    }

    private String[] toCsvRow(Contact contact) {
        return new String[] {
                nullToEmpty(contact.getFirstName()),
                nullToEmpty(contact.getLastName()),
                nullToEmpty(contact.getAddress()),
                nullToEmpty(contact.getCity()),
                nullToEmpty(contact.getState()),
                nullToEmpty(contact.getZip()),
                nullToEmpty(contact.getPhoneNumber()),
                nullToEmpty(contact.getEmail())
        };
    }

    private ContactRequest parseCsvRow(String[] row) {
        if (row == null || row.length < 8) {
            return null;
        }
        ContactRequest request = new ContactRequest();
        request.setFirstName(row[0]);
        request.setLastName(row[1]);
        request.setAddress(row[2]);
        request.setCity(row[3]);
        request.setState(row[4]);
        request.setZip(row[5]);
        request.setPhoneNumber(row[6]);
        request.setEmail(row[7]);
        return request;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private void addToIndex(Map<String, List<Contact>> index, String key, Contact contact) {
        if (key == null || key.isBlank()) {
            return;
        }
        index.computeIfAbsent(key, value -> new ArrayList<>()).add(contact);
    }

    private void removeFromIndex(Map<String, List<Contact>> index, String key, Contact contact) {
        if (key == null || key.isBlank()) {
            return;
        }
        List<Contact> contacts = index.get(key);
        if (contacts == null) {
            return;
        }
        Long id = contact.getId();
        contacts.removeIf(existing -> id != null && id.equals(existing.getId()));
        if (contacts.isEmpty()) {
            index.remove(key);
        }
    }
}
