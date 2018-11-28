package de.bjoernfischer.whiteboardclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;

@Component
public class WhiteboardClientInitializer {

    @Value("${base.url}")
    private String baseUrl;

    @PostConstruct
    public void sendAndReceiveData() {
        callHelloWorld();
        addMessageToWhiteboard();
        getMessagesFromWhiteboard();
    }

    private void callHelloWorld() {
        WebClient.create(baseUrl)
            .get()
            .uri("/hello/WebClient")
            .exchange()
            .block()
            .bodyToMono(String.class)
            .map(s -> {
                System.out.println("Result from service: " + s);
                return s;
            })
            .block();
    }

    private void addMessageToWhiteboard() {
        ClientResponse response = WebClient.create(baseUrl)
            .post()
            .uri("/addmessage")
            .body(BodyInserters.fromObject("Neue nachricht vom WebClient"))
            .exchange()
            .block();

        System.out.println("Added WhiteboardMessage with status code " + response.statusCode());
    }

    private void getMessagesFromWhiteboard() {
        WebClient.create(baseUrl)
            .get()
            .uri("/messages")
            .exchange()
            .block()
            .bodyToFlux(String.class)
            .filter(s -> !s.startsWith("Heartbeat"))
            .subscribe(System.out::println);
    }
}
