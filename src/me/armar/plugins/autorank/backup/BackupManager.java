package me.armar.plugins.autorank.backup;

import java.io.File;
import java.io.IOException;

import me.armar.plugins.autorank.Autorank;

import com.google.common.io.Files;

/**
 * Class that allows me to backup files before overwriting them.
 * Stores functions to backup.
 * <p>
 * Date created: 15:25:43 12 dec. 2014
 * 
 * @author Staartvin
 * 
 */
public class BackupManager {

	private final Autorank plugin;

	public BackupManager(final Autorank plugin) {
		this.plugin = plugin;
	}

	/**
	 * Backup a file to a folder
	 * 
	 * @param sourceFileName Path of file to backup
	 * @param storePath Path to backup the file to, can be null.
	 */
	public void backupFile(final String sourceFileName, final String storePath) {
		// CAN ONLY COPY YML
		final String folderPath = plugin.getDataFolder().getAbsolutePath()
				+ File.separator;
		final File sourceFile = new File(folderPath + sourceFileName);

		File copyFile = null;

		if (storePath == null) {
			copyFile = new File(folderPath + sourceFileName.replace(".yml", "")
					+ "-backup-" + System.currentTimeMillis() + ".yml");
		} else {
			copyFile = new File(storePath.replace(".yml", "") + "-backup-"
					+ System.currentTimeMillis() + ".yml");
		}

		// Create folder if it doesn't exist.
		copyFile.getParentFile().mkdirs();

		try {
			Files.copy(sourceFile, copyFile);
			plugin.debugMessage("Made backup of '" + sourceFileName + "'!");
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}

	public void startBackupSystem() {
		// Makes a backup every day
		plugin.getServer().getScheduler()
				.runTaskTimerAsynchronously(plugin, new Runnable() {
					public void run() {

						plugin.getLogger()
								.info("Making a backup of data.yml and playerdata.yml..");

						// Before running, backup stuff.
						plugin.getBackupManager().backupFile(
								"/playerdata/playerdata.yml",
								plugin.getDataFolder().getAbsolutePath()
										+ File.separator + "backups"
										+ File.separator + "playerdata.yml");
						plugin.getBackupManager().backupFile(
								"Data.yml",
								plugin.getDataFolder().getAbsolutePath()
										+ File.separator + "backups"
										+ File.separator + "data.yml");
					}
				}, 0, 1728000);
	}

}
