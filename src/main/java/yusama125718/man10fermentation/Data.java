package yusama125718.man10fermentation;

import org.bukkit.inventory.ItemStack;

public class Data {
    public static class recipe{
        public String name;
        public int time;
        public ItemStack material;
        public ItemStack result;

        public recipe(String NAME, int TIME, ItemStack MATERIAL, ItemStack RESULT) {
            name = NAME;
            time = TIME;
            material = MATERIAL;
            result = RESULT;
        }

        public recipe(){}
    }

    public static class addrecipe{
        String name;
        public int time;

        public addrecipe(String NAME, int TIME){
            name = NAME;
            time = TIME;
        }
    }
}
