package net.fuffo.explosionFixer;

import com.palmergames.bukkit.towny.regen.TownyRegenAPI;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;

public final class ExplosionFixer extends JavaPlugin implements Listener {

    private final HashMap<String, String> actualNames = new HashMap<>();
    private HashSet<String> noRegenDimensions = new HashSet<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        actualNames.put("overworld", "world");
        actualNames.put("nether", "world_nether");
        actualNames.put("end", "world_the_end");

        // TODO: aggiungere un comando
        noRegenDimensions.add(actualNames.get("nether"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.isCancelled()) return;

        Location loc = event.getLocation();

        // TODO: aggiungere condizioni più dettagliate così va in altri casi il plugin
        if (noRegenDimensions.contains(loc.getWorld().getName())) {
            TownyRegenAPI.cancelProtectionRegenTasks();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("explosionfixer")) {
            return false;
        }

        sender.sendMessage("You used the command");

        return true;
    }
}
