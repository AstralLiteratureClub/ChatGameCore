package bet.astral.chatgamecore.game.builtin.math;

import java.util.Random;

public class SimpleMathArguments {
    public static MathChatGame.Parentheses assignBase(Random random){
        MathChatGame.Parentheses parentheses;
        if (random.nextDouble()>0.7){
            parentheses = new MathChatGame.Parentheses(random,
                    assignBaseValue(random),
                    MathChatGame.MathEquationType.randomSimple(random),
                    assignBaseValue(random),
                    MathChatGame.MathEquationType.randomSimple(random),
                    assignBaseValue(random)
                    );
        } else {
            parentheses = new MathChatGame.Parentheses(random,
                    assignBaseValue(random),
                    MathChatGame.MathEquationType.randomSimple(random),
                    assignBaseValue(random)
            );
        }
        return parentheses;
    }

    public static Object assignBaseValue(Random random){
        return random.nextDouble() > 0.9 ? assignBaseParentheses(random) : assignBaseNumber(random);
    }
    public static Object assignBaseParentheses(Random random){
        MathChatGame.Parentheses parentheses;
        if (random.nextDouble()>0.7){
            parentheses = new MathChatGame.Parentheses(random,
                    assignBaseNumber(random),
                    MathChatGame.MathEquationType.randomSimple(random),
                    assignBaseNumber(random),
                    MathChatGame.MathEquationType.randomSimple(random),
                    assignBaseNumber(random)
            );
        } else {
            parentheses = new MathChatGame.Parentheses(random,
                    assignBaseNumber(random),
                    MathChatGame.MathEquationType.randomSimple(random),
                    assignBaseNumber(random)
            );
        }
        return parentheses;
    }
    public static Number assignBaseNumber(Random random){
        return random.nextBoolean() ? random.nextDouble(-323, 323) : random.nextInt(-323, 323);
    }
}
