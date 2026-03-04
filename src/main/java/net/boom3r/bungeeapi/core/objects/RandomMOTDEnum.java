package net.boom3r.bungeeapi.core.objects;

import net.md_5.bungee.api.ChatColor;

import static net.boom3r.bungeeapi.BungeeAPI.bungeeInstance;

public enum RandomMOTDEnum {

    BASIC_1(ChatColor.AQUA+"-- == Survival Network == -- \n", ChatColor.AQUA+"Economy - Pve - Quests -> "+ChatColor.LIGHT_PURPLE+ bungeeInstance.getProxy().getOnlineCount()+" joueurs en ligne"),
    BASIC_2(ChatColor.AQUA+"-- == BoOm3r Network == -- \n", ChatColor.AQUA+"Economy - Pve - Quests -> "+ChatColor.LIGHT_PURPLE+ bungeeInstance.getProxy().getOnlineCount()+" joueurs en ligne");

    public String motdMain;
    public String motdExtra1;


    RandomMOTDEnum(String pMotdMain, String pMotdExtra1){
        this.motdMain = pMotdMain;
        this.motdExtra1 = pMotdExtra1;
    }
}
