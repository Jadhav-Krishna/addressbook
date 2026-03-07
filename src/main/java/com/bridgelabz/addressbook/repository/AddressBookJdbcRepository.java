package com.bridgelabz.addressbook.repository;

import com.bridgelabz.addressbook.model.AddressBookEntry;
import com.bridgelabz.addressbook.model.Contact;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
                c.email,
                c.date_added
            FROM address_book ab
            LEFT JOIN contact c ON c.address_book_id = ab.id
            ORDER BY ab.name, c.first_name, c.last_name
            """;
    private static final String SELECT_CONTACT_BY_NAME = """
            SELECT
                c.id,
                c.first_name,
                c.last_name,
                c.address,
                c.city,
                c.state,
                c.zip,
                c.phone_number,
                c.email,
                c.date_added
            FROM contact c
            WHERE c.first_name = ? AND c.last_name = ?
            """;
    private static final String SELECT_CONTACTS_BY_PERIOD = """
            SELECT
                c.id,
                c.first_name,
                c.last_name,
                c.address,
                c.city,
                c.state,
                c.zip,
                c.phone_number,
                c.email,
                c.date_added
            FROM contact c
            WHERE c.date_added >= ? AND c.date_added <= ?
            ORDER BY c.date_added
            """;
            private static final String COUNT_BY_CITY = """
                SELECT c.city, COUNT(*) AS total
                FROM contact c
                WHERE c.city IS NOT NULL AND c.city <> ''
                GROUP BY c.city
                """;
            private static final String COUNT_BY_STATE = """
                SELECT c.state, COUNT(*) AS total
                FROM contact c
                WHERE c.state IS NOT NULL AND c.state <> ''
                GROUP BY c.state
                """;
        private static final String UPDATE_CONTACT_BY_NAME = """
            UPDATE contact
            SET address = ?, city = ?, state = ?, zip = ?, phone_number = ?, email = ?
            WHERE first_name = ? AND last_name = ?
            """;

    private final JdbcTemplate jdbcTemplate;

    public AddressBookJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<AddressBookEntry> findAllEntries() {
        return jdbcTemplate.query(SELECT_ALL_ENTRIES, rowMapper());
    }

    public Optional<Contact> findContactByName(String firstName, String lastName) {
        List<Contact> matches = jdbcTemplate.query(
                SELECT_CONTACT_BY_NAME,
                ps -> {
                    ps.setString(1, firstName);
                    ps.setString(2, lastName);
                },
                contactRowMapper());
        if (matches.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(matches.get(0));
    }

    public boolean updateContactByName(String firstName, String lastName, Contact updated) {
        int updatedRows = jdbcTemplate.update(
                UPDATE_CONTACT_BY_NAME,
                ps -> {
                    ps.setString(1, updated.getAddress());
                    ps.setString(2, updated.getCity());
                    ps.setString(3, updated.getState());
                    ps.setString(4, updated.getZip());
                    ps.setString(5, updated.getPhoneNumber());
                    ps.setString(6, updated.getEmail());
                    ps.setString(7, firstName);
                    ps.setString(8, lastName);
                });
        return updatedRows > 0;
    }

    public List<Contact> findContactsAddedBetween(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return jdbcTemplate.query(
                SELECT_CONTACTS_BY_PERIOD,
                ps -> {
                    ps.setTimestamp(1, java.sql.Timestamp.valueOf(start));
                    ps.setTimestamp(2, java.sql.Timestamp.valueOf(end));
                },
                contactRowMapper());
    }

    public java.util.Map<String, Long> countByCity() {
        return jdbcTemplate.query(COUNT_BY_CITY, rs -> {
            java.util.Map<String, Long> results = new java.util.LinkedHashMap<>();
            while (rs.next()) {
                results.put(rs.getString("city"), rs.getLong("total"));
            }
            return results;
        });
    }

    public java.util.Map<String, Long> countByState() {
        return jdbcTemplate.query(COUNT_BY_STATE, rs -> {
            java.util.Map<String, Long> results = new java.util.LinkedHashMap<>();
            while (rs.next()) {
                results.put(rs.getString("state"), rs.getLong("total"));
            }
            return results;
        });
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
            java.sql.Timestamp addedTs = rs.getTimestamp("date_added");
            if (addedTs != null) {
                entry.setDateAdded(addedTs.toLocalDateTime());
            }
            return entry;
        };
    }

    private RowMapper<Contact> contactRowMapper() {
        return (rs, rowNum) -> {
            Contact contact = new Contact();
            contact.setId(rs.getLong("id"));
            contact.setFirstName(rs.getString("first_name"));
            contact.setLastName(rs.getString("last_name"));
            contact.setAddress(rs.getString("address"));
            contact.setCity(rs.getString("city"));
            contact.setState(rs.getString("state"));
            contact.setZip(rs.getString("zip"));
            contact.setPhoneNumber(rs.getString("phone_number"));
            contact.setEmail(rs.getString("email"));
            java.sql.Timestamp addedTs = rs.getTimestamp("date_added");
            if (addedTs != null) {
                contact.setDateAdded(addedTs.toLocalDateTime());
            }
            return contact;
        };
    }
}
