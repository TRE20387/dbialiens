package org.example.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.time.LocalDate;

public record MysteriousDeath(
        @BsonId ObjectId id,
        int sightingID,
        String personName,
        String causeOfDeath,
        LocalDate date,
        String location) {
}
