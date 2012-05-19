package me.NerdsWBNerds.ServerGames.Objects;

import org.bukkit.Material;

public class RandomItem {
	public static RandomItem air = new RandomItem(0, 200);
	public static RandomItem stonesword = new RandomItem(272, 5);
	public static RandomItem woodsword = new RandomItem(268, 7);
	public static RandomItem leatherheml = new RandomItem(298, 6);
	public static RandomItem leatherchest = new RandomItem(299, 6);
	public static RandomItem leatherpants = new RandomItem(300, 6);
	public static RandomItem leathershoes = new RandomItem(301, 6);
	public static RandomItem bow = new RandomItem(261, 5);
	public static RandomItem arrow = new RandomItem(262, 6);
	public static RandomItem apple = new RandomItem(260, 4);
	public static RandomItem compass = new RandomItem(345, 4);
	public static RandomItem iron = new RandomItem(Material.IRON_INGOT.getId(), 4);
	public static RandomItem diamond = new RandomItem(Material.DIAMOND.getId(), 2);
	
	public int id, chance;
	
	public RandomItem(int id, int chance){
		this.id = id;
		this.chance = chance;
	}
	
	public static RandomItem[] getAll(){
		RandomItem[] ret = { air, stonesword, woodsword, leatherchest, leatherheml, leatherpants, leathershoes, apple, arrow, bow, compass};
		return ret;
	}
}
