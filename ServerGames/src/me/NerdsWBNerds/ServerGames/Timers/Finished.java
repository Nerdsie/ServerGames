package me.NerdsWBNerds.ServerGames.Timers;

import static org.bukkit.ChatColor.*;
import me.NerdsWBNerds.ServerGames.ServerGames;

@SuppressWarnings("unused")
public class Finished extends CurrentState{
	ServerGames plugin;
	
	public Finished(ServerGames p){
		plugin = p;
		time = 10;
	}
	
	public void run(){
		if(time==0){
			plugin.startLobby();
		}
		
		time--;
	}
}
