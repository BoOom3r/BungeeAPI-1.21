package net.boom3r.bungeeapi.core.objects;

public class RedisPubSubMsg {
    private String type;
    private String payload;

    RedisPubSubMsg(String type, String payload){
        this.type = type;
        this.payload = payload;
    }


}
