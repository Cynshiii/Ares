package co.aegis.ares.listeners;

import co.aegis.ares.framework.annotations.Disabled;
import co.aegis.ares.utils.RandomUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

@Disabled
@NoArgsConstructor
public class RandomSpawn implements Listener {

    private static final List<Location> spawnPoints = new ArrayList<>() {{
        add(new Location(Bukkit.getWorld("world"), 500, 70, 250));
        add(new Location(Bukkit.getWorld("world"), 200, 85, 100));
        add(new Location(Bukkit.getWorld("world"), 150, 100, 450));
    }};

    @EventHandler
    public void onNewPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!(player.hasPlayedBefore())){
            player.teleportAsync(RandomUtils.randomElement(spawnPoints));
        }
    }
}
