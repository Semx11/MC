package me.semx11.minecars.commands;

import static org.bukkit.ChatColor.*;

import me.semx11.minecars.MineCars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCar implements CommandExecutor {

    MineCars plugin;

    public SpawnCar(MineCars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(RED + "You must be a player to execute this command!");
            return true;
        }

        Player p = (Player) sender;

        MineCars.spawnControllableArmorStand(p.getLocation());

        return true;
    }

}
