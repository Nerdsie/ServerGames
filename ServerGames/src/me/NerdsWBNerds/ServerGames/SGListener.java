package me.NerdsWBNerds.ServerGames;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.HashMap;

import me.NerdsWBNerds.ServerGames.Objects.Chests;
import me.NerdsWBNerds.ServerGames.Objects.Spectator;
import me.NerdsWBNerds.ServerGames.Objects.Tribute;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import static org.bukkit.ChatColor.*;

public class SGListener implements Listener {
	ServerGames plugin;
	public HashMap<Player, Integer> spec = new HashMap<Player, Integer>();
    // player to bet, betting player (confusing as tuna potatoes)
    public HashMap<Player, Player> bets = new HashMap<Player, Player>();
	
	public SGListener(ServerGames s) {
		plugin = s;
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e){
        
       
        
		Player player = e.getPlayer();
		String[] args = e.getMessage().split(" ");
        
        // player instance must be instanceof player
        // console instance is NOT an instanceof player, therefore returning null
        // oh those lovely nullpointers
        if(player == null){
            e.setCancelled(true);
            return;        
        }

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
		
		if(args[0].equalsIgnoreCase("/final") || args[0].equalsIgnoreCase("/dm") || args[0].equalsIgnoreCase("/deathmatch")){
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
		
		if(args[0].equalsIgnoreCase("/chests")){
			e.setCancelled(true);
			
			if(!player.isOp()){
				tell(player, RED + "[ServerGames] You do not have permission to do this.");
				return;
			}
			
			ServerGames.loaded.clear();
			Chests.resetChests();
		}
		
		if(args[0].equalsIgnoreCase("/toc")){
			e.setCancelled(true);
			
			if(!player.isOp()){
				tell(player, RED + "[ServerGames] You do not have permission to do this.");
				return;
			}
			
			player.teleport(ServerGames.cornacopia);
		}
		
		if(args[0].equalsIgnoreCase("/info")){
			e.setCancelled(true);
			
			DecimalFormat rdec = new DecimalFormat("#");  
			
			tell(player, GOLD + "**Current Server Game Info**");
			tell(player, GREEN + "There is " + AQUA + rdec.format(ServerGames.game.time / 60) + GREEN + " minute(s) " + AQUA + (ServerGames.game.time % 60) + GREEN + " second(s) remaining.");
			tell(player, GREEN + "There are " + AQUA + ServerGames.tributes.size() + "/" + ServerGames.server.getOnlinePlayers().length + GREEN + " tribute(s) remaining.");
			tell(player, GREEN + "The highest ranked player in this game is " + AQUA + plugin.topInGame() + GREEN + " with " + AQUA + plugin.getScore(plugin.topInGame()) + GREEN + " points.");
		}

		if(args[0].equalsIgnoreCase("/bug")){
			e.setCancelled(true);

			String bug = e.getMessage().replaceFirst("/bug ", "");
			ServerGames.bugs.add(bug + " - " + player.getName()); 
		}

		if(args[0].equalsIgnoreCase("/clear")){
			e.setCancelled(true);

			if(!player.isOp()){
				tell(player, RED + "[ServerGames] You do not have permission to do this.");
				return;
			}

			plugin.score.clear();
			plugin.save();
		}
		
		if(args[0].equalsIgnoreCase("/top") || args[0].equalsIgnoreCase("/top10")){
			e.setCancelled(true);

			tell(player, GOLD + "**Server Games Top " + plugin.getTop().size() + "**");
			for(int i = 0; i < plugin.getTop().size(); i++){
				tell(player, AQUA + "" + i + ": " + GREEN + plugin.getTop().get(i) + " with " + plugin.getScore(plugin.getTop().get(i)) + " points.");
			}
		}
		
		if(args[0].equalsIgnoreCase("/to") || args[0].equalsIgnoreCase("/watch") || args[0].equalsIgnoreCase("/see") || args[0].equalsIgnoreCase("/spec") && args.length==2 && plugin.isSpectator(player)){
			e.setCancelled(true);

			Player target = ServerGames.server.getPlayer(args[1]);
			
			if(target != null && target.isOnline()){
				player.teleport(target);

				tell(player, GOLD + "[ServerGames] " + GREEN + "Now spectating " + AQUA + target.getName());
			}else{
				tell(player, RED + "[ServerGames] Player not found.");
			}
		}
		
		if(args[0].equalsIgnoreCase("/tow")){
			e.setCancelled(true);
			
			if(!player.isOp()){
				tell(player, RED + "[ServerGames] You do not have permission to do this.");
				return;
			}

            ServerGames.tributes.remove(player);
			player.teleport(ServerGames.waiting);
		}
		
		if(args[0].equalsIgnoreCase("/score")){
			e.setCancelled(true);
			Player target = player;
			
			if(args.length==2){
				target = ServerGames.server.getPlayer(args[1]);
			}
			
			tell(player, GOLD + "[ServerGames] " + AQUA + target.getName() + GREEN + " has a score of " + AQUA + plugin.getScore(target));
		}
		
		if(args[0].equalsIgnoreCase("/leave") || args[0].equalsIgnoreCase("/quit")){
			e.setCancelled(true);
			
			plugin.removeSpectator(player);
			plugin.removeTribute(player);
			
			tell(player, GOLD + "[ServerGames] " + GREEN + "You have left the game.");
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
        
        if(args[0].equalsIgnoreCase("/bet")){
            e.setCancelled(true);
            
            if(!plugin.isTribute(e.getPlayer())){
                try {
                    if(args.length == 3 && Integer.parseInt(args[1]) > 0 && !bets.containsValue(player)){
                        tell(player,GOLD + "[ServerGames]" + GREEN + " Added bet for player " + plugin.getServer().getPlayer(args[2]).getName() + " for " + args[1] + " points.");
                        bets.put(plugin.getServer().getPlayer(args[2]), player);
                        tell(plugin.getServer().getPlayer(args[2]), GOLD + "[ServerGames]" + GREEN + " A bet has been added for you!");
                    } else {
                        tell(player, RED + "[ServerGames] Bet number unacceptable [/bet [amount] [player]");
                    }
                } catch(Exception ef) { tell(player, RED + "[ServerGames] Bet unacceptable (/bet [amount] [player])"); }
                
            }
            
        }
        
        if(args[0].equalsIgnoreCase("/bets")){
            say(GOLD + "[ServerGames] " + GREEN + "CURRENT STANDINGS:");
            for(Player key : bets.keySet()){
                say(GOLD + "[ServerGames] " + GREEN + "* " + key.getName() + " betted for by " + bets.get(key).getName());
            }
        }
	}
	
	@EventHandler
	public void onChat(PlayerChatEvent e){
		ChatColor name = DARK_GREEN;
		
		if(e.getPlayer().isOp())
			name = DARK_RED;
		
		e.setFormat(GRAY + "<" + AQUA + plugin.getScore(e.getPlayer()) + ": " + name + e.getPlayer().getName() + GRAY + "> " + WHITE + e.getMessage());
		
		if(plugin.getTribute(e.getPlayer())==null)
			e.setFormat(RED + "(SPEC)" + GRAY + "<" + AQUA + plugin.getScore(e.getPlayer()) + ": " + name + e.getPlayer().getName() + GRAY + "> " + WHITE + e.getMessage());
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent e){
		if(e.getResult() == Result.KICK_FULL){
			e.setKickMessage(GOLD + "[ServerGames] This game is currently full.");
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		Player player = e.getPlayer();
		player.setCompassTarget(ServerGames.cornacopia);
		
		if(ServerGames.inGame() || ServerGames.inDeath() || ServerGames.inDone() || ServerGames.inSetup()){
			ServerGames.spectators.add(new Spectator(player));
			player.setGameMode(GameMode.CREATIVE);
		}else{
			ServerGames.tributes.add(new Tribute(player));
			player.setGameMode(GameMode.SURVIVAL);
		}
		
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		Player player = e.getPlayer();
		Block block = e.getBlock();
		
		if(!player.getName().equalsIgnoreCase("nerdswbnerds") && !player.getName().equalsIgnoreCase("brenhein")){
			if(ServerGames.inLobby() || ServerGames.inNothing())
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
			if(ServerGames.inLobby() || ServerGames.inNothing())
				e.setCancelled(true);
			
			if(block.getTypeId() != 106 && block.getTypeId() != 92 && block.getTypeId() != 31)
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onDie(PlayerDeathEvent e){
		Player player = e.getEntity();
		
		if(plugin.isTribute(player))
			e.setDeathMessage(GOLD + "[ServerGames] " + AQUA + player.getName() + GREEN + " has died.");
		else
			e.setDeathMessage(null);
		
		if((ServerGames.inDeath()|| ServerGames.inGame() || ServerGames.inSetup()) && plugin.isTribute(player)){
			plugin.getTribute(player).die();
			plugin.removeTribute(player);
			ServerGames.spectators.add(new Spectator(player));
			plugin.subtractScore(player, 10);
			say(GOLD + "[ServerGames]" + GREEN + " A cannon could be heard in the distance.");
			say(GOLD + "[ServerGames]" + GREEN + " There are " +  GREEN + ServerGames.tributes.size() + GREEN + " tributes remaining.");
			
			if(ServerGames.tributes.size()==2 && ServerGames.inGame()){				
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
		e.setRespawnLocation(toCenter(ServerGames.cornacopia));
		
		if(ServerGames.inLobby() || ServerGames.inNothing()){
			e.setRespawnLocation(toCenter(ServerGames.waiting));
		}
		
		if(ServerGames.inGame() || ServerGames.inDeath()){
			e.getPlayer().setGameMode(GameMode.CREATIVE);
		}
		
		if(plugin.isSpectator(e.getPlayer()))
			e.getPlayer().setGameMode(GameMode.CREATIVE);
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e){
		if(!plugin.isTribute(e.getPlayer())){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		Player player = e.getPlayer();
		
		if(!ServerGames.inGame() && !ServerGames.inDeath() && !ServerGames.inDone()){
			e.setCancelled(true);
		}
		
		if(plugin.isSpectator(player)){
			e.setCancelled(true);
			
			if(player.isSneaking()){
				player.teleport(ServerGames.cornacopia);		
				tell(player, GOLD + "[ServerGames] " + GREEN + "You are now at cornacopia.");
			}else{
				if(!spec.containsKey(player)){
					Player toSpec = ServerGames.tributes.get(0).player;
					
					spec.put(player, 1);
					player.teleport(toSpec);
					tell(player, GOLD + "[ServerGames] " + GREEN + "Now spectating " + AQUA + toSpec.getName());
				}else{
					if(spec.get(player) >= ServerGames.tributes.size())
						spec.put(player, 0);
	
					Player toSpec = ServerGames.tributes.get(spec.get(player)).player;
					
					tell(player, GOLD + "[ServerGames] " + GREEN + "Now spectating " + AQUA + toSpec.getName());
					player.teleport(toSpec);
					spec.put(player, spec.get(player) + 1);
				}
			}
		}
		
		if((!ServerGames.inGame() || (!plugin.isTribute(player) && !plugin.isSpectator(player))) && (player.getName().equalsIgnoreCase("nerdswbnerds") || player.getName().equalsIgnoreCase("brenhein")))
			e.setCancelled(false);
	}
	
	@EventHandler
	public void onPickup(PlayerPickupItemEvent e){
		Player player = e.getPlayer();
		
		if(!plugin.isTribute(player)){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onHurt(EntityDamageEvent e){
		if(e.getEntity() instanceof Player){
			Player player = (Player) e.getEntity();

			if(ServerGames.inDone() || ServerGames.inLobby() || ServerGames.inNothing()){
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
			Player hurt = (Player) e.getEntity();

			if(!plugin.isTribute(hit)){
				e.setCancelled(true);
				e.setDamage(0);
			}
			
			if(plugin.isTribute(hit) && hurt.getHealth() - e.getDamage() <= 0){
				plugin.addScore(hit, 10);
			}
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e){
		Player player = e.getPlayer();
		
		ServerGames.showPlayer(player);
		ServerGames.showAllFor(player);
		
		if((ServerGames.inDeath() || ServerGames.inGame() || ServerGames.inSetup()) && plugin.isTribute(player)){
			say(GOLD + "[ServerGames]" + GREEN + " A cannon could be heard in the distance.");
			say(GOLD + "[ServerGames]" + GREEN + " There are " +  GREEN + ServerGames.tributes.size() + GREEN + " tributes remaining.");
			
			if(ServerGames.tributes.size()==2 && ServerGames.inGame()){				
				say(GOLD + "[ServerGames]" + GREEN + " The final deathmatch will now begin");

				plugin.startDeath();
			}
			
			if(ServerGames.tributes.size()==1){
				say(GOLD + "[ServerGames] " + AQUA + ServerGames.tributes.get(0).player.getName() + GREEN + " has won the Server Games!");
				plugin.startFinished();
			}
		}
		
		plugin.removeSpectator(player);
		plugin.removeTribute(player);
		
		e.getPlayer().remove();
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e){
		Location x = e.getFrom(), y = e.getTo();
		if(x.getBlockX() != y.getBlockX() || x.getBlockZ() != y.getBlockZ()){
			if(ServerGames.inSetup()){
				e.setTo(x);
			}
		}
		
		if(ServerGames.inDeath()){
			if(y.distance(ServerGames.cornacopia) > 40 && x.distance(ServerGames.cornacopia) <= 40){
				ServerGames.server.broadcastMessage(GOLD + "[ServerGames] " + AQUA + e.getPlayer().getName() + GREEN + " tried to run from the fight!");
				e.getPlayer().setHealth(0);
			}
		}
	}
	
	@EventHandler
	public void onGM(PlayerGameModeChangeEvent e){
		//if(plugin.inGame() && !plugin.isTribute(e.getPlayer())){
			//e.getPlayer().setGameMode(GameMode.CREATIVE);
		//}
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e){
		Chunk loaded = e.getChunk();
		
		Chests.resetChests(loaded);
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
