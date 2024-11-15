package org.example.infrastructure;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertOneModel;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.jsr310.LocalDateCodec;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.example.model.AlienSighting;
import org.example.model.MysteriousDeath;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;


public class AliensDatabase {
    private final MongoClient client;
    private final MongoDatabase db;

    public static AliensDatabase fromConnectionString(String connectionString) {
        return AliensDatabase.fromConnectionString(connectionString, false);
    }

    public static AliensDatabase fromConnectionString(String connectionString, Boolean enableLogging) {
        ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME))
                .setLevel(enableLogging ? Level.DEBUG : Level.ERROR);

        var client = MongoClients.create(
                MongoClientSettings.builder()
                        .applyToClusterSettings(builder -> builder.serverSelectionTimeout(5, TimeUnit.SECONDS))
                        .applyConnectionString(new ConnectionString(connectionString))
                        .build());
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
                CodecRegistries.fromCodecs(new LocalDateCodec()),
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        var db = client.getDatabase("aliensDb").withCodecRegistry(pojoCodecRegistry);
        return new AliensDatabase(client, db);
    }

    private AliensDatabase(MongoClient client, MongoDatabase db) {
        this.client = client;
        this.db = db;
    }

    public MongoDatabase getDb() {
        return db;
    }

    public MongoClient getClient() {
        return client;
    }

    public MongoCollection<AlienSighting> getAlienSightings() {
        return db.getCollection("alienSightings", AlienSighting.class);
    }

    public MongoCollection<MysteriousDeath> getMysteriousDeaths() {
        return db.getCollection("mysteriousDeaths", MysteriousDeath.class);
    }

    public void Seed() throws FileNotFoundException, IOException {
        var collections = new String[]{"alienSightings", "mysteriousDeaths"};

        for (var collection : collections) {
            var filename = getClass().getClassLoader().getResource("dump/" + collection + ".json").getFile();
            if (filename.isEmpty())
                throw new FileNotFoundException(
                        String.format("File %s not found. Check your resources/dump directory.", filename));
            try (var reader = new BufferedReader(new FileReader(filename, Charset.forName("UTF-8")))) {
                db.getCollection(collection).drop();
                db.getCollection(collection).bulkWrite(reader
                        .lines()
                        .map(line -> new InsertOneModel<Document>(Document.parse(line)))
                        .collect(Collectors.toList()));
            }
        }
    }
}
