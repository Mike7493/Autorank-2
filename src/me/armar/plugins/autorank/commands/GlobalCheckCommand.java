package me.armar.plugins.autorank.commands;

import java.util.UUID;

import me.armar.plugins.autorank.Autorank;
import me.armar.plugins.autorank.commands.manager.AutorankCommand;
import me.armar.plugins.autorank.language.Lang;
import me.armar.plugins.autorank.util.AutorankTools;
import me.armar.plugins.autorank.util.AutorankTools.Time;
import me.armar.plugins.autorank.util.uuid.UUIDManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlobalCheckCommand extends AutorankCommand {

	private final Autorank plugin;

	public GlobalCheckCommand(final Autorank instance) {
		this.setUsage("/ar gcheck [player]");
		this.setDesc("Check [player]'s global playtime.");
		this.setPermission("autorank.check");

		plugin = instance;
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd,
			final String label, final String[] args) {

		// This is a global check. It will not show you the database numbers
		if (!plugin.getMySQLWrapper().isMySQLEnabled()) {
			sender.sendMessage(ChatColor.RED
					+ Lang.MYSQL_IS_NOT_ENABLED.getConfigValue());
			return true;
		}

		if (args.length > 1) {

			if (!plugin.getCommandsManager().hasPermission(
					"autorank.checkothers", sender)) {
				return true;
			}

			final Player player = plugin.getServer().getPlayer(args[1]);
			if (player == null) {
				sender.sendMessage(Lang.PLAYER_NOT_ONLINE
						.getConfigValue(args[1]));
				return true;
			} else {
				if (player.hasPermission("autorank.exclude")) {
					sender.sendMessage(ChatColor.RED
							+ Lang.PLAYER_IS_EXCLUDED.getConfigValue(args[1]));
					return true;
				}

				final UUID uuid = UUIDManager.getUUIDFromPlayer(player
						.getName());

				final int minutes = plugin.getPlaytimes().getGlobalTime(uuid);

				if (minutes < 0) {
					sender.sendMessage(Lang.PLAYER_IS_INVALID
							.getConfigValue(args[1]));
					return true;
				}

				AutorankTools.sendColoredMessage(
						sender,
						args[1]
								+ " has played for "
								+ AutorankTools.timeToString(minutes,
										Time.MINUTES) + " across all servers.");
				// Do no check. Players can't be checked on global times (at the moment)
				//check(sender, player);
			}
		} else if (sender instanceof Player) {
			if (!plugin.getCommandsManager().hasPermission("autorank.check",
					sender)) {
				return true;
			}

			if (sender.hasPermission("autorank.exclude")) {
				sender.sendMessage(ChatColor.RED
						+ Lang.PLAYER_IS_EXCLUDED.getConfigValue(sender
								.getName()));
				return true;
			}
			final Player player = (Player) sender;

			final UUID uuid = UUIDManager.getUUIDFromPlayer(player.getName());

			AutorankTools.sendColoredMessage(
					sender,
					"You have played for "
							+ AutorankTools.timeToString(plugin.getPlaytimes()
									.getGlobalTime(uuid), Time.MINUTES)
							+ " across all servers.");

		} else {
			AutorankTools.sendColoredMessage(sender,
					Lang.CANNOT_CHECK_CONSOLE.getConfigValue());
		}
		return true;
	}

}
