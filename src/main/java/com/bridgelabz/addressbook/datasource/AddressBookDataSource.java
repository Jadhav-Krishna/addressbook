package com.bridgelabz.addressbook.datasource;

public interface AddressBookDataSource {
    AddressBookDataSourceType getType();

    int save(AddressBookDataSourceRequest request);

    int load(AddressBookDataSourceRequest request);
}
