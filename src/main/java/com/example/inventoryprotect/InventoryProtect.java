package com.example.inventoryprotect;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class InventoryProtect extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("인벤토리 보호권 플러그인 활성화");

        // 명렁어 등록
        Objects.requireNonNull(this.getCommand("is")).setExecutor(new CommandGiveInventoryProtection());

        // 이벤트 리스너 등록
        PlayerDeathListener playerDeathListener = new PlayerDeathListener();
        Bukkit.getPluginManager().registerEvents(playerDeathListener, this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("인벤토리 보호권 플러그인 비활성화");
    }
}
