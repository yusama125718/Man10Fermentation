package yusama125718.advancedpotion;

import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

import static yusama125718.advancedpotion.Function.reloadconfig;

public final class AdvancedPotion extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {

    public static JavaPlugin potp;
    public static HashMap<Material,List<Integer>> allowitem = new HashMap<>();
    public static boolean recipeoperation;
    public static boolean protectoperation;
    public static ArrayList<Data.PotionRecipe> recipe = new ArrayList<>();
    public static File configfile;
    public static HashMap<Player,String> addname = new HashMap<>();

    @Override
    public void onEnable() {    //起動処理
        getCommand("advpot").setExecutor(new Command());
        new Event(this);
        potp = this;
        reloadconfig();
        getServer().getPluginManager().registerEvents(this, this);      //Event用
    }
}
