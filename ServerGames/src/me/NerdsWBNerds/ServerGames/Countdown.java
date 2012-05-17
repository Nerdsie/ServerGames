package me.NerdsWBNerds.ServerGames;

import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GREEN;

public class Countdown extends GameState implements Runnable{
	int time = 15;
	ServerGames plugin;
	
	public Countdown(ServerGames p){
		plugin = p;
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
			System.out.println("Changing!");
			plugin.state = State.SET_UP;
			plugin.game = new Game(plugin);
			plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, plugin.game, 20L, 20L);
		}
		
		time--;
	}

}
