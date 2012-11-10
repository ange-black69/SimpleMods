package dries007.SimpleMods.Core;

import java.util.Iterator;
import java.util.List;

import dries007.SimpleMods.Permissions;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;

public class CommandAddrank extends CommandBase
{
    public String getCommandName()
    {
        return "addrank";
    }

    public String getCommandUsage(ICommandSender sender)
    {
    	return "/" + getCommandName() + " <name> [copyOtherRankName]";
    }

    public List getCommandAliases()
    {
        return null;
    }
    
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
    	return Permissions.hasPermission(sender.getCommandSenderName(), "SC.admin");
    }
    
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if(args.length == 2)
        {
        	String msg = "";
        	for(String st : Permissions.getRanks()) msg = msg + st + ", ";
        	sender.sendChatToPlayer("List of ranks: " + msg);
        	return getListOfStringsMatchingLastWord(args, Permissions.getRanks());
        }
        return null;
    }
    
    public void processCommand(ICommandSender sender, String[] args)
    {
    	if(args.length==0) throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
    	if(args.length>2) throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
    	if(!Permissions.rankData.hasKey(args[0]))
    	{
    		if(args.length==2)
    		{
    			Iterator ranks = Permissions.rankData.getTags().iterator();
            	int i = 0;
            	while (ranks.hasNext())
            	{
            		NBTTagCompound rank = (NBTTagCompound) ranks.next();
            		if(rank.getName().trim().equalsIgnoreCase(args[1]))
            		{
            			if(Permissions.newRank(args[0], args[1]))
            			{
            				sender.sendChatToPlayer("Rank " + args[0] + " made by copying " + args[1] + ".");
            				return;
            			}
            			else
            			{
            				sender.sendChatToPlayer("Error. Coulden't make " + args[0] + " by copying " + args[1] + "!");
            			}
            		}
            	}
            	sender.sendChatToPlayer("Rank to copy (" + args[1] + ") doesn't exist.");
    		}
    		else
    		{
    			if(Permissions.newRank(args[0]))
    			{
    				sender.sendChatToPlayer("Rank " + args[0] + " made.");
    				return;
    			}
    			else
    			{
    				sender.sendChatToPlayer("Error. Coulden't make " + args[0] + "!");
    			}
    		}
    	}
    	else
    	{
    		sender.sendChatToPlayer("Rank " + args[0] + " already exists.");
    	}
    }
}
