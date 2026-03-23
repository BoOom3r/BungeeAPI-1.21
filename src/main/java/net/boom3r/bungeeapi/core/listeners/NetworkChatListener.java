package net.boom3r.bungeeapi.core.listeners;

import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import static net.boom3r.bungeeapi.BungeeAPI.bungeeLogger;

public class NetworkChatListener implements Listener {

    @EventHandler
    public void onChatMsg(ChatEvent e){
        bungeeLogger.DebugV(e.getSender()+" Envoi : "+e.getMessage()+" a "+e.getReceiver(),2);
    }
}
