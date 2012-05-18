package me.NerdsWBNerds.ServerGames;

import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GREEN;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

import me.NerdsWBNerds.ServerGames.Objects.Spectator;
import me.NerdsWBNerds.ServerGames.Objects.Tribute;
import me.NerdsWBNerds.ServerGames.Timers.Deathmatch;
import me.NerdsWBNerds.ServerGames.Timers.Finished;
import me.NerdsWBNerds.ServerGames.Timers.Game;
import me.NerdsWBNerds.ServerGames.Timers.Lobby;
import me.NerdsWBNerds.ServerGames.Timers.CurrentState;
import me.NerdsWBNerds.ServerGames.Timers.Setup;
import me.NerdsWBNerds.ServerGames.Timers.CurrentState.State;

import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerGames extends JavaPlugin implements Listener{
	public String path = "plugins/ServerGames";
	
	public SGListener Listener = new SGListener(this);
	public static Server server;
	public static Logger log;

	public int max = 24;
	public static State state = null;
	public static CurrentState game = null;
	public static ArrayList<Location> tubes = new ArrayList<Location>();
	public static ArrayList<Tribute> tributes = new ArrayList<Tribute>();
	public static ArrayList<Spectator> spectators = new ArrayList<Spectator>();
	public static Location cornacopia = null, waiting = null;
	
	public void onEnable(){
		server = this.getServer();
		log = this.getLogger();

		server.getPluginManager().registerEvents(Listener, this);
		
		new File(path).mkdir();
		load();
		
		for(Player p : server.getOnlinePlayers()){
			tributes.add(new Tribute(p));
		}
	}
	
	public void onDisable(){
		this.cancelTasks();
		
		save();
	}
	
	public void startLobby(){
		server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " Countdown started.");

		cancelTasks();
		
		state = State.LOBBY;
		game = new Lobby(this);
		
		startTimer();
	}
	
	public void startSetup(){
		int i = 0;
		
		for(Player p : server.getOnlinePlayers()){
			if(i >= ServerGames.tubes.size())
				i = 0;
			
			Location to = ServerGames.tubes.get(i);
			p.teleport(toCenter(to));
			p.setSprinting(false);
			p.setSneaking(false);
			p.setPassenger(null);
			
			i++;
		}

		cornacopia.getWorld().getEntities().clear();
		cornacopia.getWorld().setThundering(false);
		cornacopia.getWorld().setTime(0);
		cornacopia.getWorld().setWeatherDuration(0);
		cornacopia.getWorld().setStorm(false);
		
		cancelTasks();
		
		state = State.SET_UP;
		game = new Setup(this);
		
		startTimer();
	}
	
	public void startGame(){
		cancelTasks();
		
		state = State.IN_GAME;
		
		for(Player p : server.getOnlinePlayers()){
			p.setHealth(20);
			p.setFoodLevel(20);
		}
			
		server.broadcastMessage(GOLD + "[ServerGames]" + GREEN + " Let the game begin!");

		game = new Game(this);
		
		startTimer();
	}
	
	public void startDeath(){
		Location x = this.toCenter(ServerGames.tubes.get(ServerGames.tubes.size() / 2)), y = this.toCenter(ServerGames.tubes.get(0));
		Player xx = ServerGames.tributes.get(0).player, yy = ServerGames.tributes.get(1).player;
		xx.teleport(x);
		yy.teleport(y);
		Listener.tell(xx, GOLD + "[ServerGames] " + GREEN + "You have made it to the deathmatch.");
		Listener.tell(yy, GOLD + "[ServerGames] " + GREEN + "You have made it to the deathmatch.");
		
		cancelTasks();
		
		state = State.DEATHMATCH;
		game = new Deathmatch(this);
		
		startTimer();
	}
	
	public void startFinished(){
		cancelTasks();
		
		state = State.DONE;
		game = new Finished(this);
		
		startTimer();
	}
	
	public void stopAll(){
		game = null;
		state = null;
		cancelTasks();
	}
	
	public void startTimer(){
		getServer().getScheduler().scheduleAsyncRepeatingTask(this, game, 20L, 20L);
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
	
	public int toInt(String s){
		return Integer.parseInt(s);
	}

	public void save(){
		//////////// --------- Tubes ------------ ///////////////
		File file = new File(path + File.separator + "Tubes.loc");
		new File(path).mkdir();
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		 
		ArrayList<String> t = new ArrayList<String>();
		
		for(Location x: tubes){
			t.add(x.getWorld().getName() + "," + x.getBlockX() + "," + x.getBlockY() + "," + x.getBlockZ());
		}
		
		try{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path + File.separator + "Tubes.loc"));
			oos.writeObject(t);
			oos.flush();
			oos.close();
		}catch(Exception e){
			e.printStackTrace();
		}

		//////////// --------- Tubes End ------------ ///////////////
		//////////// --------- Cornacopia ------------ ///////////////
		
		if(cornacopia != null){
			file = new File(path + File.separator + "Corn.loc");
			new File(path).mkdir();
			if(!file.exists()){
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			 
			String c = "";
			c = (cornacopia.getWorld().getName() + "," + cornacopia.getBlockX() + "," + cornacopia.getBlockY() + "," + cornacopia.getBlockZ());
			
			cornacopia.getWorld().setSpawnLocation(cornacopia.getBlockX(), cornacopia.getBlockY(), cornacopia.getBlockZ());
			
			try{
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path + File.separator + "Corn.loc"));
				oos.writeObject(c);
				oos.flush();
				oos.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		//////////// --------- Cornacopia End ------------ ///////////////	
		//////////// --------- Waiting ------------ ///////////////

		if(waiting != null){
			file = new File(path + File.separator + "Wait.loc");
			new File(path).mkdir();
			if(!file.exists()){
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	
			String w = "";
			w = (waiting.getWorld().getName() + "," + waiting.getBlockX() + "," + waiting.getBlockY() + "," + waiting.getBlockZ());
			
			try{
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path + File.separator + "Wait.loc"));
				oos.writeObject(w);
				oos.flush();
				oos.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		//////////// --------- Waiting End ------------ ///////////////
	}
	
	@SuppressWarnings("unchecked")
	public void load(){
		//////////// --------- Tubes ------------ ///////////////
		try{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path + File.separator + "Tubes.loc"));
			Object result = ois.readObject();

			ArrayList<String> t = new ArrayList<String>();
			t = (ArrayList<String>)result;
			
			tubes = new ArrayList<Location>();
			
			for(String x: t){
				String[] split = x.split(",");
				tubes.add(new Location(server.getWorld(split[0]), toInt(split[1]), toInt(split[2]), toInt(split[3])));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		//////////// --------- Tubes End ------------ ///////////////
		//////////// --------- Cornacopia ------------ ///////////////
		try{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path + File.separator + "Corn.loc"));
			Object result = ois.readObject();
			
			String[] split = ((String)result).split(",");
			Location c = new Location(server.getWorld(split[0]), toInt(split[1]), toInt(split[2]), toInt(split[3]));
			
			ServerGames.cornacopia = c; 
			cornacopia.getWorld().setSpawnLocation(cornacopia.getBlockX(), cornacopia.getBlockY(), cornacopia.getBlockZ());
			
		}catch(Exception e){
			e.printStackTrace();
		}
		//////////// --------- Cornacopia End ------------ ///////////////	
		//////////// --------- Waiting ------------ ///////////////
		try{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path + File.separator + "Wait.loc"));
			Object result = ois.readObject();

			String[] split = ((String)result).split(",");
			Location w = new Location(server.getWorld(split[0]), toInt(split[1]), toInt(split[2]), toInt(split[3]));
			
			ServerGames.waiting = w; 
		}catch(Exception e){
			e.printStackTrace();
		}
		//////////// --------- Waiting End ------------ ///////////////
	}
}
