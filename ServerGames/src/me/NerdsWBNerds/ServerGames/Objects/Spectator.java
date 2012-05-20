package me.NerdsWBNerds.ServerGames.Objects;

import me.NerdsWBNerds.ServerGames.ServerGames;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Spectator {
	public Player player;
	
	public Spectator(Player p){
		player = p;
		ServerGames.hidePlayer(player);
		player.setGameMode(GameMode.CREATIVE);
		
		if(ServerGames.inNothing() || ServerGames.inLobby())
			player.teleport(ServerGames.waiting);
		else
			player.teleport(ServerGames.cornacopia);
	}
}
