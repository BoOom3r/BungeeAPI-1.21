package net.boom3r.bungeeapi.core.managers;

import net.boom3r.bungeeapi.BungeeAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

import java.util.logging.Logger;

import static net.boom3r.bungeeapi.BungeeAPI.PREFIX;
import static net.boom3r.bungeeapi.BungeeAPI.DEBUGLVL;
import static net.boom3r.bungeeapi.BungeeAPI.DEBUG;
import static net.boom3r.bungeeapi.core.managers.MessengerManager.sendToAdmins;

public class LogManager {
    public static Logger logger = BungeeAPI.bungeeInstance.getLogger();

    public static void DebugV(String msg, int level){
        if((DEBUG)&&(DEBUGLVL>level)){
            logger.info(PREFIX+"[DEBUG] -> "+msg);
        }
    }

    public static void Info(String msg){
        logger.info(PREFIX+"[INFO] -> "+msg);
        ComponentBuilder msgConsole = new ComponentBuilder("[INFO]")
                .color(ChatColor.BLUE)
                .append("")
                .color(ChatColor.WHITE)
                .append(msg);

        BungeeAPI.bungeeInstance.getProxy().getConsole().sendMessage(msgConsole.create());
    }

    public static void Err(String msg){
        logger.severe(PREFIX+"[INFO] -> "+msg);
    }

    public static void Warn(String msg){
        logger.warning(PREFIX+"[INFO] -> "+msg);
    }

    public static void Admin(String msg){
        logger.info(ChatColor.RED+"[ADMIN]"+ChatColor.RESET+" -> "+msg);
        sendToAdmins(msg);
    }

}
