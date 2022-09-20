package yusama125718.man10fermentation;

import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public final class Man10Fermentation extends JavaPlugin{

    public static List<String> allowworld = new ArrayList<>();
    public static List<Data.recipe> recipes = new ArrayList<>();
    public static HashMap<Player,Data.addrecipe> addlist = new HashMap<>();
    public static HashMap<Player, Block> activebarrel = new HashMap<>();
    public static JavaPlugin mferm;
    public static File configfile;
    public static Boolean system;
    public static List<Player> lockuser = new ArrayList<>();
    public static List<Player> unlockuser = new ArrayList<>();
    public static Component barrelname;
    public static List<Component> barrellore = new ArrayList<>();

    @Override
    public void onEnable() {
        getCommand("mferm").setExecutor(new Command());
        new Event(this);
        mferm = this;
        Function.ReloadConfig();
    }
}
