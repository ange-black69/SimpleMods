package dries007.SimpleMods;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import net.minecraft.src.*;

public class Permissions
{
	public static String defaultRank;
	public static String opRank;
	
	public static NBTTagCompound playerData;
	public static NBTTagCompound rankData;
	public static NBTTagCompound worldData;
	
	public static HashSet<String> availablePermission = new HashSet<String>();
	public static HashSet<String> availableRanks = new HashSet<String>();
	
	public static NBTTagCompound defSettings = new NBTTagCompound();
	
	/**
	 * Returns all the available ranks
	 * 
	 * @return ranks All the ranks as string[]
	 */
	public static String[] getRanks()
	{
		return availableRanks.toArray(new String[availableRanks.size()]);
	}
	
	/**
	 * Returns all the available permissions
	 * 
	 * @return permissions All the permissions as string[]
	 */
	public static String[] getPermissions()
	{
		return availablePermission.toArray(new String[availablePermission.size()]);
	}
	
	public static void addPermission(String perm)
	{
		availablePermission.add(perm);
	}
	
	public static void addRank(String rank)
	{
		availableRanks.add(rank);
	}
	
	/**
	 * Returns a player's rank as string
	 * 
	 * @param player The EntityPlayer
	 * @return rank The player's rank
	 */
	public static String getRank(EntityPlayer player)
	{
		if(!ModLoader.getMinecraftServerInstance().isDedicatedServer()) return opRank;
		if(player.username.equalsIgnoreCase("Server")) return opRank;
		if(ModLoader.getMinecraftServerInstance().getConfigurationManager().getOps().contains(player.username.trim().toLowerCase())) return opRank;
		return playerData.getCompoundTag(player.username).getString("Rank");
	}
	
	/**
	 * Returns a player's rank as string
	 * 
	 * @param username The player's username
	 * @return rank The player's rank
	 */
	public static String getRank(String username)
	{
		if(username.equalsIgnoreCase("Server")) return opRank;
		if(ModLoader.getMinecraftServerInstance().getConfigurationManager().getOps().contains(username.trim().toLowerCase())) return opRank;
		EntityPlayer player = ModLoader.getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(username);
		if(player==null)
		{
			return null;
		}
		else
		{
			return playerData.getCompoundTag(username).getString("Rank");
		}
	}
	
