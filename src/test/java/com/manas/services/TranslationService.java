package com.manas.services;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class TranslationService {

    private final HttpClient client;
    private final String apiKey;
    private final String apiHost;
    private final String endpoint;

    public TranslationService(String apiKey){
        this.apiKey = apiKey;
        this.apiHost = "google-translate113.p.rapidapi.com";
        this.endpoint = "https://google-translate113.p.rapidapi.com/api/v1/translator/text";

        //Single shared client instance which is efficient and keeps requests consistent.
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public String translateEsToEn(String text) throws IOException, InterruptedException {
        // If the title is empty, there's nothing to translate.
        if (text == null || text.isBlank()) return "";

        //RapidAPI expects JSON with: from / to / text
        JSONObject body = new JSONObject();
        body.put("from", "es");  //fixed to Spanish
        body.put("to", "en");
        body.put("text", text);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .timeout(Duration.ofSeconds(20))
                .header("x-rapidapi-key", apiKey)
                .header("x-rapidapi-host", apiHost)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

        // If RapidAPI returns an error, fail clearly so hat we can debug quickly
        if (response.statusCode() < 200 || response.statusCode() >=300) {
            throw new IOException("Translation API failed. Status=" + response.statusCode() + ", Body=" + response.body());
        }

        return extractTranslation(response.body());
    }

    private String extractTranslation(String rawResponse) {
        if (rawResponse == null || rawResponse.isBlank()) return "";

        // Sometimes APIs return JSON, sometimes key:value text. We support both.
        try {
            JSONObject json = new JSONObject(rawResponse);
            if (json.has("trans")) return json.getString("trans").trim();
        } catch (Exception ignored) {
            // If not JSON - fall back to parsing text response
        }

        // Example text format: trans:"Hello..."
        for (String line : rawResponse.split("\\R")){
            line = line.trim();
            if (line.startsWith("trans:")) {
                String value = line.substring("trans:".length()).trim();

                if(value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2){
                    value = value.substring(1, value.length() - 1);
                }
                return value.trim();
            }
        }
        return "";
    }
}
