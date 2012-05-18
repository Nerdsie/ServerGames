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
			loadConf();
		}
		
		for(Player p : server.getOnlinePlayers()){
			tributes.add(new Tribute(p));
		}
	}
	
	public void onDisable(){
		this.cancelTasks();
		
		saveConf();
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
		
		cancelTasks();
		
		state = State.SET_UP;
		game = new Setup(this);
		
		startTimer();
	}
	
	public void startGame(){
		cancelTasks();
		
		state = State.IN_GAME;
		game = new Game(this);
		
		startTimer();
	}
	
	public void startDeath(){
		cancelTasks();
		
		state = State.DEATHMATCH;
		game = new Deathmatch(this);
		
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
	
	public void saveConf(){
		File file = new File(path);
		new File("plugins/ServerGames").mkdir();
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
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
			oos.writeObject(t);
			oos.flush();
			oos.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public void loadConf(){
		try{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
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
}
