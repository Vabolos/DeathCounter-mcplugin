package deathcount.deathcount;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public final class DeathCount extends JavaPlugin implements Listener {

    private Map<String, Integer> deathCounts;
    private Scoreboard scoreboard;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final File dataFile = new File(getDataFolder(), "counter.json");

    @Override
    public void onEnable() {
        // Initialize the death count map and load data from JSON file
        deathCounts = loadDeathCounts();

        // Create a new scoreboard
        scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();

        // Register events
        getServer().getPluginManager().registerEvents(this, this);

        // Plugin startup logic
        getLogger().log(Level.INFO, "DeathCount is running!");

        // Update player list initially on plugin enable
        updatePlayerList();
    }

    @Override
    public void onDisable() {
        // Save death counts to the JSON file on plugin shutdown
        saveDeathCounts();
        getLogger().log(Level.INFO, "DeathCount is shutting down!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        // Add new players to the death counts map if not already present
        if (!deathCounts.containsKey(playerName)) {
            deathCounts.put(playerName, 0); // Set initial death count to 0
        }

        // Update player list upon join
        updatePlayerList();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String playerName = player.getName();

        // Increment death count for the player
        int deaths = deathCounts.getOrDefault(playerName, 0);
        deathCounts.put(playerName, deaths + 1);

        // Update player list upon death
        updatePlayerList();
    }

    // Method to retrieve a player's death count
    private int getDeathCount(Player player) {
        return deathCounts.getOrDefault(player.getName(), 0);
    }

    // Method to update the player list with death count
    private void updatePlayerList() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            String teamName = onlinePlayer.getName() + "_Deaths"; // Unique team identifier
            Team team = scoreboard.getTeam(teamName);
            if (team == null) {
                team = scoreboard.registerNewTeam(teamName);
            }

            int deathCount = getDeathCount(onlinePlayer);

            team.addEntry(onlinePlayer.getName());
            team.setSuffix(" [" + deathCount + "] ");
        }
    }

    // Load death counts from the JSON file
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

    // Save death counts to the JSON file
    private void saveDeathCounts() {
        try (FileWriter writer = new FileWriter(dataFile)) {
            gson.toJson(deathCounts, writer);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Error saving death counts to file", e);
        }
    }
}


