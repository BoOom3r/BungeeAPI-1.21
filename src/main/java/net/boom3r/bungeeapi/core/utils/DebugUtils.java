package net.boom3r.bungeeapi.core.utils;

public class DebugUtils {
    public static String debugCommand(String cmd, String[] args){
        String commande = cmd;
        if (args.length > 0 ) {
            StringBuilder cmdBuilder = new StringBuilder(cmd);
            for (int i = 0; i+1 == args.length; i++){
                cmdBuilder.append("argument ").append(i).append(" est : ").append(args[i]);
            }
            cmd = cmdBuilder.toString();
        }
        return cmd;
    }

}
