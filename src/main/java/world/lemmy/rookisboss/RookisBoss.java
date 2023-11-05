package world.lemmy.rookisboss;

import me.kodysimpson.simpapi.menu.MenuManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import world.lemmy.rookisboss.boss.BossEnemy;
import world.lemmy.rookisboss.boss.BossQuest;
import world.lemmy.rookisboss.boss.BossUtils;
import world.lemmy.rookisboss.commands.QuestCommandExecutor;
import world.lemmy.rookisboss.commands.SpawnBossCommandExecutor;

import java.util.ArrayList;
import java.util.Map;

public final class RookisBoss extends JavaPlugin implements Listener {
    public ArrayList<BossEnemy> bossEnemies;

    private SpawnBossCommandExecutor spawnBossCommandExecutor;
    private QuestCommandExecutor questCommandExecutor;
    private BossQuestService bossQuestService;

    public BossQuestService getBossQuestService(){
        return bossQuestService;
    }

    private static RookisBoss plugin;


    public QuestCommandExecutor getQuestCommandExecutor(){
        return questCommandExecutor;
    }

    public static RookisBoss getPlugin(){
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        bossEnemies = BossUtils.getAllBosses();
        bossQuestService = new BossQuestService(this);
        bossQuestService.generateBossQuests();

        // Plugin startup logic
        MenuManager.setup(getServer(), this);

        spawnBossCommandExecutor = new SpawnBossCommandExecutor(this);
        questCommandExecutor = new QuestCommandExecutor(this);

        getCommand("rookisboss").setExecutor(spawnBossCommandExecutor);
        getCommand("quests").setExecutor(questCommandExecutor);

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onBossDeath(EntityDeathEvent ev){
        LivingEntity entity = ev.getEntity();
        if(!BossUtils.isBossEntity(entity)) return;
        BossEnemy bossEnemy = BossUtils.getBossData(entity, true);
        if(bossEnemy == null) return;
        ev.setDroppedExp((int) (Math.random() * 100) + 50);
        ev.getDrops().clear();
        if(ev.getEntity().getKiller() == null) return;
        bossEnemy.tryDropLoot(ev.getEntity().getLocation());
        bossEnemy.onDeath(ev.getEntity());
        BossQuest quest = bossEnemy.getQuest();
        if(quest != null){
            bossQuestService.checkQuest(quest);
        }
        getServer().sendMessage(Component.text("Boss " +  bossEnemy.getName() + " has been defeated!", NamedTextColor.GRAY, TextDecoration.BOLD).appendNewline().append(Component.text("By the brave user " + ev.getEntity().getKiller().getName(), NamedTextColor.GRAY)));
    }

    @EventHandler
    public void onBossDamaged(EntityDamageEvent ev){
        if(ev.isCancelled()) return;
        if(!(ev.getEntity() instanceof Monster entity)) return;
        if(!BossUtils.isBossEntity(entity)) return;
        BossEnemy bossEnemy = BossUtils.getBossData(entity, false);
        if(bossEnemy == null) return;
        boolean cancel = bossEnemy.onDamaged(entity, ev.getFinalDamage());

        if(cancel) ev.setCancelled(true);

        if(entity.getLastDamageCause() != null) {
            if (entity.getLastDamageCause().getEntity() instanceof Player) entity.setTarget((LivingEntity) entity.getLastDamageCause().getEntity());
        }
    }

    @EventHandler
    public void onPlayerDamaged(EntityDamageEvent ev){
        if(ev.isCancelled()) return;
        if(!(ev.getEntity() instanceof Player entity)) return;
        if(entity.getLastDamageCause().getEntity() == null || !BossUtils.isBossEntity(entity.getLastDamageCause().getEntity())) return;
        if(!(entity.getLastDamageCause().getEntity() instanceof Monster boss)) return;
        BossEnemy bossEnemy = BossUtils.getBossData(boss, false);
        if(bossEnemy == null) return;
        boolean cancel = bossEnemy.onPlayerHit(entity, ev.getFinalDamage());

        if(cancel) ev.setCancelled(true);
    }


    @EventHandler
    public void onBossTransform(EntityTransformEvent ev){
        if(BossUtils.isBossEntity(ev.getEntity())){
            LivingEntity entity = (LivingEntity) ev.getEntity();
            if(ev.getEntity() instanceof Zombie z)
            {
                BossEnemy data = BossUtils.getBossData(z, false);
                Zombie trz = (Zombie) ev.getTransformedEntity();

                BossQuest quest = data.getQuest();

                quest.getBossEnemiesMap().remove(z);
                quest.getBossEnemiesMap().put(trz, data);

                data.applyBossData(trz);
                trz.setTarget(z.getTarget());

                trz.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(data.getMaxHealth());
                trz.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(data.getSpeed());
                trz.setHealth(data.getMaxHealth());

                data.applyBossTitle(data.getBossTitle(data.getMaxHealth()), trz);
                if(ev.getTransformReason() == EntityTransformEvent.TransformReason.CURED){
                    ev.setCancelled(true);
                }
            }
        };
    }

    @EventHandler
    public void onPlayerDied(EntityDeathEvent ev){
        LivingEntity entity = ev.getEntity();
        if(entity.getLastDamageCause() == null || !(entity instanceof Player)) return;
        Entity attacker = entity.getLastDamageCause().getEntity();
        if(!BossUtils.isBossEntity(attacker)) {
            return;
        }
        LivingEntity boss = (LivingEntity) attacker;

        getServer().sendMessage(Component.text("Boss " + boss.customName() + " has killed " + entity.getName(), NamedTextColor.RED, TextDecoration.BOLD));
        getServer().sendMessage(Component.text(entity.getName() + " failed his quest!", NamedTextColor.RED, TextDecoration.BOLD));
        questCommandExecutor.removeBossQuest((Player) entity);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent ev){
        Player entity = ev.getPlayer();
        BossQuest enemy = bossQuestService.getActiveQuests().get(entity);
        if (enemy == null) return;
        getServer().sendMessage(Component.text(entity.getName() + " failed his quest!", NamedTextColor.RED, TextDecoration.BOLD));
        questCommandExecutor.removeBossQuest(entity);
    }

}
