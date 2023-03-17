package yusama125718.man10fermentation;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

import static java.lang.Integer.parseInt;
import static yusama125718.man10fermentation.GUI.addrecipeGUI;
import static yusama125718.man10fermentation.Man10Fermentation.*;

public class Command implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("mferm.p")) return true;
        switch (args.length) {
            case 0:
                if (!system){
                    sender.sendMessage("§a§l[Man10Fermentation] §rシステムは現在OFFです");
                    return true;
                }
                GUI.OpenRecipe((Player) sender,1);
                return true;

            case 1:
                switch (args[0]){
                    case "help":
                        sender.sendMessage("§a§l[Man10Fermentation] §7/mferm §rレシピを表示します");
                        sender.sendMessage("§a§l[Man10Fermentation] §7/mferm lock §r発酵樽をロックします");
                        sender.sendMessage("§a§l[Man10Fermentation] §7/mferm unlock §r発酵樽のロックを解除します");
                        if (sender.hasPermission("mferm.op")){
                            sender.sendMessage("§a§l[Man10Fermentation] §7/mferm on/off §rシステムをon/offします");
                            sender.sendMessage("§a§l[Man10Fermentation] §7/mferm barrel §r発酵用の樽を自分に付与します");
                            sender.sendMessage("§a§l[Man10Fermentation] §7/mferm setbarrel §r今持っているアイテムの名前とLoreを発酵用の樽の名前とLoreにします");
                            sender.sendMessage("§a§l[Man10Fermentation] §7/mferm add [名前] [時間(分)] §rレシピを追加します");
                            sender.sendMessage("§a§l[Man10Fermentation] §7/mferm delete [名前] §rレシピを削除します");
                            sender.sendMessage("§a§l[Man10Fermentation] §7/mferm addworld [ワールド名] §r設置できるワールドを追加します");
                            sender.sendMessage("§a§l[Man10Fermentation] §7/mferm deleteworld [ワールド名] §r設置できるワールドを削除します");
                        }
                        if (!system){
                            sender.sendMessage("§a§l[Man10Fermentation] §rシステムは現在OFFです");
                        }
                        return true;

                    case "lock":
                        if (!system){
                            sender.sendMessage("§a§l[Man10Fermentation] §rシステムは現在OFFです");
                            return true;
                        }
                        unlockuser.remove(((Player) sender).getUniqueId());
                        if (!lockuser.contains(((Player) sender).getUniqueId())) lockuser.add(((Player) sender).getUniqueId());
                        sender.sendMessage("§a§l[Man10Fermentation] §rロックしたい発酵樽を壊してください");
                        return true;

                    case "unlock":
                        if (!system){
                            sender.sendMessage("§a§l[Man10Fermentation] §rシステムは現在OFFです");
                            return true;
                        }
                        lockuser.remove(((Player) sender).getUniqueId());
                        if (!unlockuser.contains(((Player) sender).getUniqueId())) unlockuser.add(((Player) sender).getUniqueId());
                        sender.sendMessage("§a§l[Man10Fermentation] §rロックしたい発酵樽を壊してください");
                        return true;

                    case "on":
                        if (!sender.hasPermission("mferm.op")){
                            sender.sendMessage("§a§l[Man10Fermentation] §r/mferm help でhelpを表示");
                            return true;
                        }
                        if (system){
                            sender.sendMessage("§a§l[Man10Fermentation] §rすでにONです");
                            return true;
                        }
                        system = true;
                        mferm.getConfig().set("system",system);
                        mferm.saveConfig();
                        sender.sendMessage("§a§l[Man10Fermentation] §rONにしました");
                        return true;

                    case "off":
                        if (!sender.hasPermission("mferm.op")){
                            sender.sendMessage("§a§l[Man10Fermentation] §r/mferm help でhelpを表示");
                            return true;
                        }
                        if (!system){
                            sender.sendMessage("§a§l[Man10Fermentation] §rすでにONです");
                            return true;
                        }
                        system = false;
                        mferm.getConfig().set("system",system);
                        mferm.saveConfig();
                        sender.sendMessage("§a§l[Man10Fermentation] §rOFFにしました");
                        return true;

                    case "barrel":
                        if (!sender.hasPermission("mferm.op")){
                            sender.sendMessage("§a§l[Man10Fermentation] §r/mferm help でhelpを表示");
                            return true;
                        }
                        if (!system){
                            sender.sendMessage("§a§l[Man10Fermentation] §rシステムは現在OFFです");
                            return true;
                        }
                        ((Player) sender).getInventory().addItem(Function.CreateBarrel());
                        sender.sendMessage("§a§l[Man10Fermentation] §r付与しました");
                        return true;

                    case "setbarrel":
                        if (!sender.hasPermission("mferm.op")){
                            sender.sendMessage("§a§l[Man10Fermentation] §r/mferm help でhelpを表示");
                            return true;
                        }
                        if (!system){
                            sender.sendMessage("§a§l[Man10Fermentation] §rシステムは現在OFFです");
                            return true;
                        }
                        barrellore.clear();
                        ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
                        if (!item.hasItemMeta()){
                            barrellore.add(Component.text("発酵用の樽"));
                            barrelname = Component.text("樽");
                            sender.sendMessage("§a§l[Man10Fermentation] §rItemMetaが存在しないので初期値を設定しました。");
                            return true;
                        }
                        if (item.getItemMeta().hasDisplayName()) barrelname = item.getItemMeta().displayName();
                        else barrelname = Component.text("樽");
                        if (item.getItemMeta().hasLore()) barrellore = item.getItemMeta().lore();
                        else barrellore.add(Component.text("発酵用の樽"));
                        sender.sendMessage("§a§l[Man10Fermentation] §r設定しました。");
                        return true;

                    default:
                        sender.sendMessage("§a§l[Man10Fermentation] §r/mferm help でhelpを表示");
                        return true;
                }

            case 2:
                if (!sender.hasPermission("mferm.op")){
                    sender.sendMessage("§a§l[Man10Fermentation] §r/mferm help でhelpを表示");
                    return true;
                }
                switch (args[0]) {
                    case "delete":
                        Data.recipe target = null;
                        for (Data.recipe data : recipes) {
                            if (Objects.equals(data.name, args[1])) {
                                target = data;
                                break;
                            }
                        }
                        if (target == null){
                            sender.sendMessage("§a§l[Man10Fermentation] §rその名前のレシピは存在しません");
                            return true;
                        }
                        for (File file : configfile.listFiles()){
                            if (!file.getName().equals(args[1] + ".yml")) continue;
                            if (file.delete()) {
                                recipes.remove(target);
                                sender.sendMessage("§a§l[Man10Fermentation] §r削除しました");
                            }else{
                                sender.sendMessage("§a§l[Man10Fermentation] §rファイルの削除に失敗しました");
                            }
                            return true;
                        }
                        sender.sendMessage("§a§l[Man10Fermentation] §rファイルが見つかりませんでした");
                        return true;

                    case "addworld":
                        List<String> worlds = new ArrayList<>();
                        for (World w : Bukkit.getWorlds()) worlds.add(w.getName());
                        if (!worlds.contains(args[1])){
                            sender.sendMessage("§a§l[Man10Fermentation] §r指定されたワールドが見つかりませんでした");
                            return true;
                        }
                        if (allowworld.contains(args[1])){
                            sender.sendMessage("§a§l[Man10Fermentation] §r指定されたワールドは既に追加されています");
                            return true;
                        }
                        allowworld.add(args[1]);
                        mferm.getConfig().set("worlds",allowworld);
                        mferm.saveConfig();
                        sender.sendMessage("§a§l[Man10Fermentation] §r追加しました");
                        return true;

                    case "deleteworld":
                        List<String> worlds1 = new ArrayList<>();
                        for (World w : Bukkit.getWorlds()) worlds1.add(w.getName());
                        if (!worlds1.contains(args[1])){
                            sender.sendMessage("§a§l[Man10Fermentation] §r指定されたワールドが見つかりませんでした");
                            return true;
                        }
                        if (!allowworld.contains(args[1])){
                            sender.sendMessage("§a§l[Man10Fermentation] §r指定されたワールドはリストに存在しません");
                            return true;
                        }
                        allowworld.remove(args[1]);
                        mferm.getConfig().set("worlds",allowworld);
                        mferm.saveConfig();
                        sender.sendMessage("§a§l[Man10Fermentation] §r削除しました");
                        return true;
                }

            case 3:
                if (!args[0].equals("add") || !sender.hasPermission("mferm.op")){
                    sender.sendMessage("§a§l[Man10Fermentation] §r/mferm help でhelpを表示");
                    return true;
                }

                List<String> returnlist = new ArrayList<>();
                for (Data.recipe p : recipes) returnlist.add(p.name);
                if (returnlist.contains(args[1])){
                    sender.sendMessage("§a§l[Man10Fermentation] §rその名前は既に使われています");
                    return true;
                }
                boolean isNumeric = args[2].matches("-?\\d+");
                if (!isNumeric){
                    sender.sendMessage("§a§l[Man10Fermentation] §r時間が無効です");
                    return true;
                }
                addlist.put((Player) sender, new Data.addrecipe(args[1],parseInt(args[2])));
                addrecipeGUI((Player) sender);
                return true;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("mferm.p")) return null;
        if(command.getName().equalsIgnoreCase("mferm")) {
            if (args.length == 1) {
                if (args[0].length() == 0) {
                    if (sender.hasPermission("mferm.op"))
                        return Arrays.asList("add", "addworld", "barrel", "delete", "deleteworld", "lock", "on", "off", "setbarrel", "unlock");
                    else return Arrays.asList("lock", "unlock");
                } else {
                    if (sender.hasPermission("mferm.op")) {
                        if ("add".startsWith(args[0]) && "addworld".startsWith(args[0])) {
                            return Arrays.asList("add", "addworld");
                        }
                        else if ("addworld".startsWith(args[0])) {
                            return Collections.singletonList("addworld");
                        }
                        else if ("barrel".startsWith(args[0])) {
                            return Collections.singletonList("barrel");
                        }
                        else if ("delete".startsWith(args[0]) && "deleteworld".startsWith(args[0])) {
                            return Arrays.asList("delete", "deleteworld");
                        }
                        else if ("deleteworld".startsWith(args[0])) {
                            return Collections.singletonList("deleteworld");
                        }
                        else if ("on".startsWith(args[0]) && "off".startsWith(args[0])) {
                            return Arrays.asList("on", "off");
                        }
                        else if ("on".startsWith(args[0])) {
                            return Collections.singletonList("on");
                        }
                        else if ("off".startsWith(args[0])) {
                            return Collections.singletonList("off");
                        }
                        else if ("setbarrel".startsWith(args[0])) {
                            return Collections.singletonList("setbarrel");
                        }
                    }
                    if ("lock".startsWith(args[0])) {
                        return Collections.singletonList("lock");
                    }
                    else if ("unlock".startsWith(args[0])) {
                        return Collections.singletonList("unlock");
                    }
                }
            } else if (args.length == 2 && sender.hasPermission("mferm.op")) {
                if (args[0].equals("addworld") || args[0].equals("deleteworld")) {
                    ArrayList<String> w = new ArrayList<>();
                    for (World world : Bukkit.getWorlds()) w.add(world.getName());
                    return w;
                }
                else if (args[0].equals("add") || args[0].equals("delete")) {
                    return Collections.singletonList("[レシピ名]");
                }
            } else if (args.length == 3 && sender.hasPermission("mferm.op")) {
                if (args[0].equals("add") || args[0].equals("delete")) {
                    return Collections.singletonList("[時間(分)]");
                }
            }
        }
        return null;
    }
}
