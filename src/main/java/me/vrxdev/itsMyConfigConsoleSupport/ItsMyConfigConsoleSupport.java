package me.vrxdev.itsMyConfigConsoleSupport;

import org.bukkit.plugin.java.JavaPlugin;
import to.itsme.itsmyconfig.ItsMyConfig;

public final class ItsMyConfigConsoleSupport extends JavaPlugin {

    @Override
    public void onEnable() {
        IMCFilterInstaller.install(ItsMyConfig.getInstance().getPlaceholderManager());
    }

    @Override
    public void onDisable() {
        IMCFilterInstaller.uninstall();
    }
}
