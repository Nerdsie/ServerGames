package me.NerdsWBNerds.ServerGames.Objects;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ShopItem {
	public int id, price, amount = 1;
	public Location location;
	public Block holder;
	public Item item;
	
	public ShopItem(Block h, int i, int p){
		id = i;
		price = p;
		holder = h;

		spawn();
	}
	
	public void setLocation(){
		location = holder.getLocation(); //simply clear everything after the comma.
		Vector vec = location.toVector();
		vec.add(new Vector(0.5,0.6,0.5));
		location = vec.toLocation(location.getWorld());
		
		if(item!=null){
			item.teleport(location);
		}
	}
	
	public void spawn(){
		setLocation();
		item = holder.getWorld().dropItem(location, new ItemStack(id));
		item.setVelocity(new Vector(0,0.1,0));
		setLocation();
	}
	
	public void checkForDups(){
		Chunk c = item.getLocation().getChunk();
		for(Entity e:c.getEntities()){
			if(e.getLocation().getBlock().equals(item.getLocation().getBlock()) && e.getType() == EntityType.DROPPED_ITEM && !e.equals(item)){
				e.remove();
			}
		}
	}
}
