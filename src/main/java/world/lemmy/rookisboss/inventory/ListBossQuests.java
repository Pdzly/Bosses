package world.lemmy.rookisboss.inventory;

import me.kodysimpson.simpapi.exceptions.MenuManagerException;
import me.kodysimpson.simpapi.exceptions.MenuManagerNotSetupException;
import me.kodysimpson.simpapi.menu.Menu;
import me.kodysimpson.simpapi.menu.PlayerMenuUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import world.lemmy.rookisboss.BossQuestService;
import world.lemmy.rookisboss.RookisBoss;
import world.lemmy.rookisboss.boss.BossQuest;
import world.lemmy.rookisboss.boss.BossUtils;

import java.util.ArrayList;
import java.util.List;

public class ListBossQuests extends Menu {

    public ListBossQuests(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "List of Boss Quests";
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public boolean cancelAllClicks() {
        return true;
    }

    @Override
    public void handleMenu(InventoryClickEvent inventoryClickEvent) throws MenuManagerNotSetupException, MenuManagerException {
        Player p = (Player) inventoryClickEvent.getWhoClicked();
        if(inventoryClickEvent.getCurrentItem().getType() == Material.BARRIER){
            p.closeInventory();
            return;
        }
        List<String> lore = inventoryClickEvent.getCurrentItem().getLore();

        if(lore == null) return;

        String id = lore.get(lore.size() - 1);

        BossQuest quest = RookisBoss.getPlugin().getBossQuestService().getQuestById(id);

        if(quest == null) return;
        BossQuest activeQuest = RookisBoss.getPlugin().getBossQuestService().getActiveQuests().get(p);
        if(activeQuest != null){
            p.sendMessage(Component.text("You are already fighting a boss!", NamedTextColor.RED));

            activeQuest.getBossEnemiesMap().keySet().forEach((entity) -> {
                Location loc = entity.getLocation();
                p.sendMessage(Component.text("A enemy is near " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ(), NamedTextColor.RED));
            });

            inventoryClickEvent.getWhoClicked().closeInventory();

            return;
        }

        if(quest.getPlayer() != null){
            p.sendMessage(Component.text("Someone is already fighting this boss!", NamedTextColor.RED));
            p.closeInventory();

            return;
        }

        RookisBoss.getPlugin().getQuestCommandExecutor().acceptQuest(p, quest);
        inventoryClickEvent.getWhoClicked().closeInventory();
    }

    @Override
    public void setMenuItems() {
        ItemStack decoy = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = decoy.getItemMeta();
        meta.displayName(Component.text("Ignore me :)"));
        decoy.setItemMeta(meta);

        setFillerGlass(decoy);

        ItemStack back = makeItem(Material.BARRIER, "Close this menu!");
        inventory.setItem(22, back);

        final int[] i = {0};
        RookisBoss.getPlugin().getBossQuestService().getQuests().forEach((bossQuest) -> {
            if(bossQuest.getPlayer() != null){
                inventory.setItem(11 + i[0], makeItem(Material.PAPER, "Quest # " + (i[0] + 1) + " - " + bossQuest.getDifficulty(), "Someone is fighting this boss already!", bossQuest.getPlayer().getName() + " is fighting this boss!", bossQuest.Id.toString()));
            }else{
                inventory.setItem(11 + i[0], makeItem(Material.PAPER, "Quest # " + (i[0] + 1) + " - " + bossQuest.getDifficulty(), "Fight some boss!", bossQuest.Id.toString()));
            }
            i[0]++;
        });

    }
}
