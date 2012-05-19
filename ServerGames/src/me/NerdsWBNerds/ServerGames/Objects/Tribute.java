package me.NerdsWBNerds.ServerGames.Objects;

import me.NerdsWBNerds.ServerGames.ServerGames;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Tribute {
	public Player player;
	
	public Tribute(Player player){
		this.player = player;
	}
	
	public void die(){
		ServerGames.tributes.remove(this);

		Location strike = ServerGames.cornacopia;
		strike.setX(ServerGames.cornacopia.getY() + 50);
		
		ServerGames.cornacopia.getWorld().strikeLightning(strike);
		
		if(ServerGames.isOwner(player)){
			return;
		}
		
		ServerGames.hidePlayer(player);
	}
	
	public void leave(){
		ServerGames.tributes.remove(this);

		Location strike = ServerGames.cornacopia;
		strike.setX(ServerGames.cornacopia.getY() + 50);
		
		ServerGames.cornacopia.getWorld().strikeLightning(strike);
	}
}
