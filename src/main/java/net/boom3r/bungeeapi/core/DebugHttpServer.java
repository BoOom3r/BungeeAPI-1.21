package net.boom3r.bungeeapi.core;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import net.boom3r.bungeeapi.commands.group.NetworkGroup;
import net.boom3r.bungeeapi.commands.group.NetworkGroupManager;
import net.boom3r.bungeeapi.core.managers.NetworkManager;
import net.boom3r.bungeeapi.core.objects.NetworkUser;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class DebugHttpServer {

    private final NetworkManager networkManager;
    private final NetworkGroupManager groupManager;
    private HttpServer server;

    public DebugHttpServer(NetworkManager networkManager, NetworkGroupManager groupManager) {
        this.networkManager = networkManager;
        this.groupManager = groupManager;
    }

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", this::handleIndex);
        server.createContext("/users", this::handleUsers);
        server.createContext("/groups", this::handleGroups);
        server.setExecutor(null); // default executor
        server.start();
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private void handleIndex(HttpExchange exchange) throws IOException {
        String html = "<html><body><h1>Debug endpoints</h1><ul>"
                + "<li><a href=\"/users\">/users</a> &ndash; liste des NetworkUser</li>"
                + "<li><a href=\"/groups\">/groups</a> &ndash; liste des NetworkGroup</li>"
                + "</ul></body></html>";
        sendHtml(exchange, html);
    }

    private void handleUsers(HttpExchange exchange) throws IOException {
        Map<UUID, NetworkUser> users = networkManager.getNetworkUserList();
        StringBuilder sb = new StringBuilder("<html><body><h1>Network Users</h1><table border=\"1\">");
        sb.append("<tr><th>UUID</th><th>Name</th><th>Online</th></tr>");
        for (NetworkUser user : users.values()) {
            sb.append("<tr><td>").append(user.getUuid()).append("</td><td>")
                    .append(escapeHtml(user.getName())).append("</td><td>")
                    .append(user.isOnline()).append("</td></tr>");
        }
        sb.append("</table></body></html>");
        sendHtml(exchange, sb.toString());
    }

    private void handleGroups(HttpExchange exchange) throws IOException {
        Map<UUID, NetworkGroup> groups = groupManager.getNetworkGroupList();
        StringBuilder sb = new StringBuilder("<html><body><h1>Network Groups</h1><table border=\"1\">");
        sb.append("<tr><th>Group UUID</th><th>Owner</th><th>Members</th></tr>");
        for (NetworkGroup group : groups.values()) {
            sb.append("<tr><td>").append(group.getGroupUUID()).append("</td><td>")
                    .append(escapeHtml(group.getGroupOwner().getName())).append("</td><td>");
            for (NetworkUser member : group.getPlayerList()) {
                sb.append(escapeHtml(member.getName())).append(" ");
            }
            sb.append("</td></tr>");
        }
        sb.append("</table></body></html>");
        sendHtml(exchange, sb.toString());
    }

    private void sendHtml(HttpExchange exchange, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    // simple HTML escaping
    private static String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&","&amp;")
                .replace("<","&lt;")
                .replace(">","&gt;")
                .replace("\"","&quot;")
                .replace("'","&#39;");
    }

    private void handle(HttpExchange exchange) throws IOException {
        Map<UUID, NetworkGroup> groups = groupManager.getNetworkGroupList();
        StringBuilder sb = new StringBuilder("<html><body><h1>Network Groups</h1><table border=\"1\">");
        sb.append("<tr><th>Group UUID</th><th>Owner</th><th>Members</th></tr>");
        for (NetworkGroup group : groups.values()) {
            sb.append("<tr><td>").append(group.getGroupUUID()).append("</td><td>")
                    .append(escapeHtml(group.getGroupOwner().getName())).append("</td><td>");
            for (NetworkUser member : group.getPlayerList()) {
                sb.append(escapeHtml(member.getName())).append(" ");
            }
            sb.append("</td></tr>");
        }
        sb.append("</table></body></html>");
        sendHtml(exchange, sb.toString());
    }
}
