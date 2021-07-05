package net.trollyloki.lokidumbcommands;

import net.trollyloki.lokidumbcommands.gun.GunCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class LokiDumbCommandsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("gun").setExecutor(new GunCommand(this));
    }

    @Override
    public void onDisable() {

    }

}
