package com.bridgelabz.addressbook.datasource;

import com.bridgelabz.addressbook.service.AddressBookService;
import org.springframework.stereotype.Component;

@Component
public class JsonAddressBookDataSource implements AddressBookDataSource {
    private final AddressBookService addressBookService;

    public JsonAddressBookDataSource(AddressBookService addressBookService) {
        this.addressBookService = addressBookService;
    }

    @Override
    public AddressBookDataSourceType getType() {
        return AddressBookDataSourceType.JSON;
    }

    @Override
    public int save(AddressBookDataSourceRequest request) {
        if (request.getFilePath() == null || request.getFilePath().isBlank()) {
            return 0;
        }
        boolean exported = addressBookService.exportAddressBookJson(
                request.getAddressBookName(),
                request.getFilePath());
        return exported
                ? addressBookService.getContacts(request.getAddressBookName()).map(java.util.List::size).orElse(0)
                : 0;
    }

    @Override
    public int load(AddressBookDataSourceRequest request) {
        if (request.getFilePath() == null || request.getFilePath().isBlank()) {
            return 0;
        }
        return addressBookService.importAddressBookJson(
                request.getAddressBookName(),
                request.getFilePath());
    }
}
