package com.mtihc.minecraft.treasurechest.v8.plugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.treasurechest.v8.core.BlockInventory;
import com.mtihc.minecraft.treasurechest.v8.core.DoubleBlockInventory;
import com.mtihc.minecraft.treasurechest.v8.core.TreasureChest;
import com.mtihc.minecraft.treasurechest.v8.core.TreasureChestMemory;
import com.mtihc.minecraft.treasurechest.v8.core.TreasureChestRepository;
import com.mtihc.minecraft.treasurechest.v8.core.TreasureManager;
import com.mtihc.minecraft.treasurechest.v8.core.TreasureManagerConfiguration;
import com.mtihc.minecraft.treasurechest.v8.plugin.util.commands.CommandException;
import com.mtihc.minecraft.treasurechest.v8.plugin.util.commands.SimpleCommand;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards.AirRewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards.BankRobberRewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards.BroadcastRewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards.ExplosionRewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards.FlyRewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards.FoodRewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards.HealthRewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards.LevelRewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards.MoneyRewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards.RedstoneRewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards.RestoreRewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards.SpawnRewardFactory;

public class TreasureChestPlugin extends JavaPlugin implements Listener {
	
	
	
	
	
	static {
		
		
		ConfigurationSerialization.registerClass(TreasureChest.class);
		ConfigurationSerialization.registerClass(BlockInventory.class);
		ConfigurationSerialization.registerClass(DoubleBlockInventory.class);
		ConfigurationSerialization.registerClass(RewardInfo.class, "RewardInfo");
	}
	
	
	
	
	
	private TreasureManagerConfiguration config;
	private TreasureManager manager;
	private SimpleCommand cmd;
	
	
	
	
	
	@Override
	public void onEnable() {
		
		// create config
		config = new TreasureManagerConfiguration(this, "config");
		config.reload();
		
		// create manager
		manager = new TreasureManager(
				this, config, 
				new TreasureChestRepository(getDataFolder() + "/treasure"), 
				new TreasureChestMemory(getDataFolder() + "/players"), 
				Permission.ACCESS_TREASURE.getNode(), 
				Permission.ACCESS_UNLIMITED.getNode(),
				Permission.RANK.getNode());

		
		// create command
		cmd = new TreasureChestCommand(manager, null);
		
		// register factories
		manager.getRewardManager().setFactory(new BankRobberRewardFactory(this));
		manager.getRewardManager().setFactory(new BroadcastRewardFactory(this));
		manager.getRewardManager().setFactory(new ExplosionRewardFactory(this));
		manager.getRewardManager().setFactory(new AirRewardFactory());
		manager.getRewardManager().setFactory(new FlyRewardFactory(this));
		manager.getRewardManager().setFactory(new FoodRewardFactory());
		manager.getRewardManager().setFactory(new HealthRewardFactory());
		manager.getRewardManager().setFactory(new LevelRewardFactory());
		manager.getRewardManager().setFactory(new MoneyRewardFactory());
		manager.getRewardManager().setFactory(new RedstoneRewardFactory(this));
		
		int subregionSize = config.getSubregionSize();
		int subregionTicks = config.getSubregionTicks();
		manager.getRewardManager().setFactory(new RestoreRewardFactory(this, subregionTicks, subregionSize));
		
		manager.getRewardManager().setFactory(new SpawnRewardFactory(this));
	}
	
	
	
	
	
	/**
	 * 
	 * @return the treasure manager
	 */
	public TreasureManager getManager() {
		return manager;
	}
	
	
	
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		String lbl = label.toLowerCase();
		if(cmd.getLabel().equals(lbl) || search(cmd.getAliases(), lbl)) {
			
			
			try {
				cmd.execute(sender, args);
			} catch (CommandException e) {
				sender.sendMessage(ChatColor.RED + e.getMessage());
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	private boolean search(String[] array, String string) {
		for (String e : array) {
			if(e.equalsIgnoreCase(string)) {
				return true;
			}
		}
		return false;
	}
	
	
	
	
	
	@Override
	public FileConfiguration getConfig() {
		return config.getConfig();
	}

	@Override
	public void reloadConfig() {
		config.reload();
	}

	@Override
	public void saveConfig() {
		config.save();
	}
	
	
	
	
	
}
