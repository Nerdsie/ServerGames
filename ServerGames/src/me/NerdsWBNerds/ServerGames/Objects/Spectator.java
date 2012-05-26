package me.NerdsWBNerds.ServerGames.Objects;

import me.NerdsWBNerds.ServerGames.ServerGames;

import org.bukkit.entity.Player;

public class Spectator {
	public Player player;
	
	public Spectator(Player p){
		player = p;
		ServerGames.hidePlayer(player);
		
		for(Spectator s : ServerGames.spectators){
			ServerGames.hidePlayer(s.player);
		}
	}
}
