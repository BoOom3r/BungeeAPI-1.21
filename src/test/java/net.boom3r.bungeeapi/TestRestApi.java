package net.boom3r.bungeeapi;

import net.boom3r.bungeeapi.api.RestAPI;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestRestApi {

    @Test
    public void testIpify() throws ExecutionException, InterruptedException {
        final String res = RestAPI.httpRequest("https://jsonplaceholder.typicode.com/todos/1").get().get("id").getAsString();
        assertEquals(res, "1");
    }

}
