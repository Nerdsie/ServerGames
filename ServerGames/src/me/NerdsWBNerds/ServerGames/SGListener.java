package me.NerdsWBNerds.ServerGames;

import java.util.ArrayList;
import java.util.HashMap;

import me.NerdsWBNerds.ServerGames.Objects.Chests;
import me.NerdsWBNerds.ServerGames.Objects.ShopItem;
import me.NerdsWBNerds.ServerGames.Objects.Spectator;
import me.NerdsWBNerds.ServerGames.Objects.Tribute;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import static org.bukkit.ChatColor.*;

public class SGListener implements Listener {
	ServerGames plugin;
	public HashMap<Player, Integer> spec = new HashMap<Player, Integer>();
    public ArrayList<Player> editing = new ArrayList<Player>();
    public ArrayList<Player> shopEditing = new ArrayList<Player>();
    
    public Material canBreak[] = { Material.VINE, Material.LEAVES, Material.RED_MUSHROOM, Material.BROWN_MUSHROOM, Material.getMaterial(31)};
    public Material canPlace[] = { Material.VINE, Material.RED_MUSHROOM, Material.BROWN_MUSHROOM };
    
	public SGListener(ServerGames s) {
		plugin = s;
	}
	
	@EventHandler
	public void onChat(PlayerChatEvent e){
		ChatColor name = DARK_GREEN;
		
		if(e.getPlayer().isOp())
			name = DARK_RED;
		
		e.setFormat(GRAY + "<" + AQUA + plugin.getScore(e.getPlayer()) + ": " + name + e.getPlayer().getDisplayName() + GRAY + "> " + WHITE + e.getMessage());
		
		if(plugin.getTribute(e.getPlayer())==null)
			e.setFormat(RED + "(SPEC)" + GRAY + "<" + AQUA + plugin.getScore(e.getPlayer()) + ": " + name + e.getPlayer().getDisplayName() + GRAY + "> " + WHITE + e.getMessage());
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
		
		if(ServerGames.inGame() || ServerGames.inDeath() || ServerGames.inDone() || ServerGames.inSetup()){
			plugin.removeTribute(player);
			ServerGames.spectators.add(new Spectator(player));		
			player.setCompassTarget(ServerGames.getCorn());
			player.setGameMode(GameMode.CREATIVE);
			
			tell(player, DARK_AQUA + "You are a spectator, others cannot see yoy, click to teleport to different people. You can also use /bet");
		}else{
			plugin.removeSpectator(player);
			ServerGames.tributes.add(new Tribute(player));
			tell(player, DARK_AQUA + "You are a tribute, you must try to survive, be the last one standing to win!.");
		}
		
		e.setJoinMessage(DARK_AQUA + player.getName() + YELLOW + " has joined the server.");
	}

