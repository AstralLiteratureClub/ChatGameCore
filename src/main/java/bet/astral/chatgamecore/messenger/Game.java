package bet.astral.chatgamecore.messenger;

import bet.astral.chatgamecore.game.ChatGame;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface Game {
    Class<? extends ChatGame> value();
}
