package com.bridgelabz.addressbook.controller;

import com.bridgelabz.addressbook.dto.AddContactResult;
import com.bridgelabz.addressbook.dto.AddContactStatus;
import com.bridgelabz.addressbook.dto.ApiResponse;
import com.bridgelabz.addressbook.dto.ContactRequest;
import com.bridgelabz.addressbook.model.Contact;
import com.bridgelabz.addressbook.service.AddressBookService;
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

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/address-books")
public class AddressBookController {
    private final AddressBookService addressBookService;

    public AddressBookController(AddressBookService addressBookService) {
        this.addressBookService = addressBookService;
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
}
