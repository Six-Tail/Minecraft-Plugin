package org.plugin.timer;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Timer extends JavaPlugin implements Listener {
    private int dayCount = 0;
    private long lastCheckedTime = 0;
    private boolean hasPlayedSoundForDayTen = false;

    @Override
    public void onEnable() {
        createDataFolder(); // 데이터 폴더가 존재하는지 확인
        Bukkit.getPluginManager().registerEvents(this, this);
        loadDayCount(); // 서버 시작 시 일수 불러오기
        getLogger().info("Timer plugin enabled! Current day: " + dayCount);
    }

    @Override
    public void onDisable() {
        saveDayCount(); // 서버 종료 시 일수 저장
        getLogger().info("Timer plugin disabled! Current day: " + dayCount);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        startDayCounter();
    }

    @Deprecated
    @EventHandler
    public void onDaySkip(TimeSkipEvent event) {
        if (event.getSkipReason() == TimeSkipEvent.SkipReason.NIGHT_SKIP) {
            dayCount++;
            updateDayCounter();

            // 모든 플레이어에게 일수 알림 및 사운드 재생
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(ChatColor.YELLOW + "" + dayCount + " 일차");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f); // 사운드 재생
            }
        }
    }

    private void startDayCounter() {
        new BukkitRunnable() {
            long secondsElapsed = 0;

            @Deprecated
            @Override
            public void run() {
                secondsElapsed++;
                World world = Bukkit.getWorlds().get(0); // 첫 번째 월드의 시간 가져오기
                long currentTime = world.getTime();

                // 시간이 자연스럽게 지나서 하루가 경과했는지 확인
                if (currentTime < lastCheckedTime) {
                    dayCount++;
                    updateDayCounter();

                    // 모든 플레이어에게 일수 알림 및 사운드 재생
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.sendMessage(ChatColor.YELLOW + "" + dayCount + " 일차");
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f); // 사운드 재생
                    }
                }
                lastCheckedTime = currentTime;

                // 모든 플레이어에게 현재 일수를 액션바로 표시
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "" + dayCount + " 일차"));
                }
            }
        }.runTaskTimer(this, 0L, 20L); // 1초마다 실행 (20틱 = 1초)
    }

    private void updateDayCounter() {
        if (dayCount % 10 == 0) {
            showCenterTextAnimation();

            // 10일 차 소리 재생 여부를 체크
            if (!hasPlayedSoundForDayTen) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f); // 특별한 소리 재생
                }
                hasPlayedSoundForDayTen = true; // 소리 재생 완료
            }
        } else {
            // 10일 차가 아닐 경우 소리 재생 여부 초기화
            hasPlayedSoundForDayTen = false;
        }
    }

    private void showCenterTextAnimation() {
        new BukkitRunnable() {
            int step = 0;

            @Deprecated
            @Override
            public void run() {
                if (step >= 40) { // 애니메이션이 끝났을 때
                    cancel();
                    return;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (step < 20) {
                        // 중앙에 큰 텍스트
                        player.sendTitle(
                                ChatColor.RED + "" + ChatColor.BOLD + dayCount + " 일차",
                                "",
                                0, 20, 0
                        );
                    } else {
                        // 텍스트를 줄여가며 오른쪽 상단으로 이동
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "" + dayCount + " 일차"));
                    }
                }

                step++;
            }
        }.runTaskTimer(this, 0L, 2L); // 2틱마다 애니메이션 실행
    }

    // 데이터 폴더가 존재하는지 확인하는 메서드
    private void createDataFolder() {
        File folder = getDataFolder();
        if (!folder.exists()) {
            boolean created = folder.mkdirs(); // 존재하지 않으면 폴더 생성 및 결과 저장
            if (created) {
                getLogger().info("Data folder created: " + folder.getPath()); // 폴더 생성 성공 시 로깅
            } else {
                getLogger().severe("Failed to create data folder: " + folder.getPath()); // 폴더 생성 실패 시 로깅
            }
        }
    }

    // 일수를 저장하는 메서드
    private void saveDayCount() {
        try {
            Files.write(Paths.get(getDataFolder() + "/dayCount.txt"), String.valueOf(dayCount).getBytes());
        } catch (IOException e) {
            getLogger().severe("Failed to save day count: " + e.getMessage()); // 로깅
        }
    }

    // 일수를 불러오는 메서드
    private void loadDayCount() {
        File file = new File(getDataFolder(), "dayCount.txt");
        if (file.exists()) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()));
                dayCount = Integer.parseInt(content);
            } catch (IOException | NumberFormatException e) {
                getLogger().severe("Failed to load day count: " + e.getMessage()); // 로깅
            }
        }
    }
}
