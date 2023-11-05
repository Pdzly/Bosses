package world.lemmy.rookisboss.commands;

import me.kodysimpson.simpapi.exceptions.MenuManagerException;
import me.kodysimpson.simpapi.exceptions.MenuManagerNotSetupException;
import me.kodysimpson.simpapi.menu.MenuManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import world.lemmy.rookisboss.RookisBoss;
import world.lemmy.rookisboss.boss.BossEnemy;
import world.lemmy.rookisboss.boss.BossQuest;
import world.lemmy.rookisboss.boss.BossUtils;
import world.lemmy.rookisboss.inventory.ListBossQuests;

import java.util.HashMap;
import java.util.Map;

public class QuestCommandExecutor implements CommandExecutor {

    private RookisBoss plugin;

    public QuestCommandExecutor(RookisBoss plugin){
        this.plugin = plugin;
    }

    public void acceptQuest(Player player, BossQuest bossQuest){
        plugin.getBossQuestService().getActiveQuests().put(player, bossQuest);
        bossQuest.setPlayer(player);
        bossQuest.spawnBosses();
        player.sendMessage(Component.text("You have accepted the " + bossQuest.getDifficulty() + " Quest", NamedTextColor.GREEN, TextDecoration.BOLD));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player p){
            try {
                MenuManager.openMenu(ListBossQuests.class, p);
            } catch (MenuManagerException | MenuManagerNotSetupException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        sender.sendMessage("You must be a player to use this command");
        return true;
    }

    public void removeBossQuest(Player player){
        BossQuest quest = plugin.getBossQuestService().getActiveQuests().remove(player);
        try {
            quest.getBossEnemiesMap().keySet().forEach(LivingEntity::remove);
            plugin.getBossQuestService().getActiveQuests().remove(player);
            plugin.getBossQuestService().getQuests().remove(quest);
            plugin.getBossQuestService().generateBossQuest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
