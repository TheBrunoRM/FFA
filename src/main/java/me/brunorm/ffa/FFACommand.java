package me.brunorm.ffa;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.brunorm.bukkitutils.Messager;

public class FFACommand implements CommandExecutor {

	String[] helpLines = { "&a&lFFA Commands", "", "&b/ffa play &e- joins the FFA arena.",
			"&b/ffa leave &e- leaves the FFA arena.", "&b/ffa kit &e- select a kit" };

	private Tombstone test;

	@Override
	public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args) {
		if (args.length <= 0) {
			sender.sendMessage(Messager.color("&b&lFFA &e- Type &b/ffa help &eto see the commands."));
			return false;
		}
		Player player = null;
		if (sender instanceof Player)
			player = (Player) sender;
		switch (args[0].toLowerCase()) {
		case "test":
			if (this.test != null)
				this.test.remove();
			this.test = new Tombstone(player.getLocation(), "Notch");
			break;
		case "list":
			sender.sendMessage("Player list");
			sender.sendMessage("");
			for (FFAPlayer p : FFA.get().getGame().getPlayers())
				sender.sendMessage("- " + p.getPlayer().getName());
			break;
		case "play":
		case "join":
			if (player == null) {
				sender.sendMessage(Messager.color("&cYou need to be a player!"));
				return false;
			}
			if (!FFA.get().getGame().joinPlayer(player))
				player.sendMessage(Messager.color("&cCould not join."));
			break;
		case "leave":
		case "exit":
		case "quit":
			if (player == null) {
				sender.sendMessage(Messager.color("&cYou need to be a player!"));
				return false;
			}
			if (!FFA.get().getGame().leavePlayer(player))
				player.sendMessage(Messager.color("&cCould not exit."));
			break;
		case "setmainlobby":
			if (player == null) {
				sender.sendMessage(Messager.color("&cYou need to be a player!"));
				return false;
			}
			if (!this.permissionCheckWithMessage(player, "ffa.setmainlobby"))
				return false;
			FFA.get().saveLocationConfig("mainlobby", player.getLocation());
			player.sendMessage(Messager.color("&aMain lobby set."));
			break;
		case "setgamespawn":
			if (player == null) {
				sender.sendMessage(Messager.color("&cYou need to be a player!"));
				return false;
			}
			if (!this.permissionCheckWithMessage(player, "ffa.setgamespawn"))
				return false;
			FFA.get().saveLocationConfig("spawn", player.getLocation());
			player.sendMessage(Messager.color("&aGame spawn set."));
			break;
		case "help":
			for (final String s : this.helpLines)
				sender.sendMessage(Messager.color(s));
			break;
		default:
			sender.sendMessage(Messager.color("&cUnknown arguments. &6Use /ffa help"));
		}
		return false;
	}

	private boolean permissionCheckWithMessage(Player player, String perm) {
		if (player.hasPermission(perm))
			return true;
		return false;
	}

}
