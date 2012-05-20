package me.NerdsWBNerds.ServerGames.Objects;

import me.NerdsWBNerds.ServerGames.ServerGames;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

/**
 * Chests created by Brenhein
 */
public class Chests {
    // might not work
    public static void resetChests(){
    	// fishing rod, bow, arrow, diamond, wood sword, compass, iron sword, stone sword, gold sword, all armour
    	RandomItem[] possible = RandomItem.getAll();
    	ArrayList<Integer> items = new ArrayList<Integer>();    	
    	
    	for(RandomItem i : possible){
    		for(int x = 0; x < i.chance; x++){
    			items.add(i.id);
    		}
    	}
    	
    	for(Chunk c : ServerGames.cornacopia.getWorld().getLoadedChunks()){
    		if(!ServerGames.loaded.contains(c)){
	    		BlockState[] blockState = c.getTileEntities();
	    		for(BlockState tileEntity : blockState){
	    			if(tileEntity instanceof Chest){    	
	    				ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
	    				Chest chestBlock = (Chest)tileEntity;
	    				Random rnd = new Random();
	    				for(int i = 0; i < 27; i++){
	    					arrayList.add(new ItemStack(items.get(rnd.nextInt(items.size())), 1));
	    				}
	    				setChest(chestBlock, arrayList);
	    			}
	    		}
        		ServerGames.loaded.add(c);
    		}
        }
    }
    
    public static void resetChests(Chunk c){
    	// fishing rod, bow, arrow, diamond, wood sword, compass, iron sword, stone sword, gold sword, all armour
    	RandomItem[] possible = RandomItem.getAll();
    	ArrayList<Integer> items = new ArrayList<Integer>();    	
    	
    	for(RandomItem i : possible){
    		for(int x = 0; x < i.chance; x++){
    			items.add(i.id);
    		}
    	}
    	
    	if(ServerGames.loaded.contains(c)){
    		return;
    	}else{
    		ServerGames.loaded.add(c);
    	}
    	
   		BlockState[] blockState = c.getTileEntities();
   		for(BlockState tileEntity : blockState){
   			if(tileEntity instanceof Chest){    	
   				ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
   				Chest chestBlock = (Chest)tileEntity;
   				Random rnd = new Random();
   				for(int i = 0; i < 27; i++){
   					arrayList.add(new ItemStack(items.get(rnd.nextInt(items.size())), 1));
   				}
   				setChest(chestBlock, arrayList);
   			}
   		}
    }

    public static void setChest(Chest c, ArrayList<ItemStack> items){
    	try{
    		Chest chest = (Chest) c;
            ItemStack[] stuff = new ItemStack[27];
            
            for(int i = 0; i < 27; i++){
            	stuff[i] = items.get(i);
            }

            chest.getInventory().clear();  
            chest.getInventory().setContents(stuff);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
}
