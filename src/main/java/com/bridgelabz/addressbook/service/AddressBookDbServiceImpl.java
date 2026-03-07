package com.bridgelabz.addressbook.service;

import com.bridgelabz.addressbook.model.AddressBookEntry;
import com.bridgelabz.addressbook.repository.AddressBookJdbcRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookDbServiceImpl implements AddressBookDbService {
    private final AddressBookJdbcRepository repository;

    public AddressBookDbServiceImpl(AddressBookJdbcRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<AddressBookEntry> getAllEntries() {
        return repository.findAllEntries();
    }
}
