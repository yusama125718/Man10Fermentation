package yusama125718.advancedpotion;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static yusama125718.advancedpotion.AdvancedPotion.*;
import static yusama125718.advancedpotion.Data.*;
import static yusama125718.advancedpotion.Function.*;

public class Config {
    private static final File folder = new File(potp.getDataFolder().getAbsolutePath() + File.separator + "recipes");

    public static void RoadFile(){
        if (potp.getDataFolder().listFiles() != null){
            for (File file : Objects.requireNonNull(potp.getDataFolder().listFiles())) {
                if (file.getName().equals("recipes")) {
                    configfile = file;
                    return;
                }
            }
        }
        if (folder.mkdir()) {
            Bukkit.broadcast("§9§l[AdvancedPotion] §rレシピフォルダを作成しました", "advpot.op");
            configfile = folder;
        } else {
            Bukkit.broadcast("§9§l[AdvancedPotion] §rレシピフォルダの作成に失敗しました", "advpot.op");
        }
    }

    public static PotionRecipe getConfig(YamlConfiguration config,File file){
        if (!checknull(config)) {
            Bukkit.broadcast("§9§l[AdvancedPotion] §r" + file.getName() + "の読み込みに失敗しました","advpot.op");
            return null;
        }
        ItemStack ingre = config.getItemStack("ingredient");
        ItemStack potmaterial = config.getItemStack("material");
        List<ItemStack> item = new ArrayList<ItemStack>();
        for (int i = 1;i < 3;i++) {
            if (config.get("result."+ i) == null) return new PotionRecipe(config.getString("name"),ingre,potmaterial,item,config.getInt("maxcreate"));
            item.add(config.getItemStack("result." + i));
        }
        return new PotionRecipe(config.getString("name"),ingre,potmaterial,item,config.getInt("maxcreate"));
    }
}
