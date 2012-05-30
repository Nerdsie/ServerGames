package me.NerdsWBNerds.ServerGames.Objects;

import org.bukkit.entity.Player;

public class Bet {
	public String better, tribute;
	public int wager;
	
	public Bet(Player bet, Player trib, int wage){
		better = bet.getName();
		tribute = trib.getName();
		wager = wage;
	}
}
