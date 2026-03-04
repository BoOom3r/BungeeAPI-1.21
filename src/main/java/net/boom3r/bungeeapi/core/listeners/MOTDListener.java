package net.boom3r.bungeeapi.core.listeners;

import net.boom3r.bungeeapi.BungeeAPI;
import net.boom3r.bungeeapi.core.managers.LogManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import static net.boom3r.bungeeapi.core.objects.RandomMOTD.getRandomMotdTC;

public class MOTDListener implements Listener {
    @EventHandler
    public void onPing(ProxyPingEvent e){
        ServerPing serverPing = e.getResponse();
        BaseComponent motdText;
        if(BungeeAPI.maintenance){
            motdText = new TextComponent(ChatColor.RED+"/"+ChatColor.YELLOW+"!"+ChatColor.RED+"\\"+ChatColor.GOLD+" Maintenance En Cours "+ChatColor.RED+"/"+ChatColor.YELLOW+"!"+ChatColor.RED+"\\  \n");
            motdText.addExtra(new TextComponent(ChatColor.GOLD+"Merci de ré-essayer plus tard"));
        } else {

            motdText = getRandomMotdTC(e.getConnection().toString().substring(2, e.getConnection().toString().indexOf(':')));
            //motdText = new TextComponent(ChatColor.AQUA+"-- == Survival Network == -- \n");
            //motdText.addExtra(new TextComponent(ChatColor.AQUA+"Economy - Pve - Quests -> "+ChatColor.LIGHT_PURPLE+ bungeeInstance.getProxy().getOnlineCount()+" joueurs en ligne"));
        }

        serverPing.setDescriptionComponent(motdText);
        e.setResponse(serverPing);
    }

}
