package org.example;

import com.mongodb.client.MongoDatabase;
import org.example.infrastructure.AliensDatabase;
import org.example.infrastructure.MongoDBOperations;
import org.example.infrastructure.RelationalDBOperations;
import org.example.model.AlienSighting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PerformanceTest {
    private static final String MONGO_URI = "mongodb://root:1234@localhost:27017/aliensdb";
    private static final String RELATIONAL_DB_URL = "jdbc:mysql://localhost:3306/relationalDb";
    private static final String RELATIONAL_DB_USER = "root";
    private static final String RELATIONAL_DB_PASSWORD = "allebasi1505";

    public static void main(String[] args) {
        try {
            // Initialize MongoDB
            MongoDatabase mongoDb = AliensDatabase.fromConnectionString(MONGO_URI).getDb();
            MongoDBOperations mongoOps = new MongoDBOperations(mongoDb);

            // Initialize Relational DB
            Connection relationalConn = DriverManager.getConnection(RELATIONAL_DB_URL, RELATIONAL_DB_USER, RELATIONAL_DB_PASSWORD);
            RelationalDBOperations relationalOps = new RelationalDBOperations(relationalConn);

            // Test CRUD operations
            testCreateOperations(mongoOps, relationalOps);
            testReadOperations(mongoOps, relationalOps);
            testUpdateOperations(mongoOps, relationalOps);
            testDeleteOperations(mongoOps, relationalOps);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testCreateOperations(MongoDBOperations mongoOps, RelationalDBOperations relationalOps) throws SQLException {
        long startTime, endTime;

        // MongoDB Create
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            AlienSighting sighting = new AlienSighting(null, "Location" + i, LocalDate.now(), "Witness" + i, "Description" + i);
            mongoOps.createAlienSighting(sighting);
        }
        endTime = System.currentTimeMillis();
        System.out.println("MongoDB Create Time: " + (endTime - startTime) + " ms");

        // Relational DB Create
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            AlienSighting sighting = new AlienSighting(null, "Location" + i, LocalDate.now(), "Witness" + i, "Description" + i);
            relationalOps.createAlienSighting(sighting);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Relational DB Create Time: " + (endTime - startTime) + " ms");
    }

    private static void testReadOperations(MongoDBOperations mongoOps, RelationalDBOperations relationalOps) throws SQLException {
        long startTime, endTime;

        // MongoDB Read
        startTime = System.currentTimeMillis();
        List<AlienSighting> mongoSightings = mongoOps.readAlienSightings();
        endTime = System.currentTimeMillis();
        System.out.println("MongoDB Read Time: " + (endTime - startTime) + " ms");

        // Relational DB Read
        startTime = System.currentTimeMillis();
        List<AlienSighting> relationalSightings = relationalOps.readAlienSightings();
        endTime = System.currentTimeMillis();
        System.out.println("Relational DB Read Time: " + (endTime - startTime) + " ms");
    }

    private static void testUpdateOperations(MongoDBOperations mongoOps, RelationalDBOperations relationalOps) throws SQLException {
        long startTime, endTime;

        // MongoDB Update
        startTime = System.currentTimeMillis();
        mongoOps.updateAlienSighting(mongoOps.readAlienSightings().get(0).id(), "Updated Description");
        endTime = System.currentTimeMillis();
        System.out.println("MongoDB Update Time: " + (endTime - startTime) + " ms");

        // Relational DB Update
        startTime = System.currentTimeMillis();
        relationalOps.updateAlienSighting(relationalOps.readAlienSightings().get(0).id().toString(), "Updated Description");
        endTime = System.currentTimeMillis();
        System.out.println("Relational DB Update Time: " + (endTime - startTime) + " ms");
    }

    private static void testDeleteOperations(MongoDBOperations mongoOps, RelationalDBOperations relationalOps) throws SQLException {
        long startTime, endTime;

        // MongoDB Delete
        startTime = System.currentTimeMillis();
        mongoOps.deleteAlienSighting(mongoOps.readAlienSightings().get(0).id());
        endTime = System.currentTimeMillis();
        System.out.println("MongoDB Delete Time: " + (endTime - startTime) + " ms");

        // Relational DB Delete
        startTime = System.currentTimeMillis();
        relationalOps.deleteAlienSighting(relationalOps.readAlienSightings().get(0).id().toString());
        endTime = System.currentTimeMillis();
        System.out.println("Relational DB Delete Time: " + (endTime - startTime) + " ms");
    }
}
