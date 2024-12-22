package bet.astral.chatgamecore.internal;

import bet.astral.chatgamecore.dispatcher.ChatEventDispatcher;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {
    private final ChatEventDispatcher dispatcher;

    public ChatListener(ChatEventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @EventHandler(ignoreCancelled = true)
    public void onAsyncChat(AsyncChatEvent event) {
        Component original = event.originalMessage();
        String plain = PlainTextComponentSerializer.plainText().serialize(original);
        dispatcher.dispatchAnswer(event.getPlayer(), plain);
    }

}
