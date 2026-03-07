package com.bridgelabz.addressbook.dto;

import com.bridgelabz.addressbook.model.Contact;

public class AddContactResult {
    private final AddContactStatus status;
    private final Contact contact;

    public AddContactResult(AddContactStatus status, Contact contact) {
        this.status = status;
        this.contact = contact;
    }

    public AddContactStatus getStatus() {
        return status;
    }

    public Contact getContact() {
        return contact;
    }
}
