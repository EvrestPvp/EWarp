package therr.EWarp;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.floor;


public class EWarp extends JavaPlugin {

    private List<Warp> warplist = new LinkedList<>();
    public static final String WARPS_JSON = "plugins/wp/warps.json";


    @Override
    public void onEnable() {
        warplist = new LinkedList<>();
        getWarps();
        getLogger().info("EWarp active!");
    }

    @Override
    public void onDisable() {
        writeWarpsToYml();
        getLogger().info("EWarp eteint!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("warp")) {
            if (args.length > 0) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    for (Warp it : warplist) {
                        if (it.getName().equalsIgnoreCase(args[0])) {
                            player.teleport(it.getLocation());
                            player.sendMessage("Vous avez été téléporté à " + ChatColor.AQUA + args[0]);
                            return true;
                        }
                    }
                    player.sendMessage(ChatColor.RED + "Ce warp n'existe pas");
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Vous devez également écrire le nom du warp! §6/warp <nom>");
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("setwarp")) {
            if (args.length > 0) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;

                    for (Warp it : warplist) {
                        if (it.getName().equalsIgnoreCase(args[0])) {
                            player.sendMessage("Le warp " + ChatColor.AQUA + args[0] + ChatColor.WHITE + " existe déjà !");
                            return true;
                        }
                    }

                    warplist.add(new Warp(args[0], player.getLocation().getWorld().getName(), floor(player.getLocation().getX()) + 0.5, floor(player.getLocation().getY()), floor(player.getLocation().getZ()) + 0.5, 0, 0));

                    player.sendMessage("Warp " +
                            ChatColor.AQUA + args[0] +
                            ChatColor.WHITE + " créé à la localisation [" + (floor(player.getLocation().getX()) + 0.5) + ", " + floor(player.getLocation().getY()) + ", " + (floor(player.getLocation().getZ()) + 0.5) + "]");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Vous devez également écrire le nom du warp! §6/setwarp <nom>");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("warplist")) {
            Player player = null;
            if (sender instanceof Player) {
                player = (Player) sender;
            } else {
                return true;
            }

            if (warplist.isEmpty()) {
                player.sendMessage(ChatColor.RED + "Il n'y a aucun warp");
            } else {
                player.sendMessage(ChatColor.YELLOW + "Liste des warps:");
                for (Warp it : warplist) {
                    player.sendMessage(it.toString());
                }
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("delwarp")) {
            if (args.length > 0) {
                Warp toRem = null;
                Player player = null;
                if (sender instanceof Player) {
                    player = (Player) sender;
                    for (Warp it : warplist) {
                        toRem = it;
                        break;
                    }
                    if (toRem != null) {
                        warplist.remove(toRem);
                        player.sendMessage("Warp " + ChatColor.AQUA + toRem.getName() + ChatColor.WHITE + " supprimé");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Il n'y a pas de warp avec ce nom");
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Vous devez également écrire le nom du warp! §6/delwarp <nom>");
            }

            return true;
        } else if (cmd.getName().equalsIgnoreCase("warprename")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Il faut deux paramètre ! §6/warprename <ancien nom> <nouveau nom>");
            } else {
                for (Warp it : warplist) {
                    if (it.getName().equalsIgnoreCase(args[0])) {
                        it.setName(args[1]);
                        sender.sendMessage("Vous avez changé le nom du warp " + ChatColor.AQUA + args[0] + ChatColor.WHITE + " en " + ChatColor.AQUA + args[1]);
                        return true;
                    }
                }
                sender.sendMessage(ChatColor.RED + "Il n'y a pas de warp avec ce nom");
                return true;
            }

        } else if (cmd.getName().equalsIgnoreCase("warpUpdate")) {
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Vous devez également écrire le nom du warp! §6/warpupdate <nom>");
                return true;
            } else {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    for (Warp it : warplist) {
                        if (it.getName().equalsIgnoreCase(args[0])) {
                            it.setWorld(player.getWorld().getName());
                            it.setX(floor(player.getLocation().getX()) + 0.5);
                            it.setY(floor(player.getLocation().getY()));
                            it.setZ(floor(player.getLocation().getZ()) + 0.5);
                            player.sendMessage("Vous avez bien changé la localisation du warp  " + ChatColor.AQUA + args[0]);
                            return true;
                        }
                    }
                    sender.sendMessage(ChatColor.RED + "Vous n'avez pas de warp avec ce nom");
                }

                return true;
            }
        } else if(cmd.getName().equalsIgnoreCase("warpLoadFile")){
            writeWarpsToYml();
            sender.sendMessage("Warps enregistré dans le fichier");
            return true;
        }
        return false;
    }

    private void writeWarpsToYml(){
        File file = new File(getDataFolder(), "warps.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (Warp it : warplist) {
            config.set(it.getName() + ".world", it.getWorld());
            config.set(it.getName() + ".x", it.getX());
            config.set(it.getName() + ".y", it.getY());
            config.set(it.getName() + ".z", it.getZ());
            config.set(it.getName() + ".yaw", it.getYaw());
            config.set(it.getName() + ".pitch", it.getPitch());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getWarps(){
        File file = new File(getDataFolder(), "warps.yml");
        if (!file.exists()) {
            warplist = new LinkedList<>();
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String it : config.getKeys(false)) {
            warplist.add(new Warp(it, config.getString(it + ".world"), config.getDouble(it + ".x"), config.getDouble(it + ".y"), config.getDouble(it + ".z"), Float.parseFloat(config.getString(it + ".yaw")), Float.parseFloat(config.getString(it + ".pitch"))));
        }
    }
}
