package net.boom3r.bungeeapi.core.objects;

import java.util.Map;

public class PubSubMessage {
    private final String type;
    private final String source;
    private final Map<String, Object> payload;

    public PubSubMessage(String type, String source, Map<String, Object> payload) {
        this.type = type;
        this.source = source;
        this.payload = payload;
    }

    public String getType() { return type; }
    public String getSource() { return source; }
    public Map<String, Object> getPayload() { return payload; }
}
