package me.NerdsWBNerds.ServerGames;

import me.NerdsWBNerds.ServerGames.CurrentState.State;
import me.NerdsWBNerds.ServerGames.Objects.Spectator;
import me.NerdsWBNerds.ServerGames.Objects.Tribute;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

		if(args[0].equalsIgnoreCase("/sg") && args[1].equalsIgnoreCase("add") && args.length == 2){
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

				tell(player, GOLD + "[ServerGames]" + GREEN + " Tube location added.");
			}else{
				tell(player, RED + "[ServerGames] You have reached a max, you must delete a tube location.");
			}
			
			System.out.println(ServerGames.tubes.size());
		}

		if(args[0].equalsIgnoreCase("/sg") && args[1].equalsIgnoreCase("del")){
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

						tell(player, GOLD + "[ServerGames] All tube locations at your spot have been removed.");
					}
				}
			}
		}
		
		if(args[0].equalsIgnoreCase("/sg") && args[1].equalsIgnoreCase("stop")){
			plugin.game = null;
			plugin.state = null;
			plugin.cancelTasks();
		}

		if(args[0].equalsIgnoreCase("/start")){
			e.setCancelled(true);

			if(plugin.state != null){
				tell(player, RED + "[ServerGames] You cannot edit spawns while game is in progress.  Use /reload to stop current game.");
				return;
			}
			
			if(ServerGames.tubes.size() == 0){
				tell(player, RED + "[ServerGames] You must have at least 1 spawning point.  Use /sg add to set a spawn point.");
				return;
			}
			
			tell(player, GOLD + "[ServerGames]" + GREEN + " Countdown started.");

			plugin.game = new Countdown(plugin);
			plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, plugin.game, 20L, 20L);		
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
	public void onDie(PlayerDeathEvent e){
		Player player = e.getEntity();

		if((plugin.state == State.DEATHMATCH || plugin.state == State.IN_GAME) && plugin.getTribute(player) != null){
			e.setDeathMessage(GOLD + "[ServerGames]" + GREEN + " A cannon could be heard in the distance.");
			e.setDeathMessage(GOLD + "[ServerGames]" + GREEN + " There are " +  AQUA + ServerGames.tributes.size() + GREEN + " tributes remaining.");
			plugin.removeTribute(player);
			
			if(ServerGames.tributes.size()==2){
				e.setDeathMessage(GOLD + "[ServerGames]" + GREEN + " The final deathmatch will now begin");
				
				Location x = this.toCenter(ServerGames.tubes.get(ServerGames.tubes.size() / 2)), y = this.toCenter(ServerGames.tubes.get(0));
				Player xx = ServerGames.tributes.get(0).player, yy = ServerGames.tributes.get(1).player;
				xx.teleport(x);
				yy.teleport(y);
				tell(xx, GOLD + "[ServerGames] " + GREEN + "You have maid it to the deathmatch.");
				tell(yy, GOLD + "[ServerGames] " + GREEN + "You have maid it to the deathmatch.");
			}
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e){
		Player player = e.getPlayer();
		
		if(plugin.state == State.DEATHMATCH || plugin.state == State.IN_GAME){
			player.setHealth(0);
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
	
	public void tell(Player player, String m){
		player.sendMessage(m);
	}

	public Location toCenter(Location l){
		return new Location(l.getWorld(), l.getBlockX() + .5, l.getBlockY(), l.getBlockZ() + .5);
	}
}
