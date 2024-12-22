package bet.astral.chatgamecore.game.builtin.type;

import bet.astral.chatgamecore.game.ChatGame;
import bet.astral.chatgamecore.game.GameData;
import bet.astral.chatgamecore.game.RunData;
import bet.astral.chatgamecore.game.State;
import bet.astral.chatgamecore.messenger.GameTranslations;
import bet.astral.messenger.v2.placeholder.collection.PlaceholderList;
import bet.astral.messenger.v2.translation.TranslationKey;

import java.util.Set;

public abstract class CopyTheWordChatGame extends ChatGame {
    public CopyTheWordChatGame(String correctAnswer, GameData gameData, RunData runData) {
        super(Set.of(correctAnswer), gameData, runData);
    }

    public String getAnswer(){
        return getCorrectAnswers().stream().findFirst().orElseThrow();
    }

    @Override
    public PlaceholderList getPlaceholders(State state) {
        PlaceholderList placeholders = new PlaceholderList();
        placeholders.add("seconds", getTimeSinceStartDuration().toSeconds());
        placeholders.add("winner", getWinner() != null ? getWinner().getName() : "No Winner");
        placeholders.add("word", getAnswer());
        return placeholders;
    }

    @Override
    public TranslationKey getTranslation(State state) {
        return switch (state){
            case CREATED -> getRandom().nextBoolean() ? GameTranslations.COPY_STATE_CREATED : GameTranslations.COPY_STATE_CREATED_2;
            case ENDED_NOBODY_GUESSED -> GameTranslations.COPY_STATE_NO_WINNER;
            case ENDED_PLAYER_GUESSED -> GameTranslations.COPY_STATE_WON;
        };
    }
}
