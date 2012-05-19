package me.NerdsWBNerds.ServerGames.Objects;

public class RandomItem {
	public static RandomItem air = new RandomItem(0, 300);
	public static RandomItem stonesword = new RandomItem(272, 10);
	public static RandomItem woodsword = new RandomItem(268, 10);
	public static RandomItem leatherheml = new RandomItem(298, 8);
	public static RandomItem leatherchest = new RandomItem(299, 8);
	public static RandomItem leatherpants = new RandomItem(300, 8);
	public static RandomItem leathershoes = new RandomItem(301, 8);
	public static RandomItem bow = new RandomItem(261, 5);
	public static RandomItem arrow = new RandomItem(262, 10);
	public static RandomItem apple = new RandomItem(260, 4);
	public static RandomItem compass = new RandomItem(345, 4);
    public static RandomItem diamond = new RandomItem(264, 2);
    public static RandomItem iron = new RandomItem(265, 7);
    public static RandomItem gold = new RandomItem(266, 7);
    public static RandomItem ironhelm = new RandomItem(306, 7);
    public static RandomItem ironbody = new RandomItem(307, 7);
    public static RandomItem ironlegs = new RandomItem(308, 7);
    public static RandomItem ironboots = new RandomItem(309, 7);
    public static RandomItem chainhelm = new RandomItem(302, 7);
    public static RandomItem chainchest = new RandomItem(303, 7);
    public static RandomItem chainpants = new RandomItem(304, 7);
    public static RandomItem chainboots = new RandomItem(305, 7);
    public static RandomItem goldhelm = new RandomItem(314, 7);
    public static RandomItem goldchest = new RandomItem(315, 7);
    public static RandomItem goldlegs = new RandomItem(316, 7);
    public static RandomItem goldboots = new RandomItem(317, 7);
    
    public static RandomItem ironSword = new RandomItem(267, 7);
    public static RandomItem goldSword = new RandomItem(283, 5);
    
    //public static RandomItem gApple = bew
	
	public int id, chance;
	
	public RandomItem(int id, int chance){
		this.id = id;
		this.chance = chance;
	}
	
	public static RandomItem[] getAll(){
		RandomItem[] ret = { air, stonesword, woodsword, leatherchest, leatherheml, leatherpants, leathershoes, apple, arrow, bow, compass, diamond, ironhelm, ironlegs, ironbody, ironboots, chainboots, chainchest, chainhelm, chainpants, goldboots, goldchest, goldhelm, goldlegs, iron, gold, goldSword, ironSword };
		return ret;
	}
}
