package me.NerdsWBNerds.ServerGames;

import org.bukkit.entity.Player;

public class Tribute {
	Player player;
	public boolean alive = true;
	
	public Tribute(Player player){
		this.player = player;
	}
	
	public void die(){
		alive = false;
	}
}
