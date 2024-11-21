package com.example.inventoryprotect;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CommandGiveInventoryProtection implements CommandExecutor {
    @Deprecated
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§4플레이어를 찾을 수 없습니다.");
                return true;
            }

            int amount;

            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§4양은 '숫자'를 적어주세요!");
                return false;
            }

            if (amount < 1) {
                sender.sendMessage("§4양은 '1'이상 이어야 합니다.");
                return false;
            }

            ItemStack item = new ItemStack(Material.PAPER, amount);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§a인벤토리 보호권");
                meta.setLore(Arrays.asList("§e사망시 인벤토리 보호권을 보유중이면", "§e인벤토리가 보호됩니다."));
                item.setItemMeta(meta);
            }

            target.getInventory().addItem(item);
            sender.sendMessage("§a인벤토리 보호권§f이 §b" + amount + "§b장 §f만큼 " + target.getName() +"§f에게 지급되었습니다.");
        } else {
            sender.sendMessage("Usage: /is give <player> <amount>");
        }
        return true;
    }
}
