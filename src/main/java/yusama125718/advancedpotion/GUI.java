package yusama125718.advancedpotion;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static yusama125718.advancedpotion.AdvancedPotion.recipe;
import static yusama125718.advancedpotion.Function.*;

public class GUI {
    public static void RecipeList(Player player,Integer page){
        Inventory inv = Bukkit.createInventory(null,54,Component.text("[AdvPot]Recipe List" + page));
        for (int i = 51;i < 54;i++){
            inv.setItem(i,getItem(Material.BLUE_STAINED_GLASS_PANE,1,"次のページへ",1));
            inv.setItem(i - 3,getItem(Material.WHITE_STAINED_GLASS_PANE,1,"",1));
            inv.setItem(i - 6,getItem(Material.RED_STAINED_GLASS_PANE,1,"前のページへ",1));
        }
        for (int i = 0;i < recipe.size();i++){
            if (i == 45 || recipe.size() == i + 45 * (page - 1)){
                player.openInventory(inv);
                return;
            }
            Data.PotionRecipe list;
            if (page == 1){
                list = recipe.get(i);
            }else{
                list = recipe.get(i + 45 * (page - 1));
            }
            ItemStack item = new ItemStack(list.result.get(0));
            ItemMeta meta = item.getItemMeta();
            if (meta.hasLore()) meta.lore().clear();
            meta.displayName(Component.text(list.name));
            item.setItemMeta(meta);
            inv.setItem(i,item);
        }
        player.openInventory(inv);
    }

    public static void RecipeExample(Player p,Integer index){
        Data.PotionRecipe target = recipe.get(index);
        Inventory inv = Bukkit.createInventory(null,36,Component.text("[AdvPot Recipe]" + target.name));
        for (int i = 0;i < 36;i++) inv.setItem(i,getItem(Material.WHITE_STAINED_GLASS_PANE,1,"",0));
        inv.setItem(11,getItem(Material.BLACK_STAINED_GLASS_PANE,1,"材料(上)",0));
        inv.setItem(23,getItem(Material.BLACK_STAINED_GLASS_PANE,1,"完成品(1)",0));
        inv.setItem(24,getItem(Material.BLACK_STAINED_GLASS_PANE,1,"完成品(2)",0));
        inv.setItem(25,getItem(Material.BLACK_STAINED_GLASS_PANE,1,"完成品(3)",0));
        inv.setItem(29,getItem(Material.BLACK_STAINED_GLASS_PANE,1,"材料(下)",0));
        inv.setItem(2,target.ingredient);
        inv.setItem(14,target.result.get(0));
        inv.setItem(20,target.material);
        if (target.result.size() == 1){
            inv.setItem(5,getItem(Material.QUARTZ,1,"一度に作れる量",target.cancreate + 48));
            inv.setItem(15,getItem(Material.BARRIER,1,"",0));
            inv.setItem(16,getItem(Material.BARRIER,1,"",0));
        } else if (target.result.size() == 2){
            inv.setItem(15,target.result.get(1));
            inv.setItem(16,getItem(Material.BARRIER,1,"",0));
        } else{
            inv.setItem(15,target.result.get(1));
            inv.setItem(16,target.result.get(2));
        }
        p.openInventory(inv);
    }

    public static void addrecipeGUI(Player player){
        Inventory inv = Bukkit.createInventory(null,54, Component.text("[AdvPot]Add Recipe"));
        for (int i = 0;i < 54;i++) if (i != 11 && i != 23 && i != 24 && i != 25 && i != 29) inv.setItem(i,getItem(Material.WHITE_STAINED_GLASS_PANE,1,"",0));
        inv.setItem(13,getItem(Material.RED_STAINED_GLASS_PANE,1,"最大作成個数(結果が1種類のときのみ)",0));
        inv.setItem(20,getItem(Material.BLACK_STAINED_GLASS_PANE,1,"上に材料(上)を置く",0));
        inv.setItem(32,getItem(Material.BLACK_STAINED_GLASS_PANE,1,"上に完成品(1)を置く",0));
        inv.setItem(33,getItem(Material.BLACK_STAINED_GLASS_PANE,1,"上に完成品(2)を置く",0));
        inv.setItem(34,getItem(Material.BLACK_STAINED_GLASS_PANE,1,"上に完成品(3)を置く",0));
        inv.setItem(38,getItem(Material.BLACK_STAINED_GLASS_PANE,1,"上に材料(下)を置く",0));
        inv.setItem(49,getItem(Material.RED_STAINED_GLASS_PANE,1,"追加する",0));
        player.openInventory(inv);
    }
}
