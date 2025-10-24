package org.turbojax;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.turbojax.config.MainConfig;
import org.turbojax.config.Message;
import org.turbojax.listeners.AnvilRenameListener;
import org.turbojax.listeners.ChatListener;

public final class ChatFilter extends JavaPlugin {
    private static ChatFilter instance = null;
    private static final HttpRequest request;
    private static final HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();

    static {
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI("https://github.com/Turbojax07/ChatFilter/releases/latest")).GET()
                    .build();
        } catch (URISyntaxException e) {
            // For some reason I need to check that the string constant that NEVER CHANGES could throw an error after testing...
            Map<String,String> placeholders = Message.getCommonPlaceholders();
            placeholders.put("%url%", "https://github.com/Turbojax07/ChatFilter/releases/latest");
            Message.sendToConsole(Message.VERSION_URI_ERROR, placeholders);
            throw new RuntimeException(e);
        }
    }

    public static ChatFilter getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        // Loading configs
        // unsafeLoad does no logging or version checks
        MainConfig.unsafeLoad();
        Message.unsafeLoad();

        MainConfig.load();
        Message.load();

        // Loading the log file
        LogManager.initialize();

        // Loading the wordlist
        WordlistManager.reload();

        // Registering the command executors
        PluginCommand cmd = getCommand("chatfilter");
        cmd.setExecutor(new ChatFilterCommand());
        cmd.setTabCompleter(new ChatFilterCommand());

        // Registering event listeners
        getServer().getPluginManager().registerEvents(new AnvilRenameListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);

        LogManager.log("ChatFilter enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        instance = null;

        // Unregistering the command
        getCommand("chatfilter").unregister(getServer().getCommandMap());

        // Saving the wordlist
        WordlistManager.save();

        LogManager.log("ChatFilter disabled!");

        // Compressing the log file
        if (LogManager.getLogFile() == null) return;
        LogManager.gzipCompressFile(LogManager.getLogFile(), new File(LogManager.getLogFile().getPath() + ".gz"));

    }

    public static boolean hasUpdate() {
        return getLatestVersion().compareTo(getPluginVersion()) > 0;
    }

    public static String getPluginVersion() {
        return getInstance().getPluginMeta().getVersion();
    }

    public static String getLatestVersion() {
        try {
            // Sending the request
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parsing the version from the redirected url.
            return resp.uri().toString().split("/")[7];
        } catch (IOException | InterruptedException e) {
            // Log smth here
            throw new RuntimeException(e);
        } catch (ArrayIndexOutOfBoundsException e) {
            return "1.0.0";
        }
    }
}