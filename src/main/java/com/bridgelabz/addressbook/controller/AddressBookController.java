package com.bridgelabz.addressbook.controller;

import com.bridgelabz.addressbook.dto.AddContactResult;
import com.bridgelabz.addressbook.dto.AddContactStatus;
import com.bridgelabz.addressbook.dto.ApiResponse;
import com.bridgelabz.addressbook.dto.ContactRequest;
import com.bridgelabz.addressbook.model.AddressBookEntry;
import com.bridgelabz.addressbook.model.Contact;
import com.bridgelabz.addressbook.service.AddressBookService;
import com.bridgelabz.addressbook.service.AddressBookDbService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/address-books")
public class AddressBookController {
    private final AddressBookService addressBookService;
    private final AddressBookDbService addressBookDbService;

    public AddressBookController(AddressBookService addressBookService,
                                 AddressBookDbService addressBookDbService) {
        this.addressBookService = addressBookService;
        this.addressBookDbService = addressBookDbService;
    }

    @PostMapping("/{name}")
    public ResponseEntity<Void> createAddressBook(@PathVariable String name) {
        boolean created = addressBookService.createAddressBook(name);
        if (created) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @GetMapping
    public List<String> getAllAddressBooks() {
        return addressBookService.getAllAddressBookNames();
    }

    @GetMapping("/{name}/contacts")
    public ResponseEntity<List<Contact>> getContacts(@PathVariable String name) {
        return addressBookService.getContacts(name)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{name}/contacts/sorted-by-name")
    public ResponseEntity<List<Contact>> getContactsSortedByName(@PathVariable String name) {
        return addressBookService.getContactsSortedByName(name)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{name}/contacts/sorted-by-city")
    public ResponseEntity<List<Contact>> getContactsSortedByCity(@PathVariable String name) {
        return addressBookService.getContactsSortedByCity(name)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{name}/contacts/sorted-by-state")
    public ResponseEntity<List<Contact>> getContactsSortedByState(@PathVariable String name) {
        return addressBookService.getContactsSortedByState(name)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{name}/contacts/sorted-by-zip")
    public ResponseEntity<List<Contact>> getContactsSortedByZip(@PathVariable String name) {
        return addressBookService.getContactsSortedByZip(name)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/{name}/contacts")
    public ResponseEntity<?> addContact(
            @PathVariable String name,
            @RequestBody ContactRequest request) {
        AddContactResult result = addressBookService.addContact(name, request);
        if (result.getStatus() == AddContactStatus.CREATED) {
            return ResponseEntity.status(HttpStatus.CREATED).body(result.getContact());
        }
        if (result.getStatus() == AddContactStatus.DUPLICATE_NAME) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse("Duplicate person name in address book", Instant.now().toString()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse("Address book not found", Instant.now().toString()));
    }

    @PutMapping("/{name}/contacts/{id}")
    public ResponseEntity<Contact> updateContact(
            @PathVariable String name,
            @PathVariable long id,
            @RequestBody ContactRequest request) {
        return addressBookService.updateContact(name, id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{name}/contacts/{id}")
    public ResponseEntity<Void> deleteContact(
            @PathVariable String name,
            @PathVariable long id) {
        boolean deleted = addressBookService.deleteContact(name, id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/search/city/{city}")
    public List<Contact> searchByCity(@PathVariable String city) {
        return addressBookService.searchByCity(city);
    }

    @GetMapping("/search/state/{state}")
    public List<Contact> searchByState(@PathVariable String state) {
        return addressBookService.searchByState(state);
    }

    @GetMapping("/search/count/city")
    public Map<String, Long> countByCity() {
        return addressBookService.countByCity();
    }

    @GetMapping("/search/count/state")
    public Map<String, Long> countByState() {
        return addressBookService.countByState();
    }

    @GetMapping("/db/entries")
    public CompletableFuture<List<AddressBookEntry>> getAllEntriesFromDb() {
        return addressBookDbService.getAllEntriesAsync();
    }

    @GetMapping("/db/contacts")
        public CompletableFuture<ResponseEntity<Contact>> getContactFromDb(
            @RequestParam String firstName,
            @RequestParam String lastName) {
        return addressBookDbService.getContactByNameAsync(firstName, lastName)
            .thenApply(result -> result.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }

    @PutMapping("/db/contacts")
        public CompletableFuture<ResponseEntity<Contact>> updateContactInDb(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestBody ContactRequest request) {
        return addressBookDbService.updateContactByNameAsync(firstName, lastName, request)
            .thenApply(result -> result.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }

    @GetMapping("/db/contacts/period")
    public CompletableFuture<List<Contact>> getContactsAddedInPeriod(
            @RequestParam String start,
            @RequestParam String end) {
        LocalDateTime startDateTime = LocalDateTime.parse(start);
        LocalDateTime endDateTime = LocalDateTime.parse(end);
        return addressBookDbService.getContactsAddedBetweenAsync(startDateTime, endDateTime);
    }

    @GetMapping("/db/contacts/count/city")
    public CompletableFuture<Map<String, Long>> countContactsByCityFromDb() {
        return addressBookDbService.countContactsByCityAsync();
    }

    @GetMapping("/db/contacts/count/state")
    public CompletableFuture<Map<String, Long>> countContactsByStateFromDb() {
        return addressBookDbService.countContactsByStateAsync();
    }

    @PostMapping("/db/contacts")
        public CompletableFuture<ResponseEntity<Contact>> addContactToDb(
            @RequestParam String addressBookName,
            @RequestBody ContactRequest request) {
        return addressBookDbService.addContactToDbAsync(addressBookName, request)
            .thenApply(result -> result
                .map(contact -> ResponseEntity.status(HttpStatus.CREATED).body(contact))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build()));
    }

    @PostMapping("/db/contacts/bulk")
    public CompletableFuture<ResponseEntity<List<Contact>>> addContactsToDb(
            @RequestParam String addressBookName,
            @RequestBody List<ContactRequest> requests) {
        return addressBookDbService.addContactsToDbAsync(addressBookName, requests)
                .thenApply(created -> {
                    if (created.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                    }
                    return ResponseEntity.status(HttpStatus.CREATED).body(created);
                });
    }

    @PostMapping("/{name}/export")
    public CompletableFuture<ResponseEntity<ApiResponse>> exportAddressBook(
            @PathVariable String name,
            @RequestParam String path) {
        return addressBookService.exportAddressBookAsync(name, path)
                .thenApply(exported -> {
                    if (exported) {
                        return ResponseEntity.ok(new ApiResponse("Address book exported", Instant.now().toString()));
                    }
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ApiResponse("Export failed", Instant.now().toString()));
                });
    }

    @PostMapping("/{name}/import")
    public CompletableFuture<ResponseEntity<ApiResponse>> importAddressBook(
            @PathVariable String name,
            @RequestParam String path) {
        return addressBookService.importAddressBookAsync(name, path)
                .thenApply(addedCount -> {
                    if (addedCount > 0) {
                        return ResponseEntity.ok(new ApiResponse("Imported contacts: " + addedCount, Instant.now().toString()));
                    }
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ApiResponse("Import failed or no new contacts", Instant.now().toString()));
                });
    }

    @PostMapping("/{name}/export-json")
    public CompletableFuture<ResponseEntity<ApiResponse>> exportAddressBookJson(
            @PathVariable String name,
            @RequestParam String path) {
        return addressBookService.exportAddressBookJsonAsync(name, path)
                .thenApply(exported -> {
                    if (exported) {
                        return ResponseEntity.ok(new ApiResponse("Address book exported as JSON", Instant.now().toString()));
                    }
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ApiResponse("Export failed", Instant.now().toString()));
                });
    }

    @PostMapping("/{name}/import-json")
    public CompletableFuture<ResponseEntity<ApiResponse>> importAddressBookJson(
            @PathVariable String name,
            @RequestParam String path) {
        return addressBookService.importAddressBookJsonAsync(name, path)
                .thenApply(addedCount -> {
                    if (addedCount > 0) {
                        return ResponseEntity.ok(new ApiResponse("Imported contacts: " + addedCount, Instant.now().toString()));
                    }
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ApiResponse("Import failed or no new contacts", Instant.now().toString()));
                });
    }
}