	@EventHandler
	public void onVehicleBreak(VehicleDamageEvent e){
		if(e.getAttacker() instanceof Player){
			Player player = (Player) e.getAttacker();
			
			if(!plugin.isTribute(player)){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		Player player = e.getPlayer();
		Block block = e.getBlock();
		
		if(!player.getName().equalsIgnoreCase("nerdswbnerds") && !player.getName().equalsIgnoreCase("brenhein")){
			e.setCancelled(true);
			
			for(Material m : canBreak){
				if(block.getType() == m && (!ServerGames.inLobby() || !ServerGames.inSetup() || !ServerGames.inNothing())){
					e.setCancelled(false);
				}
			}
			
			if(!plugin.isTribute(player))
				e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e){
		Player player = e.getPlayer();
		Block block = e.getBlock();
		
		if(!ServerGames.isOwner(player)){
			e.setCancelled(true);
			
			for(Material m : canPlace){
				if(block.getType() == m && (!ServerGames.inLobby() || !ServerGames.inSetup() || !ServerGames.inNothing())){
					e.setCancelled(false);
				}
			}
		}
	}

	@EventHandler
	public void onDie(PlayerDeathEvent e){
		Player player = e.getEntity();
		
		if(plugin.isTribute(player)){
			e.setDeathMessage(GOLD + "[ServerGames] " + AQUA + player.getName() + GREEN + " has died.");

			if(ServerGames.inGame()){
				ServerGames.game.time-=30;
			}
			
			if(player.getKiller() instanceof Player && (ServerGames.inDeath()|| ServerGames.inGame() || ServerGames.inSetup())){
		        Player attacker = (Player) player.getKiller();

				if(plugin.isTribute(attacker)){
					int p = (int) 2 * plugin.getScore(player) / 100;
					
					if(p<5)
						p = 5;
					
					tell(attacker, GOLD + "[ServerGames] " + GREEN + "You are awarded " + AQUA + p + GREEN + " points for killing " + AQUA + player.getName());
					tell(player, GOLD + "[ServerGames] " + GREEN + "You have lost " + AQUA + p + GREEN + " points for being killed by " + AQUA + attacker.getName());
					
					plugin.addScore(attacker, p);
				}
			}
		}else{
			e.setDeathMessage(null);
			e.getDrops().clear();
		}
		
		if((ServerGames.inDeath()|| ServerGames.inGame() || ServerGames.inSetup()) && plugin.isTribute(player)){
			int p = (int) 2 * plugin.getScore(player) / 100;
			
			if(p<5)
				p = 5;

			plugin.subtractScore(player, p);
			
			plugin.getTribute(player).die();
			plugin.removeTribute(player);
			ServerGames.spectators.add(new Spectator(player));
			say(GOLD + "[ServerGames]" + GREEN + " A cannon could be heard in the distance.");
			say(GOLD + "[ServerGames]" + GREEN + " There are " +  GREEN + ServerGames.tributes.size() + GREEN + " tributes remaining.");
			
			if(ServerGames.tributes.size()==2 && ServerGames.inGame()){		
				ServerGames.game.time = 15;
			}
			
			if(ServerGames.tributes.size()==1){
				say(GOLD + "[ServerGames] " + AQUA + ServerGames.tributes.get(0).player.getName() + GREEN + " has won the Server Games!");
				plugin.addScore(player, 30);
				plugin.startFinished();
			}
		}
	}
	
	@EventHandler
	public void onSpawn(CreatureSpawnEvent e){
		if(e.getSpawnReason() == SpawnReason.EGG){
			e.setCancelled(true);
		}
		
		if(e.getEntityType()==EntityType.SLIME){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onSpawn(PlayerRespawnEvent e){
		e.setRespawnLocation(toCenter(ServerGames.getCorn()));
		
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
		
		if(ServerGames.inLobby() || ServerGames.inNothing()){
			e.setCancelled(true);
		}
		
		if(!plugin.isTribute(player)){
			System.out.println("!");
			if((e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK) && ServerGames.isShopHolder(e.getClickedBlock())){

				System.out.println("!");e.setCancelled(true);
				ShopItem i = ServerGames.getShopItem(e.getClickedBlock());
				tell(player, GOLD + "[ServerShop] " + GREEN + "This is a " + AQUA + Material.getMaterial(i.id).name() + GREEN + " that costs " + AQUA + i.price);
				tell(player, GOLD + "[ServerShop] " + GREEN + "To buy this, use the command " + AQUA + "/buy <player_name>");
				return;
			}
		}
		
		if(plugin.isSpectator(player)){
			if(player.getGameMode()==GameMode.SURVIVAL)
				player.setGameMode(GameMode.CREATIVE);
		
			e.setCancelled(true);
		
			if(player.isSneaking()){
				player.teleport(ServerGames.getCorn());		
				tell(player, GOLD + "[ServerGames] " + GREEN + "You are now at cornucopia.");
			}else{
				try{
					if(!spec.containsKey(player)){
						spec.put(player, 0);
					}
					
					if(spec.get(player) >= ServerGames.tributes.size())
						spec.put(player, 0);
						
					Player toSpec = ServerGames.tributes.get(spec.get(player)).player;
					
					tell(player, GOLD + "[ServerGames] " + GREEN + "Now spectating " + AQUA + toSpec.getName());
					player.teleport(toSpec);
					spec.put(player, spec.get(player) + 1);
				}catch(Exception ee){}					
			}
		}
		
		if(player.isOp() && editing.contains(player) && e.getAction() == Action.LEFT_CLICK_BLOCK){
			if(ServerGames.tubes.get(player.getWorld().getName())==null){
				ServerGames.tubes.put(player.getWorld().getName(), new ArrayList<Location>());
			}
			
			if(!ServerGames.tubes.get(player.getWorld().getName()).contains(e.getClickedBlock().getLocation())){
				ServerGames.tubes.get(player.getWorld().getName()).add(e.getClickedBlock().getLocation());
				tell(player, GOLD + "[ServerGames] " + GREEN + "Tube #" + AQUA + ServerGames.tubes.get(player.getWorld().getName()).size() + GREEN + " location added.");
			}else{
				ServerGames.tubes.get(player.getWorld().getName()).remove(e.getClickedBlock().getLocation());
				tell(player, GOLD + "[ServerGames] " + GREEN + "There are " + AQUA + ServerGames.tubes.get(player.getWorld().getName()).size() + GREEN + " tubes remaining.");
			}
			
			e.setCancelled(true);
			
			plugin.save();
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
		
		if(ServerGames.isShopItem(e.getItem())){
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
	public void onDespawn(ItemDespawnEvent e){
		if(ServerGames.isShopItem(e.getEntity())){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onHurtByOther(EntityDamageByEntityEvent e){
		if(e.getDamager() instanceof Player){
			if(e.getEntity() instanceof Player){
				Player hit = (Player) e.getDamager();
				Player hurt = (Player) e.getEntity();
	
				if(!plugin.isTribute(hit) || !plugin.isTribute(hurt)){
					e.setCancelled(true);
					e.setDamage(0);
					
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e){
		Player player = e.getPlayer();
		
		ServerGames.showPlayer(player);
		ServerGames.showAllFor(player);

		if(plugin.isTribute(player)){
			if(ServerGames.inGame()){
				ServerGames.game.time-=30;
			}
		}
		
		if((ServerGames.inDeath()|| ServerGames.inGame() || ServerGames.inSetup()) && plugin.isTribute(player)){
			player.setHealth(0);
		}
		
		plugin.removeSpectator(player);
		plugin.removeTribute(player);
		
		e.setQuitMessage(DARK_AQUA + player.getName() + YELLOW + " has left the server.");
		e.getPlayer().remove();
	}
	
	@EventHandler
	public void onKick(PlayerKickEvent e){
		Player player = e.getPlayer();
		
		ServerGames.showPlayer(player);
		ServerGames.showAllFor(player);

		if(plugin.isTribute(player)){
			if(ServerGames.inGame()){
				ServerGames.game.time-=30;
			}
		}
		
		if((ServerGames.inDeath()|| ServerGames.inGame() || ServerGames.inSetup()) && plugin.isTribute(player)){
			player.setHealth(0);
		}
		
		plugin.removeSpectator(player);
		plugin.removeTribute(player);
		
		e.setLeaveMessage(DARK_AQUA + player.getName() + YELLOW + " has left the server.");
		e.getPlayer().remove();
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e){
		Location x = e.getFrom(), y = e.getTo();

		if(x.getWorld() != y.getWorld()){
			return;
		}
		
		if(x.getBlockX() != y.getBlockX() || x.getBlockZ() != y.getBlockZ()){
			if(ServerGames.inSetup() && plugin.isTribute(e.getPlayer())){
				e.setTo(x);
			}
		}
		
		if(ServerGames.inDeath() && plugin.isTribute(e.getPlayer())){
			if(y.distance(ServerGames.getCorn()) > 40 && x.distance(ServerGames.getCorn()) <= 40){
				ServerGames.server.broadcastMessage(GOLD + "[ServerGames] " + AQUA + e.getPlayer().getName() + GREEN + " tried to run from the fight!");
				e.getPlayer().setHealth(0);
			}
		}
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e){
		Chunk loaded = e.getChunk();
		
		Chests.resetChests(loaded);
		
		for(ShopItem i: ServerGames.items){
			i.checkForDups();
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
		return new Location(l.getWorld(), l.getBlockX() + .5, l.getBlockY() + 1, l.getBlockZ() + .5);
	}
}
