package me.NerdsWBNerds.ServerGames;

import me.NerdsWBNerds.ServerGames.Objects.Spectator;
import me.NerdsWBNerds.ServerGames.Objects.Tribute;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

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
			
			if(ServerGames.state != null){
				tell(player, RED + "[ServerGames] You cannot edit spawns while game is in progress.  Use /reload to stop current game.");
				return;
			}

			if(ServerGames.tubes.size() < plugin.max){
				Location l = player.getLocation();
				ServerGames.tubes.add(toCenter(l));

				plugin.save();
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
			
			if(ServerGames.state != null){
				tell(player, RED + "[ServerGames] You cannot edit spawns while game is in progress.  Use /reload to stop current game.");
				return;
			}
			
			if(args.length == 1){
				Location l = player.getLocation();
				Location check = new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ());

				for(Location i: ServerGames.tubes){
					if(i==check){
						ServerGames.tubes.remove(i);
						plugin.save();
						tell(player, GOLD + "[ServerGames]" + GREEN + " All tube locations at your spot have been removed.");
					}
				}
			}
		}

		if(args[0].equalsIgnoreCase("/end")){
			e.setCancelled(true);
			
			if(!player.isOp()){
				tell(player, RED + "[ServerGames] You do not have permission to do this.");
				return;
			}

			say(GOLD + "[ServerGames]" + GREEN + " Current game has ended.");
			
			plugin.tpAll(ServerGames.waiting);
			plugin.stopAll();
		}

		if(args[0].equalsIgnoreCase("/list")){
			e.setCancelled(true);
			
			for(Tribute t:ServerGames.tributes){
				tell(player, RED + "[ServerGames] " + t.player.getName());
			}
		}
		
		if(args[0].equalsIgnoreCase("/final")){
			e.setCancelled(true);
			
			if(!player.isOp()){
				tell(player, RED + "[ServerGames] You do not have permission to do this.");
				return;
			}
			
			plugin.startDeath();
		}
		
		if(args[0].equalsIgnoreCase("/corn") || args[0].equalsIgnoreCase("/setc") || args[0].equalsIgnoreCase("/setcorn")){
			e.setCancelled(true);
			
			if(!player.isOp()){
				tell(player, RED + "[ServerGames] You do not have permission to do this.");
				return;
			}
			
			ServerGames.cornacopia = toCenter(player.getLocation());
			tell(player, GOLD + "[ServerGames]" + GREEN + " Cornacoptia set at your location.");
			plugin.save();
		}
		
		if(args[0].equalsIgnoreCase("/wait") || args[0].equalsIgnoreCase("/setw") || args[0].equalsIgnoreCase("/setwait")){
			e.setCancelled(true);
			
			if(!player.isOp()){
				tell(player, RED + "[ServerGames] You do not have permission to do this.");
				return;
			}
			
			ServerGames.waiting = toCenter(player.getLocation());
			tell(player, GOLD + "[ServerGames]" + GREEN + " Waiting spawn set at your location.");
			plugin.save();
		}

		if(args[0].equalsIgnoreCase("/start")){
			e.setCancelled(true);
			
			plugin.load();

			if(ServerGames.state != null){
				tell(player, RED + "[ServerGames] You cannot edit spawns while game is in progress.  Use /end to stop current game.");
				return;
			}
			
			if(ServerGames.tubes.size() == 0){
				tell(player, RED + "[ServerGames] You must have at least 1 spawning point.  Use /add to set a spawn point.");
				return;
			}
			
			if(ServerGames.cornacopia==null){
				tell(player, RED + "[ServerGames] You must have a cornacopia. Use /setc to set a spawn point.");
				return;
			}
			
			if(ServerGames.waiting==null){
				tell(player, RED + "[ServerGames] You must have a waiting spawn. Use /setw to set a spawn point.");
				return;
			}
				
			plugin.startLobby();
		}
	}
	
	@EventHandler
	public void onChat(PlayerChatEvent e){
		e.setFormat(GRAY + "<" + AQUA + e.getPlayer().getName() + GRAY + "> " + WHITE + e.getMessage());
		
		if(plugin.getTribute(e.getPlayer())==null)
			e.setFormat(RED + "(SPEC)" + GRAY + "<" + AQUA + e.getPlayer().getName() + GRAY + "> " + WHITE + e.getMessage());
	}
	
	@EventHandler
	public void onJoin(PlayerLoginEvent e){
		Player player = e.getPlayer();
		
		if(!plugin.inNothing() && !plugin.inLobby()){
			ServerGames.spectators.add(new Spectator(player));
			ServerGames.hidePlayer(player);	
		}else{
			ServerGames.tributes.add(new Tribute(player));
		}
		
		player.teleport(ServerGames.waiting);
			
		if(e.getResult() == Result.KICK_FULL){
			e.setKickMessage("[ServerGames] This game is currently full.");
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		Player player = e.getPlayer();
		Block block = e.getBlock();
		
		if(!player.getName().equalsIgnoreCase("nerdswbnerds") && !player.getName().equalsIgnoreCase("brenhein")){
			if(plugin.inLobby() || plugin.inNothing())
				e.setCancelled(true);
			
			if(block.getTypeId() != 106 && block.getTypeId() != 92 && block.getTypeId() != 31 && block.getTypeId() != 18)
				e.setCancelled(true);
			
			if(!plugin.isTribute(player))
				e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e){
		Player player = e.getPlayer();
		Block block = e.getBlock();
		
		if(!ServerGames.isOwner(player)){
			if(plugin.inLobby() || plugin.inNothing())
				e.setCancelled(true);
			
			if(block.getTypeId() != 106 && block.getTypeId() != 92 && block.getTypeId() != 31)
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onDie(PlayerDeathEvent e){
		Player player = e.getEntity();
		e.setDeathMessage(GOLD + "[ServerGames] " + AQUA + player.getName() + GREEN + " has died.");
		
		if((plugin.inDeath()|| plugin.inGame()) && plugin.isTribute(player)){
			plugin.getTribute(player).die();
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
	public void onSpawn(PlayerRespawnEvent e){
		e.setRespawnLocation(ServerGames.waiting);
		
		if(plugin.inGame() || plugin.inDeath() || plugin.inSetup())
			e.setRespawnLocation(ServerGames.cornacopia);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		Player player = e.getPlayer();
		
		if(!plugin.inGame() && !plugin.inDeath() && !plugin.inDone()){
			e.setCancelled(true);
		}
		
		if(player.getName().equalsIgnoreCase("nerdswbnerds") || player.getName().equalsIgnoreCase("brenhein"))
			e.setCancelled(false);
	}
	
	@EventHandler
	public void onHurt(EntityDamageEvent e){
		if(e.getEntity() instanceof Player){
			Player player = (Player) e.getEntity();

			if(plugin.inDone() || plugin.inLobby() || plugin.inNothing()){
				e.setCancelled(true);
				e.setDamage(0);
				player.setFireTicks(0);
			}
		}
	}	
	
	@EventHandler
	public void onHurtByOther(EntityDamageByEntityEvent e){
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player){
			Player hit = (Player) e.getDamager();

			if(!plugin.isTribute(hit)){
				e.setCancelled(true);
				e.setDamage(0);
			}
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e){
		Player player = e.getPlayer();
		
		ServerGames.tributes.remove(plugin.getTribute(player));
		
		ServerGames.showPlayer(player);
		ServerGames.showAllFor(player);
		
		if(plugin.inDeath() || plugin.inGame()){
			player.setHealth(0);
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e){
		Location x = e.getFrom(), y = e.getTo();
		if(x.getBlockX() != y.getBlockX() || x.getBlockZ() != y.getBlockZ()){
			if(plugin.inSetup()){
				e.setTo(x);
			}
		}
		
		if(plugin.inDeath()){
			if(y.distance(ServerGames.cornacopia) > 30 && x.distance(ServerGames.cornacopia) <= 30){
				ServerGames.server.broadcastMessage(GOLD + "[ServerGames] " + AQUA + e.getPlayer().getName() + GREEN + " tried to run from the fight!");
				e.getPlayer().setHealth(0);
			}
		}
	}
	
	@EventHandler
	public void onGM(PlayerGameModeChangeEvent e){
		if(plugin.inGame() || plugin.inSetup() || plugin.inDeath() || plugin.inDone()){
			e.setCancelled(true);
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
		ServerGames.server.broadcastMessage(m);
	}

	public Location toCenter(Location l){
		return new Location(l.getWorld(), l.getBlockX() + .5, l.getBlockY(), l.getBlockZ() + .5);
	}
}
