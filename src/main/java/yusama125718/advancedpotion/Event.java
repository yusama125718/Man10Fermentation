package yusama125718.advancedpotion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.BrewingStandFuelEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Integer.parseInt;
import static org.bukkit.Material.*;
import static yusama125718.advancedpotion.AdvancedPotion.*;
import static yusama125718.advancedpotion.Data.*;
import static yusama125718.advancedpotion.Function.checkingredient;
import static yusama125718.advancedpotion.GUI.RecipeExample;
import static yusama125718.advancedpotion.GUI.RecipeList;

public class Event implements Listener{
    public Event(AdvancedPotion plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void BrewingStandFuelEvent(BrewingStandFuelEvent event) {             //燃料注入Protect用処理
        if (!protectoperation) return;
        if (!allowitem.containsKey(event.getFuel().getType())) return;
        if (!event.getFuel().hasItemMeta()) return;
        if (!event.getFuel().getItemMeta().hasCustomModelData()) return;
        for (Integer number : allowitem.get(BLAZE_POWDER)) {       //確認処理
            if (event.getFuel().getItemMeta().getCustomModelData() == number) return;
        }
        event.setCancelled(true);       //キャンセル処理
    }

    @EventHandler
    public void BrewRecipe(BrewEvent event) {
        ///////////////////////////////////////////////////////
        //  ここからRecipe用処理処理                             //
        ///////////////////////////////////////////////////////
        if (recipeoperation){
            for (PotionRecipe pot : recipe) {
                if (event.getContents().getIngredient() == null) return;
                ItemStack ingredient = new ItemStack(event.getContents().getIngredient()) ;
                int  iamo = ingredient.getAmount();
                if (ingredient.getAmount() > pot.ingredient.getAmount()) ingredient.setAmount(pot.ingredient.getAmount());
                if (!ingredient.equals(pot.ingredient)) continue;
                int create = 0;
                boolean finish = false;
                if (event.getResults().size() < pot.result.size()) continue;
                for (int i = 0; i <= event.getResults().size() - 1; i++){       //下の材料が正しいか確認
                    if (event.getContents().getHolder().getInventory().getContents()[i] == null) continue;
                    ItemStack result = event.getResults().get(i);
                    ItemStack material = event.getContents().getHolder().getInventory().getContents()[i];
                    if (material == null) continue;
                    if (finish){
                        System.out.println("finish");
                        if (material.hasItemMeta()) result.setItemMeta(material.getItemMeta());
                        result.setType(material.getType());
                        result.setAmount(material.getAmount());
                        continue;
                    }
                    if (!material.equals(pot.material)){
                        if (material.hasItemMeta()) result.setItemMeta(material.getItemMeta());
                        result.setType(material.getType());
                        result.setAmount(material.getAmount());
                        continue;
                    }
                    create++;
                    if (pot.cancreate == 0) if (create >= pot.result.size()) finish = true;
                    else if (create <= pot.cancreate) finish = true;
                }
                if ((create == 0 && pot.cancreate != 0) || (create != pot.result.size() && pot.cancreate == 0)) continue;
                int replace = 0;
                for (int i = 0;create > 0;i++){     //置き換え処理
                    ItemStack result = event.getResults().get(i);
                    ItemStack material = event.getContents().getHolder().getInventory().getContents()[i];
                    if (material == null || !material.equals(pot.material)) continue;
                    if (pot.cancreate == 0){
                        result.setType(pot.result.get(create - 1).getType());
                        result.setAmount(pot.result.get(create - 1).getAmount());
                        if (pot.result.get(create - 1).hasItemMeta()) result.setItemMeta(pot.result.get(create - 1).getItemMeta());
                    } else {
                        if (replace >= pot.cancreate) {
                            if (material.hasItemMeta()) result.setItemMeta(material.getItemMeta());
                            result.setType(material.getType());
                            result.setAmount(material.getAmount());
                            create--;
                            continue;
                        }
                        result.setType(pot.result.get(0).getType());
                        result.setAmount(pot.result.get(0).getAmount());
                        if (pot.result.get(0).hasItemMeta()) result.setItemMeta(pot.result.get(0).getItemMeta());
                    }
                    replace++;
                    create--;
                }
                ingredient.setAmount(iamo - pot.ingredient.getAmount() + 1);
                event.getContents().setIngredient(ingredient);
                return;
            }
        }
        ///////////////////////////////////////////////////////
        //  ここからProtect用処理                               //
        ///////////////////////////////////////////////////////
        if (!protectoperation) return;
        if (!allowitem.containsKey(Objects.requireNonNull(event.getContents().getIngredient()).getType())) return;
        if (!event.getContents().getIngredient().hasItemMeta() || !event.getContents().getIngredient().getItemMeta().hasCustomModelData()) return;
        for (Integer n : allowitem.get(event.getContents().getIngredient().getType())) {
            if (event.getContents().getIngredient().getItemMeta().getCustomModelData() == n) return;     //通常の除外対象か確認(上)
        }
        event.setCancelled(true);       //キャンセル処理
    }

    @EventHandler
    public void AddGUIClick(InventoryClickEvent e) throws IOException {     //レシピ追加用
        if (!e.getView().title().equals(Component.text("[AdvPot]Add Recipe"))) return;
        if (!e.getWhoClicked().hasPermission("advpot.op")) {
            e.setCancelled(true);
            return;
        }
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType() == BLACK_STAINED_GLASS_PANE || e.getCurrentItem().getType() == WHITE_STAINED_GLASS_PANE){
            e.setCancelled(true);
            return;
        }
        if (e.getCurrentItem().getType() == RED_STAINED_GLASS_PANE && e.getRawSlot() == 13){      //最大作成個数選択処理
            e.setCancelled(true);
            if (e.getInventory().getItem(13).getAmount() == 3) e.getInventory().getItem(13).setAmount(1);
            else e.getInventory().getItem(13).setAmount(e.getInventory().getItem(13).getAmount() + 1);
            return;
        }
        if (e.getCurrentItem().getType() != RED_STAINED_GLASS_PANE || e.getRawSlot() != 49) return;
        Inventory inv = e.getInventory();
        if (inv.getItem(11) == null || inv.getItem(23) == null || inv.getItem(29) == null) {
            e.getWhoClicked().sendMessage("§9§l[AdvancedPotion] §cアイテムが不足しています");
            e.setCancelled(true);
            return;
        }
        if (!checkingredient(inv.getItem(11).getType())){
            e.getWhoClicked().sendMessage("§9§l[AdvancedPotion] §c材料(上)が不正です");
            e.setCancelled(true);
            return;
        }
        if (inv.getItem(29).getType() != POTION){
            e.getWhoClicked().sendMessage("§9§l[AdvancedPotion] §c材料(下)が不正です");
            e.setCancelled(true);
            return;
        }
        if (addname == null || !addname.containsKey(e.getWhoClicked())){
            e.setCancelled(true);
            return;
        }
        int max = inv.getItem(13).getAmount();      //代入用変数代入処理
        List<ItemStack> addlist = new ArrayList<>();
        addlist.add(inv.getItem(23));
        if (inv.getItem(24) != null) {
            addlist.add(inv.getItem(24));
            if (!inv.getItem(24).equals(inv.getItem(23))) max = 0;
        }
        if (inv.getItem(25) != null) {
            addlist.add(inv.getItem(25));
            if (!inv.getItem(25).equals(inv.getItem(23))) max = 0;
        }
        File folder = new File(configfile.getAbsolutePath() + File.separator + addname.get(e.getWhoClicked()) + ".yml");
        YamlConfiguration yml = new YamlConfiguration();        //config作成
        yml.set("name",addname.get(e.getWhoClicked()));
        yml.set("maxcreate",max);
        yml.set("ingredient",inv.getItem(11));
        yml.set("material",inv.getItem(29));
        for (int i = 1;i <= addlist.size();i++){
            yml.set("result."+ i,addlist.get(i-1));
        }
        yml.save(folder);
        recipe.add(new PotionRecipe(addname.get(e.getWhoClicked()),inv.getItem(11),inv.getItem(29),addlist,max));       //変数代入処理
        addname.remove(e.getWhoClicked());
        e.setCancelled(true);
        e.getInventory().close();
        e.getWhoClicked().sendMessage("§9§l[AdvancedPotion] §r追加しました");
    }

