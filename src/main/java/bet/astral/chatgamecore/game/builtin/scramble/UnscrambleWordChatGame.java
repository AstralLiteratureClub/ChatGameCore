package bet.astral.chatgamecore.game.builtin.scramble;

import bet.astral.chatgamecore.game.ChatGame;
import bet.astral.chatgamecore.game.GameData;
import bet.astral.chatgamecore.game.RunData;
import bet.astral.chatgamecore.game.State;
import bet.astral.chatgamecore.messenger.GameTranslations;
import bet.astral.messenger.v2.placeholder.collection.PlaceholderList;
import bet.astral.messenger.v2.translation.TranslationKey;
import lombok.Getter;

import java.util.*;

public abstract class UnscrambleWordChatGame extends ChatGame {
    public static String scramble(Random random, String correctAnswer) {
        return scramble(random, correctAnswer, false, 1);
    }
    public static String scramble(Random random, String correctAnswer, boolean scrambleCase){
        return scramble(random, correctAnswer, scrambleCase, 1);
    }
    public static String scramble(Random random, String correctAnswer, boolean scrambleCase, int shuffleTimes){
        ArrayList<String> scrambled = new ArrayList<>(Arrays.stream(correctAnswer.split("")).map(s->scrambleCase ? random.nextBoolean() ? s.toUpperCase() : s.toLowerCase() : s).toList());
        for (int i = 0; i < shuffleTimes; i++){
            Collections.shuffle(scrambled);
        }
        return String.join("", scrambled);
    }

    @Getter
    private final String scrambled;

    public UnscrambleWordChatGame(String correctAnswer, String scrambled, GameData gameData, RunData runData) {
        super(Set.of(correctAnswer), gameData, runData);
        this.scrambled = scrambled;
    }
    public UnscrambleWordChatGame(String correctAnswer, GameData gameData, RunData runData) {
        this(correctAnswer,
                scramble(gameData.getRandom(), correctAnswer, gameData.getRandom().nextBoolean(), gameData.getRandom().nextInt(1, 3)),
                gameData, runData);
    }
    public String getAnswer(){
        return getCorrectAnswers().stream().findFirst().orElseThrow();
    }

    @Override
    public PlaceholderList getPlaceholders(State state) {
        PlaceholderList placeholders = new PlaceholderList();
        placeholders.add("seconds", getTimeSinceStartDuration().toSeconds());
        placeholders.add("winner", getWinner() != null ? getWinner().getName() : "No Winner");
        placeholders.add("scrambled", scrambled);
        placeholders.add("unscrambled", getAnswer());
        return placeholders;
    }

    @Override
    public TranslationKey getTranslation(State state) {
        return switch (state){
            case CREATED -> getRandom().nextBoolean() ? GameTranslations.UNSCRAMBLE_STATE_CREATED : GameTranslations.UNSCRAMBLE_STATE_CREATED_2;
            case ENDED_NOBODY_GUESSED -> GameTranslations.UNSCRAMBLE_STATE_NO_WINNER;
            case ENDED_PLAYER_GUESSED -> GameTranslations.UNSCRAMBLE_STATE_WON;
        };
    }
}
