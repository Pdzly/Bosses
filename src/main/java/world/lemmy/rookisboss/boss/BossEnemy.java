package world.lemmy.rookisboss.boss;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import world.lemmy.rookisboss.RookisBoss;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BossEnemy {
    private String name;
    private int maxHealth;
    private double speed;
    private int attackSpeed;
    private int attackRange;
    private int attackDamage;
    private Map<String, Object> attributes = new HashMap<>();
    private @Nullable BossQuest quest;
    private ArrayList<ItemStack> drops;
    private ArrayList<ItemStack> mainDrops;
    private ItemStack[] armor;
    private ItemStack weapon;
    private ItemStack secondaryWeapon;
    private EntityType entityType;

    private Monster entity;

    public Monster getEntity(){
        return entity;
    }

    public void setEntity(Monster entity){
        this.entity = entity;
    }

    public Object getAttribute(String key){
        return attributes.get(key);
    }

    public void setAttribute(String key, Object value){
        attributes.put(key, value);
    }

    public @Nullable BossQuest getQuest() {
        return quest;
    }

    public void setQuest(@Nullable BossQuest quest) {
        this.quest = quest;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

    public ItemStack getWeapon() {
        return weapon;
    }

    public void setWeapon(ItemStack weapon) {
        this.weapon = weapon;
    }

    public void setSecondaryWeapon(ItemStack secondaryWeapon) {
        this.secondaryWeapon = secondaryWeapon;
    }

    public ItemStack getSecondaryWeapon() {
        return secondaryWeapon;
    }

    public ArrayList<ItemStack>getDrops() {
        return drops;
    }

    public void setDrops(ArrayList<ItemStack> drops) {
        this.drops = drops;
    }

    public ArrayList<ItemStack> getMainDrops() {
        return mainDrops;
    }

    public void setMainDrops(ArrayList<ItemStack> mainDrops) {
        this.mainDrops = mainDrops;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public ArrayList<Component> getBossTitle(double health) {
        ArrayList<Component> title = new ArrayList<>();

        Component healthbar = Component.text(name).color(NamedTextColor.GOLD).decoration(TextDecoration.BOLD, true).append(Component.text(" [ "  + (int)health + " / " +  (int)maxHealth + " ]", NamedTextColor.RED));
        title.add(healthbar);

        return title;
    }

    public void applyBossTitle(ArrayList<Component> bossTitle, LivingEntity parent) {
        parent.customName(bossTitle.get(0));
        parent.setCustomNameVisible(true);
    }

    public boolean onDamaged(Monster entity, double damage) {
        double health = entity.getHealth() + entity.getAbsorptionAmount();
        if(health > damage){
            health -= damage;
            applyBossTitle(getBossTitle(health), entity);
        }else{
            if(entity.getEquipment().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING){
                entity.getEquipment().setItemInOffHand(new ItemStack(Material.AIR));
                entity.setHealth(getMaxHealth());
                entity.getWorld().playSound(entity.getLocation(), Sound.ITEM_TOTEM_USE, 0.5f, 1);
                entity.getWorld().spawnParticle(Particle.TOTEM, entity.getLocation(), 10, 10, 10, 10);
                applyBossTitle(getBossTitle(getMaxHealth()), entity);
                if((getEntity().getTarget() instanceof Player p)) {
                    p.sendMessage(Component.text("Boss " + entity.customName() + " has been revived! And teleported somewhere near you!", NamedTextColor.GREEN));
                };
                spawnNearPlayer();
                return true;
            }else{
                applyBossTitle(getBossTitle(0), entity);
                entity.setHealth(0);
            }
        }

        return false;
    }

    public void onDeath(LivingEntity entity) {
        Particle p = Particle.HEART;
        entity.getWorld().spawnParticle(p, entity.getLocation(), 10, 10, 10, 10);
        entity.getWorld().playSound(entity.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_2, 0.25f, 1);
    }

    public boolean onPlayerHit(Player player, double damage) {
        player.sendMessage(Component.text("You have been hit by " + name + " for " + damage + " damage! Big L for you!", NamedTextColor.RED, TextDecoration.BOLD));
        return false;
    }

    public @Nullable Monster spawn(Location loc) {
        Monster entity = (Monster) loc.getWorld().spawnEntity(loc, getEntityType());
        if(entity.isDead()){
            entity.remove();
            return null;
        }

        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(getMaxHealth());
        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(getSpeed());
        entity.setCanPickupItems(false);
        entity.setRemoveWhenFarAway(false);
        entity.setGlowing(true);
        entity.setAware(true);
        entity.setAI(true);

        applyArmor(entity);
        applyWeapons(entity);

        entity.setHealth(getMaxHealth());

        applyBossTitle(getBossTitle(maxHealth), entity);


        PersistentDataContainer container = entity.getPersistentDataContainer();

        container.set(new NamespacedKey(RookisBoss.getPlugin(), "BossEnemy"), PersistentDataType.BOOLEAN, true);
        applyBossData(entity);
        return entity;
    }

    public void applyBossData(Monster entity){
        entity.setMetadata("BossEnemy", new FixedMetadataValue(RookisBoss.getPlugin(), true));
        entity.setMetadata("BossEnemy_data", new FixedMetadataValue(RookisBoss.getPlugin(), this));
        setEntity(entity);
    }

    public void applyArmor(LivingEntity entity){
        entity.setCanPickupItems(false);
        EntityEquipment equipment = entity.getEquipment();
        ItemStack[] armor = getArmor();

        equipment.setHelmet(armor[0]);
        equipment.setChestplate(armor[1]);
        equipment.setLeggings(armor[2]);
        equipment.setBoots(armor[3]);
    }

    public void applyWeapons(LivingEntity entity){
        entity.setCanPickupItems(false);
        EntityEquipment equipment = entity.getEquipment();
        equipment.setItemInMainHand(getWeapon());
        equipment.setItemInOffHand(getSecondaryWeapon());
    }

    public void tryDropLoot(Location loc){
        loc.add(0, 1, 0);
        ArrayList<ItemStack> drops = getDrops();
        ItemStack dropItem = drops.get((int) (Math.random() * drops.size()));
        dropItem.setAmount((int) (Math.random() * dropItem.getAmount()) + 1);
        Item dItem = loc.getWorld().dropItemNaturally(loc, dropItem);
        dItem.setCanMobPickup(false);
        dItem.setUnlimitedLifetime(true);
        dItem.setWillAge(false);

        for(ItemStack drop : drops){
            if(drop == null || Math.random() >= 0.5) continue;
            drop.setAmount((int) (Math.random() * drop.getAmount()) + 1);
            Item item = loc.getWorld().dropItemNaturally(loc, drop);
            item.setCanMobPickup(false);
            item.setUnlimitedLifetime(true);
            item.setWillAge(false);
        }

        for(ItemStack drop : getMainDrops()){
            if(drop == null) continue;
            Item item = loc.getWorld().dropItemNaturally(loc, drop);
            item.setGlowing(true);
            item.setCanMobPickup(false);
            item.setUnlimitedLifetime(true);
            item.setWillAge(false);
        }
    }

    public boolean isSpawnable(Location loc) {
        return loc.getBlock().getType().isSolid() && loc.getBlock().getRelative(0, 1, 0).getType().isAir() && loc.getBlock().getRelative(0, 2, 0).getType().isAir();
    }

    public void spawnNearPlayer(){
        if(getEntity() == null) return;
        if(!(getEntity().getTarget() instanceof Player p)) return;

        Location loc = p.getLocation();

        Location spawnLoc = loc.clone().add(Math.random() * 25 + 1, Math.random() * 25 + 1, 0).toHighestLocation();
    }
}
