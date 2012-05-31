package me.NerdsWBNerds.ServerGames.Timers;

import static org.bukkit.ChatColor.*;
import me.NerdsWBNerds.ServerGames.ServerGames;
import me.NerdsWBNerds.ServerGames.Objects.Chests;

public class Game extends CurrentState{
	ServerGames plugin;
	int ticks = 0;
	
	public Game(ServerGames p){
		plugin = p;
		time = 2 * ServerGames.server.getOnlinePlayers().length * 60;
	}
	
	public void run(){
		if(time == 60 * 45){
			ServerGames.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 45 minutes until deathmatch.");
		}if(time == 60 * 30){
			ServerGames.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 30 minutes until deathmatch.");
		}if(time == 60 * 15){
			ServerGames.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 15 minutes until deathmatch.");
		}if(time == 60 * 5){
			ServerGames.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 5 minutes until deathmatch.");
		}if(time == 30){
			ServerGames.server.broadcastMessage(GOLD + "[ServerGames]" + RED + " 30 seconds until deathmatch.");
		}if(time == 15){
			ServerGames.server.broadcastMessage(GOLD + "[ServerGames]" + RED + " 15 seconds until deathmatch.");
		}if(time <= 0){
			plugin.startDeath();
		}
		
		long wTime = ServerGames.getCorn().getWorld().getTime();
		if(wTime >= 13200 && wTime < 13220){
			ServerGames.loaded.clear();
			Chests.resetChests();
			
			ServerGames.server.broadcastMessage(GOLD + "[ServerGames] " + DARK_AQUA + "All the chests have been refilled.");
		}
		
		ticks++;
		time--;
	}
}
