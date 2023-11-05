package world.lemmy.rookisboss;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class Utils {
    public static ItemStack[] makeArmorSet(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots){
        ItemStack[] armor = new ItemStack[4];
        armor[0] = helmet;
        armor[1] = chestplate;
        armor[2] = leggings;
        armor[3] = boots;
        return armor;
    }

    public static String getHealthBar(double health, double maxHealth){
        StringBuilder healthBar = new StringBuilder();
        int healthBarLength = 10;
        int healthBarFill = (int) Math.ceil((double) health / maxHealth * healthBarLength);
        for(int i = 0; i < healthBarFill; i++){
            healthBar.append("█");
        }
        for(int i = 0; i < healthBarLength - healthBarFill; i++){
            healthBar.append("░");
        }
        return healthBar.toString();
    }

    public static ArmorStand createArmorStand(LivingEntity parent){
        ArmorStand ent = (ArmorStand) parent.getWorld().spawnEntity(parent.getLocation(), EntityType.ARMOR_STAND);
        ent.setInvisible(true);
        ent.setInvulnerable(true);
        ent.setCustomNameVisible(true);
        ent.setSilent(true);
        ent.setSmall(true);
        ent.setVisible(false);

        return ent;
    }

    public static Location getRandomLocation(){
        Location loc = new Location(Bukkit.getWorld("world"), 0,0,0);
        int tries = 0;
        do {
            loc.setX((Math.random() * 1000) - 500);
            loc.setZ((Math.random() * 1000) - 500);
            loc.setY(loc.getWorld().getHighestBlockYAt(loc));
            tries++;
        } while (isValidLocation(loc) && tries < 25);

        return loc;
    }

    public static boolean isValidLocation(Location loc){
        return loc.isGenerated() && loc.getNearbyPlayers(100).isEmpty();
    }

    public static Biome[] validBiomes = {Biome.BADLANDS, Biome.BIRCH_FOREST, Biome.DARK_FOREST, Biome.DESERT, Biome.WINDSWEPT_SAVANNA, Biome.CHERRY_GROVE, Biome.FOREST, Biome.JUNGLE, Biome.PLAINS, Biome.SAVANNA, Biome.SAVANNA_PLATEAU, Biome.SNOWY_PLAINS, Biome.SNOWY_TAIGA};

    public static boolean isValidBiome(Biome biome){
        return Arrays.asList(validBiomes).contains(biome);
    }
}
