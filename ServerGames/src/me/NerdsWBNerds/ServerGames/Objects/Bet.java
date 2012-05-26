package me.NerdsWBNerds.ServerGames.Objects;

import org.bukkit.entity.Player;

public class Bet {
	public Player better, tribute;
	public int wager;
	
	public Bet(Player bet, Player trib, int wage){
		better = bet;
		tribute = trib;
		wager = wage;
	}
}
