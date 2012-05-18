package me.NerdsWBNerds.ServerGames.Timers;

public class CurrentState implements Runnable {
	public enum State{
		LOBBY, IN_GAME, DEATHMATCH, SET_UP, DONE
	}
	
	public int time = 0;
	
	public void run() {
		
	}		
}
