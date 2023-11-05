package world.lemmy.rookisboss.boss;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import world.lemmy.rookisboss.BossQuestService;
import world.lemmy.rookisboss.RookisBoss;
import world.lemmy.rookisboss.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BossUtils {
    private static BossQuestService bossQuestService;
    public static boolean isBossEntity(Entity en){

        if(bossQuestService == null) bossQuestService = RookisBoss.getPlugin().getBossQuestService();

        List<MetadataValue> meta = en.getMetadata("BossEnemy");
        if(en instanceof LivingEntity){
            return RookisBoss.getPlugin().getBossQuestService().getActiveQuests().containsKey(en) || meta.stream().anyMatch(MetadataValue::asBoolean);
        }
        return false;
    }

    public static BossEnemy getBossData(LivingEntity en) {
        return getBossData(en, true);
    }

    public static BossEnemy getBossData(LivingEntity en, boolean remove){
        if(bossQuestService == null) bossQuestService = RookisBoss.getPlugin().getBossQuestService();
        List<MetadataValue> meta = en.getMetadata("BossEnemy_data");
        for(MetadataValue value : meta){
            if(value.value() instanceof BossEnemy enemy){
                if(remove){
                    for(BossQuest quest : bossQuestService.getActiveQuests().values().toArray(new BossQuest[0])){
                        if(quest.getBossEnemiesMap().containsKey(en)){
                            quest.getBossEnemiesMap().remove(en);
                            bossQuestService.checkQuest(quest);
                        }
                    }
                }

                return enemy;
            }
        }
        return null;
    }

    public static BossEnemy createBoss(String name, int maxHealth, double speed, ArrayList<ItemStack> drops, ArrayList<ItemStack> mainDrops, ItemStack[] armor, ItemStack weapon, EntityType entityType){
        if(bossQuestService == null) bossQuestService = RookisBoss.getPlugin().getBossQuestService();
        BossEnemy bossEnemy = new BossEnemy();
        bossEnemy.setName(name);
        bossEnemy.setMaxHealth(maxHealth);
        bossEnemy.setSpeed(speed);
        bossEnemy.setDrops(drops);
        bossEnemy.setArmor(armor);
        bossEnemy.setWeapon(weapon);
        bossEnemy.setEntityType(entityType);
        bossEnemy.setMainDrops(mainDrops);
        return bossEnemy;
    }

    public static ArrayList<BossEnemy> getAllBosses() {
        if(bossQuestService == null) bossQuestService = RookisBoss.getPlugin().getBossQuestService();
        ArrayList<BossEnemy> bosses = new ArrayList<>();

        ItemStack weapon = new ItemStack(Material.DIAMOND_SWORD);

        weapon.addEnchantment(Enchantment.FIRE_ASPECT, 2);
        weapon.addEnchantment(Enchantment.DAMAGE_ALL, 5);

        ItemStack[] armor = Utils.makeArmorSet(new ItemStack(Material.DIAMOND_HELMET), new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.DIAMOND_LEGGINGS), new ItemStack(Material.DIAMOND_BOOTS));

        ArrayList<ItemStack> drops = new ArrayList<>();

        drops.add(new ItemStack(Material.DIAMOND, 2));
        drops.add(new ItemStack(Material.EMERALD, 5));
        drops.add(new ItemStack(Material.IRON_INGOT, 5));
        drops.add(new ItemStack(Material.NETHERITE_INGOT, 1));

        ArrayList<ItemStack> mainDrops = new ArrayList<>();

        mainDrops.add(new ItemStack(Material.DIAMOND, 5));

        bosses.add(createBoss("Big Boy Rooki", 100, 0.25, drops, mainDrops, armor, weapon, EntityType.ZOMBIE));

        armor = Utils.makeArmorSet(new ItemStack(Material.IRON_HELMET), new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.IRON_BOOTS));


        drops.clear();
        drops.add(new ItemStack(Material.IRON_INGOT, 5));
        drops.add(new ItemStack(Material.EMERALD, 5));
        drops.add(new ItemStack(Material.NETHERITE_INGOT, 1));

        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 600 * 20, 2), true);

        drops.add(potion);

        potion = new ItemStack(Material.SPLASH_POTION);
        meta = (PotionMeta) potion.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 600 * 20, 1), true);

        drops.add(potion);

        potion = new ItemStack(Material.SPLASH_POTION);
        meta = (PotionMeta) potion.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 300 * 20, 2), true);

        drops.add(potion);

        mainDrops.clear();
        mainDrops.add(new ItemStack(Material.IRON_INGOT, 5));

        bosses.add(createBoss("Stealthy Boy Rooki", 50, 0.35, drops, mainDrops, armor, weapon, EntityType.ZOMBIE));

        armor = Utils.makeArmorSet(new ItemStack(Material.CHAINMAIL_HELMET), new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.CHAINMAIL_LEGGINGS), new ItemStack(Material.CHAINMAIL_BOOTS));

        drops.clear();
        drops.add(new ItemStack(Material.DIAMOND, 2));
        drops.add(new ItemStack(Material.EMERALD, 5));
        drops.add(new ItemStack(Material.IRON_INGOT, 5));
        drops.add(new ItemStack(Material.NETHERITE_INGOT, 1));
        mainDrops.clear();
        mainDrops.add(new ItemStack(Material.IRON_INGOT, 5));

        weapon = new ItemStack(Material.BOW);

        weapon.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
        weapon.addEnchantment(Enchantment.ARROW_FIRE, 1);
        weapon.addEnchantment(Enchantment.ARROW_INFINITE, 1);

        bosses.add(createBoss("Skelly", 25, 0.30, drops, mainDrops, armor, weapon, EntityType.SKELETON));

        return bosses;
    }
}
