package bet.astral.chatgamecore.game.builtin.math;

import java.util.List;
import java.util.Random;

public class HardMathArguments {
    public static Object assignValue(List<MathChatGame.Variable> usedVariables, Random random) {
        return assignValue(usedVariables, random, -323, 323);
    }
    public static Object assignValue(List<MathChatGame.Variable> usedVariables, Random random, MathChatGame.ValueType valueType) {
        return assignValue(usedVariables, random, -323, 323, valueType);
    }

    public static Object assignValue(List<MathChatGame.Variable> usedVariables, Random random, double min, double max) {
        MathChatGame.ValueType type = MathChatGame.ValueType.random(random);
        return assignValue(usedVariables, random, min, max, type);
    }
    public static Object assignValue(List<MathChatGame.Variable> usedVariables, Random random, double min, double max, MathChatGame.ValueType type) {
        switch (type) {
            case PARENTHESES -> {
                double difficulty = random.nextDouble();
                if (difficulty > 0.8) {
                    Object value1 = assignValue(usedVariables, random);
                    MathChatGame.MathEquationType equationType = MathChatGame.MathEquationType.random(random);
                    Object value2 = assignValueForType(usedVariables, random, equationType);

                    MathChatGame.MathEquationType equationType2 = MathChatGame.MathEquationType.random(random);
                    Object value3 = assignValueForType(usedVariables, random, equationType2);

                    return new MathChatGame.Parentheses(
                            random,
                            value1,
                            equationType,
                            value2,
                            equationType2,
                            value3
                    );
                } else if (difficulty > 0.4) {
                    Object value1 = assignValue(usedVariables, random);
                    Object value2 = assignValue(usedVariables, random);
                    return new MathChatGame.Parentheses(
                            random,
                            value1,
                            MathChatGame.MathEquationType.random(random),
                            value2
                    );
                } else {
                    return new MathChatGame.Parentheses(
                            random,
                            assignSimpleValue(random),
                            MathChatGame.MathEquationType.randomSimple(random),
                            assignSimpleValue(random)
                    );
                }
            }
            case VARIABLE -> {
                MathChatGame.Variable variable = new MathChatGame.Variable(random, MathChatGame.Variable.generateVariableName(usedVariables), assignValue(usedVariables, random));
                usedVariables.add(variable);
                return variable;
            }
            case DOUBLE -> {
                return random.nextDouble(min, max);
            }
            case INTEGER -> {
                return random.nextInt((int) min, (int) max);
            }
        }
        return Double.NEGATIVE_INFINITY;
    }

    public static Object assignValueForType(List<MathChatGame.Variable> variables, Random random, MathChatGame.MathEquationType type) {
        if (type == MathChatGame.MathEquationType.POWER) {
            return assignValue(variables, random, -5, 5);
        } else if (type == MathChatGame.MathEquationType.DIVIDE || type == MathChatGame.MathEquationType.MULTIPLY) {
            return assignValue(variables, random, -25, 25);
        } else {
            return assignValue(variables, random);
        }
    }
    public static Object assignSimpleValue(Random random) {
        return random.nextBoolean() ? random.nextDouble(-100, 100) : random.nextInt(-100, 100);
    }
}
