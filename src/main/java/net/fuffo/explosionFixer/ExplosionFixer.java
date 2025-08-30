package net.fuffo.explosionFixer;

import com.palmergames.bukkit.towny.regen.TownyRegenAPI;

import org.bukkit.ChatColor;
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

// TODO: sostituire ChatColor con l'alternativa non deprecata

public final class ExplosionFixer extends JavaPlugin implements Listener {
    private boolean status = true;
    private final String helpMessage = String.format("""
            %s"%s/explosionfixer enable%s" enables the plugin
            "%s/explosionfixer disable%s" disables the plugin
            "%s/explosionfixer%s" or "%s/explosionfixer help%s" shows this message
            """, ChatColor.YELLOW, ChatColor.AQUA, ChatColor.YELLOW, ChatColor.AQUA, ChatColor.YELLOW, ChatColor.AQUA, ChatColor.YELLOW, ChatColor.AQUA, ChatColor.YELLOW);
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
        if (!status) return;

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
        try{
            switch(args[0].toLowerCase()) {
                case "enable":
                    status = true;
                    sender.sendMessage(ChatColor.YELLOW + "ExplosionFixer succesfully" + ChatColor.DARK_GREEN + " enabled!");
                    break;
                case "disable":
                    status = false;
                    sender.sendMessage(ChatColor.YELLOW + "ExplosionFixer succesfully" + ChatColor.RED + " disabled!");
                    break;
                case "listdimensions":
                    String dims = "";
                    for (String dim : noRegenDimensions) {
                        dims += dim + ", ";
                    }
                    dims = dims.substring(0, dims.length() - 2);
                    sender.sendMessage(ChatColor.YELLOW + "Dimensions that won't regenerate explosions: " + dims);
                    break;
                default:
                    sender.sendMessage(helpMessage);
            }
        } catch(ArrayIndexOutOfBoundsException e) {
            // we ball
            sender.sendMessage(helpMessage);
        }

        return true;
    }
}
