package me.sinnoh.MasterPromote.Commands;


import me.sinnoh.MasterPromote.MasterPromote;
import me.sinnoh.MasterPromote.MasterPromotePermissions;
import me.sinnoh.MasterPromote.Events.PlayerPromoteEvent.PROMOTIONTYPE;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MPApplyCommand implements CommandExecutor 
{
	
	public static MasterPromote plugin = MasterPromote.instance;

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) 
	{
		String pw = plugin.config.getString("Apply.Password");
		String defaultgroup = plugin.config.getString("Apply.Defaultgroup");
		String group = plugin.config.getString("Apply.Group");
		if(sender instanceof Player)
		{
			Player player = (Player) sender;
			if(plugin.config.getBoolean("Apply.Enabled"))
			{
				if(!player.hasPermission("MasterPromote.member"))
				{
					if(args.length == 1)
					{
						if(args[0].equals(pw))
						{
							MasterPromotePermissions.promote(player, group, PROMOTIONTYPE.APPLY);
							String msg = plugin.messages.getString("UsedPW").replace("&", "\u00A7");
							player.sendMessage(msg.replace("<group>", group));
							System.out.println("[PLAYER_COMMAND] " + player.getName() + ": /apply");
							System.out.println("[MasterPromote]User " + player.getName() + " has been promoted to " + group + " group!");
							return true;
						}
						else
						{
							if(plugin.config.getBoolean("Apply.KickWrongPW"))
							{
								player.kickPlayer(plugin.messages.getString("WrongPW").replace("&", "\u00A7"));
								System.out.println("[MasterPromote]Player " + player.getName() + " has been kicked for typing in the wrong password");
								return true;
							}
							else
							{
							player.sendMessage(plugin.messages.getString("WrongPW").replace("&", "\u00A7"));
							return true;
							}
						}
					}
				}
				else
				{
					player.sendMessage(ChatColor.RED + "You have to be in group " + defaultgroup + " to use this command!");
					return true;
				}
			}
			else
			{
				player.sendMessage(plugin.messages.getString("FunctionDisabled").replace("&", "\u00A7"));
				return true;
			}
		}
		else
		{
			System.out.println("You can use this command only as a player");
			return true;
		}
		return false;
	}

}
