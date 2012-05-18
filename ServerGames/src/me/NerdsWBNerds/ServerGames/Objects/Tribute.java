package me.NerdsWBNerds.ServerGames.Objects;

import me.NerdsWBNerds.ServerGames.ServerGames;

import org.bukkit.entity.Player;

public class Tribute {
	public Player player;
	
	public Tribute(Player player){
		this.player = player;
	}
	
	public void die(){
		ServerGames.spectators.add(new Spectator(player));
		ServerGames.tributes.remove(this);
	}
}
