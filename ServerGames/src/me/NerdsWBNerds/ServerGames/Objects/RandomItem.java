package me.NerdsWBNerds.ServerGames.Objects;

import org.bukkit.Material;

public class RandomItem {
	public static RandomItem air = new RandomItem(0, 300);
	public static RandomItem stonesword = new RandomItem(272, 4);
	public static RandomItem woodsword = new RandomItem(268, 6);
	public static RandomItem leatherheml = new RandomItem(298, 2);
	public static RandomItem leatherchest = new RandomItem(299, 2);
	public static RandomItem leatherpants = new RandomItem(300, 2);
	public static RandomItem leathershoes = new RandomItem(301, 2);
	public static RandomItem bow = new RandomItem(261, 5);
	public static RandomItem arrow = new RandomItem(262, 9);
	public static RandomItem apple = new RandomItem(260, 8);
	public static RandomItem bread = new RandomItem(Material.BREAD, 6);
	public static RandomItem compass = new RandomItem(345, 3);
    public static RandomItem diamond = new RandomItem(264, 1);
    public static RandomItem stick = new RandomItem(Material.STICK, 5);
    public static RandomItem iron = new RandomItem(265, 4);
    public static RandomItem gold = new RandomItem(266, 4);
    public static RandomItem chainhelm = new RandomItem(302, 1);
    public static RandomItem chainchest = new RandomItem(303, 1);
    public static RandomItem chainpants = new RandomItem(304, 1);
    public static RandomItem chainboots = new RandomItem(305, 1);
    public static RandomItem goldhelm = new RandomItem(314, 1);
    public static RandomItem goldchest = new RandomItem(315, 1);
    public static RandomItem goldlegs = new RandomItem(316, 1);
    public static RandomItem goldboots = new RandomItem(317, 1);
    public static RandomItem flint = new RandomItem(Material.FLINT, 6);
    public static RandomItem goldSword = new RandomItem(283, 4);
    public static RandomItem feather = new RandomItem(Material.FEATHER, 5);
    public static RandomItem shear = new RandomItem(Material.SHEARS, 4);
    public static RandomItem soup = new RandomItem(Material.MUSHROOM_SOUP, 4);
    public static RandomItem gApple = new RandomItem(Material.GOLDEN_APPLE, 1);
    
    //public static RandomItem gApple = bew
	
	public int id, chance;

	public RandomItem(int id, int chance){
		this.id = id;
		this.chance = chance;
	}
	
	public RandomItem(Material id, int chance){
		this.id = id.getId();
		this.chance = chance;
	}
	
	public static RandomItem[] getAll(){
		RandomItem[] ret = { gApple, soup, shear, flint, feather, air, stonesword, woodsword, leatherchest, leatherheml, leatherpants, leathershoes, apple, arrow, bow, compass, diamond, stick, chainboots, chainchest, chainhelm, chainpants, goldboots, goldchest, goldhelm, goldlegs, iron, gold, goldSword, bread};
		return ret;
	}
}
