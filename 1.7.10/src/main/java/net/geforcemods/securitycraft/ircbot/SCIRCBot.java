package net.geforcemods.securitycraft.ircbot;

import java.io.IOException;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import cpw.mods.fml.common.Loader;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class SCIRCBot extends PircBot{

	public SCIRCBot(String par1String){
		this.setName(par1String);
	}

	public void connectToChannel() throws IOException, IrcException, NickAlreadyInUseException{

		this.connect("irc.esper.net");
		this.joinChannel("#GeforceMods");
		this.setVerbose(true);

		if(Minecraft.getMinecraft().getSession().getToken() == null)
			sendMessage("#GeforceMods", "I am using a cracked client! (No Session token found.)");
	}

	@Override
	protected void onMessage(String channel, String sender, String login, String hostname, String message) {
		for(User user: this.getUsers(channel)){
			if(channel.matches("#GeforceMods") && (user.hasVoice() || user.isOp()) && (message.startsWith((this.getNick() + ":")) || message.startsWith((this.getNick() + ",")))){
				sendMessageToPlayer(EnumChatFormatting.YELLOW + "<" + sender + " (IRC) --> " + getPlayerFromName((this.getNick())).getCommandSenderName() + "> " + EnumChatFormatting.RESET + (message.startsWith(this.getNick() + ":") ? message.replace(this.getNick() + ":", "") : message.replace(this.getNick() + ",", "")), getPlayerFromName((this.getNick())));
				break;
			}
		}
	}

	@Override
	protected void onServerResponse(int code, String response)
	{
		if(code == 474 && response.contains("Cannot join channel (+b) - you are banned"))
			sendMessageToPlayer(StatCollector.translateToLocal("messages.irc.banned"), getPlayerFromName((this.getNick())));
	}

	@Override
	protected void onKick(String channel, String user, String login, String hostname, String userKicked, String reason){

		if(Minecraft.getMinecraft().getSession().getUsername().equals(userKicked.replaceFirst("SCUser_", "")))
		{
			if(mod_SecurityCraft.instance.getIrcBot(this.getNick().replaceFirst("SCUser_", "")) != null){
				mod_SecurityCraft.instance.getIrcBot(this.getNick().replaceFirst("SCUser_", "")).disconnect();
			}

			try{
				sendMessageToPlayer(EnumChatFormatting.RED + StatCollector.translateToLocal("messages.irc.disconnected").replace("#", reason), getPlayerFromName((this.getNick())));
			}catch(PlayerNotFoundException e){
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onJoin(String channel, String sender, String login, String hostname)
	{
		try
		{
			Thread.sleep(2000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		if(sender.equals(this.getNick()))
		{
			sendMessage("#GeforceMods", "Minecraft version: " + Loader.MC_VERSION);
			sendMessage("#GeforceMods", "Uses LookingGlass: " + (mod_SecurityCraft.configHandler.useLookingGlass ? "Yes" : "No"));
			sendMessage("#GeforceMods", "SecurityCraft version: " + mod_SecurityCraft.getVersion());
		}
	}

	private void sendMessageToPlayer(String par1String, EntityPlayer par2EntityPlayer){
		ChatComponentText component = new ChatComponentText(par1String);
		par2EntityPlayer.addChatComponentMessage(component);
	}

	private EntityPlayer getPlayerFromName(String name){
		EntityPlayerMP entityplayermp = MinecraftServer.getServer().getConfigurationManager().func_152612_a(name.replaceFirst("SCUser_", ""));//MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(name);

		if (entityplayermp == null)
		{
			throw new PlayerNotFoundException();
		}
		else
		{
			return entityplayermp;
		}
	}

}
