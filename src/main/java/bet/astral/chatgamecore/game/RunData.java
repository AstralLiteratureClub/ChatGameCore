package bet.astral.chatgamecore.game;

import bet.astral.messenger.v2.Messenger;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class RunData {
    private final Messenger messenger;
}
