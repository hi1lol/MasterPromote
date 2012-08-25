package me.sinnoh.MasterPromote.Commands;

import me.sinnoh.MasterPromote.MPConfig;
import me.sinnoh.MasterPromote.MasterPromote;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MPMPReloadCommand  implements CommandExecutor
{
	
	public MasterPromote plugin = MasterPromote.instance;

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
	{
    	if(sender instanceof Player)
    	{
    		Player player = (Player) sender;
    		if(player.hasPermission("MasterPromote.reload"))
    			{
    			MPConfig.loadYamls();
    			sender.sendMessage(plugin.messages.getString("Reload").replace("&", "\u00A7"));
				System.out.println("[PLAYER_COMMAND] " + player.getName() + ": /mpreload");
    			return true;

    			}
    		else
    		{
    			player.sendMessage(plugin.messages.getString("NoPermissions").replace("&", "\u00A7"));
    			System.out.println("[MasterPromote]Player " + player.getName() + " tried to use /MPreload");
    			return true;
    		}
    	}
    	else // if commandsender is not a player
    	{
    	MPConfig.loadYamls();
    	System.out.println("[MasterPromote]Config succssesfully reloaded!");
    	return true;
    	}
	}

}
