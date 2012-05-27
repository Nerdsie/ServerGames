package me.NerdsWBNerds.ServerGames.Objects;

import me.NerdsWBNerds.ServerGames.ServerGames;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Tribute {
	public Player player;
	public Location start;
	
	public Tribute(Player player){
		this.player = player;
		player.setGameMode(GameMode.SURVIVAL);
	}
	
	public void die(){
		Location strike = new Location(ServerGames.getCorn().getWorld(), ServerGames.getCorn().getBlockX(), ServerGames.getCorn().getBlockY() + 20, ServerGames.getCorn().getZ());
		ServerGames.getCorn().getWorld().strikeLightning(strike);
		ServerGames.hidePlayer(player);
	}
	
	public void leave(){
		Location strike = new Location(ServerGames.getCorn().getWorld(), ServerGames.getCorn().getBlockX(), ServerGames.getCorn().getBlockY() + 20, ServerGames.getCorn().getZ());
		ServerGames.getCorn().getWorld().strikeLightning(strike);
	}
}
