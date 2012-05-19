package me.NerdsWBNerds.ServerGames.Objects;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

/**
 * Chests created by Brenhein
 */
public class Chests {


    // WIP -bren
    // DO NOT USE!!!!!1111111oneoneone!!111shift!!!111
    public void resetChests(Plugin plugin){
        for(World f : Bukkit.getWorlds()){
            if(f.getWorldType() == WorldType.NORMAL){
                //f.getEntities()
            }
        }
    }

    public void setChest(Block chest, ArrayList<ItemStack> items){
        Chest chestTileEntity = null;
        try{ chestTileEntity = (Chest) chest; } catch(Exception e){  } // yeah yeah let java scream at us
        Inventory chestInv = chestTileEntity.getBlockInventory();
        for(ItemStack a : items){
            chestInv.addItem(a);
        }
    }
    
    
}
