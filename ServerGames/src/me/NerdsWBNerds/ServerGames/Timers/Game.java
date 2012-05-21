package me.NerdsWBNerds.ServerGames.Timers;

import static org.bukkit.ChatColor.*;
import me.NerdsWBNerds.ServerGames.ServerGames;
import me.NerdsWBNerds.ServerGames.Objects.Chests;

public class Game extends CurrentState{
	ServerGames plugin;
	int ticks = 0;
	
	public Game(ServerGames p){
		plugin = p;
		time = 60 * 30;
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
		
		long wTime = ServerGames.cornacopia.getWorld().getTime();
		if(wTime >= 13200 && wTime < 13220){
			ServerGames.loaded.clear();
			Chests.resetChests();
			
			ServerGames.server.broadcastMessage(GOLD + "[ServerGames] " + GREEN + "All the chests have been refilled.");
		}
		
		ticks++;
		time--;
	}
}
