package me.NerdsWBNerds.ServerGames.Timers;

import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GREEN;

import me.NerdsWBNerds.ServerGames.ServerGames;

public class Setup extends CurrentState implements Runnable{
	ServerGames plugin;
	
	public Setup(ServerGames p){
		plugin = p;
		time = 60;
	}
	
	@Override
	public void run() {
		if(ServerGames.inSetup()){
			if(time == 60){
				ServerGames.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 1 minute remaining.");
			}
			if(time == 45){
				ServerGames.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 45 seconds remaining.");
			}
			if(time == 30){
				ServerGames.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 30 seconds remaining.");
			}
			if(time == 15){
				ServerGames.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 15 seconds remaining.");
			}
			if(time <= 10 && time > 0){
				ServerGames.server.broadcastMessage(GOLD + "[ServerGames] " + GREEN + time +" seconds remaining.");
			}
			if(time == 0){
				plugin.startGame();
			}
		}else{
			if(time == 0){
				ServerGames.state = State.DEATHMATCH;
			}
		}
		
		time--;
	}

}
