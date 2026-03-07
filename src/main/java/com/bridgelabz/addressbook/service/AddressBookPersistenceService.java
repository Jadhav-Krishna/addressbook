package com.bridgelabz.addressbook.service;

import com.bridgelabz.addressbook.datasource.AddressBookDataSource;
import com.bridgelabz.addressbook.datasource.AddressBookDataSourceRequest;
import com.bridgelabz.addressbook.datasource.AddressBookDataSourceType;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class AddressBookPersistenceService {
    private final Map<AddressBookDataSourceType, AddressBookDataSource> dataSources = new EnumMap<>(AddressBookDataSourceType.class);

    public AddressBookPersistenceService(List<AddressBookDataSource> sources) {
        for (AddressBookDataSource source : sources) {
            dataSources.put(source.getType(), source);
        }
    }

    public int save(AddressBookDataSourceType type, AddressBookDataSourceRequest request) {
        AddressBookDataSource dataSource = dataSources.get(type);
        if (dataSource == null) {
            return 0;
        }
        return dataSource.save(request);
    }

    public int load(AddressBookDataSourceType type, AddressBookDataSourceRequest request) {
        AddressBookDataSource dataSource = dataSources.get(type);
        if (dataSource == null) {
            return 0;
        }
        return dataSource.load(request);
    }
}
