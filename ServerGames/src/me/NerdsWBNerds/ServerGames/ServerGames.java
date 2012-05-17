package me.NerdsWBNerds.ServerGames;

import java.util.ArrayList;
import java.util.logging.Logger;

import me.NerdsWBNerds.ServerGames.GameState.State;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerGames extends JavaPlugin {
	public OSGListener Listener = new OSGListener(this);
	public Server server;
	public Logger log;
	
	public GameState game = null;
	public State state = State.LOBBY;
	public int max = 24;
	public ArrayList<Location> tubes = new ArrayList<Location>();
	public ArrayList<Tribute> tributes = new ArrayList<Tribute>();
	
	public void onEnable(){
		server = this.getServer();
		log = this.getLogger();

		server.getPluginManager().registerEvents(Listener, this);
	}
	
	public void onDisable(){
		game = null;
	}
	
	public Tribute getTribute(Player player){
		for(Tribute t : tributes){
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
	
	public int tributesAlive(){
		int a = 0;
		for(Tribute i : tributes){
			if(i.alive)
				a++;
		}
		
		return a;
	}
	
	public Tribute[] getRemaining(){
		Tribute t[] = new Tribute[2];
		boolean first = true;
		
		for(Tribute i : tributes){
			if(i.alive){
				if(first){
					first = false;
					t[0] = i;
				}else{
					t[1] = i;
				}
			}
		}
		
		return t;
	}
}
