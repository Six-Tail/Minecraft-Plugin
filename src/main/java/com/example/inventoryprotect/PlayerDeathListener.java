package com.example.inventoryprotect;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerDeathListener implements Listener {
    @Deprecated
    private static final String PROTECTION_ITEM_NAME = ChatColor.GREEN + "인벤토리 보호권";

    @Deprecated
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        boolean protectionUsed = false;

        ItemStack[] inventoryItems = player.getInventory().getContents();
        for (ItemStack item : inventoryItems) {
            if (item != null && item.getType() == Material.PAPER) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && PROTECTION_ITEM_NAME.equals(meta.getDisplayName())) {
                    // 보호권 사용
                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                        player.sendMessage(ChatColor.AQUA + "인벤토리 보호권을 사용했습니다. 남은 보호권: " + item.getAmount());
                        player.sendMessage(ChatColor.GOLD + "인벤토리 보호권을 사용하여 사망 전 인벤토리로 복구되었습니다.");
                    } else {
                        player.getInventory().remove(item);
                        player.sendMessage(ChatColor.AQUA + "인벤토리 보호권을 사용했습니다. 남은 보호권: 0");
                        player.sendMessage(ChatColor.RED + "남은 인벤토리 보호권이 없습니다. 주의해 주세요!");
                    }
                    protectionUsed = true;
                    break;
                }
            }
        }

        if (protectionUsed) {
            // 인벤토리 보호
            event.setKeepInventory(true);
            event.getDrops().clear();

        } else {
            // 보호권이 없을 때 기본 동작 (아이템 드롭)
            event.setKeepInventory(false);
        }
    }
}
