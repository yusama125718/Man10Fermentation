package yusama125718.man10fermentation;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;


import java.io.File;
import java.util.List;

import static yusama125718.man10fermentation.Config.getConfig;
import static yusama125718.man10fermentation.Man10Fermentation.*;

public class Function {
    public static Boolean checknull(YamlConfiguration config){              //ロード用
        return (config.getString("name") != null && config.getString("time") != null && config.getItemStack("material") != null && config.getItemStack("material") != null);
    }

    public static void ReloadConfig(){              //りろーど
        recipes.clear();
        system = false;
        allowworld.clear();
        mferm.saveDefaultConfig();
        system = mferm.getConfig().getBoolean("system");
        allowworld.addAll(mferm.getConfig().getStringList("worlds"));
        barrelname = Component.text(mferm.getConfig().getString("name"));
        List<String> l = mferm.getConfig().getStringList("lore");
        for (String s : l) barrellore.add(Component.text(s));
        Config.LoadFile();
        if (configfile.listFiles() != null){
            for (File file : configfile.listFiles()){
                if (getConfig(YamlConfiguration.loadConfiguration(file),file) != null) recipes.add(getConfig(YamlConfiguration.loadConfiguration(file),file));
            }
        }
    }

    public static ItemStack CreateBarrel(){             //発酵樽作成
        ItemStack barrel = new ItemStack(Material.BARREL);
        ItemMeta meta = barrel.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(mferm , "Man10Fermentation"), PersistentDataType.STRING,"barrel");
        meta.displayName(barrelname);
        meta.lore(barrellore);
        barrel.setItemMeta(meta);
        return barrel;
    }

    public static ItemStack getItem(Material mate,Integer amount,String name,Integer cmd){
        ItemStack item = new ItemStack(mate,amount);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name));
        meta.setCustomModelData(cmd);
        item.setItemMeta(meta);
        return item;
    }
}
