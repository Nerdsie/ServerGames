package me.NerdsWBNerds.ServerGames.Timers;

import static org.bukkit.ChatColor.*;
import me.NerdsWBNerds.ServerGames.ServerGames;

public class Game extends CurrentState{
	ServerGames plugin;
	
	public Game(ServerGames p){
		plugin = p;
		time = 60 * 15;
	}
	
	public void run(){
		if(time == 60 * 45){
			ServerGames.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 45 minutes remaining.");
		}if(time == 60 * 30){
			ServerGames.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 30 minutes remaining.");
		}if(time == 60 * 15){
			ServerGames.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 15 minutes remaining.");
		}if(time == 60 * 5){
			ServerGames.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 5 minutes remaining.");
		}if(time == 0){
			plugin.startDeath();
		}
		
		time--;
	}
}
