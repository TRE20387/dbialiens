package org.example;

import com.mongodb.MongoSecurityException;
import com.mongodb.MongoTimeoutException;
import com.mongodb.client.model.Filters;
import org.example.infrastructure.AliensDatabase;
import org.example.model.AlienSighting;
import org.example.model.MysteriousDeath;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) {
        var aliensDatabase = AliensDatabase.fromConnectionString("mongodb://root:1234@localhost:27017");
        try {
            aliensDatabase.Seed();
        } catch (MongoTimeoutException e) {
            System.err.println("Die Datenbank ist nicht erreichbar. Läuft der Container?");
            System.exit(1);
            return;
        } catch (MongoSecurityException e) {
            System.err.println("Mit dem Benutzer root (Passwort 1234) konnte keine Verbindung aufgebaut werden.");
            System.exit(2);
            return;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(3);
            return;
        }

        {
            var db = aliensDatabase.getDb();

            var alienSightings = db.getCollection("alienSightings", AlienSighting.class).find().into(new ArrayList<>());
            var mysteriousDeaths = db.getCollection("mysteriousDeaths", MysteriousDeath.class).find().into(new ArrayList<>());

            System.out.println(String.format("%d Dokumente in alienSightings gelesen", alienSightings.size()));
            System.out.println(String.format("%d Dokumente in mysteriousDeaths gelesen", mysteriousDeaths.size()));
        }

        // Beispielabfrage
        {
            System.out.println("Alle Alien-Sichtungen in einer bestimmten Stadt.");
            var result = aliensDatabase.getAlienSightings()
                    .find(Filters.eq("location", "Neusiedl am See"))
                    .into(new ArrayList<>());
            System.out.println(
                    result.stream().map(r -> r.id().toHexString().substring(16, 24)).collect(Collectors.joining(", ")));
        }
    }
}
