package bet.astral.chatgamecore.internal;

import bet.astral.chatgamecore.dispatcher.ChatEventDispatcher;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ConnectionListener implements Listener {
    private final ChatEventDispatcher dispatcher;

    public ConnectionListener(ChatEventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.dispatcher.onJoin(event.getPlayer());
    }

}
