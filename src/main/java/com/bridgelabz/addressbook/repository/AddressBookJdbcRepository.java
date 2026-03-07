package com.bridgelabz.addressbook.repository;

import com.bridgelabz.addressbook.model.AddressBookEntry;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AddressBookJdbcRepository {
    private static final String SELECT_ALL_ENTRIES = """
            SELECT
                ab.id AS address_book_id,
                ab.name AS address_book_name,
                c.id AS contact_id,
                c.first_name,
                c.last_name,
                c.address,
                c.city,
                c.state,
                c.zip,
                c.phone_number,
                c.email
            FROM address_book ab
            LEFT JOIN contact c ON c.address_book_id = ab.id
            ORDER BY ab.name, c.first_name, c.last_name
            """;

    private final JdbcTemplate jdbcTemplate;

    public AddressBookJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AddressBookEntry> findAllEntries() {
        return jdbcTemplate.query(SELECT_ALL_ENTRIES, rowMapper());
    }

    private RowMapper<AddressBookEntry> rowMapper() {
        return (rs, rowNum) -> {
            AddressBookEntry entry = new AddressBookEntry();
            entry.setAddressBookId(rs.getLong("address_book_id"));
            entry.setAddressBookName(rs.getString("address_book_name"));
            long contactId = rs.getLong("contact_id");
            if (rs.wasNull()) {
                entry.setContactId(null);
            } else {
                entry.setContactId(contactId);
            }
            entry.setFirstName(rs.getString("first_name"));
            entry.setLastName(rs.getString("last_name"));
            entry.setAddress(rs.getString("address"));
            entry.setCity(rs.getString("city"));
            entry.setState(rs.getString("state"));
            entry.setZip(rs.getString("zip"));
            entry.setPhoneNumber(rs.getString("phone_number"));
            entry.setEmail(rs.getString("email"));
            return entry;
        };
    }
}
