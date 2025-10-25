package net.boom3r.bungeeapi.api;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class RestAPI {

    public static CompletableFuture<JsonObject> httpRequest(String urlStr) {
        final CompletableFuture<JsonObject> future = new CompletableFuture<>();

        try {
            final URL url = new URL(urlStr);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if (conn.getResponseCode() == 200) {
                try (InputStream stream = url.openStream();
                     Scanner scanner = new Scanner(stream)) {

                    final StringBuilder inline = new StringBuilder();
                    while (scanner.hasNext()) {
                        inline.append(scanner.nextLine());
                    }

                    final JsonObject object = (JsonObject) JsonParser.parseString(inline.toString());

                    future.complete(object);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    future.completeExceptionally(e);
                } finally {
                    conn.disconnect();
                }
            } else {
                System.out.println("Invalid http code returned by url " + urlStr);
                future.completeExceptionally(new InvalidObjectException("Invalid http response code"));
            }

        } catch (IOException e) {
            e.printStackTrace();
            future.completeExceptionally(e);
        }

        return future;
    }

}
