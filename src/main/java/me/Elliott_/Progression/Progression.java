package me.Elliott_.Progression;

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import me.Elliott_.Progression.lobby.Lobby;
import me.Elliott_.Progression.lobby.LobbyProtection;
import me.Elliott_.Progression.menus.Menus;
import me.Elliott_.Progression.menus.ProgressionMenu;
import me.Elliott_.Progression.menus.WorldEdititingMenu;
import me.Elliott_.Progression.menus.WorldMenu;
import me.Elliott_.Progression.world.NullChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Progression extends JavaPlugin {

    private CommandsManager<CommandSender> commands;
    public CommandsManagerRegistration cmdRegister;
    private static Progression instance;


    @Override
    public void onEnable() {
        setupCommands();
        registerEvents();
        getConfig().options().copyDefaults(true);
        saveConfig();
        instance = this;
        generateWorlds();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            this.commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                sender.sendMessage(ChatColor.RED + "Number expected, string received instead.");
            } else {
                sender.sendMessage(ChatColor.RED + "An error has occurred. See console.");
                e.printStackTrace();
            }
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }

        return true;
    }

    private void setupCommands() {
        this.commands = new CommandsManager<CommandSender>() {
            @Override
            public boolean hasPermission(CommandSender sender, String perm) {
                return sender instanceof ConsoleCommandSender || sender.hasPermission(perm);
            }
        };
        CommandsManagerRegistration cmdRegister = new CommandsManagerRegistration(this, this.commands);

    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new Menus(), this);
        getServer().getPluginManager().registerEvents(new ProgressionMenu(), this);
        getServer().getPluginManager().registerEvents(new WorldEdititingMenu(), this);
        getServer().getPluginManager().registerEvents(new WorldMenu(), this);

        getServer().getPluginManager().registerEvents(new Lobby(), this);
        getServer().getPluginManager().registerEvents(new LobbyProtection(), this);
    }

    public static Progression getPlugin() {
        return instance;
    }

    private void generateWorlds() {
        for (String name : getConfig().getConfigurationSection("worlds").getKeys(false)) {
            WorldCreator wc = new WorldCreator(name).generator(new NullChunkGenerator());
            getServer().createWorld(wc);
        }
    }

}
