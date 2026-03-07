package com.bridgelabz.addressbook.service;

import com.bridgelabz.addressbook.model.AddressBookEntry;

import java.util.List;

public interface AddressBookDbService {
    List<AddressBookEntry> getAllEntries();
}
