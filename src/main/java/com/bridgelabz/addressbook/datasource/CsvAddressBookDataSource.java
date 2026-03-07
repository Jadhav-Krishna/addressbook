package com.bridgelabz.addressbook.datasource;

import com.bridgelabz.addressbook.service.AddressBookService;
import org.springframework.stereotype.Component;

@Component
public class CsvAddressBookDataSource implements AddressBookDataSource {
    private final AddressBookService addressBookService;

    public CsvAddressBookDataSource(AddressBookService addressBookService) {
        this.addressBookService = addressBookService;
    }

    @Override
    public AddressBookDataSourceType getType() {
        return AddressBookDataSourceType.CSV;
    }

    @Override
    public int save(AddressBookDataSourceRequest request) {
        if (request.getFilePath() == null || request.getFilePath().isBlank()) {
            return 0;
        }
        boolean exported = addressBookService.exportAddressBook(
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
        return addressBookService.importAddressBook(
                request.getAddressBookName(),
                request.getFilePath());
    }
}
