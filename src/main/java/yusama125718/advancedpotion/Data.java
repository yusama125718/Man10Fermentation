package yusama125718.advancedpotion;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Data {

    public static class PotionRecipe{
        public String name;
        public ItemStack ingredient;
        public ItemStack material;
        public List<ItemStack> result;
        public Integer cancreate;
        public PotionRecipe(String recipename, ItemStack ingre,ItemStack mate,List<ItemStack> res,Integer create){
            name = recipename;
            ingredient = ingre;
            material = mate;
            result = res;
            cancreate = create;
        }
    }
}
