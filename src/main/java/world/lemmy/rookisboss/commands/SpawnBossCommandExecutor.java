package world.lemmy.rookisboss.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import world.lemmy.rookisboss.RookisBoss;
import world.lemmy.rookisboss.boss.BossEnemy;

public class SpawnBossCommandExecutor implements CommandExecutor {

    private RookisBoss plugin;

    public SpawnBossCommandExecutor(RookisBoss plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player p){
                BossEnemy bossEnemy = plugin.bossEnemies.get((int) (Math.random() * plugin.bossEnemies.size()));

                p.sendMessage("Spawning " + bossEnemy.getName());
                Entity ent = bossEnemy.spawn(p.getLocation());
                return true;
        }
        sender.sendMessage("You must be a player to use this command");
        return true;
    }
}
