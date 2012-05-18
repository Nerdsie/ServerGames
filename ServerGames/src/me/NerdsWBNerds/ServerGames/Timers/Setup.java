package me.NerdsWBNerds.ServerGames.Timers;

import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GREEN;

import me.NerdsWBNerds.ServerGames.ServerGames;

import org.bukkit.entity.Player;

public class Setup extends CurrentState implements Runnable{
	ServerGames plugin;
	
	public Setup(ServerGames p){
		plugin = p;
		time = 15;
	}
	
	@Override
	public void run() {
		if(plugin.state == State.SET_UP){
			if(time == 60){
				plugin.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 1 minute remaining.");
			}
			if(time == 45){
				plugin.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 45 seconds remaining.");
			}
			if(time == 30){
				plugin.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 30 seconds remaining.");
			}
			if(time == 15){
				plugin.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 15 seconds remaining.");
			}
			if(time <= 10 && time > 0){
				plugin.server.broadcastMessage(GOLD + "[ServerGames] " + GREEN + time +" seconds remaining.");
			}
			if(time == 0){
				plugin.state = State.IN_GAME;
				
				for(Player p : plugin.server.getOnlinePlayers()){
					p.setHealth(20);
					p.setFoodLevel(20);
				}
					
				plugin.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " Let the game begin!");
				time = 60 * 60;
			}
		}else{
			if(time == 0){
				plugin.state = State.DEATHMATCH;
			}
		}
		
		time--;
	}

}
