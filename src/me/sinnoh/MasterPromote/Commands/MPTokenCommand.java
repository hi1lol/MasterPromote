package me.sinnoh.MasterPromote.Commands;

import me.sinnoh.MasterPromote.MPConfig;
import me.sinnoh.MasterPromote.MasterPromote;
import me.sinnoh.MasterPromote.Events.PlayerPromoteEvent.PROMOTIONTYPE;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MPTokenCommand implements CommandExecutor
{

	public MasterPromote plugin = MasterPromote.instance;
	

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if(args.length == 1)
    	{
	    	if(sender instanceof Player)
	    	{
	    		Player player = (Player) sender;
	    		if(plugin.token.contains("token." + args[0]))
	    		{
	    			if(player.hasPermission("MasterPromote.token.use." + args[0]))
	    			{
	    				String group = plugin.token.getString("token." + args[0] + ".group");
	    				plugin.getPermissionsHandler().promote(player, group, PROMOTIONTYPE.TOKEN);
	    				String msg = plugin.messages.getString("TokenUse").replace("<group>", group);
						player.sendMessage(msg.replace("&", "\u00A7"));
						System.out.println("[PLAYER_COMMAND] " + player.getName() + ": /token");
						System.out.println("[MasterPromote]Player " + player.getName() + " has used token " + args[0] + " and has been promotet to " + group + "!");
						String usage = plugin.token.getString("token." + args[0] + ".usage");
						int u = new Integer(usage);
						if(u >0)
						{
							if(u == 1)
							{
								plugin.token.set("token." + args[0],null );
								MPConfig.saveYamls();
							}
							else
							{
								u--;
								String us = String.valueOf(u);
								plugin.token.set("token." + args[0] + ".usage", us);
							}
							MPConfig.saveYamls();
							return true;
						}
						else
						{
							return true;
						}
	    			}
	    			else
	    			{

	        			player.sendMessage(plugin.messages.getString("NoPermissions").replace("&", "\u00A7"));
	        			System.out.println("[MasterPromote]Player " + player.getName() + " tried to use /token " + args[0]);
	    				return true;
	    			}
	    		}
	    		else
	    		{
	    			player.sendMessage(ChatColor.RED + "This Token does not exist!");
	    			return true;
	    		}
	    	}
	    	else
	    	{
	    		System.out.println("[MasterPromote]You have to be a player to use a token!");
	    		return true;
	    	}
    	}
		return false;
	}

}
