package net.boom3r.bungeeapi.core.objects;

import net.boom3r.bungeeapi.BungeeAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class RandomMOTD {

    TextComponent motdMain;

    public RandomMOTD(String pMotdMain, String pMotdExtra1, String pMotdExtra2) {
        motdMain = new TextComponent(pMotdMain);
        TextComponent motdExtra1 = new TextComponent(pMotdExtra1);
        TextComponent motdExtra2 = new TextComponent(pMotdExtra2);
        motdMain.addExtra(motdExtra1);
    }

    public RandomMOTD(String pMotdMain, String pMotdExtra1) {
        motdMain = new TextComponent(pMotdMain);
        TextComponent motdExtra1 = new TextComponent(pMotdExtra1);
        motdMain.addExtra(motdExtra1);
    }

    public static RandomMOTD getRandomMotd(){
        int rand = new Random().nextInt(RandomMOTDEnum.values().length);
        RandomMOTDEnum rdmEnum = RandomMOTDEnum.values()[rand];

        return new RandomMOTD(rdmEnum.motdMain, rdmEnum.motdExtra1);
    }

    public static TextComponent getRandomMotdTC(String ip){


        int rand = new Random().nextInt(RandomMOTDEnum.values().length);
        RandomMOTDEnum rdmEnum = RandomMOTDEnum.values()[rand];

        TextComponent tmpTC = new TextComponent(rdmEnum.motdMain);
        TextComponent tmpTC1 = null;
        if (getCustomMotd(ip) != null) {
            tmpTC1 = new TextComponent(ChatColor.AQUA+"Viens jouer "+getCustomMotd(ip)+ " ! ");
        } else {
            tmpTC1 = new TextComponent(rdmEnum.motdExtra1);
        }

        tmpTC.addExtra(tmpTC1);

        return tmpTC;
    }


    public static String getCustomMotd(String ip){
        String tmpTC = null;

        try (Connection sql = BungeeAPI.dataSourcePool.getConnection();
             PreparedStatement statement = sql.prepareStatement("SELECT name FROM network_users WHERE last_known_ip = ?");
        ) {
            statement.setString(1, ip);
            ResultSet result = statement.executeQuery();
            if(result.next()) {
                BungeeAPI.logger.info("Réponse trouvée pour " + ip + " : " + result.getString("name"));
                tmpTC = result.getString("name");
                result.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tmpTC;
    }

}
