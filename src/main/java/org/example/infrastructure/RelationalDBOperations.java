package org.example.infrastructure;

import org.bson.types.ObjectId;
import org.example.model.AlienSighting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class RelationalDBOperations {
    private final Connection connection;

    public RelationalDBOperations(Connection connection) {
        this.connection = connection;
    }

    public void createAlienSighting(AlienSighting sighting) throws SQLException {
        String sql = "INSERT INTO alienSightings (location, date, witness, description) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, sighting.location());
            stmt.setDate(2, java.sql.Date.valueOf(sighting.date()));
            stmt.setString(3, sighting.witness());
            stmt.setString(4, sighting.description());
            stmt.executeUpdate();
        }
    }

    public List<AlienSighting> readAlienSightings() throws SQLException {
        String sql = "SELECT * FROM alienSightings";
        List<AlienSighting> sightings = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                sightings.add(new AlienSighting(
                        new ObjectId(rs.getString("SightingID")),
                        rs.getString("location"),
                        rs.getDate("date").toLocalDate(),
                        rs.getString("witness"),
                        rs.getString("description")
                ));
            }
        }
        return sightings;
    }

    public void updateAlienSighting(String id, String newDescription) throws SQLException {
        String sql = "UPDATE alienSightings SET description = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newDescription);
            stmt.setString(2, id);
            stmt.executeUpdate();
        }
    }

    public void deleteAlienSighting(String id) throws SQLException {
        String sql = "DELETE FROM alienSightings WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }
}