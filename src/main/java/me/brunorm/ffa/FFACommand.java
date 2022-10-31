package me.brunorm.ffa;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

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
                if (!this.playerCheckWithMessage(player, sender))
                    return false;
                if (!this.permissionCheckWithMessage(player, "ffa.test"))
                    return false;
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
                if (!this.playerCheckWithMessage(player, sender))
                    return false;
                if (!FFA.get().getGame().joinPlayer(player))
                    player.sendMessage(Messager.color("&cCould not join."));
                break;
            case "leave":
            case "exit":
            case "quit":
                if (!this.playerCheckWithMessage(player, sender))
                    return false;
                if (!FFA.get().getGame().leavePlayer(player))
                    player.sendMessage(Messager.color("&cCould not exit."));
                break;
            case "setmainlobby":
                if (!this.playerCheckWithMessage(player, sender))
                    return false;
                if (!this.permissionCheckWithMessage(player, "ffa.setmainlobby"))
                    return false;
                FFA.get().saveLocationConfig("mainlobby", player.getLocation());
                player.sendMessage(Messager.color("&aMain lobby set."));
                break;
            case "setgamespawn":
                if (!this.playerCheckWithMessage(player, sender))
                    return false;
                if (!this.permissionCheckWithMessage(player, "ffa.setgamespawn"))
                    return false;
                FFA.get().saveLocationConfig("spawn", player.getLocation());
                player.sendMessage(Messager.color("&aGame spawn set."));
                break;
            case "help":
                for (final String s : this.helpLines)
                    sender.sendMessage(Messager.color(s));
                break;
            case "ver":
            case "version":
            case "about":
            case "info":
                PluginDescriptionFile pdf = FFA.get().getDescription();
                sender.sendMessage(Messager.colorFormat("&b%s &eversion &a%s &emade by &b%s", pdf.getName(),
                        pdf.getVersion(), String.join(", ", pdf.getAuthors())));
                break;
            default:
                sender.sendMessage(Messager.get("unknown_arguments"));
        }
        return false;
    }

    private boolean playerCheckWithMessage(Player player, CommandSender sender) {
        if (player != null)
            return true;
        sender.sendMessage(Messager.color("&cYou need to be a player!"));
        return false;
    }

    private boolean permissionCheckWithMessage(Player player, String perm) {
        if (player == null)
            return true;
        if (player.hasPermission(perm))
            return true;
        player.sendMessage(Messager.color("&cYou don't have the required permission: " + perm.toLowerCase()));
        return false;
    }

}
