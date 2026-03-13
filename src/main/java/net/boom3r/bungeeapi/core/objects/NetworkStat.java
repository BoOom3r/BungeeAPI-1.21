package net.boom3r.bungeeapi.core.objects;

import java.util.UUID;

public class NetworkStat {
    private UUID playerUuid;
    private String stat_key;
    private String stat_name;
    private String stat_desc;
    private String stat_value;

    public NetworkStat(UUID playerUuid, String stat_key, String stat_name, String stat_desc, String stat_value) {
        this.playerUuid = playerUuid;
        this.stat_key = stat_key;
        this.stat_name = stat_name;
        this.stat_desc = stat_desc;
        this.stat_value = stat_value;

    }


    public boolean saveStat(){

        return true;
    }
}
