package me.NerdsWBNerds.ServerGames;

import static org.bukkit.ChatColor.*;
import java.text.DecimalFormat;

import me.NerdsWBNerds.ServerGames.Objects.Bet;

import org.bukkit.Location;
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

			//////////////// --------- BETS (List all bets) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("bets")){
		        tell(player, GOLD + "[ServerGames] " + GREEN + "CURRENT STANDINGS:");
		        for(Bet b: ServerGames.bets){
		        	tell(player, GOLD + "[ServerGames] " + GREEN + "* " + b.better.getName() + " has bet on " + b.tribute.getName());
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
	                		tell(player, RED + "[ServerGames] There must be at least 4 tributes to place a bet.");
	                		return true;
	                	}
	                	if(plugin.hasBet(player)){
	                		tell(player, RED + "[ServerGames] You can only place one bet per round.");
	                		return true;
	                	}
	                	if(plugin.getScore(player) - score < 0){
	                		tell(player, RED + "[ServerGames] You don't have enough points to do this..");
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
				
				if(ServerGames.cornacopia==null){
					tell(player, RED + "[ServerGames] You must have a cornacopia. Use /setcorn to set a spawn point.");
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

			//////////////// --------- SETCORN (Set cornacopia) --------- //////////////////
			if(cmd.getName().equalsIgnoreCase("setCorn")){
				if(!player.isOp()){
					tell(player, RED + "[ServerGames] You do not have permission to do this.");
					return true;
				}
				
				ServerGames.cornacopia.put(player.getWorld().getName(), toCenter(player.getLocation()));
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
				
				plugin.startDeath();
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
				plugin.startDeath();
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
				
				if(ServerGames.cornacopia==null){
					System.out.println("[ServerGames] You must have a cornacopia. Use /setcorn to set a spawn point.");
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
		        	System.out.println("[ServerGames] * " + b.better.getName() + " has bet on " + b.tribute.getName());
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
