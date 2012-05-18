package me.NerdsWBNerds.ServerGames;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

import me.NerdsWBNerds.ServerGames.CurrentState.State;
import me.NerdsWBNerds.ServerGames.Objects.Spectator;
import me.NerdsWBNerds.ServerGames.Objects.Tribute;

import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerGames extends JavaPlugin implements Listener{
	public String path = "plugins/ServerGames" + File.separator + "Tubes.loc";
	
	public SGListener Listener = new SGListener(this);
	public Server server;
	public Logger log;
	
	public State state = null;
	public CurrentState game = null;
	public int max = 24;
	public static ArrayList<Location> tubes = new ArrayList<Location>();
	public static ArrayList<Tribute> tributes = new ArrayList<Tribute>();
	public static ArrayList<Spectator> spectators = new ArrayList<Spectator>();
	
	public void onEnable(){
		server = this.getServer();
		log = this.getLogger();

		server.getPluginManager().registerEvents(Listener, this);
		
		File file = new File(path);
		new File("plugins/ServerGames").mkdir();
		if(file.exists()){
			load();
		}
		
		for(Player p : server.getOnlinePlayers()){
			tributes.add(new Tribute(p));
		}
	}
	
	public void onDisable(){
		this.cancelTasks();
		
		save();
	}
	
	public void save(){
		File file = new File(path);
		new File("plugins/ServerGames").mkdir();
		   if(!file.exists()){
		   	try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
			oos.writeObject(tubes);
			oos.flush();
			oos.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void load(){
		try{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
			Object result = ois.readObject();
			tubes = (ArrayList<Location>)result;
		}catch(Exception e){}
	}

	public Tribute getTribute(Player player){
		for(Tribute t : tributes){
			if(t.player == player)
				return t;
		}
		
		return null;
	}

	public Spectator getSpectator(Player player){
		for(Spectator t : spectators){
			if(t.player == player)
				return t;
		}
		
		return null;
	}
	
	public void tpall(Location l){
		for(Player p:server.getOnlinePlayers()){
			p.teleport(l);
		}
	}
	
	public void cancelTasks(){
		this.getServer().getScheduler().cancelAllTasks();
	}
	
	public void showPlayer(Player player){
		for(Player p : server.getOnlinePlayers()){
			p.showPlayer(player);
		}
	}
	
	public void hidePlayer(Player player){
		for(Player p : server.getOnlinePlayers()){
			p.hidePlayer(player);
		}
	}
	
	public void hideFrom(Player player){
		for(Player p : server.getOnlinePlayers()){
			player.hidePlayer(p);
		}
	}
	
	public void showFor(Player player){
		for(Player p : server.getOnlinePlayers()){
			player.showPlayer(p);
		}
	}
	
	public Location toCenter(Location l){
		return new Location(l.getWorld(), l.getBlockX() + .5, l.getBlockY(), l.getBlockZ() + .5);
	}
	
	public void removeTribute(Player p){
		tributes.remove(this.getTribute(p));
	}
	
	public void removeSpectator(Player p){
		spectators.remove(getSpectator(p));
	}
}
