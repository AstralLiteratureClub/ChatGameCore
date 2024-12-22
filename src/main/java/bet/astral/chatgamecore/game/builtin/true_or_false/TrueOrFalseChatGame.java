package bet.astral.chatgamecore.game.builtin.true_or_false;

import bet.astral.chatgamecore.game.*;
import bet.astral.chatgamecore.messenger.GameTranslations;
import bet.astral.messenger.v2.component.ComponentType;
import bet.astral.messenger.v2.info.MessageInfoBuilder;
import bet.astral.messenger.v2.placeholder.collection.PlaceholderList;
import bet.astral.messenger.v2.translation.TranslationKey;
import com.google.gson.*;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;
import java.util.*;

public abstract class TrueOrFalseChatGame extends ChatGame {
    private final Set<UUID> alreadyAnswered = new HashSet<>();
    private final Component info;
    private final Component trueComp;
    private final Component falseComp;

    private static GameData overrideData(GameData gameData){
        gameData.setRequireCaseMatch(false);
        return gameData;
    }
    public TrueOrFalseChatGame(Statement statement, GameData gameData, RunData runData) {
        super(Collections.singleton("" + statement.isTrue), overrideData(gameData), runData);
        this.info = new MessageInfoBuilder(statement.statement)
                .build().parseAsComponent(runData.getMessenger(), ComponentType.CHAT);
        Answer trueAnswer = new BooleanAnswer(true, statement.isTrue);
        Answer falseAnswer = new BooleanAnswer(false, !statement.isTrue);
        Component trueComp = new MessageInfoBuilder(trueAnswer.getValue()).hidePrefix().build().parseAsComponent(getMessenger(), ComponentType.CHAT);
        Component falseComp = new MessageInfoBuilder(falseAnswer.getValue()).hidePrefix().build().parseAsComponent(getMessenger(), ComponentType.CHAT);
        Component trueContextComp = new MessageInfoBuilder(trueAnswer.getContext()).hidePrefix().build().parseAsComponent(getMessenger(), ComponentType.CHAT);
        Component falseContextComp = new MessageInfoBuilder(falseAnswer.getContext()).hidePrefix().build().parseAsComponent(getMessenger(), ComponentType.CHAT);

        assert trueComp != null;
        assert falseComp != null;

        this.trueComp = trueComp.hoverEvent(trueContextComp);
        this.falseComp = falseComp.hoverEvent(falseContextComp);
    }

    @Override
    public PlaceholderList getPlaceholders(State state) {
        PlaceholderList placeholders = new PlaceholderList();

        assert trueComp != null;
        assert falseComp != null;

        placeholders.add("true", trueComp);
        placeholders.add("false", falseComp);

        placeholders.add("winner", getWinner() != null ? getWinner().getName() : "No Winner");
        placeholders.add("info", info);
        placeholders.add("seconds", getTimeSinceStartDuration().toSeconds());
        return placeholders;
    }

    @Override
    public TranslationKey getTranslation(State state) {
        return switch (state) {
            case CREATED -> getRandom().nextBoolean() ? GameTranslations.TOF_STATE_CREATED_TRUE_FIRST : GameTranslations.TOF_STATE_CREATED_FALSE_FIRST;
            case ENDED_NOBODY_GUESSED -> GameTranslations.TOF_STATE_NO_WINNER;
            case ENDED_PLAYER_GUESSED -> GameTranslations.TOF_STATE_WON;
        };
    }

    @Override
    public void guess(Player player, String guess) {
        if (alreadyAnswered.contains(player.getUniqueId())){
            getMessenger().message(player, GameTranslations.TOF_STATE_CANNOT_VOTE_AGAIN, getPlaceholders(State.CREATED));
            return;
        }
        alreadyAnswered.add(player.getUniqueId());
        super.guess(player, guess);
    }

    public static class Statement {
        private final TranslationKey statement;
        private final boolean isTrue;

        public Statement(TranslationKey statement, boolean isTrue) {
            this.statement = statement;
            this.isTrue = isTrue;
        }
    }

    @Getter
    public static class BooleanAnswer extends Answer{
        private final boolean valueBoolean;
        public BooleanAnswer(boolean value, boolean isCorrect) {
            super(value ? GameTranslations.TOF_VALUE_TRUE : GameTranslations.TOF_VALUE_FALSE,
                    value ? GameTranslations.TOF_VALUE_TRUE : GameTranslations.TOF_VALUE_FALSE, isCorrect);
            this.valueBoolean = value;
        }
    }

    public static class StatementSerializer implements com.google.gson.JsonSerializer<TrueOrFalseChatGame.Statement>, JsonDeserializer<TrueOrFalseChatGame.Statement> {
        @Override
        public JsonElement serialize(Statement src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.addProperty("statement", src.statement.getKey());
            object.addProperty("true", src.isTrue);
            return object;
        }

        @Override
        public Statement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonObject()){
                JsonObject object = json.getAsJsonObject();
                return new Statement(
                        TranslationKey.of(
                                (object.get("statement").getAsString())),
                        object.get("true").getAsBoolean()
                );
            }
            throw new JsonParseException(json.getClass().getName() + "is not "+ JsonObject.class);
        }
    }
}
