package net.boom3r.bungeeapi.objects;

import net.boom3r.bungeeapi.managers.MaintenanceManager;

import java.util.UUID;

public class ServerObject {

    UUID uuid;
    String name;
    String address;
    int port;
    String motd;
    int nbPlayers;
    int status;
    boolean maintenance;

    public ServerObject(String uuid, String name, String address, int port, String motd, int status, boolean maintenance) {
        this.uuid = UUID.fromString(uuid);
        this.name = name;
        this.address = address;
        this.port = port;
        this.status = status;
        this.maintenance = maintenance;
        this.motd = motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public void setNbPlayers(int nbPlayers) {
        this.nbPlayers = nbPlayers;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ServerObject(UUID uuid, String name, String address, int port, String motd, int status, boolean maintenance) {
        this.uuid = uuid;
        this.name = name;
        this.address = address;
        this.port = port;
        this.status = status;
        this.maintenance = maintenance;
        this.motd = motd;
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

    public boolean getMaintenance() {
        return maintenance;
    }
    public void setMaintenance(String name, boolean maintenance) {
        if (maintenance){
            MaintenanceManager.enableMaintenance(name);
        } else {
            MaintenanceManager.disableMaintenance(name);
        }

        this.maintenance = maintenance;
    }





}
