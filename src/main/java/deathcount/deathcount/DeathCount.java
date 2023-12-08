package deathcount.deathcount;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public final class DeathCount extends JavaPlugin implements Listener {

    private Scoreboard scoreboard;
    private Map<String, Integer> deathCounts = new HashMap<>();
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFile = new File(getDataFolder(), "..\\counter.json");

    @Override
    public void onEnable() {
        scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
        getServer().getPluginManager().registerEvents(this, this);
        deathCounts = loadDeathCounts(); // Load existing death counts
        getLogger().log(Level.INFO, "DeathCount is running!");
    }

    @Override
    public void onDisable() {
        saveDeathCounts(); // Save death counts to the JSON file on plugin shutdown
        getLogger().log(Level.INFO, "DeathCount is shutting down!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        if (!deathCounts.containsKey(playerName)) {
            deathCounts.put(playerName, 0);
        }

        resetPlayerName(player); // Reset player's name
        updatePlayerList(); // Update the player list
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String playerName = player.getName();

        int deaths = deathCounts.getOrDefault(playerName, 0);
        deathCounts.put(playerName, deaths + 1);

        updatePlayerList(); // Update the player list after death
    }

    private void resetPlayerName(Player player) {
        Team team = scoreboard.getEntryTeam(player.getName());
        if (team != null) {
            team.removeEntry(player.getName());
        }
        player.setPlayerListName(player.getName()); // Resetting player's display name
    }

    private void updatePlayerList() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            int deathCount = deathCounts.getOrDefault(onlinePlayer.getName(), 0);
            onlinePlayer.setPlayerListName(onlinePlayer.getName() + " [" + ChatColor.RED + deathCount + ChatColor.RESET + "]");
        }
    }

    private Map<String, Integer> loadDeathCounts() {
        if (!dataFile.exists()) {
            return new HashMap<>(); // Return empty map if the file doesn't exist
        }

        try (FileReader reader = new FileReader(dataFile)) {
            Type type = new TypeToken<Map<String, Integer>>() {}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Error loading death counts from file", e);
        }
        return new HashMap<>(); // Return empty map if there's an error
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        saveDeathCounts(); // Save death counts to the JSON file when a player leaves the server
    }

    private void saveDeathCounts() {
        try (FileWriter writer = new FileWriter(dataFile)) {
            gson.toJson(deathCounts, writer);
            writer.flush(); // Flushes the buffer to ensure data is written immediately
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Error saving death counts to file", e);
        }
    }
}
