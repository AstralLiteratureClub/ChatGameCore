package bet.astral.chatgamecore.game;

import bet.astral.chatgamecore.ChatGameCore;
import bet.astral.messenger.v2.Messenger;
import bet.astral.messenger.v2.placeholder.collection.PlaceholderList;
import bet.astral.messenger.v2.translation.TranslationKey;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public abstract class ChatGame {
    private final String correctAnswer;
    private final boolean requireCaseMatch;
    private final Messenger messenger;
    private State state = State.CREATED;
    private int ticksLeft;

    public ChatGame(String correctAnswer, boolean requireCaseMatch, Messenger messenger, int timeInTicks) {
        this.correctAnswer = correctAnswer;
        this.requireCaseMatch = requireCaseMatch;
        this.messenger = messenger;
        this.ticksLeft = timeInTicks;
    }

    public abstract PlaceholderList getPlaceholders(State state);
    public abstract TranslationKey getTranslation(State state);
    public abstract void rewardPlayer(Player player);
    public abstract void nobodyGuessedCorrectly();

    public void guess(Player player, String guess){
        if (!requireCaseMatch && guess.equalsIgnoreCase(correctAnswer)
                || guess.contentEquals(guess)
        ){
            state = State.ENDED_PLAYER_GUESSED;
            messenger.broadcast(getTranslation(state), getPlaceholders(state));
            rewardPlayer(player);
        }
    }

    public void onJoin(Player player) {
        if (state==State.CREATED) {
            messenger.message(player, getTranslation(state), getPlaceholders(state));
        }
    }

    public void tick(){
        if (state!=State.CREATED) {
            return;
        }
        ticksLeft--;
        if (ticksLeft==0){
            state=State.ENDED_NOBODY_GUESSED;
            messenger.broadcast(getTranslation(state), getPlaceholders(state));
        }
    }

    public void unregister(){
        ChatGameCore.INSTANCE.getEventDispatcher().unregister(this);
    }
}
