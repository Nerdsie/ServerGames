package me.NerdsWBNerds.ServerGames.Timers;

import static org.bukkit.ChatColor.*;
import me.NerdsWBNerds.ServerGames.ServerGames;

public class Deathmatch extends CurrentState{
	ServerGames plugin;
	
	public Deathmatch(ServerGames p){
		plugin = p;
		time = 60 * 2;
	}
	
	public void run(){
		if(time==2){
			plugin.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 2 minutes remaining.");
			plugin.startLobby();
		}if(time==1){
			plugin.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 1 minute remaining.");
			plugin.startLobby();
		}if(time==0){
			plugin.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " This Hunger Games has no victor.");
			plugin.startLobby();
		}
		
		time--;
	}
}
