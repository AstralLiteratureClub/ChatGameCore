package bet.astral.chatgamecore.game;

import bet.astral.messenger.v2.translation.TranslationKey;
import lombok.Getter;

@Getter
public class Answer {
    private final TranslationKey value;
    private final TranslationKey context;
    private final boolean isCorrect;

    public Answer(TranslationKey value, TranslationKey context, boolean isCorrect) {
        this.value = value;
        this.context = context;
        this.isCorrect = isCorrect;
    }
}
