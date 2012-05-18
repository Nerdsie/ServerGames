package me.NerdsWBNerds.ServerGames.Timers;

import static org.bukkit.ChatColor.*;

import me.NerdsWBNerds.ServerGames.ServerGames;

public class Lobby extends CurrentState{
	ServerGames plugin;
	
	public Lobby(ServerGames p){
		plugin = p;
		time = 15;
	}
	
	@Override
	public void run() {
		if(time == 60 * 4){
			plugin.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 4 Minutes remaining.");
		}if(time == 60 * 3.5){
			plugin.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 3.5 Minutes remaining.");
		}if(time == 60 * 3){
			plugin.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 3 Minutes remaining.");
		}if(time == 60 * 2.5){
			plugin.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 2.5 Minutes remaining.");
		}if(time == 60 * 2){
			plugin.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 2 Minutes remaining.");
		}if(time == 60 * 1.5){
			plugin.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 1.5 Minutes remaining.");
		}if(time == 60 * 1){
			plugin.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 1 Minute remaining.");
		}if(time == 30){
			plugin.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 30 Seconds remaining.");
		}if(time == 15){
			plugin.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 15 Seconds remaining.");
		}if(time == 5){
			plugin.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 5 Seconds remaining.");
		}if(time == 0){
			plugin.startSetup();
		}
		
		time--;
	}

}
