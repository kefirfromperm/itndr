package com.itndr;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
public class ItndrControllerTest {
    @Inject @Client("/") private HttpClient client;

    @Test
    public void testIndex() {
        var response = client.toBlocking().exchange("/");
        assertEquals(HttpStatus.FOUND, response.getStatus());
    }

    @Test
    public void testShowForm() {
        String id = UUID.randomUUID().toString();
        var response = client.toBlocking().exchange("/" + id, String.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        var doc = Jsoup.parse(response.getBody().orElseThrow());
        assertFalse(doc.select("form").isEmpty());
        assertTrue(doc.select("#match-result").isEmpty());
        assertTrue(doc.select("#error-message").isEmpty());
    }

    @Test
    public void testSaveEmptyForm() {
        String id = UUID.randomUUID().toString();
        var response = client.toBlocking().exchange(
                HttpRequest.POST("/" + id, "offer=&demand=")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED),
                String.class
        );
        assertEquals(HttpStatus.OK, response.getStatus());
        var doc = Jsoup.parse(response.getBody().orElseThrow());
        assertFalse(doc.select("form").isEmpty());
        assertTrue(doc.select("#match-result").isEmpty());
        assertFalse(doc.select("#error-message").isEmpty());
    }

    @Test
    public void testSaveOfferThenDemand() {
        String id = UUID.randomUUID().toString();
        final var uri = "/" + id;

        var saveOfferResponse = client.toBlocking().exchange(
                HttpRequest.POST(uri, "offer=100000&demand=")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );
        assertEquals(HttpStatus.FOUND, saveOfferResponse.getStatus());

        var response = client.toBlocking().exchange(uri, String.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        var doc = Jsoup.parse(response.getBody().orElseThrow());
        assertFalse(doc.select("form").isEmpty());
        assertTrue(doc.select("#match-result").isEmpty());
        assertTrue(doc.select("#error-message").isEmpty());

        var saveDemandResponse = client.toBlocking().exchange(
                HttpRequest.POST(uri, "offer=&demand=90000")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );
        assertEquals(HttpStatus.FOUND, saveDemandResponse.getStatus());

        var response1 = client.toBlocking().exchange(uri, String.class);
        assertEquals(HttpStatus.OK, response1.getStatus());
        var doc1 = Jsoup.parse(response1.getBody().orElseThrow());
        assertTrue(doc1.select("form").isEmpty());
        final var matchResult = doc1.select("#match-result");
        assertFalse(matchResult.isEmpty());
        assertEquals("MATCH!", matchResult.text().trim());
        assertTrue(doc1.select("#error-message").isEmpty());
    }

    @Test
    public void testSaveDemandThenOffer() {
        String id = UUID.randomUUID().toString();
        final var uri = "/" + id;

        var saveOfferResponse = client.toBlocking().exchange(
                HttpRequest.POST(uri, "offer=&demand=100000")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );
        assertEquals(HttpStatus.FOUND, saveOfferResponse.getStatus());

        var response = client.toBlocking().exchange(uri, String.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        var doc = Jsoup.parse(response.getBody().orElseThrow());
        assertFalse(doc.select("form").isEmpty());
        assertTrue(doc.select("#match-result").isEmpty());
        assertTrue(doc.select("#error-message").isEmpty());

        var saveDemandResponse = client.toBlocking().exchange(
                HttpRequest.POST(uri, "offer=90000&demand=")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );
        assertEquals(HttpStatus.FOUND, saveDemandResponse.getStatus());

        var response1 = client.toBlocking().exchange(uri, String.class);
        assertEquals(HttpStatus.OK, response1.getStatus());
        var doc1 = Jsoup.parse(response1.getBody().orElseThrow());
        assertTrue(doc1.select("form").isEmpty());
        final var matchResult = doc1.select("#match-result");
        assertFalse(matchResult.isEmpty());
        assertEquals("MISMATCH!", matchResult.text().trim());
        assertTrue(doc1.select("#error-message").isEmpty());
    }

    @Test
    public void testSaveFullForm() {
        String id = UUID.randomUUID().toString();
        var response = client.toBlocking().exchange(
                HttpRequest.POST("/" + id, "offer=1&demand=2")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED),
                String.class
        );
        assertEquals(HttpStatus.OK, response.getStatus());
        var doc = Jsoup.parse(response.getBody().orElseThrow());
        assertFalse(doc.select("form").isEmpty());
        assertTrue(doc.select("#match-result").isEmpty());
        assertFalse(doc.select("#error-message").isEmpty());
    }
}
