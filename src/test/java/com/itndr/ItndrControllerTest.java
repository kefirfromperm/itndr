package com.itndr;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        var response = client.toBlocking().exchange("/" + id);
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testSaveEmptyForm() {
        String id = UUID.randomUUID().toString();
        var response = client.toBlocking().exchange(
                HttpRequest.POST("/" + id, "offer=&demand=")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    public void testSaveOfferThenDemand() {
        String id = UUID.randomUUID().toString();

        var saveOfferResponse = client.toBlocking().exchange(
                HttpRequest.POST("/" + id, "offer=100000&demand=")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );
        assertEquals(HttpStatus.FOUND, saveOfferResponse.getStatus());

        var saveDemandResponse = client.toBlocking().exchange(
                HttpRequest.POST("/" + id, "offer=&demand=90000")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );
        assertEquals(HttpStatus.FOUND, saveDemandResponse.getStatus());
    }

    @Test
    public void testSaveDemandThenOffer() {
        String id = UUID.randomUUID().toString();

        var saveOfferResponse = client.toBlocking().exchange(
                HttpRequest.POST("/" + id, "offer=&demand=100000")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );
        assertEquals(HttpStatus.FOUND, saveOfferResponse.getStatus());

        var saveDemandResponse = client.toBlocking().exchange(
                HttpRequest.POST("/" + id, "offer=90000&demand=")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );
        assertEquals(HttpStatus.FOUND, saveDemandResponse.getStatus());
    }

    @Test
    public void testSaveFullForm() {
        String id = UUID.randomUUID().toString();
        var response = client.toBlocking().exchange(
                HttpRequest.POST("/" + id, "offer=1&demand=2")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );
        assertEquals(HttpStatus.OK, response.getStatus());
    }
}
