package yusama125718.man10fermentation;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static yusama125718.man10fermentation.Function.getItem;
import static yusama125718.man10fermentation.Man10Fermentation.recipes;

public class GUI {
    public static void OpenRecipe(Player p, int page){      //レシピリスト
        Inventory inv = Bukkit.createInventory(null,54, Component.text("[MFerm]Recipe List" + page));
        for (int i = 51;i < 54;i++){
            inv.setItem(i,getItem(Material.BLUE_STAINED_GLASS_PANE,1,"次のページへ",1));
            inv.setItem(i - 3,getItem(Material.WHITE_STAINED_GLASS_PANE,1,"",1));
            inv.setItem(i - 6,getItem(Material.RED_STAINED_GLASS_PANE,1,"前のページへ",1));
        }
        for (int i = 0;i < recipes.size();i++){
            if (i == 45 || recipes.size() == i + 45 * (page - 1)){
                p.openInventory(inv);
                return;
            }
            Data.recipe list;
            if (page == 1){
                list = recipes.get(i);
            }else{
                list = recipes.get(i + 45 * (page - 1));
            }
            ItemStack item = new ItemStack(list.result);
            ItemMeta meta = item.getItemMeta();
            if (meta.hasLore()) meta.lore().clear();
            item.setItemMeta(meta);
            inv.setItem(i,item);
        }
        p.openInventory(inv);
    }

    public static void addrecipeGUI(Player p){          //レシピ追加
        Inventory inv = Bukkit.createInventory(null,18, Component.text("[MFerm]Add Recipe"));
        for (int i = 0;i < 18;i++) {
            if (i == 3 || i == 5) continue;
            inv.setItem(i,getItem(Material.WHITE_STAINED_GLASS_PANE,1,"",1));
        }
        inv.setItem(12,getItem(Material.RED_STAINED_GLASS_PANE,1,"材料",1));
        inv.setItem(13,getItem(Material.BLACK_STAINED_GLASS_PANE,1,"追加",1));
        inv.setItem(14,getItem(Material.BLUE_STAINED_GLASS_PANE,1,"完成品",1));
        inv.setItem(4,getItem(Material.QUARTZ,1,"",62));
        p.openInventory(inv);
    }

    public static void RecipeExample(Player p,Integer index){
        Data.recipe target = recipes.get(index);
        Inventory inv = Bukkit.createInventory(null,18,Component.text("[Mferm Recipe]" + target.name));
        for (int i = 0;i < 18;i++) inv.setItem(i,getItem(Material.WHITE_STAINED_GLASS_PANE,1,"",0));
        int min, hour, day = 0;
        String time = "";
        if (target.time >= 1440){
            day = target.time / 1440;
            hour = (target.time % 1440) / 60;
            min = target.time % 60;
            time = day + "日" + hour + "時間" + min + "分";
        } else if (target.time >= 60) {
            hour = target.time / 60;
            min = target.time % 60;
            time = hour + "時間" + min + "分";
        } else time = target.time + "分";
        inv.setItem(12,getItem(Material.RED_STAINED_GLASS_PANE,1,"材料",1));
        inv.setItem(14,getItem(Material.BLUE_STAINED_GLASS_PANE,1,"完成品",1));
        inv.setItem(4,getItem(Material.QUARTZ,1,time,62));
        inv.setItem(3,target.material);
        inv.setItem(5,target.result);
        p.openInventory(inv);
    }

    public static void OpenBarrel(Player p){
        Inventory inv = Bukkit.createInventory(null,18, Component.text("[MFerm]発酵樽"));
        for (int i = 0;i < 18;i++) {
            if (i == 4) continue;
            inv.setItem(i,getItem(Material.WHITE_STAINED_GLASS_PANE,1,"",1));
        }
        inv.setItem(13,getItem(Material.RED_STAINED_GLASS_PANE,1,"開始",1));
        p.openInventory(inv);
    }

    public static void OpenRecipeBarrel(Player p, Data.recipe r, LocalDateTime d){
        Inventory inv = Bukkit.createInventory(null,18, Component.text("[MFerm]発酵中"));
        for (int i = 0;i < 18;i++) inv.setItem(i,getItem(Material.WHITE_STAINED_GLASS_PANE,1,"",1));
        LocalDateTime finishtime = d.plusMinutes(r.time);
        if (LocalDateTime.now().isAfter(finishtime)){
            inv.setItem(4,r.result);
            inv.setItem(13,getItem(Material.RED_STAINED_GLASS_PANE,1,"受け取り",1));
            p.openInventory(inv);
        } else {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("MM/dd HH:mm");
            String name = "完成予定 : " + finishtime.format(f);
            int i = 0;
            if (r.material.hasItemMeta() && r.material.getItemMeta().hasCustomModelData()) i = r.material.getItemMeta().getCustomModelData();
            inv.setItem(4, getItem(r.material.getType(), r.material.getAmount(), name, i));
            inv.setItem(13, getItem(Material.RED_STAINED_GLASS_PANE,1,"キャンセル",1));
            p.openInventory(inv);
        }
    }
}
