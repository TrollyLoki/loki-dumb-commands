package net.trollyloki.lokidumbcommands.gun;

import net.trollyloki.lokidumbcommands.LokiDumbCommandsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GunCommand implements CommandExecutor, TabCompleter {

    public static final String BASE_PERMISSION = "lokidumbcommands.gun",
            MULTIPLE_PERMISSION = "lokidumbcommands.gun.multiple",
            ENTITIES_PERMISSION = "lokidumbcommands.gun.entities",
            SPEED_PERMISSION = "lokidumbcommands.gun.speed";

    private final LokiDumbCommandsPlugin plugin;

    public GunCommand(LokiDumbCommandsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && !sender.hasPermission(BASE_PERMISSION)) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
            return false;
        }

        if (!(sender instanceof Entity)) {
            sender.sendMessage(ChatColor.RED + "Only in-game entities can use this command");
            return false;
        }
        
        if (args.length > 0) {

            List<Entity> entities = Bukkit.selectEntities(sender, args[0]);
            if (entities.isEmpty()) {

                sender.sendMessage(ChatColor.RED + "No entities found");
                return false;

            } else if (entities.size() > 1 && !sender.hasPermission(MULTIPLE_PERMISSION)) {

                sender.sendMessage(ChatColor.RED + "Multiple entities found");
                return false;

            } else {

                if (!sender.hasPermission(ENTITIES_PERMISSION)) {
                    for (Entity entity : entities) {
                        if (entity.getType() != EntityType.PLAYER) {

                            sender.sendMessage(ChatColor.RED + "You do not have permission to target entities");
                            return false;

                        }
                    }
                }

                double speed = 5;
                if (args.length > 1 && sender.hasPermission(SPEED_PERMISSION)) {
                    try {

                        speed = Double.parseDouble(args[1]);
                        if (speed <= 0)
                            throw new NumberFormatException();

                    } catch (NumberFormatException e) {

                        sender.sendMessage(ChatColor.RED + args[1] + " is not a valid number");
                        return false;

                    }
                }

                for (Entity entity : entities) {
                    GunRunnable gun = new GunRunnable(plugin, (Entity) sender, entity);
                    gun.fire(speed);
                }
                return true;

            }
            
        }
        
        else {
            String usage = "<target>";
            if (sender.hasPermission(SPEED_PERMISSION))
                usage += " [speed]";
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + usage);
            return false;
        }
        
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        
        if (args.length == 1)
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        else
            return new LinkedList<>();
        
    }
    
}