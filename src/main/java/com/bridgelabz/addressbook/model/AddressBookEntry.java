package com.bridgelabz.addressbook.model;

public class AddressBookEntry {
    private long addressBookId;
    private String addressBookName;
    private Long contactId;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String phoneNumber;
    private String email;
    private java.time.LocalDateTime dateAdded;

    public AddressBookEntry() {
    }

    public AddressBookEntry(long addressBookId, String addressBookName, Long contactId, String firstName,
                            String lastName, String address, String city, String state, String zip,
                            String phoneNumber, String email, java.time.LocalDateTime dateAdded) {
        this.addressBookId = addressBookId;
        this.addressBookName = addressBookName;
        this.contactId = contactId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.dateAdded = dateAdded;
    }

    public long getAddressBookId() {
        return addressBookId;
    }

    public void setAddressBookId(long addressBookId) {
        this.addressBookId = addressBookId;
    }

    public String getAddressBookName() {
        return addressBookName;
    }

    public void setAddressBookName(String addressBookName) {
        this.addressBookName = addressBookName;
    }

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public java.time.LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(java.time.LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }
}
