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
	}
	
	public void die(){
		ServerGames.tributes.remove(this);

		Location strike = new Location(ServerGames.cornacopia.getWorld(), ServerGames.cornacopia.getBlockX(), ServerGames.cornacopia.getBlockY() + 20, ServerGames.cornacopia.getZ());
		
		ServerGames.cornacopia.getWorld().strikeLightning(strike);

		ServerGames.hidePlayer(player);
        
        player.setGameMode(GameMode.CREATIVE);
	}
	
	public void leave(){
		ServerGames.tributes.remove(this);

		Location strike = new Location(ServerGames.cornacopia.getWorld(), ServerGames.cornacopia.getBlockX(), ServerGames.cornacopia.getBlockY() + 20, ServerGames.cornacopia.getZ());
		
		ServerGames.cornacopia.getWorld().strikeLightning(strike);
	}
}
