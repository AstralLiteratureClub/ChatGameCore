package bet.astral.chatgamecore.game;

import bet.astral.messenger.v2.Messenger;
import bet.astral.messenger.v2.placeholder.collection.PlaceholderList;
import bet.astral.messenger.v2.translation.TranslationKey;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.Random;
import java.util.Set;

@Getter
public abstract class ChatGame {
    private final Set<String> correctAnswers;
    private final boolean requireCaseMatch;
    private final Messenger messenger;
    private State state = State.CREATED;
    private int ticksLeft;
    private Player winner;
    private final Random random;
    private long started = -1;

    public ChatGame(Set<String> correctAnswers, GameData gameData, RunData runData) {
        this.correctAnswers = correctAnswers;
        this.requireCaseMatch = gameData.isRequireCaseMatch();
        this.messenger = runData.getMessenger();
        this.ticksLeft = gameData.getCompleteTime();
        this.random = gameData.getRandom();
    }

    public long getTimeSinceStart(){
        return System.currentTimeMillis()-started;
    }
    public Duration getTimeSinceStartDuration(){
        return Duration.ofMillis(getTimeSinceStart());
    }

    public abstract PlaceholderList getPlaceholders(State state);
    public abstract TranslationKey getTranslation(State state);
    public abstract void rewardPlayer(Player player);
    public abstract void nobodyGuessedCorrectly();
    public void launch(){
        started = System.currentTimeMillis();
        state = State.CREATED;
        messenger.broadcast(
                getTranslation(State.CREATED),
                getPlaceholders(State.CREATED)
        );
    }
    public void guess(Player player, String guess){
        if (!requireCaseMatch && correctAnswers.stream().anyMatch(s->guess.toLowerCase().startsWith(s))
                || correctAnswers.stream().anyMatch(guess::startsWith)
        ){
            winner = player;
            state = State.ENDED_PLAYER_GUESSED;
            messenger.broadcast(getTranslation(state), getPlaceholders(state));
            rewardPlayer(player);
        }
    }

    public final void onJoin(Player player) {
        if (state==State.CREATED) {
            messenger.message(player, getTranslation(state), getPlaceholders(state));
        }
    }

    public final void end(){
        state=State.ENDED_NOBODY_GUESSED;
        messenger.broadcast(getTranslation(state), getPlaceholders(state));
    }

    public final void tick(){
        if (state!=State.CREATED) {
            return;
        }
        ticksLeft--;
        if (ticksLeft==0){
            end();
        }
    }
}
