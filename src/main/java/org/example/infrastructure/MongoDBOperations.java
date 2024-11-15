package org.example.infrastructure;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.model.AlienSighting;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MongoDBOperations {
    private final MongoDatabase db;

    public MongoDBOperations(MongoDatabase db) {
        this.db = db;
    }

    public void createAlienSighting(AlienSighting sighting) {
        MongoCollection<Document> collection = db.getCollection("alienSightings");
        Document doc = new Document("_id", new ObjectId())
                .append("location", sighting.location())
                .append("date", sighting.date().toString())
                .append("witness", sighting.witness())
                .append("description", sighting.description());
        collection.insertOne(doc);
    }

    public List<AlienSighting> readAlienSightings() {
        MongoCollection<Document> collection = db.getCollection("alienSightings");
        List<AlienSighting> sightings = new ArrayList<>();
        for (Document doc : collection.find()) {
            sightings.add(new AlienSighting(
                    doc.getObjectId("_id"),
                    doc.getString("location"),
                    LocalDate.parse(doc.getString("date")),
                    doc.getString("witness"),
                    doc.getString("description")
            ));
        }
        return sightings;
    }

    public void updateAlienSighting(ObjectId id, String newDescription) {
        MongoCollection<Document> collection = db.getCollection("alienSightings");
        Document query = new Document("_id", id);
        Document update = new Document("$set", new Document("description", newDescription));
        collection.updateOne(query, update);
    }

    public void deleteAlienSighting(ObjectId id) {
        MongoCollection<Document> collection = db.getCollection("alienSightings");
        Document query = new Document("_id", id);
        collection.deleteOne(query);
    }
}
