package com.bridgelabz.addressbook.datasource;

public class AddressBookDataSourceRequest {
    private final String addressBookName;
    private final String filePath;
    private final String serverUrl;
    private final String serverEndpoint;

    public AddressBookDataSourceRequest(String addressBookName, String filePath,
                                        String serverUrl, String serverEndpoint) {
        this.addressBookName = addressBookName;
        this.filePath = filePath;
        this.serverUrl = serverUrl;
        this.serverEndpoint = serverEndpoint;
    }

    public String getAddressBookName() {
        return addressBookName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getServerEndpoint() {
        return serverEndpoint;
    }
}
