package yusama125718.advancedpotion;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.List;

import static org.bukkit.Material.*;
import static yusama125718.advancedpotion.AdvancedPotion.*;
import static yusama125718.advancedpotion.Config.*;

public class Function {
    public static boolean reloadconfig(){
        Bukkit.broadcast("§9§l[AdvancedPotion] §rリロードします", "advpot.op");       //configロード
        recipe.clear();
        potp.saveDefaultConfig();
        RoadFile();
        if (configfile.listFiles() != null){
            for (File file : configfile.listFiles()){
                if (getConfig(YamlConfiguration.loadConfiguration(file),file) != null) recipe.add(getConfig(YamlConfiguration.loadConfiguration(file),file));
            }
        }

        List<Integer> blaze = potp.getConfig().getIntegerList("BLAZE_POWDER");
        allowitem.put(Material.BLAZE_POWDER,blaze);
        List<Integer> fspider = potp.getConfig().getIntegerList("FERMENTED_SPIDER_EYE");
        allowitem.put(Material.FERMENTED_SPIDER_EYE,fspider);
        List<Integer> ghast = potp.getConfig().getIntegerList("GHAST_TEAR");
        allowitem.put(Material.GHAST_TEAR,ghast);
        List<Integer> melon = potp.getConfig().getIntegerList("GLISTERING_MELON_SLICE");
        allowitem.put(Material.GLISTERING_MELON_SLICE,melon);
        List<Integer> carrot = potp.getConfig().getIntegerList("GOLDEN_CARROT");
        allowitem.put(Material.GOLDEN_CARROT,carrot);
        List<Integer> gunpowder = potp.getConfig().getIntegerList("GUNPOWDER");
        allowitem.put(Material.GUNPOWDER,gunpowder);
        List<Integer> magma = potp.getConfig().getIntegerList("MAGMA_CREAM");
        allowitem.put(Material.MAGMA_CREAM,magma);
        List<Integer> wart = potp.getConfig().getIntegerList("NETHER_WART");
        allowitem.put(NETHER_WART,wart);
        List<Integer> phantom = potp.getConfig().getIntegerList("PHANTOM_MEMBRANE");
        allowitem.put(Material.PHANTOM_MEMBRANE,phantom);
        List<Integer> pufferfish = potp.getConfig().getIntegerList("PUFFERFISH");
        allowitem.put(Material.PUFFERFISH,pufferfish);
        List<Integer> rabbit = potp.getConfig().getIntegerList("RABBIT_FOOT");
        allowitem.put(Material.RABBIT_FOOT,rabbit);
        List<Integer> spider = potp.getConfig().getIntegerList("SPIDER_EYE");
        allowitem.put(Material.SPIDER_EYE,spider);
        List<Integer> sugar = potp.getConfig().getIntegerList("SUGAR");
        allowitem.put(Material.SUGAR,sugar);
        List<Integer> turtle = potp.getConfig().getIntegerList("TURTLE_HELMET");
        allowitem.put(Material.TURTLE_HELMET,turtle);
        List<Integer> potion = potp.getConfig().getIntegerList("POTION");
        allowitem.put(Material.POTION,potion);
        recipeoperation = potp.getConfig().getBoolean("recipe");
        protectoperation = potp.getConfig().getBoolean("protect");
        Bukkit.broadcast("§9§l[AdvancedPotion] §rリロード完了", "advpot.op");
        return true;
    }

    public static boolean checkingredient(Material mate){
        return mate == BLAZE_POWDER || mate == FERMENTED_SPIDER_EYE || mate == GHAST_TEAR || mate == GLISTERING_MELON_SLICE || mate == GOLDEN_CARROT || mate == GUNPOWDER || mate == MAGMA_CREAM || mate == NETHER_WART || mate == PHANTOM_MEMBRANE || mate == PUFFERFISH || mate == RABBIT_FOOT || mate == SPIDER_EYE || mate == SUGAR || mate == TURTLE_HELMET || mate == POTION;
    }

    public static boolean checknull(YamlConfiguration config){
        if (config.getString("name") == null) {
            Bukkit.broadcast("name","advpot.op");
            return false;
        }
        if (config.get("maxcreate") == null) {
            Bukkit.broadcast("maxcreate", "advpot.op");
            return false;
        }
        if (config.get("ingredient") == null) {
            Bukkit.broadcast("ingredient", "advpot.op");
            return false;
        }
        if (config.get("material") == null) {
            Bukkit.broadcast("material","advpot.op");
            return false;
        }
        if (config.get("result") == null) {
            Bukkit.broadcast("result","advpot.op");
            return false;
        }
        if (config.get("result.1") == null){
            Bukkit.broadcast("result.1","advpot.op");
            return false;
        }
        return true;
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
