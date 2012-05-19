package me.NerdsWBNerds.ServerGames.Objects;

public class RandomItem {
	public static RandomItem air = new RandomItem(0, 100);
	public static RandomItem stonesword = new RandomItem(272, 5);
	public static RandomItem woodsword = new RandomItem(268, 10);
	public static RandomItem leatherheml = new RandomItem(298, 8);
	public static RandomItem leatherchest = new RandomItem(299, 8);
	public static RandomItem leatherpants = new RandomItem(300, 8);
	public static RandomItem leathershoes = new RandomItem(301, 8);
	public static RandomItem bow = new RandomItem(261, 5);
	public static RandomItem arrow = new RandomItem(262, 10);
	public static RandomItem apple = new RandomItem(260, 4);
	
	public int id, chance;
	
	public RandomItem(int id, int chance){
		this.id = id;
		this.chance = chance;
	}
}
