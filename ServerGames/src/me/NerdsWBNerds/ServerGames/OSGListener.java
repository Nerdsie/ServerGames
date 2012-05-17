package me.NerdsWBNerds.ServerGames;

import static org.bukkit.ChatColor.*;

import me.NerdsWBNerds.ServerGames.GameState.State;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class OSGListener implements Listener{
	public static ServerGames plugin;
	public OSGListener(ServerGames p){
		plugin = p;
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e){
		Player player = e.getPlayer();
		String[] args = e.getMessage().split(" ");
		
		if(args[0].equalsIgnoreCase("/add") && args.length == 1){
			e.setCancelled(true);
			if(!player.isOp()){
				tell(player, RED + "[ServerGames] You do not have permission to do this.");
				return;
			}
			
			if(plugin.tubes.size() < plugin.max){
				Location l = player.getLocation();
				plugin.tubes.add(new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ()));
			
				tell(player, GOLD + "[ServerGames]" + GREEN + " Tube location added.");
			}else{
				tell(player, RED + "[ServerGames] You have reached a max, you must delete a tube location.");
			}
		}
		
		if(args[0].equalsIgnoreCase("/delete")){
			e.setCancelled(true);
			if(args.length == 1){
				Location l = player.getLocation();
				Location check = new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
				
				for(Location i: plugin.tubes){
					if(i==check){
						plugin.tubes.remove(i);
						
						tell(player, GOLD + "[ServerGames] All tube locations at your spot have been removed.");
					}
				}
			}
		}
		
		if(args[0].equalsIgnoreCase("/start")){
			e.setCancelled(true);

			tell(player, GOLD + "[ServerGames]" + GREEN + " Countdown started.");
			
			int time = 60 * 4;
			
			plugin.game = new Countdown(plugin);
			plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, plugin.game, 20L, 20L);		
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e){
		Player player = e.getEntity();		
		e.setDeathMessage(null);	
		
		if(plugin.state == State.IN_GAME){
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 0, 0));
			plugin.getTribute(player).die();
			
			if(plugin.tributesAlive()==2){
				Tribute t[] = plugin.getRemaining();
				
				t[0].player.teleport(plugin.tubes.get(0));
				t[1].player.teleport(plugin.tubes.get(plugin.tubes.size() / 2 - 1));
			}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		Player player = e.getPlayer();
		
		plugin.tributes.add(new Tribute(player));
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e){
		Player player = e.getPlayer();
		
		plugin.tributes.remove(plugin.getTribute(player));
		
		if(State.IN_GAME == plugin.state){
			player.setHealth(0);
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		Player player = e.getPlayer();
		
		Location x = e.getFrom(), y = e.getTo();
		if(x.getBlockX() != y.getBlockX() || x.getBlockZ() != y.getBlockZ()){
			if(plugin.state == State.SET_UP){
				e.setTo(x);
			}
		}
	}
	
	public void tell(Player player, String m){
		player.sendMessage(m);
	}
}
