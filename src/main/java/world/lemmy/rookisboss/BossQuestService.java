package world.lemmy.rookisboss;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import world.lemmy.rookisboss.boss.BossEnemy;
import world.lemmy.rookisboss.boss.BossQuest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BossQuestService {
    private RookisBoss plugin;

    private static Map<Player, BossQuest> activeQuests = new HashMap<>();

    private static ArrayList<BossQuest> quests = new ArrayList<>();

    public BossQuestService(RookisBoss plugin){
        this.plugin = plugin;
    }

    public BossQuest getQuestById(String id) {
        for (BossQuest quest : quests) {
            if(quest.Id.toString().equals(id)){
                return quest;
            }
        }
        return null;
    }

    public void startQuest(Player player, BossQuest quest){
        activeQuests.put(player, quest);
        for (BossQuest quest1 : quests) {
            if(quest1.Id.equals(quest.Id)){
                quest1.setPlayer(player);
                break;
            }
        }
    }

    public ArrayList<BossQuest> getQuests(){
        return quests;
    }

    public Map<Player, BossQuest> getActiveQuests(){
        return activeQuests;
    }

    /*
    * Checks if a quest is completed and announces it and removes it from quests and activequests
     */
    public void checkQuest(BossQuest quest){
        if(quest.getBossEnemiesMap().isEmpty() && !quest.isCompleted()){
            quest.onComplete();
            Player player = quest.getPlayer();
            plugin.getServer().sendMessage(Component.text("User " + player.getName() + " has completed a quest!", NamedTextColor.GOLD, TextDecoration.BOLD));
            activeQuests.remove(player);
            quests.remove(quest);
            generateBossQuest();
        }
    }

    public void generateBossQuests() {
        for (int i = 0; i < 5; i++) {
            generateBossQuest();
        }
    }

    public void generateBossQuest(){
        BossQuest bossQuest = new BossQuest();

        ArrayList<BossEnemy> bossEnemies = RookisBoss.getPlugin().bossEnemies;

        int bossAmount = (int) Math.ceil(Math.random() * Math.min(3, bossEnemies.size()));
        for (int i = 0; i < bossAmount; i++) {
            bossQuest.AddEnemy(bossEnemies.get((int) (Math.random() * bossEnemies.size())));
        }

        bossQuest.setDifficulty((bossAmount < 2) ? "Easy" : (bossAmount < 3) ? "Medium" : "Hard");

        quests.add(bossQuest);
    }

    public void removeAllBossEntities(){
        for (BossQuest quest : quests) {
            for (LivingEntity bossEnemy : quest.getBossEnemiesMap().keySet()) {
                bossEnemy.remove();
            }
        }
    }

    public void removeBossEntity(LivingEntity entity){
        for (BossQuest quest : quests) {
            if(quest.getBossEnemiesMap().containsKey(entity)){
                quest.getBossEnemiesMap().remove(entity);
                entity.remove();
            }
        }
    }
}
