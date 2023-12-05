package deathcount.deathcount;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class DeathCount extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("DeathCount is running!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("DeathCount is shutting down!");
    }
}
