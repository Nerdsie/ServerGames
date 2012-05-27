package me.NerdsWBNerds.ServerGames.Timers;

import static org.bukkit.ChatColor.*;
import me.NerdsWBNerds.ServerGames.ServerGames;

public class Deathmatch extends CurrentState{
	ServerGames plugin;
	
	public Deathmatch(ServerGames p){
		plugin = p;
		time = 60 * 4;
		ServerGames.getCorn().getWorld().setTime(0);
		
		plugin.clearEnt();
	}
	
	public void run(){
		if(time==2 * 60){
			ServerGames.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 2 minutes remaining.");
		}if(time==1 * 60){
			ServerGames.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 1 minute remaining.");
		}if(time<=0){
			ServerGames.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " This Hunger Games has no victor.");
			plugin.startLobby();
		}
		
		time--;
	}
}
