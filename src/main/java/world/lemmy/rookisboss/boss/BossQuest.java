package world.lemmy.rookisboss.boss;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import javax.annotation.Nullable;
import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossQuest {

    public UUID Id = UUID.randomUUID();
    private ArrayList<BossEnemy> bossEnemies = new ArrayList<>();
    private Map<LivingEntity, BossEnemy> bossEnemiesMap = new HashMap<>();
    private @Nullable Player player;
    private String difficulty;
    private boolean completed = false;

    public void setCompleted(boolean completed){
        this.completed = completed;
    }

    public boolean isCompleted(){
        return completed;
    }

    public void setPlayer(Player player){
        this.player = player;
    }

    public @Nullable Player getPlayer(){
        return player;
    }

    public void setDifficulty(String difficulty){
        this.difficulty = difficulty;
    }

    public String getDifficulty(){
        return difficulty;
    }

    public void AddEnemy(BossEnemy bossEnemy){
        bossEnemies.add(bossEnemy);
    }

    public void setEnemies(ArrayList<BossEnemy> bossEnemies){
        this.bossEnemies = bossEnemies;
    }

    public void RemoveEnemy(BossEnemy bossEnemy){
        bossEnemies.remove(bossEnemy);
        if(bossEnemiesMap.containsValue(bossEnemy)){
            LivingEntity en = null;
            for (Map.Entry<LivingEntity, BossEnemy> entry : bossEnemiesMap.entrySet()) {
                if(entry.getValue().equals(bossEnemy)){
                    en = entry.getKey();
                    break;
                }
            }
            if(en != null && !en.isDead()){
                en.remove();
            }
            bossEnemiesMap.remove(en);
        }
    }

    public void onComplete(){
        this.setCompleted(true);
        if(player == null) return;
        player.sendMessage(Component.text("You have completed the " + difficulty + " Quest", NamedTextColor.GREEN, TextDecoration.BOLD));
        player.giveExp((int) (Math.random() * 25) + 25);
    }

    public ArrayList<BossEnemy> getBossEnemies(){
        return bossEnemies;
    }

    public Map<LivingEntity, BossEnemy> getBossEnemiesMap(){
        return bossEnemiesMap;
    }

    public void setBossEnemiesMap(Map<LivingEntity, BossEnemy> bossEnemiesMap){
        this.bossEnemiesMap = bossEnemiesMap;
    }

    public void spawnBosses(){
        if(player == null) return;
        Location loc = player.getLocation().clone().add(Math.random() * 25 + 25, Math.random() * 25 + 25, 0).toHighestLocation();
        player.sendMessage(Component.text("Bosses have spawned at " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ(), NamedTextColor.GRAY, TextDecoration.BOLD));
        for (BossEnemy bossEnemy : bossEnemies) {
            Monster en = bossEnemy.spawn(loc);
            if(en == null) continue;
            en.setTarget(player);
            bossEnemy.setQuest(this);
            bossEnemiesMap.put(en, bossEnemy);
        }
    }
}