    @EventHandler
    public void RecipeGUIClick(InventoryClickEvent e) {     //レシピ確認用
        if (e.getInventory().getSize() != 54) return;
        String title = null;
        Component component = e.getView().title();
        if (component instanceof TextComponent text) title = text.content();
        if (title == null || title.length() != 20 || !title.startsWith("[AdvPot]Recipe List")) return;
        if (e.getCurrentItem() == null) {
            e.setCancelled(true);
            return;
        }
        boolean isNumeric = title.substring(19).matches("-?\\d+");
        if (!isNumeric) return;
        int page = parseInt(title.substring(19));
        if (51 <= e.getRawSlot() && e.getRawSlot() <= 53 && e.getCurrentItem().getType().equals(BLUE_STAINED_GLASS_PANE)){    //次のページへ
            if ((double) recipe.size() / 45 > page) RecipeList((Player) e.getWhoClicked(),page + 1);
            e.setCancelled(true);
            return;
        }
        if (45 <= e.getRawSlot() && e.getRawSlot() <= 47 && e.getCurrentItem().getType().equals(RED_STAINED_GLASS_PANE)){     //前のページへ
            if (page != 1) RecipeList((Player) e.getWhoClicked(),page - 1);
            e.setCancelled(true);
            return;
        }
        if (45 <= e.getRawSlot() && e.getRawSlot() <= 53 || e.getRawSlot() + 45 * (page - 1) >= recipe.size()) {
            e.setCancelled(true);
            return;
        }
        RecipeExample((Player) e.getWhoClicked(), e.getRawSlot() + 45 * (page - 1));
        e.setCancelled(true);
    }

    @EventHandler
    public void ExampleGUIClick(InventoryClickEvent e) {     //レシピ詳細確認用
        if (e.getInventory().getSize() != 36) return;
        String title = null;
        Component component = e.getView().title();
        if (component instanceof TextComponent text) title = text.content();
        if (title == null || !title.startsWith("[AdvPot Recipe]")) return;
        e.setCancelled(true);
    }
}
