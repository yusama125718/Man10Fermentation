package yusama125718.man10fermentation;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Objects;

import static yusama125718.man10fermentation.Man10Fermentation.*;

public class Config {
    private static final File folder = new File(mferm.getDataFolder().getAbsolutePath() + File.separator + "recipes");

    public static void LoadFile(){
        if (mferm.getDataFolder().listFiles() != null){
            for (File file : Objects.requireNonNull(mferm.getDataFolder().listFiles())) {
                if (file.getName().equals("recipes")) {
                    configfile = file;
                    return;
                }
            }
        }
        if (folder.mkdir()) {
            Bukkit.broadcast("§a§l[Man10Fermentation] §rレシピフォルダを作成しました", "mferm.op");
            configfile = folder;
        } else {
            Bukkit.broadcast("§a§l[Man10Fermentation] §rレシピフォルダの作成に失敗しました", "mferm.op");
        }
    }

    public static Data.recipe getConfig(YamlConfiguration config, File file){
        if (!Function.checknull(config)) {
            Bukkit.broadcast("§a§l[Man10Fermentation] §r" + file.getName() + "の読み込みに失敗しました","mferm.op");
            return null;
        }
        ItemStack material = config.getItemStack("material");
        ItemStack result = config.getItemStack("result");
        return new Data.recipe(config.getString("name"),config.getInt("time"),material,result);
    }
}
