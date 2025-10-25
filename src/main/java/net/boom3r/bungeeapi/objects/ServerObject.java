package net.boom3r.bungeeapi.objects;

import java.util.UUID;

public class ServerObject {

    UUID uuid;
    String name;
    String address;
    int port;
    String motd;
    int nbPlayers;
    int status;

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public void setNbPlayers(int nbPlayers) {
        this.nbPlayers = nbPlayers;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ServerObject(UUID uuid, String name, String address, int port, int status) {
        this.uuid = uuid;
        this.name = name;
        this.address = address;
        this.port = port;
        this.status = status;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getMotd() {
        return motd;
    }

    public int getNbPlayers() {
        return nbPlayers;
    }

    public int getStatus() {
        return status;
    }





}