	/**
	 * Returns true if a player has the requested permission
	 * 
	 * @param player The player
	 * @param reqPermission The requested permission
	 * @return boolean
	 */
	public static boolean hasPermission(EntityPlayer player, String reqPermission)
	{
		availablePermission.add(reqPermission);
		if(player.username.equalsIgnoreCase("Server")) return true;
		if (!ModLoader.getMinecraftServerInstance().isDedicatedServer()) return true;
		if (getRank(player).equals(opRank)) return true;
		if(ModLoader.getMinecraftServerInstance().getConfigurationManager().getOps().contains(player.username.trim().toLowerCase())) return true;
		NBTTagCompound permissions = rankData.getCompoundTag(getRank(player)).getCompoundTag("Permissions");
		
		Iterator i = permissions.getTags().iterator();
		
		while (i.hasNext())
		{
			NBTTagByte tag = (NBTTagByte) i.next();
			if (tag.getName().equalsIgnoreCase(reqPermission))
			{
				if (tag.data != 0)
				{
					return true;
				}
			}
		}
		
		
		i = playerData.getCompoundTag(player.username).getCompoundTag("Permissions").getTags().iterator();
		
		while (i.hasNext())
		{
			NBTTagByte tag = (NBTTagByte) i.next();
			if (tag.getName().equalsIgnoreCase(reqPermission))
			{
				if (tag.data != 0)
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Returns true if a player has the requested permission
	 * False too if username does,'t exist!
	 * 
	 * @param username The player's username
	 * @param reqPermission The requested permission
	 * @return boolean
	 */
	public static boolean hasPermission(String username, String reqPermission)
	{
		availablePermission.add(reqPermission);
		if(username.equalsIgnoreCase("Server")) return true;
		if(ModLoader.getMinecraftServerInstance().getConfigurationManager().getOps().contains(username.trim().toLowerCase())) return true;
		EntityPlayer player = ModLoader.getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(username);
		if(player==null)
		{
			return false;
		}
		else
		{
			return hasPermission(player, reqPermission);
		}
	}

	/**
	 * Returns a setting that corresponds with the given player's rank.
	 * If no such setting exists, null will be the response!
	 * 
	 * @param rank The EntityPlayer
	 * @param setting The name of the setting wanted
	 * @return String the setting
	 */
	public static NBTTagCompound getRankSetting(EntityPlayer player)
	{
		return rankData.getCompoundTag(getRank(player)).getCompoundTag("Settings");
	}
	
	/**
	 * Returns a setting that corresponds with the given player's rank.
	 * If no such setting exists, null will be the response!
	 * 
	 * @param rank The player's username
	 * @param setting The name of the setting wanted
	 * @return String the setting
	 */
	public static NBTTagCompound getRankSetting(String username)
	{
		EntityPlayer player = ModLoader.getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(username);
		if(player==null)
		{
			return null;
		}
		else
		{
			return getRankSetting(player);
		}
	}

	
	/**
	 * Sets the settings that correspond with a player's rank
	 * 
	 * WARING: DON'T MAKE A NEW NBTTagCompound, USE getRankSetting(player)
	 * 
	 * @param rank The EntityPlayer
	 * @param settings the changed settings
	 */
	public static void setRankSetting(EntityPlayer player, NBTTagCompound settings)
	{
		NBTTagCompound rank = getRankSetting(player);
		rank.setCompoundTag("Settings", settings);
		rankData.setCompoundTag(getRank(player), rank);
		Data.saveData(rankData, "rankData");
	}
	
	/**
	 * Sets the settings that correspond with a player's rank
	 * If no such setting exists, null will be the response!
	 * WARING: DON'T MAKE A NEW NBTTagCompound, USE getRankSetting(player)
	 * 
	 * @param rank The player's username
	 * @param settings the changed settings
	 */
	public static void setRankSetting(String username , NBTTagCompound settings)
	{
		EntityPlayer player = ModLoader.getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(username);
		if(player==null)
		{
			return;
		}
		else
		{
			setRankSetting(player, settings);
		}
	}
	
	/**
	 * Returns a setting that corresponds with the given player.
	 * If no such setting exists, null will be the response!
	 * 
	 * @param rank The EntityPlayer
	 * @param setting The name of the setting wanted
	 * @return String the setting
	 */
	public static NBTTagCompound getPlayerSetting(EntityPlayer player)
	{
		String username = player.username;
		if (!ModLoader.getMinecraftServerInstance().isDedicatedServer()) username = "player";
		return playerData.getCompoundTag(player.username).getCompoundTag("Settings");
	}
	
	/**
	 * Returns a setting that corresponds with the given player.
	 * If no such setting exists, null will be the response!
	 * 
	 * @param rank The player's username
	 * @param setting The name of the setting wanted
	 * @return String the setting
	 */
	public static NBTTagCompound getPlayerSetting(String username)
	{
		if (!ModLoader.getMinecraftServerInstance().isDedicatedServer()) username = "player";
		return playerData.getCompoundTag(username).getCompoundTag("Settings");
	}

	
	/**
	 * Sets the settings that correspond with a player's rank
	 * 
	 * WARING: DON'T MAKE A NEW NBTTagCompound, USE getPlayerSetting(player)
	 * 
	 * @param rank The EntityPlayer
	 * @param settings the changed settings
	 */
	public static void setPlayerSetting(EntityPlayer player, NBTTagCompound settings)
	{
		String username = player.username;
		if (!ModLoader.getMinecraftServerInstance().isDedicatedServer()) username = "player";
		NBTTagCompound rank = playerData.getCompoundTag(username);
		rank.setCompoundTag("Settings", settings);
		playerData.setCompoundTag(username, rank);
	}
	
	/**
	 * Sets the settings that correspond with a player's rank
	 * If no such setting exists, null will be the response!
	 * WARING: DON'T MAKE A NEW NBTTagCompound, USE getPlayerSetting(player)
	 * 
	 * @param rank The player's username
	 * @param settings the changed settings
	 */
	public static void setPlayerSetting(String username , NBTTagCompound settings)
	{
		if (!ModLoader.getMinecraftServerInstance().isDedicatedServer()) username = "player";
		NBTTagCompound rank = playerData.getCompoundTag(username);
		rank.setCompoundTag("Settings", settings);
		playerData.setCompoundTag(username, rank);
		Data.saveData(playerData, "playerData");
	}
	
	/**
	 * Every time a new rank gets made, the settings added by this function get added.
	 */
	public static void addDefaultSetting(NBTBase setting)
	{
		defSettings.setTag(setting.getName(), setting);
	}
	
	public static boolean newRank(String name, String nameToCopy)
	{
		Permissions.addRank(name);
		if (!rankData.hasKey(name))
		{
			rankData.setCompoundTag(name, rankData.getCompoundTag(name));
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static boolean newRank(String name)
	{
		Permissions.addRank(name);
		if (!rankData.hasKey(name))
		{
			NBTTagCompound rank = new NBTTagCompound();
			rank.setCompoundTag("Settings", defSettings);
			rank.setCompoundTag("Permissions", new NBTTagCompound());
			rankData.setCompoundTag(name, rank);
			return true;
		}
		else
		{
			return false;
		}
	}


}