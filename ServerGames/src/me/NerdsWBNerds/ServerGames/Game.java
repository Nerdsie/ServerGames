package me.NerdsWBNerds.ServerGames;

import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GREEN;

public class Game extends GameState implements Runnable{
	ServerGames plugin;
	int time = 15;
	
	public Game(ServerGames p){
		plugin = p;
	}
	
	@Override
	public void run() {
		if(time == 60){
			plugin.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 1 Minute remaining.");
		}
		if(time == 45){
			plugin.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 45 Seconds remaining.");
		}
		if(time == 30){
			plugin.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 30 Seconds remaining.");
		}
		if(time == 15){
			plugin.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " 15 Seconds remaining.");
		}
		if(time <= 10 && time > 0){
			plugin.server.broadcastMessage(GOLD + "[ServerGames] " + GREEN + time +" Seconds remaining.");
		}
		if(time == 0){
			plugin.server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " Let the game begin!");
			plugin.state = State.IN_GAME;
		}
		
		time--;
	}

}
