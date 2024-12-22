package bet.astral.chatgamecore.game;

import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Random;
import java.util.UUID;

@Getter
public class GameData {
    @Setter
    private boolean requireCaseMatch;
    private final int completeTime;
    private final Random random;
    private final UUID uniqueId = UUID.randomUUID();

    public GameData(Random random, boolean requireCaseMatch, int completeTime) {
        this.requireCaseMatch = requireCaseMatch;
        this.completeTime = completeTime;
        this.random = random;
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DefaultData {
    }
}
