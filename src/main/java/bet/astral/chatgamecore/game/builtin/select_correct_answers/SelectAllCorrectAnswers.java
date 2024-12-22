package bet.astral.chatgamecore.game.builtin.select_correct_answers;

import bet.astral.chatgamecore.game.ChatGame;
import bet.astral.chatgamecore.game.GameData;
import bet.astral.chatgamecore.game.RunData;

import java.util.Set;

public abstract class SelectAllCorrectAnswers extends ChatGame {
    public SelectAllCorrectAnswers(Set<String> correctAnswers, GameData gameData, RunData runData) {
        super(correctAnswers, gameData, runData);
    }
}
