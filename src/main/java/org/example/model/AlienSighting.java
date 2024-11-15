package org.example.model;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.time.LocalDate;

public record AlienSighting(
        @BsonId ObjectId id,
        String location,
        LocalDate date,
        String witness,
        String description) {
}
