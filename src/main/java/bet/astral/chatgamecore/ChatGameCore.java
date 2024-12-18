package bet.astral.chatgamecore;

import bet.astral.chatgamecore.dispatcher.ChatEventDispatcher;

public class ChatGameCore {
    public static final ChatGameCore INSTANCE = new ChatGameCore();
    private final ChatEventDispatcher eventDispatcher = new ChatEventDispatcher();

    public ChatEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }
}