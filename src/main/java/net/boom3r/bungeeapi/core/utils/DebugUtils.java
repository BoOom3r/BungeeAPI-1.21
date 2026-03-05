package net.boom3r.bungeeapi.core.utils;

import static net.boom3r.bungeeapi.BungeeAPI.bungeeLogger;

public class DebugUtils {
    public static String debugCommand(String cmd, String[] args){
        if (args.length > 0 ) {
            StringBuilder cmdBuilder = new StringBuilder("Commande /"+cmd+" -> ");
            for (int i = 0 ;  i == args.length-1; i++){
                cmdBuilder.append(cmd+"argument ").append(i).append(" est : ").append(args[i]);
            }
            cmd = cmdBuilder.toString();
        }
        return cmd;
    }

}
