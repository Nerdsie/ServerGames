package me.NerdsWBNerds.ServerGames.Objects;

import me.NerdsWBNerds.ServerGames.ServerGames;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Random;

/**
 * Chests created by Brenhein
 */
public class Chests {


    // might not work
    public static void resetChests(ServerGames plugin, Location corn){
        // fishing rod, bow, arrow, diamond, wood sword, compass, iron sword, stone sword, gold sword, all armour
        int[] items = { 346, 261, 262, 260, 264, 268, 345, 267, 272, 283, 298, 299, 300, 301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315, 317 };
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>()
        for(World f : Bukkit.getWorlds()){
            if(f.getWorldType() == WorldType.NORMAL){
                BlockState[] blockState = f.getChunkAt(corn).getTileEntities();
                for(BlockState tileEntity : blockState){
                    Block chestBlock = tileEntity.getBlock();
                    Random rnd = new Random();
                    for(int i = 0; i < 7; i++){
                        arrayList.add(new ItemStack(items[rnd.nextInt(items.length)], 1));
                    }
                    setChest(chestBlock, arrayList);
                }
            }
        }
    }

    public static void setChest(Block chest, ArrayList<ItemStack> items){
        Chest chestTileEntity = null;
        try{ chestTileEntity = (Chest) chest; } catch(Exception e){  } // yeah yeah let java scream at us
        Inventory chestInv = chestTileEntity.getBlockInventory();
        for(ItemStack a : items){
            chestInv.addItem(a);
        }
    }
    
    
}
