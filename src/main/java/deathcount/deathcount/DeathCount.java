package deathcount.deathcount;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class DeathCount extends JavaPlugin implements Listener {

    private Map<String, Integer> deathCounts;
    private Scoreboard scoreboard;

    @Override
    public void onEnable() {
        // Initialize the death count map
        deathCounts = new HashMap<>();

        // Create a new scoreboard
        scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();

        // Register events
        getServer().getPluginManager().registerEvents(this, this);

        // Plugin startup logic
        System.out.println("DeathCount is running!");

        // Update player list initially on plugin enable
        updatePlayerList();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("DeathCount is shutting down!");
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
}
