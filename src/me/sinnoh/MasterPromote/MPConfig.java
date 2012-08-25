package me.sinnoh.MasterPromote;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MPConfig 
{
	public static MasterPromote plugin = MasterPromote.instance;
	
	
	public static void createdefaults()
	{
		try
		{
			if(!plugin.configFile.exists())
			{
				plugin.configFile.getParentFile().mkdirs();
				copy(plugin.getResource("config.yml"), plugin.configFile);
			}
			if(!plugin.messagesFile.exists())
			{
				plugin.messagesFile.getParentFile().mkdirs();
				copy(plugin.getResource("messages.yml"), plugin.messagesFile);
			}
			if(!plugin.tokenFile.exists())
			{
				plugin.tokenFile.getParentFile().mkdirs();
				copy(plugin.getResource("token.yml"), plugin.tokenFile);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void copy(InputStream in, File file)
	{
	    try 
	    {
	        OutputStream out = new FileOutputStream(file);
	        byte[] buf = new byte[1024];
	        int len;
	        while((len=in.read(buf))>0)
	        {
	            out.write(buf,0,len);
	        }
	        out.close();
	        in.close();
	    } catch (Exception e) 
	    {
	        e.printStackTrace();
	    }
	}
	
	public static void loadYamls() 
	{
	    try {
	        plugin.config.load(plugin.configFile);
	        plugin.messages.load(plugin.messagesFile);
	        plugin.token.load(plugin.tokenFile);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	public static void saveYamls() 
	{
	    try 
	    {
	        plugin.config.save(plugin.configFile);
	        plugin.messages.save(plugin.messagesFile);
	        plugin.token.save(plugin.tokenFile);
	    } catch (IOException e)
	    {
	        e.printStackTrace();
	    }
	}
	
	public static void updateconfig()
	{
		if(!plugin.config.getString("configversion").equals("1.5"))
		{
			if(plugin.config.getString("configversion").equals("1.3"))
			{
				plugin.config.set("PromoteSyntax", "none");
				plugin.config.set("configversion", "1.4.1");
			}
			saveYamls();
			if(plugin.config.getString("configversion").equals("1.4.1"))
			{
				plugin.config.set("configversion", "1.4.2");
			}
			saveYamls();
			if(plugin.config.getString("configversion").equals("1.4.2"))
			{
				plugin.config.set("configversion", "1.4.3");
			}
			saveYamls();
			if(plugin.config.getString("configversion").equals("1.4.3"))
			{
				plugin.config.set("Time.CountOffline", Boolean.FALSE);
				plugin.config.set("configversion", "1.5");
			}
			saveYamls();
		}
		if(plugin.config.getString("token") != null)
		{
			for(String token : plugin.config.getConfigurationSection("token").getKeys(false))
			{
				String usage = plugin.config.getString("token." + token + ".usage"); 
				String group = plugin.config.getString("token." + token + ".group"); 
				plugin.token.set("token." + token + ".usage", usage);
				plugin.token.set("token." + token + ".group", group);
			}
			plugin.config.set("token", null);
			saveYamls();
		}

	}
	
	

}
