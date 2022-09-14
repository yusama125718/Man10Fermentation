package yusama125718.man10fermentation;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Integer.parseInt;
import static yusama125718.man10fermentation.GUI.addrecipeGUI;
import static yusama125718.man10fermentation.Man10Fermentation.*;

public class Command implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("mferm.p")) return true;
        switch (args.length) {
            case 0:
                GUI.OpenRecipe((Player) sender,1);
                return true;

            case 1:
                switch (args[0]){
                    case "help":
                        sender.sendMessage("§a§l[Man10Fermentation] §r/mferm レシピを表示します");
                        sender.sendMessage("§a§l[Man10Fermentation] §r/mferm lock 発酵樽をロックします");
                        sender.sendMessage("§a§l[Man10Fermentation] §r/mferm unlock 発酵樽のロックを解除します");
                        if (sender.hasPermission("mferm.op")){
                            sender.sendMessage("§a§l[Man10Fermentation] §r/mferm on/off システムをon/offします");
                            sender.sendMessage("§a§l[Man10Fermentation] §r/mferm barral 発酵用の樽を自分付与します");
                            sender.sendMessage("§a§l[Man10Fermentation] §r/mferm add [名前] [時間(分)] レシピを追加します");
                            sender.sendMessage("§a§l[Man10Fermentation] §r/mferm delete [名前] レシピを削除します");
                            sender.sendMessage("§a§l[Man10Fermentation] §r/mferm addworld [ワールド名] 設置できるワールドを追加します");
                            sender.sendMessage("§a§l[Man10Fermentation] §r/mferm deleteworld [ワールド名] 設置できるワールドを削除します");
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
                        unlockuser.remove((Player) sender);
                        if (lockuser.contains((Player) sender)) lockuser.add((Player) sender);
                        sender.sendMessage("§a§l[Man10Fermentation] §rロックしたい発酵樽を壊してください");
                        return true;

                    case "unlock":
                        if (!system){
                            sender.sendMessage("§a§l[Man10Fermentation] §rシステムは現在OFFです");
                            return true;
                        }
                        lockuser.remove((Player) sender);
                        if (unlockuser.contains((Player) sender)) unlockuser.add((Player) sender);
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

                    case "barral":
                        if (!system){
                            sender.sendMessage("§a§l[Man10Fermentation] §rシステムは現在OFFです");
                            return true;
                        }
                        if (!sender.hasPermission("mferm.op")){
                            sender.sendMessage("§a§l[Man10Fermentation] §r/mferm help でhelpを表示");
                            return true;
                        }
                        ((Player) sender).getInventory().addItem(Function.CreateBarrel());
                        sender.sendMessage("§a§l[Man10Fermentation] §r付与しました");
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
                            if (!file.getName().equals(args[2] + ".yml")) continue;
                            if (file.delete()) {
                                recipes.remove(target);
                                sender.sendMessage("§a§l[Man10Fermentation] §r削除しました");
                                return true;
                            }else{
                                sender.sendMessage("§a§l[Man10Fermentation] §rファイルの削除に失敗しました");
                                return true;
                            }
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
        if (sender.hasPermission("mferm.p")) return null;
        return null;
    }
}
