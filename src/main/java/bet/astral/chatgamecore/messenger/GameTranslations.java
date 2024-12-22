package bet.astral.chatgamecore.messenger;

import bet.astral.chatgamecore.game.builtin.math.MathChatGame;
import bet.astral.chatgamecore.game.builtin.scramble.UnscrambleWordChatGame;
import bet.astral.chatgamecore.game.builtin.true_or_false.TrueOrFalseChatGame;
import bet.astral.chatgamecore.game.builtin.type.CopyTheWordChatGame;
import bet.astral.messenger.v2.component.ComponentType;
import bet.astral.messenger.v2.translation.Translation;
import net.kyori.adventure.text.Component;

import static bet.astral.messenger.v2.translation.Translation.text;

public class GameTranslations {
    /*
     * Math
     */
    @Game(MathChatGame.class)
    public static final Translation MATH_VALUE_VARIABLE = new Translation("chat-games.math.value.variable").add(ComponentType.CHAT, text("<white>%default-render%"));
    @Game(MathChatGame.class)
    public static final Translation MATH_VALUE_VARIABLE_COMMA = new Translation("chat-games.math.value.variable").add(ComponentType.CHAT, text("<white>%default-render%\n"));
    @Game(MathChatGame.class)
    public static final Translation MATH_STATE_CREATED = new Translation("chat-games.math.solve-no-variables").add(ComponentType.CHAT,
            Component.newline(),
            text("<gold><bold>Solve the Equation <reset><yellow>First person calculate the following math equation wins!").appendNewline(),
            text("%equation%").appendNewline()
    );
    @Game(MathChatGame.class)
    public static final Translation MATH_STATE_CREATED_HAS_VARIABLES = new Translation("chat-games.math.solve-variables").add(ComponentType.CHAT,
            Component.newline(),
            text("<gold><bold>Solve the Equation <reset><yellow>First person calculate the following math equation wins: <white>%rewards%</white>.").appendNewline(),
            text("%variables%").appendNewline(),
            text("%equation%").appendNewline()
            );

    @Game(MathChatGame.class)
    public static final Translation MATH_STATE_WON = new Translation("chat-games.math.winner").add(ComponentType.CHAT,
            text("<red><bold>Math <reset><white>%winner%<green> was the first person to get the right answer! <hover:show_text:'<yellow>%equation%'><white>%equation%</white></hover> <gray>= <yellow>%answer%</yellow>!")
    );
    @Game(MathChatGame.class)
    public static final Translation MATH_STATE_NO_WINNER = new Translation("chat-games.math.no-winner").add(ComponentType.CHAT,
            text("<red><bold>Math <reset><red>Nobody could calculate <hover:show_text:'<yellow>%equation%'><white>%equation%</white></hover>!")
            );

    /*
     * True or False
     */
    @Game(TrueOrFalseChatGame.class)
    public static final Translation TOF_VALUE_TRUE = new Translation("chat-game.true-or-false.value-true").add(ComponentType.CHAT, text("<green>True"));
    @Game(TrueOrFalseChatGame.class)
    public static final Translation TOF_VALUE_FALSE = new Translation("chat-game.true-or-false.value-false").add(ComponentType.CHAT, text("<red>False"));
    @Game(TrueOrFalseChatGame.class)
    public static final Translation TOF_STATE_CREATED_TRUE_FIRST = new Translation("chat-games.true-or-false.solve-true-first").add(ComponentType.CHAT,
            Component.newline(),
            text("<green><bold>True <reset><gray>or <red><bold>False <reset><yellow>%info% <green><bold><hover:show_text:'<gray>Click to answer <green><bold>TRUE<reset>'><click:run_command:/chatgame %id% true>TRUE <red><bold><hover:show_text:'<gray>Click to answer <red><bold>FALSE<reset>'><click:run_command:/chatgame %id% false>FALSE").appendNewline()
            );
    @Game(TrueOrFalseChatGame.class)
    public static final Translation TOF_STATE_CREATED_FALSE_FIRST = new Translation("chat-games.true-or-false.solve-false-first").add(ComponentType.CHAT,
            Component.newline(),
            text("<green><bold>True <reset><gray>or <red><bold>False <reset><yellow>%info% <red><bold><hover:show_text:'<gray>Click to answer <red><bold>FALSE<reset>'><click:run_command:/chatgame %id% false>FALSE <green><bold><hover:show_text:'<gray>Click to answer <green><bold>TRUE<reset>'><click:run_command:/chatgame %id% true>TRUE").appendNewline()
    );
    @Game(TrueOrFalseChatGame.class)
    public static final Translation TOF_STATE_WON = new Translation("chat-games.true-or-false.winner").add(ComponentType.CHAT,
            text("<gold><bold>True <reset><gray>or <gold><bold>False <reset><white>%winner% <green>has won in <white>%seconds%</white> seconds! <white>%info%</white> <green>is %value%!")
    );
    @Game(TrueOrFalseChatGame.class)
    public static final Translation TOF_STATE_NO_WINNER = new Translation("chat-games.true-or-false.no-winner").add(ComponentType.CHAT,
            text("<dark_red><bold>Unscramble <reset><red>Nobody got the correct answer for <white>%info%</white>!")
    );
    @Game(TrueOrFalseChatGame.class)
    public static final Translation TOF_STATE_CANNOT_VOTE_AGAIN = new Translation("chat-games.true-or-false.cannot-vote-second-time").add(ComponentType.CHAT,
            text("<gold><bold>True <reset><gray>or <gold><bold>False <reset><red>You cannot answer to the current <green>True <gray>or <red>False <white>again!")
    );

