package me.NerdsWBNerds.ServerGames.Timers;

import me.NerdsWBNerds.ServerGames.ServerGames;

public class Game extends CurrentState{
	ServerGames plugin;
	
	public Game(ServerGames p){
		plugin = p;
		time = 60 * 45;
	}
	
	public void run(){
		
	}
}
