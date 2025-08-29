package net.fuffo.explosionFixer;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.regen.TownyRegenAPI;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class ExplosionFixer extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
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
        if (true) {
            TownBlock townBlock = TownyAPI.getInstance().getTownBlock(loc);
            if (townBlock != null) {
                TownyRegenAPI.cancelProtectionRegenTasks();
            }
        }
    }

}
