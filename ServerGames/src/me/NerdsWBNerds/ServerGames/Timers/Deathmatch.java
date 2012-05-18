package me.NerdsWBNerds.ServerGames.Timers;

import me.NerdsWBNerds.ServerGames.ServerGames;

public class Deathmatch extends CurrentState{
	ServerGames plugin;
	
	public Deathmatch(ServerGames p){
		plugin = p;
		time = 60 * 4;
	}
	
	public void run(){
		
		
		time--;
	}
}
