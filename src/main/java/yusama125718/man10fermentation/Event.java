package yusama125718.man10fermentation;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Barrel;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Integer.parseInt;
import static org.bukkit.Material.*;
import static yusama125718.man10fermentation.GUI.*;
import static yusama125718.man10fermentation.Man10Fermentation.*;

public class Event implements Listener {
    public Event(Man10Fermentation plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void PlaceBarrel(BlockPlaceEvent e){         //発酵樽設置
        if (!e.getBlockPlaced().getState().getType().equals(Material.BARREL)) return;
        if (!e.getItemInHand().hasItemMeta() || e.getItemInHand().getItemMeta().getPersistentDataContainer().isEmpty()) return;
        if (!e.getItemInHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(mferm , "Man10Fermentation"), PersistentDataType.STRING)) return;
        if (!system) {
            e.getPlayer().sendMessage("§a§l[Man10Fermentation] §r現在システムがoffのため設置できません。");
            e.setCancelled(true);
            return;
        }
        if (!allowworld.contains(e.getPlayer().getWorld().getName())){
            e.getPlayer().sendMessage("§a§l[Man10Fermentation] §rこのワールドでは設置できません。");
            e.setCancelled(true);
            return;
        }
        if (e.getBlockPlaced().getState() instanceof Barrel barrel) {
            barrel.getPersistentDataContainer().set(new NamespacedKey(mferm, "Man10Fermentation"), PersistentDataType.STRING, e.getPlayer().getUniqueId().toString());
            byte b = 0;
            barrel.getPersistentDataContainer().set(new NamespacedKey(mferm, "MFermLock"), PersistentDataType.BYTE, b);
            barrel.update();
            e.getPlayer().sendMessage("§a§l[Man10Fermentation] §r設置しました");
        }else e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void BreakBarrel(BlockBreakEvent e){         //発酵樽破壊
        if (!e.getBlock().getState().getType().equals(Material.BARREL) || unlockuser.contains(e.getPlayer()) || lockuser.contains(e.getPlayer())) return;
        if (e.getBlock().getState() instanceof Barrel barrel) {
            if (barrel.getPersistentDataContainer().has(new NamespacedKey(mferm, "Man10Fermentation"), PersistentDataType.STRING) && barrel.getPersistentDataContainer().has(new NamespacedKey(mferm, "MFermLock"), PersistentDataType.BYTE)){
                if (!barrel.getPersistentDataContainer().get(new NamespacedKey(mferm, "Man10Fermentation"), PersistentDataType.STRING).equals(e.getPlayer().getUniqueId().toString()) && barrel.getPersistentDataContainer().get(new NamespacedKey(mferm, "MFermLock"), PersistentDataType.BYTE) == 1 && !e.getPlayer().hasPermission("mferm.op")){
                    e.setCancelled(true);
                    e.getPlayer().sendMessage("§a§l[Man10Fermentation] §rこの樽は保護されています");
                    return;
                }
                if (e.getPlayer().getInventory().firstEmpty() == -1){
                    e.setCancelled(true);
                    e.getPlayer().sendMessage("§a§l[Man10Fermentation] §rインベントリが満杯のためキャンセルしました");
                    return;
                }
                e.setDropItems(false);
                e.getPlayer().getInventory().addItem(Function.CreateBarrel());
                e.getPlayer().sendMessage("§a§l[Man10Fermentation] §r破壊しました");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void LockBarrel(BlockBreakEvent e){             //発酵樽保護
        if (!system) return;
        if (lockuser == null || unlockuser == null) return;
        if (lockuser.contains(e.getPlayer())){
            if (!e.getBlock().getState().getType().equals(Material.BARREL)){
                e.getPlayer().sendMessage("§a§l[Man10Fermentation] §rこのアイテムは保護できません");
                lockuser.remove(e.getPlayer());
                e.setCancelled(true);
                return;
            }
            if (e.getBlock().getState() instanceof Barrel barrel){
                if (!barrel.getPersistentDataContainer().has(new NamespacedKey(mferm, "Man10Fermentation"), PersistentDataType.STRING) && !barrel.getPersistentDataContainer().has(new NamespacedKey(mferm, "MFermLock"), PersistentDataType.BYTE)){
                    e.getPlayer().sendMessage("§a§l[Man10Fermentation] §rこの樽は保護できません");
                    lockuser.remove(e.getPlayer());
                    e.setCancelled(true);
                    return;
                }
                if (barrel.getPersistentDataContainer().get(new NamespacedKey(mferm, "MFermLock"), PersistentDataType.BYTE) == 1){
                    e.getPlayer().sendMessage("§a§l[Man10Fermentation] §rこの発酵樽は保護されています");
                    lockuser.remove(e.getPlayer());
                    e.setCancelled(true);
                    return;
                }
                if (!barrel.getPersistentDataContainer().get(new NamespacedKey(mferm, "Man10Fermentation"), PersistentDataType.STRING).equals(e.getPlayer().getUniqueId().toString())){
                    e.getPlayer().sendMessage("§a§l[Man10Fermentation] §rこの発酵樽をあなたが保護することはできません");
                    lockuser.remove(e.getPlayer());
                    e.setCancelled(true);
                    return;
                }
                byte b = 1;
                barrel.getPersistentDataContainer().set(new NamespacedKey(mferm, "MFermLock"), PersistentDataType.BYTE, b);
                barrel.update();
                lockuser.remove(e.getPlayer());
                e.setCancelled(true);
                e.getPlayer().sendMessage("§a§l[Man10Fermentation] §r保護しました");
                return;
            }
            lockuser.remove(e.getPlayer());
            return;
        }
        if (unlockuser.contains(e.getPlayer())){
            if (!e.getBlock().getState().getType().equals(Material.BARREL)){
                e.getPlayer().sendMessage("§a§l[Man10Fermentation] §rこのアイテムは保護できません");
                lockuser.remove(e.getPlayer());
                e.setCancelled(true);
                return;
            }
            if (e.getBlock().getState() instanceof Barrel barrel){
                if (!barrel.getPersistentDataContainer().has(new NamespacedKey(mferm, "Man10Fermentation"), PersistentDataType.STRING) && !barrel.getPersistentDataContainer().has(new NamespacedKey(mferm, "MFermLock"), PersistentDataType.BYTE)){
                    e.getPlayer().sendMessage("§a§l[Man10Fermentation] §rこの樽は保護できません");
                    lockuser.remove(e.getPlayer());
                    e.setCancelled(true);
                    return;
                }
                if (barrel.getPersistentDataContainer().get(new NamespacedKey(mferm, "MFermLock"), PersistentDataType.BYTE) == 0){
                    e.getPlayer().sendMessage("§a§l[Man10Fermentation] §rこの発酵樽は保護されていません");
                    lockuser.remove(e.getPlayer());
                    e.setCancelled(true);
                    return;
                }
                if (!barrel.getPersistentDataContainer().get(new NamespacedKey(mferm, "Man10Fermentation"), PersistentDataType.STRING).equals(e.getPlayer().getUniqueId().toString())){
                    e.getPlayer().sendMessage("§a§l[Man10Fermentation] §rこの発酵樽はあなたが保護を解除することはできません");
                    lockuser.remove(e.getPlayer());
                    e.setCancelled(true);
                    return;
                }
                byte b = 0;
                barrel.getPersistentDataContainer().set(new NamespacedKey(mferm, "MFermLock"), PersistentDataType.BYTE, b);
                barrel.update();
                unlockuser.remove(e.getPlayer());
                e.setCancelled(true);
                e.getPlayer().sendMessage("§a§l[Man10Fermentation] §r保護を解除しました");
                return;
            }
            unlockuser.remove(e.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void Interact(PlayerInteractEvent e){        //発酵樽を開く
        if (!e.getPlayer().hasPermission("mferm.p") || !e.hasBlock() || !e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || !e.getHand().equals(EquipmentSlot.HAND)) return;
        if (!e.getClickedBlock().getType().equals(BARREL)) return;
        LocalDateTime d = null;
        Data.recipe r = new Data.recipe();
        if (e.getClickedBlock().getState() instanceof Barrel barrel){
            if (!barrel.getPersistentDataContainer().has(new NamespacedKey(mferm, "Man10Fermentation"), PersistentDataType.STRING) && !barrel.getPersistentDataContainer().has(new NamespacedKey(mferm, "MFermLock"), PersistentDataType.BYTE)) return;
            if (!barrel.getPersistentDataContainer().get(new NamespacedKey(mferm, "Man10Fermentation"), PersistentDataType.STRING).equals(e.getPlayer().getUniqueId().toString()) && barrel.getPersistentDataContainer().get(new NamespacedKey(mferm, "MFermLock"), PersistentDataType.BYTE) == 1 && !e.getPlayer().hasPermission("mferm.op")){
                e.getPlayer().sendMessage("§a§l[Man10Fermentation] §rこの発酵樽は保護されています");
                e.setCancelled(true);
                return;
            }
            for (Player p : activebarrel.keySet()){
                if (activebarrel.get(p).equals(e.getClickedBlock())){
                    e.getPlayer().sendMessage("§a§l[Man10Fermentation] §rこの発酵樽は開かれています");
                    e.setCancelled(true);
                    return;
                }
            }
            if (barrel.getPersistentDataContainer().has(new NamespacedKey(mferm, "MFermDate"), PersistentDataType.STRING) && barrel.getPersistentDataContainer().has(new NamespacedKey(mferm, "MFermRecipe"), PersistentDataType.STRING)){
                for (Data.recipe recipe : recipes) {
                    if (barrel.getPersistentDataContainer().get(new NamespacedKey(mferm, "MFermRecipe"), PersistentDataType.STRING).equals(recipe.name)) {
                        r = recipe;
                        String s = barrel.getPersistentDataContainer().get(new NamespacedKey(mferm, "MFermDate"), PersistentDataType.STRING);
                        d = LocalDateTime.parse(s);
                    }
                }
            }
            e.setCancelled(true);
            if (!system) return;
            activebarrel.put(e.getPlayer(),e.getClickedBlock());
            if (r.name == null || d == null) OpenBarrel(e.getPlayer());
            else OpenRecipeBarrel(e.getPlayer(), r, d);
        }
    }

    @EventHandler
    public void AddGUIClick(InventoryClickEvent e) throws IOException {     //レシピ追加用
        if (!e.getView().title().equals(Component.text("[MFerm]Add Recipe"))) return;
        if (!e.getWhoClicked().hasPermission("mferm.op")) {
            e.setCancelled(true);
            return;
        }
        if (e.getCurrentItem() == null) return;
        if (e.getCurrentItem().getType() == BLUE_STAINED_GLASS_PANE  ||  e.getCurrentItem().getType() == RED_STAINED_GLASS_PANE || e.getCurrentItem().getType() == WHITE_STAINED_GLASS_PANE){
            e.setCancelled(true);
            return;
        }
        if (e.getCurrentItem().getType() != BLACK_STAINED_GLASS_PANE || e.getRawSlot() != 13) return;
        Inventory inv = e.getInventory();
        if (inv.getItem(3) == null || inv.getItem(5) == null) {
            e.getWhoClicked().sendMessage("§a§l[Man10Fermentation] §rアイテムが不足しています");
            e.setCancelled(true);
            return;
        }
        if (addlist == null || !addlist.containsKey(e.getWhoClicked())){
            e.setCancelled(true);
            return;
        }
        File folder = new File(configfile.getAbsolutePath() + File.separator + addlist.get(e.getWhoClicked()).name + ".yml");
        YamlConfiguration yml = new YamlConfiguration();        //config作成
        yml.set("name",addlist.get(e.getWhoClicked()).name);
        yml.set("time",addlist.get(e.getWhoClicked()).time);
        yml.set("material",inv.getItem(3));
        yml.set("result",inv.getItem(5));
        yml.save(folder);
        recipes.add(new Data.recipe(addlist.get(e.getWhoClicked()).name,addlist.get(e.getWhoClicked()).time,inv.getItem(3),inv.getItem(5)));       //変数代入処理
        addlist.remove(e.getWhoClicked());
        e.setCancelled(true);
        e.getInventory().close();
        e.getWhoClicked().sendMessage("§a§l[Man10Fermentation] §r追加しました");
    }

    @EventHandler
    public void RecipeGUIClick(InventoryClickEvent e) {     //レシピ確認用
        if (e.getInventory().getSize() != 54) return;
        String title = null;
        Component component = e.getView().title();
        if (component instanceof TextComponent text) title = text.content();
        if (title == null || title.length() != 19 || !title.startsWith("[MFerm]Recipe List")) return;
        if (e.getCurrentItem() == null) {
            e.setCancelled(true);
            return;
        }
        boolean isNumeric = title.substring(18).matches("-?\\d+");
        if (!isNumeric) return;
        int page = parseInt(title.substring(18));
        if (51 <= e.getRawSlot() && e.getRawSlot() <= 53 && e.getCurrentItem().getType().equals(BLUE_STAINED_GLASS_PANE)){    //次のページへ
            if ((double) recipes.size() / 45 > page) OpenRecipe((Player) e.getWhoClicked(),page + 1);
            e.setCancelled(true);
            return;
        }
        if (45 <= e.getRawSlot() && e.getRawSlot() <= 47 && e.getCurrentItem().getType().equals(RED_STAINED_GLASS_PANE)){     //前のページへ
            if (page != 1) OpenRecipe((Player) e.getWhoClicked(),page - 1);
            e.setCancelled(true);
            return;
        }
        if (45 <= e.getRawSlot() && e.getRawSlot() <= 53 || e.getRawSlot() + 45 * (page - 1) >= recipes.size()) {
            e.setCancelled(true);
            return;
        }
        RecipeExample((Player) e.getWhoClicked(), e.getRawSlot() + 45 * (page - 1));
        e.setCancelled(true);
    }

    @EventHandler
    public void ExampleGUIClick(InventoryClickEvent e) {     //レシピ詳細確認用
        if (e.getInventory().getSize() != 18) return;
        String title = null;
        Component component = e.getView().title();
        if (component instanceof TextComponent text) title = text.content();
        if (title == null || !title.startsWith("[Mferm Recipe]")) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void Ferment(InventoryClickEvent e){     //発酵樽用
        if (e.getCurrentItem() == null) return;
        if (!e.getView().title().equals(Component.text("[MFerm]発酵樽"))) return;
        if (!activebarrel.containsKey(e.getWhoClicked())) return;
        if (e.getCurrentItem().getType().equals(WHITE_STAINED_GLASS_PANE)){
            e.setCancelled(true);
            return;
        }
        if (e.getCurrentItem().getType().equals(RED_STAINED_GLASS_PANE) && e.getRawSlot() == 13){
            e.setCancelled(true);
            ItemStack item = e.getInventory().getItem(4);
            if (item == null) return;
            HashMap<ItemStack,Data.recipe> r = new HashMap<>();
            for (Data.recipe data : recipes) r.put(data.material,data);
            if (!r.containsKey(item)){
                e.getWhoClicked().sendMessage("§a§l[Man10Fermentation] §rそのアイテムはレシピにありません");
                return;
            }
            if (activebarrel.get(e.getWhoClicked()).getState() instanceof Barrel barrel) {
                NamespacedKey key = new NamespacedKey(mferm, "MFermRecipe");
                barrel.getPersistentDataContainer().set(key, PersistentDataType.STRING, r.get(item).name);
                key = new NamespacedKey(mferm, "MFermDate");
                barrel.getPersistentDataContainer().set(key, PersistentDataType.STRING, LocalDateTime.now().toString());
                barrel.update();
                e.getInventory().clear();
                e.getWhoClicked().sendMessage("§a§l[Man10Fermentation] §r発酵を開始します");
            }
            e.getWhoClicked().closeInventory();
        }
    }

    @EventHandler
    public void Fermentation(InventoryClickEvent e){     //発酵中用
        if (!e.getView().title().equals(Component.text("[MFerm]発酵中"))) return;
        if (!activebarrel.containsKey(e.getWhoClicked())) return;
        e.setCancelled(true);
        if (e.getCurrentItem().getType().equals(RED_STAINED_GLASS_PANE) && e.getRawSlot() == 13){
            if (e.getWhoClicked().getInventory().firstEmpty() == -1){
                e.getWhoClicked().sendMessage("§a§l[Man10Fermentation] §rインベントリが満杯のためキャンセルしました");
                return;
            }
            HashMap<String,Data.recipe> r = new HashMap<>();
            for (Data.recipe data : recipes) r.put(data.name,data);
            if (activebarrel.get(e.getWhoClicked()).getState() instanceof Barrel barrel) {
                String name = barrel.getPersistentDataContainer().get(new NamespacedKey(mferm,"MFermRecipe"), PersistentDataType.STRING);
                NamespacedKey key = new NamespacedKey(mferm, "MFermRecipe");
                barrel.getPersistentDataContainer().remove(key);
                key = new NamespacedKey(mferm, "MFermDate");
                barrel.getPersistentDataContainer().remove(key);
                if (e.getCurrentItem().getItemMeta().displayName().equals(Component.text("受け取り"))){
                    e.getWhoClicked().getInventory().addItem(r.get(name).result);
                    e.getWhoClicked().sendMessage("§a§l[Man10Fermentation] §r完成品を受け取りました");
                } else {
                    e.getWhoClicked().getInventory().addItem(r.get(name).material);
                    e.getWhoClicked().sendMessage("§a§l[Man10Fermentation] §r発酵を中断しました");
                }
                barrel.update();
            }
            e.getWhoClicked().closeInventory();
        }
    }

    @EventHandler
    public void AddRecipeClose(InventoryCloseEvent e){
        if (!e.getView().title().equals(Component.text("[MFerm]Add Recipe"))) return;
        if (addlist == null || !addlist.containsKey(e.getPlayer())) return;
        addlist.remove(e.getPlayer());
    }

    @EventHandler
    public void FermentClose(InventoryCloseEvent e) {
        if (!e.getView().title().equals(Component.text("[MFerm]発酵樽"))) return;
        if (e.getInventory().getItem(4) != null) e.getPlayer().getInventory().addItem(e.getInventory().getItem(4));
        if (activebarrel == null || !activebarrel.containsKey(e.getPlayer())) return;
        activebarrel.remove(e.getPlayer());
    }

    @EventHandler
    public void FermentationClose(InventoryCloseEvent e){
        if (!e.getView().title().equals(Component.text("[MFerm]発酵中"))) return;
        if (activebarrel == null || !activebarrel.containsKey(e.getPlayer())) return;
        activebarrel.remove(e.getPlayer());
    }
}
