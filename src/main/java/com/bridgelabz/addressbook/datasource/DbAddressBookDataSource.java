package com.bridgelabz.addressbook.datasource;

import com.bridgelabz.addressbook.dto.ContactRequest;
import com.bridgelabz.addressbook.model.Contact;
import com.bridgelabz.addressbook.service.AddressBookDbService;
import com.bridgelabz.addressbook.service.AddressBookService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DbAddressBookDataSource implements AddressBookDataSource {
    private final AddressBookDbService addressBookDbService;
    private final AddressBookService addressBookService;

    public DbAddressBookDataSource(AddressBookDbService addressBookDbService,
                                   AddressBookService addressBookService) {
        this.addressBookDbService = addressBookDbService;
        this.addressBookService = addressBookService;
    }

    @Override
    public AddressBookDataSourceType getType() {
        return AddressBookDataSourceType.DB;
    }

    @Override
    public int save(AddressBookDataSourceRequest request) {
        List<Contact> contacts = addressBookService.getContacts(request.getAddressBookName())
                .orElse(List.of());
        if (contacts.isEmpty()) {
            return 0;
        }
        List<ContactRequest> payload = contacts.stream()
                .map(this::toRequest)
                .collect(Collectors.toList());
        List<Contact> created = addressBookDbService.addContactsToDb(request.getAddressBookName(), payload);
        return created.size();
    }

    @Override
    public int load(AddressBookDataSourceRequest request) {
        List<Contact> contacts = addressBookDbService.getContactsByAddressBookName(request.getAddressBookName());
        if (contacts.isEmpty()) {
            return 0;
        }
        List<ContactRequest> payload = contacts.stream()
                .map(this::toRequest)
                .collect(Collectors.toList());
        return addressBookService.loadAddressBook(request.getAddressBookName(), payload);
    }

    private ContactRequest toRequest(Contact contact) {
        ContactRequest request = new ContactRequest();
        request.setFirstName(contact.getFirstName());
        request.setLastName(contact.getLastName());
        request.setAddress(contact.getAddress());
        request.setCity(contact.getCity());
        request.setState(contact.getState());
        request.setZip(contact.getZip());
        request.setPhoneNumber(contact.getPhoneNumber());
        request.setEmail(contact.getEmail());
        return request;
    }
}
