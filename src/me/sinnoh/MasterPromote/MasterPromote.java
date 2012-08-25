package me.sinnoh.MasterPromote;

import java.io.File;
import java.util.HashMap;
import java.util.Map;



import me.sinnoh.MasterPromote.Commands.MPApplyCommand;
import me.sinnoh.MasterPromote.Commands.MPBuyrankCommand;
import me.sinnoh.MasterPromote.Commands.MPConfirmCommand;
import me.sinnoh.MasterPromote.Commands.MPCreatetokenCommand;
import me.sinnoh.MasterPromote.Commands.MPMPReloadCommand;
import me.sinnoh.MasterPromote.Commands.MPRanksCommand;
import me.sinnoh.MasterPromote.Commands.MPTokenCommand;
import me.sinnoh.MasterPromote.Events.PlayerPromoteEvent.PROMOTIONTYPE;
import me.sinnoh.MasterPromote.Metrics.Metrics;
import me.sinnoh.MasterPromote.Metrics.Metrics.Graph;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class MasterPromote extends JavaPlugin 
{
	public static MasterPromote instance; //main instance
	public File configFile; //The config.yml file
	public File messagesFile; // The messages.yml file
	public File tokenFile; // The token.yml file
	public FileConfiguration config;
	public FileConfiguration messages;
	public FileConfiguration token;
	public Map<String, Long> timepromote = new HashMap<String, Long>(); //All players who are waiting to get promoted
	public Map<Player, String>confirm = new HashMap<Player, String>(); // All players who want to buy a rank
   
	//Vault
	public Economy economy = null;
    private Boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) 
        {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
    //Vault end
    
    public void prepareconfigfiles() //create/load the files
    {
		configFile = new File(getDataFolder(), "config.yml");
		messagesFile = new File(getDataFolder(), "messages.yml");
		tokenFile = new File(getDataFolder(), "token.yml");
		config = new YamlConfiguration();
		messages = new YamlConfiguration();
		token = new YamlConfiguration();
		MPConfig.createdefaults();
		MPConfig.loadYamls();
		MPConfig.updateconfig();
    }
    
    public void commands() //register the commands
    {
    	getCommand("apply").setExecutor(new MPApplyCommand());
    	getCommand("mpreload").setExecutor(new MPMPReloadCommand());
    	getCommand("token").setExecutor(new MPTokenCommand());
    	getCommand("createtoken").setExecutor(new MPCreatetokenCommand());
    	getCommand("buyrank").setExecutor(new MPBuyrankCommand());
    	getCommand("confirm").setExecutor(new MPConfirmCommand());
    	getCommand("ranks").setExecutor(new MPRanksCommand());
    }
    
    
	
	
	
	public void onEnable()
	{
		
		instance = this;//Initialize the main instance
		prepareconfigfiles();//Create/Load the files
		commands();//Register the commands		
		this.getServer().getPluginManager().registerEvents(new MasterPromoteListener(), this);//Register the Events		
		setupEconomy();//Enable Vault-Economy		
		PluginDescriptionFile pdfFile = this.getDescription();//Initialite the PluginDescriptionFile		
		MasterPromotePermissions.loadPermission();//Check for Permissions-Systems		
		sUtil.loadMap();//Load the HashMap from file		
		scheduler();//Start the scheduler
		
		if(MasterPromotePermissions.activePermissions.equalsIgnoreCase("none"))//deactivate the plugin if no permissions system is found 
		{
			sUtil.log(ChatColor.DARK_PURPLE + "[MasterPromote]" + ChatColor.GRAY + " No permissionssystem found!");
			Plugin MP = Bukkit.getPluginManager().getPlugin("MasterPromote");
			Bukkit.getPluginManager().disablePlugin(MP);
		}
		else
		{
		sUtil.log(ChatColor.DARK_PURPLE + "[MasterPromote]" + ChatColor.GRAY + " Using " + MasterPromotePermissions.activePermissions);
		sUtil.log(ChatColor.DARK_PURPLE + "[MasterPromote]" + ChatColor.GRAY + " v." + pdfFile.getVersion() + " enabled!");
		}
		
		setupMetrics();//Setup Plugin-Metrics

		
		
	}
	
	public void onDisable()
	{
		getServer().getScheduler().cancelTasks(this);//Cancel the Scheduler
		sUtil.saveMap();//Save the HashMap
		PluginDescriptionFile pdfFile = this.getDescription();
		sUtil.log(ChatColor.DARK_PURPLE + "[MasterPromote]" + ChatColor.GRAY + " v." + pdfFile.getVersion() + " disabled!");
	}
	
	public void scheduler()
	{
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() 
		{

			@Override
			public void run() 
			{
				for(String playername : timepromote.keySet())
				{
					if(sUtil.playerisonline(playername) || config.getBoolean("Time.CountOffline"))
					{
						Long timeleft = timepromote.get(playername);
						timeleft = timeleft -1;
						if(timeleft <=0)
						{
							String msg = messages.getString("PromotedAfterTime").replace("<group>", config.getString("Time.Group"));
							Bukkit.getPlayer(playername).sendMessage(msg.replace("&", "\247"));
							MasterPromotePermissions.promote(Bukkit.getPlayer(playername), config.getString("Time.Group"), PROMOTIONTYPE.TIME);
							timepromote.remove(playername);
						}
						else
						{
						timepromote.remove(playername);
						timepromote.put(playername, timeleft);
						}
					}
				}
			}

		}, 0L, 20L);
	}
	
	
	public void setupMetrics()
	{
		try
		{
		Metrics metrics = new Metrics(this);
		Graph graph = metrics.createGraph("Numbers of servers are using");
		graph.addPlotter(new Metrics.Plotter("Using Application System") 
		{
			
			@Override
			public int getValue() 
			{
				if(config.getBoolean("Apply.Enabled"))
				{
					return 1;
				}
				else
				{
					return 0;
				}
			}
		});
		graph.addPlotter(new Metrics.Plotter("Using Timed Promotion") 
		{
			
			@Override
			public int getValue() 
			{
				if(config.getBoolean("Time.Enabled"))
				{
					return 1;
				}
				else
				{
					return 0;
				}
			}
		});
		metrics.start();
		sUtil.log(ChatColor.DARK_PURPLE + "[MasterPromote]" + ChatColor.GRAY + "PluginMetrics enabled!");
		}catch(Exception e)
		{
			sUtil.log(ChatColor.DARK_PURPLE + "[MasterPromote]" + ChatColor.GRAY + " Failed to enable PluginMetrics");
		}
	}


	

	


}
