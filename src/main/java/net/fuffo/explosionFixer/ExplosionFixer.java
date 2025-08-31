package net.fuffo.explosionFixer;

import com.palmergames.bukkit.towny.regen.TownyRegenAPI;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
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
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        actualNames.put("overworld", "world");
        actualNames.put("nether", "world_nether");
        actualNames.put("end", "world_the_end");

        noRegenDimensions = new HashSet<>(getConfig().getStringList("no-regen-dimensions"));
    }

    @Override
    public void onDisable() {
        getConfig().set("no-regen-dimensions", new ArrayList<>(noRegenDimensions));
        saveConfig();
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
                case "toggledimension":
                    if(args.length == 2){
                        if (!noRegenDimensions.contains(args[1])){
                            noRegenDimensions.add(args[1]);
                        } else {
                            noRegenDimensions.remove(args[1]);
                        }
                    }
                    TextComponent textComponent = Component.text("\nHere are the current explosion rules in each dimension: \n(").color(NamedTextColor.YELLOW)
                            .append(Component.text("green").color(NamedTextColor.DARK_GREEN))
                            .append(Component.text(" = enabled, ").color(NamedTextColor.YELLOW))
                            .append(Component.text("red").color(NamedTextColor.RED))
                            .append(Component.text(" = regenerating)\n\n").color(NamedTextColor.YELLOW));
                    for (String dim : actualNames.values()){
                        TextComponent dimComponent = Component.text(dim + "\n")
                                .color(noRegenDimensions.contains(dim) ? NamedTextColor.DARK_GREEN : NamedTextColor.RED)
                                .clickEvent(ClickEvent.runCommand("ef toggledimension " + dim))
                                .decoration(TextDecoration.BOLD, true);
                        textComponent = textComponent.append(dimComponent);
                    }
                    textComponent = textComponent.append(Component.text("\nYou may click any of the options above to switch between modes.").color(NamedTextColor.YELLOW));
                    sender.sendMessage(textComponent);
                    getConfig().set("no-regen-dimensions", new ArrayList<>(noRegenDimensions));
                    saveConfig();
                    break;
                default:
                    sender.sendMessage(helpMessage);
            }
        } catch(ArrayIndexOutOfBoundsException e) {
            sender.sendMessage(helpMessage);
        }

        return true;
    }
}
