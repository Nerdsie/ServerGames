package me.NerdsWBNerds.ServerGames;

import static org.bukkit.ChatColor.*;

import java.text.DecimalFormat;

import me.NerdsWBNerds.ServerGames.Objects.Bet;
import me.NerdsWBNerds.ServerGames.Objects.ShopItem;
import me.NerdsWBNerds.ServerGames.Objects.Spectator;
import me.NerdsWBNerds.ServerGames.Objects.Tribute;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class CommandExec implements CommandExecutor {
	ServerGames plugin;
	
	public CommandExec(ServerGames p){
		plugin = p;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender instanceof Player){

			//////////////// -------------------------------------------- /////////////////
			//////////////// ------------ PLAYER COMMANDS --------------- /////////////////
			//////////////// -------------------------------------------- /////////////////
			
			Player player = (Player) sender;

			//////////////// --------- BUY (Buy item for tribute) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("buy")){
				Block b = player.getTargetBlock(null, 100);
				
				if(ServerGames.isShopHolder(b)){
					ShopItem i = ServerGames.getShopItem(b);
					
					if(plugin.getScore(player) - i.price >= 0){
						Player target = ServerGames.server.getPlayer(args[0]);
						
						if(target!=null && target.isOnline() && plugin.isTribute(target)){
							ServerGames.packages.get(target.getName()).add(i);
							plugin.subtractScore(player, i.price);
							tell(target, GOLD + "[ServerShop] " + GREEN + "You have a " + AQUA + Material.getMaterial(i.id).name() + GREEN + " waiting. Use " + AQUA + "/redeem " + GREEN + "to get it.");
							tell(player, GOLD + "[ServerShop] " + GREEN + "You have sent " + AQUA + target.getName() + GREEN + " a " + AQUA + Material.getMaterial(i.id).name());
						}else{
							tell(player, RED + "[ServerShop] Player is either not online or not a tribute.");			
						}
					}else{
						tell(player, RED + "[ServerShop] You do not have enough points to buy this.");						
					}
				}else{
					tell(player, RED + "[ServerShop] Not a shop block, look at the block the item you want to buy is resting on.");
				}
				
				return true;
			}

			//////////////// --------- BETS (List all bets) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("bets")){
		        tell(player, GOLD + "[ServerGames] " + GREEN + "CURRENT STANDINGS:");
		        for(Bet b: ServerGames.bets){
		        	tell(player, GOLD + "[ServerGames] " + GREEN + "* " + b.better + " has bet on " + b.tribute);
		        }
		        
				return true;
			}
			
			//////////////// --------- BET (Bet on someone) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("bet")){
				if(!plugin.isTribute(player)){
	                try {
	                	Player target = plugin.getServer().getPlayer(args[1]);
	                	int score = Integer.parseInt(args[0]);
	                	
	                	if(ServerGames.tributes.size()<= 3){
	                		tell(player, RED + "[ServerGames] There must be at least 4 tributes to bet.");
	                		return true;
	                	}
	                	if(plugin.hasBet(player)){
	                		tell(player, RED + "[ServerGames] You can only place one bet per round.");
	                		return true;
	                	}
	                	if(plugin.getScore(player) - score < 0){
	                		tell(player, RED + "[ServerGames] You don't have enough points to do this.");
	                		return true;
	                	}
	                	if(!plugin.isTribute(target)){
	                		tell(player, RED + "[ServerGames] You can't bet on spectators.");
	                		return true;
	                	}
	                	
	                    if(score > 0 && args.length==2){
	                        tell(player,GOLD + "[ServerGames]" + GREEN + " You have bet on player " + target.getName() + " with " + score + " points.");
	                        ServerGames.bets.add(new Bet(player, target, score));
	                        tell(target, GOLD + "[ServerGames]" + GREEN + " A bet has been added for you!");
	        				return true;
	                    } else {
	                        tell(player, RED + "[ServerGames] Bet number unacceptable [/bet [amount] [player]");
	        				return true;
	                    }
	                } catch(Exception ef) {
	                	tell(player, RED + "[ServerGames] Bet unacceptable (/bet [amount] [player])"); 
	                	return true;
					}
	                
	            }
			}

			//////////////// --------- SCORE (Check scores) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("score")){
				Player target = player;
				
				if(args.length==1){
					target = ServerGames.server.getPlayer(args[0]);
				}
				
				tell(player, GOLD + "[ServerGames] " + AQUA + target.getName() + GREEN + " has a score of " + AQUA + plugin.getScore(target));
				return true;
			}

			//////////////// --------- START (Start the game) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("start")){
				plugin.load();

				if(!ServerGames.inNothing()){
					tell(player, RED + "[ServerGames] You cannot start another game while a game is in progress.  Use /force to stop current game.");
					return true;
				}
				
				if(ServerGames.getTubes()==null){
					tell(player, RED + "[ServerGames] You must have at least 1 spawning point.  Use /edit to toggle editing, then break a block to add it.");
					return true;
				}
				
				if(ServerGames.cornucopia==null){
					tell(player, RED + "[ServerGames] You must have a cornucopia. Use /setcorn to set a spawn point.");
					return true;
				}
				
				if(ServerGames.waiting==null){
					tell(player, RED + "[ServerGames] You must have a waiting spawn. Use /setwait to set a spawn point.");
					return true;
				}
				
				if(ServerGames.worlds==null || ServerGames.worlds.isEmpty()){
					tell(player, RED + "[ServerGames] You must have a hunger games world. Use /addworld <world_name> to set a spawn point.");
					return true;
				}
					
				plugin.startLobby();
				return true;
			}

			//////////////// --------- SPEC (Teleport to people) --------- //////////////////
			if((cmd.getName().equalsIgnoreCase("to") || cmd.getName().equalsIgnoreCase("watch") || cmd.getName().equalsIgnoreCase("spec") || cmd.getName().equalsIgnoreCase("see")) && args.length==1){
				if(!plugin.isSpectator(player)){
					return false;
				}
				
				Player target = ServerGames.server.getPlayer(args[0]);
				if(target != null && target.isOnline()){
					player.teleport(target);

					tell(player, GOLD + "[ServerGames] " + GREEN + "Now spectating " + AQUA + target.getName());
					return true;
				}else{
					tell(player, RED + "[ServerGames] Player not found.");
					return true;
				}
			}

			//////////////// --------- INFO (Get lobby info)  --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("sg")){
				try{
					DecimalFormat rdec = new DecimalFormat("#");  
					
					tell(player, GOLD + "**Current Server Game Info** " + DARK_AQUA + "Map(" + ServerGames.current + ")");					
					tell(player, YELLOW + "Plugin by NerdsWBNerds (@NerdsWBNerds) and Brenhein.");
					tell(player, GREEN + "There is " + AQUA + rdec.format(ServerGames.game.time / 60) + GREEN + " minute(s) " + AQUA + (ServerGames.game.time % 60) + GREEN + " second(s) remaining.");
					tell(player, GREEN + "There are " + AQUA + ServerGames.tributes.size() + "/" + ServerGames.server.getOnlinePlayers().length + GREEN + " tribute(s) remaining.");
					tell(player, GREEN + "Highest ranked player here is " + AQUA + plugin.topInGame() + GREEN + " with " + AQUA + plugin.getScore(plugin.topInGame()) + GREEN + " points.");
				}catch(Exception e){}
					
				return true;
			}

			//////////////// --------- SETWAIT (Set wait spawn location) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("setWait")){
				if(!player.isOp()){
					tell(player, RED + "[ServerGames] You do not have permission to do this.");
					return true;
				}
				
				ServerGames.waiting = toCenter(player.getLocation());
				tell(player, GOLD + "[ServerGames]" + GREEN + " Waiting spawn set at your location.");
				plugin.save();
				return true;
			}

			//////////////// --------- SETCORN (Set cornucopia) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("setCorn")){
				if(!player.isOp()){
					tell(player, RED + "[ServerGames] You do not have permission to do this.");
					return true;
				}
				
				ServerGames.cornucopia.put(player.getWorld().getName(), toCenter(player.getLocation()));
				tell(player, GOLD + "[ServerGames]" + GREEN + " Cornacoptia set at your location.");
				plugin.save();
				return true;
			}

			//////////////// --------- DEATHMATCH (Force deathmatch) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("dm")){
				if(!player.isOp()){
					tell(player, RED + "[ServerGames] You do not have permission to do this.");
					return true;
				}

				ServerGames.game.time = 15;
				return true;
			}

			//////////////// --------- LOBBY (Force lobby) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("force")){
				if(!player.isOp()){
					tell(player, RED + "[ServerGames] You do not have permission to do this.");
					return true;
				}

				say(GOLD + "[ServerGames]" + GREEN + " Current game has ended.");
				
				plugin.tpAll(ServerGames.waiting);
				plugin.stopAll();
				ServerGames.state = null;
				ServerGames.game = null;

				return true;
			}

			//////////////// --------- END (End current game) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("end")){
				if(!player.isOp()){
					tell(player, RED + "[ServerGames] You do not have permission to do this.");
					return true;
				}

				say(GOLD + "[ServerGames]" + GREEN + " Current game is ending.");
				
				plugin.startFinished();
				return true;
			}
			
			//////////////// --------- EDIT (Toggle tube editing) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("edit")){
				if(!player.isOp()){
					tell(player, RED + "[ServerGames] You do not have permission to do this.");
					return true;
				}
				
				if(!ServerGames.inNothing()){
					tell(player, RED + "[ServerGames] You cannot edit spawns while game is in progress.  Use /force to stop current game.");
					return true;
				}
				
				if(plugin.Listener.editing.contains(player)){
					tell(player, GOLD + "[ServerGames] " + GREEN + "You are no longer editing.");
					plugin.Listener.editing.remove(player);
					return true;
				}else{
					tell(player, GOLD + "[ServerGames] " + GREEN + "You are now editing.");
					plugin.Listener.editing.add(player);
					return true;
				}
			}

			//////////////// --------- ADDWORLD (Add world to rotation) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("addworld")){
				if(!player.isOp()){
					tell(player, RED + "[ServerGames] You do not have permission to do this.");
					return true;
				}
				
				if(!ServerGames.inNothing()){
					tell(player, RED + "[ServerGames] Cannot edit worlds while game is active, use /force to stop game.");
					return true;
				}
				
				String w = ServerGames.server.getWorld(args[0]).getName();
				
				if(!ServerGames.worlds.contains(w)){
					ServerGames.worlds.add(w);
				}else{
					tell(player, RED + "[ServerGames] World already in rotation.");
					return true;
				}

				tell(player, GOLD + "[ServerGames] " + GREEN + "The world "+ AQUA + w + GREEN + " has been added.");
				return true;
			}

			//////////////// --------- DELWORLD (Delete world from rotation) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("delworld")){
				if(!player.isOp()){
					tell(player, RED + "[ServerGames] You do not have permission to do this.");
					return true;
				}
				
				if(!ServerGames.inNothing()){
					tell(player, RED + "[ServerGames] Cannot edit worlds while game is active, use /force to stop game.");
					return true;
				}
				
				String w = ServerGames.server.getWorld(args[0]).getName();
				
				if(!ServerGames.worlds.contains(w)){
					ServerGames.worlds.remove(w);
				}else{
					tell(player, RED + "[ServerGames] World already isn't in rotation.");
					return true;
				}

				tell(player, GOLD + "[ServerGames] " + GREEN + "The world "+ AQUA + w + GREEN + " has been deleted.");
				return true;
			}

			//////////////// --------- MAP (Set map to be used in next game) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("map")){
				if(!player.isOp()){
					tell(player, RED + "[ServerGames] You do not have permission to do this.");
					return true;
				}
				
				if(!ServerGames.inNothing() && !ServerGames.inLobby()){
					tell(player, RED + "[ServerGames] Cannot edit worlds while game is active, use /force to stop game.");
					return true;
				}
				
				String w = ServerGames.server.getWorld(args[0]).getName();

				if(ServerGames.worlds.contains(w)){
					ServerGames.current = w;
					tell(player, GOLD + "[ServerGames] " + GREEN + "You're next game will now be in the world " + AQUA + w);
					return true;
				}else{
					tell(player, GOLD + "[ServerGames] " + RED + "World is not in rotation, use /addworld <world_name> first.");
					return true;
				}
			}

			//////////////// --------- SETMIN (Set minimum amount of people to start game.) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("setmin")){
				if(!player.isOp()){
					tell(player, RED + "[ServerGames] You do not have permission to do this.");
					return true;
				}

				try{
					int newM = Integer.parseInt(args[0]);
					ServerGames.min=newM;
					tell(player, GOLD + "[ServerGames] " + GREEN + "Games must now have "+ AQUA + newM + GREEN + " people to start.");
					return true;
				}catch(Exception e){
					tell(player, RED + "[ServerGames] Error changing minimum, did you enter a number?");
					return true;
				}
			}
			
			//////////////// --------- SEE (See spectator) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("show")){
				if(!player.isOp()){
					tell(player, RED + "[ServerGames] You do not have permission to do this.");
					return true;
				}

				if(args==null || args.length==0){
					for(Spectator s: ServerGames.spectators){
						player.showPlayer(s.player);
					}
					
					tell(player, GOLD + "[ServerGames] " + GREEN + "Now showing all spectators.");
					return true;
				}
				if(args.length==1){
					if(plugin.isSpectator(ServerGames.server.getPlayer(args[0]))){
						player.showPlayer(ServerGames.server.getPlayer(args[0]));
						tell(player, GOLD + "[ServerGames] " + GREEN + "Now showing " + AQUA + ServerGames.server.getPlayer(args[0]).getName());
						return true;
					}else{
						tell(player, RED + "[ServerGames] Non-spectators are already visible.");
						return true;
					}
				}
			}

			//////////////// --------- HIDE (Hide spectator) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("hide")){
				if(!player.isOp()){
					tell(player, RED + "[ServerGames] You do not have permission to do this.");
					return true;
				}

				if(args==null || args.length==0){
					for(Spectator s: ServerGames.spectators){
						player.hidePlayer(s.player);
					}
					
					tell(player, GOLD + "[ServerGames] " + GREEN + "Now hiding all spectators.");
				}
				if(args.length==1){
					if(plugin.isSpectator(ServerGames.server.getPlayer(args[0]))){
						player.hidePlayer(ServerGames.server.getPlayer(args[0]));
						tell(player, GOLD + "[ServerGames] " + GREEN + "Now hiding " + AQUA + ServerGames.server.getPlayer(args[0]).getName());
					}else{
						tell(player, RED + "[ServerGames] You can't hide non-spectators.");
						return true;
					}
				}
			}

			//////////////// --------- QUIT (Quit current game) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("quit")){
				if(!player.isOp()){
					tell(player, RED + "[ServerGames] You do not have permission to do this.");
					return true;
				}

				plugin.removeSpectator(player);
				plugin.removeTribute(player);
				
				for(Player p: ServerGames.server.getOnlinePlayers()){
					if(p!=player){
						p.hidePlayer(player);
					}
				}
				
				tell(player, GOLD + "[ServerGames] " + GREEN + "You have quit the game.");
				return true;
			}

			//////////////// --------- JOIN (Join current game) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("join")){
				if(!player.isOp()){
					tell(player, RED + "[ServerGames] You do not have permission to do this.");
					return true;
				}

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
				
				return true;
			}
			
			//////////////// --------- SETSHOP (Set the sop location) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("setshop")){
				if(!player.isOp()){
					tell(player, RED + "[ServerShop] You do not have permission to do this.");
					return true;
				}

				ServerGames.shop = toCenter(player.getLocation());
				tell(player, GOLD + "[ServerShop] " + GREEN + "Shop set at you location.");
				
				return true;
			}

			//////////////// --------- TOSHOP (Teleport to shop) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("toshop")){
				if(ServerGames.shop == null){
					tell(player, RED + "[ServerShop] Shop does not exist.");
				}else{
					player.teleport(ServerGames.shop);
					tell(player, GOLD + "[ServerShop] " + GREEN + "You are now at the shop.");
				}
				
				return true;
			}

			//////////////// --------- CLEARSHOP (Delete all items from shop) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("clearshop")){
				if(!player.isOp()){
					tell(player, RED + "[ServerShop] You do not have permission to do this.");
					return true;
				}
				
				if(ServerGames.items.isEmpty()){
					tell(player, RED + "[ServerShop] No items to clear.");
					return true;
				}
				
				for(ShopItem i: ServerGames.items){
					i.item.remove();
					ServerGames.items.remove(i);
				}
				
				tell(player, GOLD + "[ServerShop] " + GREEN + "All shop items now removed.");
				return true;
			}

			//////////////// --------- SHOP (Edit a shop) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("shop")){
				if(args == null || args.length == 0){
					if(ServerGames.shop == null){
						tell(player, RED + "[ServerShop] Shop does not exist.");
						return true;
					}else{
						player.teleport(ServerGames.shop);
						tell(player, GOLD + "[ServerShop] " + GREEN + "You are now at the shop.");
						return true;
					}
				}
				
				if(!player.isOp()){
					tell(player, RED + "[ServerShop] You do not have permission to do this.");
					return true;
				}

				if(args.length==1){
					if(args[0].equalsIgnoreCase("del")){
						Block b = player.getTargetBlock(null, 100);
						for(ShopItem i: ServerGames.items){
							if(i.holder.equals(b)){
								System.out.println("4");
								tell(player, GOLD + "[ServerShop] " + GREEN + "Shop item has been removed.");
								i.item.remove();
								ServerGames.items.remove(i);
							}
						}
						return true;
					}
				}
				
				if(args.length==3){
					if(args[0].equalsIgnoreCase("set")){
						Block b = player.getTargetBlock(null, 100);
						
						if(!ServerGames.isShopHolder(b)){
							int id = Integer.parseInt(args[1]);
							int price = Integer.parseInt(args[2]);
							ServerGames.items.add(new ShopItem(b, id, price));
							tell(player, GOLD + "[ServerShop] " + AQUA + Material.getMaterial(id).name() + GREEN + " added to shop for " + AQUA + price + GREEN + " points.");
						}else{
							for(ShopItem i: ServerGames.items){
								if(i.holder==b){
									i.item.remove();
									i.id = Integer.parseInt(args[1]);
									i.price = Integer.parseInt(args[2]);
									i.spawn();
									tell(player, GOLD + "[ServerShop] " + GREEN + "Item changed to " + AQUA + Material.getMaterial(i.id).name() + GREEN + " for " + AQUA + i.price + GREEN + " points.");
								}
							}
						}
					}
					
					return true;
				}
			}
		}else{
			//////////////// -------------------------------------------- /////////////////
			//////////////// ----------- CONSOLE COMMANDS --------------- /////////////////
			//////////////// -------------------------------------------- /////////////////
			

			//////////////// --------- SETMIN (Set minimum amount of people to start game.) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("setmin")){
				try{
					int newM = Integer.parseInt(args[0]);
					ServerGames.min=newM;
					System.out.println("[ServerGames] " + "Games must now have " + newM + " people to start.");
					plugin.getConfig().set("min-to-start", newM);
					plugin.saveConfig();
					return true;
				}catch(Exception e){
					System.out.println("[ServerGames] Error changing minimum, did you enter a number?");
					return true;
				}
			}
			
			//////////////// --------- INFO (Get lobby info)  --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("sg")){
				try{
					DecimalFormat rdec = new DecimalFormat("#");  
					
					System.out.println("**Current Server Game Info**");
					System.out.println("There is " + rdec.format(ServerGames.game.time / 60) + " minute(s) " + (ServerGames.game.time % 60) + " second(s) remaining.");
					System.out.println("There are " + ServerGames.tributes.size() + "/" + ServerGames.server.getOnlinePlayers().length + " tribute(s) remaining.");
					System.out.println("The highest ranked player in this game is " + plugin.topInGame() + " with " + plugin.getScore(plugin.topInGame()) + " points.");
				}catch(Exception e){}
					
				return true;
			}

			//////////////// --------- END (End current game) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("end")){
				say(GOLD + "[ServerGames]" + GREEN + " Current game is ending.");
				System.out.println("[ServerGames] Current game is ending.");
				
				plugin.startFinished();
				return true;
			}
			
			//////////////// --------- LOBBY (Force lobby) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("force")){
				say(GOLD + "[ServerGames]" + GREEN + " Current game has ended.");
				System.out.println("[ServerGames] Current game has ending.");
				
				plugin.tpAll(ServerGames.waiting);
				plugin.stopAll();
				ServerGames.state = null;
				ServerGames.game = null;

				return true;
			}

			//////////////// --------- DEATHMATCH (Force deathmatch) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("dm")){
				ServerGames.game.time = 15;
				return true;
			}

			//////////////// --------- START (Start the game) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("sgstart")){
				plugin.load();

				if(!ServerGames.inNothing()){
					System.out.println("[ServerGames] You cannot start another game while a game is in progress.  Use /end to stop current game.");
					return true;
				}
				
				if(ServerGames.tubes.size() == 0){
					System.out.println("[ServerGames] You must have at least 1 spawning point.  Use /edit to toggle editing, then break a block to add it.");
					return true;
				}
				
				if(ServerGames.cornucopia==null){
					System.out.println("[ServerGames] You must have a cornucopia. Use /setcorn to set a spawn point.");
					return true;
				}
				
				if(ServerGames.waiting==null){
					System.out.println("[ServerGames] You must have a waiting spawn. Use /setwait to set a spawn point.");
					return true;
				}
				
				if(ServerGames.worlds==null || ServerGames.worlds.isEmpty()){
					System.out.println("[ServerGames] You must have a hunger games world. Use /addworld <world_name> to set a spawn point.");
					return true;
				}
					
				plugin.startLobby();
				return true;
			}
			
			//////////////// --------- BETS (List all bets) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("bets")){
				System.out.println("[ServerGames] CURRENT STANDINGS:");
		        for(Bet b: ServerGames.bets){
		        	System.out.println("[ServerGames] * " + b.better + " has bet on " + b.tribute);
		        }
		        
				return true;
			}

			//////////////// --------- SCORE (Check scores) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("score") && args.length==1){
				Player target = ServerGames.server.getPlayer(args[0]);
				
				System.out.println("[ServerGames] " + target.getName() + " has a score of " + plugin.getScore(target));
				return true;
			}
		}
        
		return false;
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
