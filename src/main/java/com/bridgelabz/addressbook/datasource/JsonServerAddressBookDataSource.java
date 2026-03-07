package com.bridgelabz.addressbook.datasource;

import com.bridgelabz.addressbook.dto.ContactRequest;
import com.bridgelabz.addressbook.model.Contact;
import com.bridgelabz.addressbook.service.AddressBookService;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class JsonServerAddressBookDataSource implements AddressBookDataSource {
    private final AddressBookService addressBookService;
    private final RestTemplate restTemplate = new RestTemplate();

    public JsonServerAddressBookDataSource(AddressBookService addressBookService) {
        this.addressBookService = addressBookService;
    }

    @Override
    public AddressBookDataSourceType getType() {
        return AddressBookDataSourceType.JSON_SERVER;
    }

    @Override
    public int save(AddressBookDataSourceRequest request) {
        String url = buildUrl(request);
        if (url == null) {
            return 0;
        }
        List<Contact> contacts = addressBookService.getContacts(request.getAddressBookName())
                .orElse(List.of());
        int created = 0;
        for (Contact contact : contacts) {
            ContactRequest payload = toRequest(contact);
            try {
                restTemplate.postForObject(url, payload, Object.class);
                created++;
            } catch (Exception ex) {
                // skip failed post
            }
        }
        return created;
    }

    @Override
    public int load(AddressBookDataSourceRequest request) {
        String url = buildUrl(request);
        if (url == null) {
            return 0;
        }
        ContactRequest[] contacts = restTemplate.getForObject(url, ContactRequest[].class);
        if (contacts == null || contacts.length == 0) {
            return 0;
        }
        List<ContactRequest> payload = Arrays.asList(contacts);
        return addressBookService.loadAddressBook(request.getAddressBookName(), payload);
    }

    private String buildUrl(AddressBookDataSourceRequest request) {
        if (request.getServerUrl() == null || request.getServerUrl().isBlank()) {
            return null;
        }
        String endpoint = request.getServerEndpoint();
        if (endpoint == null || endpoint.isBlank()) {
            endpoint = "/contacts";
        }
        return request.getServerUrl().endsWith("/")
                ? request.getServerUrl().substring(0, request.getServerUrl().length() - 1) + endpoint
                : request.getServerUrl() + endpoint;
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
