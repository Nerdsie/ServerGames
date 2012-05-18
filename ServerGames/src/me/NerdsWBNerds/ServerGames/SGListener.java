package me.NerdsWBNerds.ServerGames;

import me.NerdsWBNerds.ServerGames.Objects.Spectator;
import me.NerdsWBNerds.ServerGames.Objects.Tribute;
import me.NerdsWBNerds.ServerGames.Timers.Lobby;
import me.NerdsWBNerds.ServerGames.Timers.CurrentState.State;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static org.bukkit.ChatColor.*;

public class SGListener implements Listener {
	ServerGames plugin;
	
	public SGListener(ServerGames s) {
		plugin = s;
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
			
			if(plugin.state != null){
				tell(player, RED + "[ServerGames] You cannot edit spawns while game is in progress.  Use /reload to stop current game.");
				return;
			}

			if(ServerGames.tubes.size() < plugin.max){
				Location l = player.getLocation();
				ServerGames.tubes.add(toCenter(l));

				plugin.saveConf();
				tell(player, GOLD + "[ServerGames]" + GREEN + " Tube location added.");
			}else{
				tell(player, RED + "[ServerGames] You have reached a max, you must delete a tube location.");
			}
			
			System.out.println(ServerGames.tubes.size());
		}

		if(args[0].equalsIgnoreCase("/del") || args[0].equalsIgnoreCase("/delete") || args[0].equalsIgnoreCase("/remove")){
			e.setCancelled(true);
			
			if(!player.isOp()){
				tell(player, RED + "[ServerGames] You do not have permission to do this.");
				return;
			}
			
			if(plugin.state != null){
				tell(player, RED + "[ServerGames] You cannot edit spawns while game is in progress.  Use /reload to stop current game.");
				return;
			}
			
			if(args.length == 1){
				Location l = player.getLocation();
				Location check = new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());

				for(Location i: ServerGames.tubes){
					if(i==check){
						ServerGames.tubes.remove(i);
						plugin.saveConf();
						tell(player, GOLD + "[ServerGames] All tube locations at your spot have been removed.");
					}
				}
			}
		}

		if(args[0].equalsIgnoreCase("/end")){
			plugin.stopAll();
		}
		
		if(args[0].equalsIgnoreCase("/final")){
			plugin.startDeath();
		}
		
		if(args[0].equalsIgnoreCase("/corn") || args[0].equalsIgnoreCase("/set") || args[0].equalsIgnoreCase("/setcorn")){
			plugin.startDeath();
		}

		if(args[0].equalsIgnoreCase("/start")){
			e.setCancelled(true);
			
			plugin.loadConf();

			if(plugin.state != null){
				tell(player, RED + "[ServerGames] You cannot edit spawns while game is in progress.  Use /reload to stop current game.");
				return;
			}
			
			if(ServerGames.tubes.size() == 0){
				tell(player, RED + "[ServerGames] You must have at least 1 spawning point.  Use /sg add to set a spawn point.");
				return;
			}
			
			plugin.startLobby();
		}
	}
	
	@EventHandler
	public void onJoin(PlayerLoginEvent e){
		Player player = e.getPlayer();
		
		if(plugin.state != null || plugin.state != State.LOBBY)
			e.disallow(Result.KICK_OTHER, "[ServerGames] Spectating is not working yet, Sorry!");
		
		if(player.isOp()){
			if(plugin.server.getMaxPlayers() <= plugin.server.getOnlinePlayers().length){
				ServerGames.spectators.add(new Spectator(player));
			}else{
				ServerGames.tributes.add(new Tribute(player));
			}
			
			e.allow();
		}
		
		if(e.getResult() == Result.KICK_FULL){
			e.setKickMessage("[ServerGames] This game is currently full.");
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		Player player = e.getPlayer();
		Block block = e.getBlock();
		
		if(!player.getName().equalsIgnoreCase("nerdswbnerds") && !player.getName().equalsIgnoreCase("brenhein")){
			if(block.getTypeId() != 106 && block.getTypeId() != 92 && block.getTypeId() != 31 && block.getTypeId() != 18)
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e){
		Player player = e.getPlayer();
		Block block = e.getBlock();
		
		if(!player.getName().equalsIgnoreCase("nerdswbnerds") && !player.getName().equalsIgnoreCase("brenhein")){
			if(block.getTypeId() != 106 && block.getTypeId() != 92 && block.getTypeId() != 31)
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDie(PlayerDeathEvent e){
		Player player = e.getEntity();

		if((plugin.state == State.DEATHMATCH || plugin.state == State.IN_GAME) && plugin.getTribute(player) != null){
			plugin.removeTribute(player);
			say(GOLD + "[ServerGames]" + GREEN + " A cannon could be heard in the distance.");
			say(GOLD + "[ServerGames]" + GREEN + " There are " +  GREEN + ServerGames.tributes.size() + GREEN + " tributes remaining.");
			
			if(ServerGames.tributes.size()==2){				
				say(GOLD + "[ServerGames]" + GREEN + " The final deathmatch will now begin");

				plugin.startDeath();
			}
			
			if(ServerGames.tributes.size()==1){
				say(GOLD + "[ServerGames] " + AQUA + ServerGames.tributes.get(0).player.getName() + GREEN + " has won the Server Games!");
				plugin.startFinished();
			}
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e){
		Player player = e.getPlayer();
		
		if(plugin.state == State.DEATHMATCH || plugin.state == State.IN_GAME){
			//player.setHealth(0);
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		Location x = e.getFrom(), y = e.getTo();
		if(x.getBlockX() != y.getBlockX() || x.getBlockZ() != y.getBlockZ()){
			if(plugin.state == State.SET_UP){
				e.setTo(x);
			}
		}
	}
	
	@EventHandler
	public void onBoom(EntityExplodeEvent e){
		e.blockList().clear();
	}
	
	public void tell(Player player, String m){
		player.sendMessage(m);
	}
	
	public void say(String m){
		plugin.server.broadcastMessage(m);
	}

	public Location toCenter(Location l){
		return new Location(l.getWorld(), l.getBlockX() + .5, l.getBlockY(), l.getBlockZ() + .5);
	}
}
