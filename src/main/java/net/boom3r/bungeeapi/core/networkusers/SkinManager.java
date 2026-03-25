package net.boom3r.bungeeapi.core.networkusers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public final class SkinManager {

    public static SkinData fetchSkin(UUID uuid) throws IOException {
        String url = "https://sessionserver.mojang.com/session/minecraft/profile/"
                + uuid.toString().replace("-", "") + "?unsigned=false";
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() != 200) return null;
        JsonObject root = JsonParser.parseReader(new InputStreamReader(conn.getInputStream())).getAsJsonObject();
        JsonArray props = root.getAsJsonArray("properties");
        for (JsonElement elt : props) {
            JsonObject propObj = elt.getAsJsonObject();
            if ("textures".equals(propObj.get("name").getAsString())) {
                String value = propObj.get("value").getAsString();
                String signature = propObj.has("signature") ? propObj.get("signature").getAsString() : null;
                return new SkinData(value, signature);
            }
        }
        return null;
    }
}