    /*
     * Unscramble
     */
    @Game(UnscrambleWordChatGame.class)
    public static final Translation UNSCRAMBLE_STATE_CREATED = new Translation("chat-games.unscramble.created-1").add(ComponentType.CHAT,
            Component.newline(),
            text("<green><bold>Unscramble <reset><yellow>First person to unscramble <hover:show_text:'<yellow>%scrambled%'><white>%scrambled%</white> wins!").appendNewline()
    );
    @Game(UnscrambleWordChatGame.class)
    public static final Translation UNSCRAMBLE_STATE_CREATED_2 = new Translation("chat-games.unscramble.created-2").add(ComponentType.CHAT,
            Component.newline(),
            text("<green><bold>Unscramble <reset><yellow>First person to unscramble <hover:show_text:'<yellow>%scrambled%'><white><obfuscated>%scrambled%</obfuscated></white> <gray>(Hover)</gray></hover> wins!").appendNewline()
    );
    public static final Translation UNSCRAMBLE_STATE_WON = new Translation("chat-games.unscramble.won").add(ComponentType.CHAT,
            text("<gold><bold>Unscramble <reset><white>%winner% <green>has unscrambled <white>%scrambled% <green>as <white>%unscrambled%</white> in <white>%seconds% seconds!")
    );
    public static final Translation UNSCRAMBLE_STATE_NO_WINNER = new Translation("chat-games.unscramble.no-winner").add(ComponentType.CHAT,
            text("<dark_red><bold>Unscramble <reset><red>Nobody unscrambled the word <hover:show_text:'<yellow>%scrambled%'><white>%scrambled%</white></hover>!")
            );

    /*
     * Copy
     */
    @Game(CopyTheWordChatGame.class)
    public static final Translation COPY_STATE_CREATED = new Translation("chat-games.copy.created-1").add(ComponentType.CHAT,
            Component.newline(),
            text("<green><bold>Write Fastest <reset><yellow>First person to type <hover:show_text:'<yellow>%word%'><white>%word%</white> wins!").appendNewline()
    );
    @Game(CopyTheWordChatGame.class)
    public static final Translation COPY_STATE_CREATED_2 = new Translation("chat-games.copy.created-2").add(ComponentType.CHAT,
            Component.newline(),
            text("<green><bold>Write Fastest <reset><yellow>First person to type <hover:show_text:'<yellow>%word%'><white><obfuscated>%word%</obfuscated></white> <gray>(Hover)</gray></hover> wins!").appendNewline()
    );
    public static final Translation COPY_STATE_WON = new Translation("chat-games.copy.won").add(ComponentType.CHAT,
            text("<gold><bold>Write Fastest <reset><white>%winner% <green>wrote <white>%word% <green>in <white>%seconds% seconds!")
    );
    public static final Translation COPY_STATE_NO_WINNER = new Translation("chat-games.copy.no-winner").add(ComponentType.CHAT,
            text("<dark_red><bold>Write Fastest <reset><red>Nobody typed the word <hover:show_text:'<yellow>%word%'><white>%word%</white></hover>!")
    );
}
