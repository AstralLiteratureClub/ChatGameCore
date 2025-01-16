package bet.astral.chatgamecore.game.builtin.math;

import bet.astral.more4j.tuples.Pair;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static bet.astral.chatgamecore.game.builtin.math.NumberArguments.randomDouble;
import static bet.astral.chatgamecore.game.builtin.math.NumberArguments.randomInteger;

public interface MathArguments extends NumberArguments {
    
    
    static MathChatGame.Parentheses createMathEquation(Random random){
        return createMathEquation(random, false);
    }
    static MathChatGame.Parentheses createMathEquation(Random random, boolean allowDoubles) {
        final double min = -100;
        final double max = 100;
        final double chance = random.nextDouble(0, 1);

        MathChatGame.Parentheses parentheses;
        if (chance > 0.9) {
            return parentheses(random, randomNumber(random, min, max, allowDoubles), MathChatGame.MathEquationType.randomSimple(random));
        } else if (chance > 0.7) {
            return parentheses(random,
                    randomNumber(random, min, max, allowDoubles),
                    MathChatGame.MathEquationType.randomSimple(random),
                    randomNumber(random, min, max, allowDoubles),
                    MathChatGame.MathEquationType.randomSimple(random),
                    randomNumber(random, min, max, allowDoubles)
            );
        } else {
            parentheses = new MathChatGame.Parentheses(random,
                    randomParenthesesValue(random, allowDoubles),
                    MathChatGame.MathEquationType.randomSimple(random),
                    randomParenthesesValue(random, allowDoubles)
            );
        }
        return parentheses;
    }

    static Pair<Object, List<MathChatGame.Variable>> createMathEquationHard(Random random, boolean allowDoubles){
        List<MathChatGame.Variable> variables = new LinkedList<>();
        Object obj = assignValue(variables, random, -323, 323, allowDoubles);
        return Pair.immutable(obj, variables);
    }

    static Object randomParenthesesValue(Random random, boolean allowDoubles){
        return random.nextDouble() > 0.9 ? randomParentheses(random, allowDoubles) : randomNumber(random, -100, 100, allowDoubles);
    }

    static Object randomParentheses(Random random, boolean allowDoubles){
        final double min = -100;
        final double max = 100;
        double chance = random.nextDouble();
        if (chance > 0.95){
            return parentheses(random,
                    randomNumber(random, min, max, allowDoubles),
                    MathChatGame.MathEquationType.randomSimple(random),
                    randomNumber(random, min, max, allowDoubles),
                    MathChatGame.MathEquationType.randomSimple(random),
                    randomNumber(random, min, max, allowDoubles),
                    MathChatGame.MathEquationType.randomSimple(random),
                    randomNumber(random, min, max, allowDoubles)
            );
        } else if (chance > 0.70){
            return parentheses(random,
                    randomNumber(random, min, max, allowDoubles),
                    MathChatGame.MathEquationType.randomSimple(random),
                    randomNumber(random, min, max, allowDoubles),
                    MathChatGame.MathEquationType.randomSimple(random),
                    randomNumber(random, min, max, allowDoubles)
            );
        } else if (chance > 0.20) {
            return parentheses(random,
                    randomNumber(random, min, max, allowDoubles),
                    MathChatGame.MathEquationType.randomSimple(random),
                    randomNumber(random, min, max, allowDoubles)
            );
        } else {
            return parentheses(random,
                    randomNumber(random, min, max, allowDoubles),
                    MathChatGame.MathEquationType.randomSimple(random)
            );
        }
    }

    static Number randomNumber(Random random, double min, double max, boolean allowDoubles) {
        return allowDoubles ? randomInteger(random, (int) min, (int) max) : randomDouble(random, min, max);
    }

    static Object assignValue(List<MathChatGame.Variable> usedVariables, Random random, boolean allowDoubles) {
        return assignValue(usedVariables, random, -323, 323, allowDoubles);
    }
    static Object assignValue(List<MathChatGame.Variable> usedVariables, Random random, MathChatGame.ValueType valueType, boolean allowDoubles) {
        return assignValue(usedVariables, random, -323, 323, valueType, allowDoubles);
    }

    static Object assignValue(List<MathChatGame.Variable> usedVariables, Random random, double min, double max, boolean allowDoubles) {
        MathChatGame.ValueType type = MathChatGame.ValueType.random(random);
        return assignValue(usedVariables, random, min, max, type, allowDoubles);
    }
    static Object assignValue(List<MathChatGame.Variable> usedVariables, Random random, double min, double max, MathChatGame.ValueType type, boolean allowDoubles) {
        switch (type) {
            case PARENTHESES -> {
                double difficulty = random.nextDouble();
                if (difficulty > 0.8) {
                    Object value1 = assignValue(usedVariables, random, allowDoubles);
                    MathChatGame.MathEquationType equationType = MathChatGame.MathEquationType.random(random);
                    Object value2 = assignValueForType(usedVariables, random, equationType, allowDoubles);

                    MathChatGame.MathEquationType equationType2 = MathChatGame.MathEquationType.random(random);
                    Object value3 = assignValueForType(usedVariables, random, equationType2, allowDoubles);

                    return new MathChatGame.Parentheses(
                            random,
                            value1,
                            equationType,
                            value2,
                            equationType2,
                            value3
                    );
                } else if (difficulty > 0.4) {
                    Object value1 = assignValue(usedVariables, random, allowDoubles);
                    Object value2 = assignValue(usedVariables, random, allowDoubles);
                    return new MathChatGame.Parentheses(
                            random,
                            value1,
                            MathChatGame.MathEquationType.random(random),
                            value2
                    );
                } else {
                    return new MathChatGame.Parentheses(
                            random,
                            randomNumber(random, min, max, allowDoubles),
                            MathChatGame.MathEquationType.randomSimple(random),
                            randomNumber(random, min, max, allowDoubles)
                    );
                }
            }
            case VARIABLE -> {
                MathChatGame.Variable variable = new MathChatGame.Variable(random, MathChatGame.Variable.generateVariableName(usedVariables), assignValue(usedVariables, random, allowDoubles));
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

    static Object assignValueForType(List<MathChatGame.Variable> variables, Random random, MathChatGame.MathEquationType type, boolean allowDoubles) {
        if (type == MathChatGame.MathEquationType.POWER) {
            return assignValue(variables, random, -5, 5, allowDoubles);
        } else if (type == MathChatGame.MathEquationType.DIVIDE || type == MathChatGame.MathEquationType.MULTIPLY) {
            return assignValue(variables, random, -25, 25, allowDoubles);
        } else {
            return assignValue(variables, random, allowDoubles);
        }
    }

    @Contract("_, _, _, _ -> new")
    static MathChatGame.@NotNull Parentheses parentheses(Random random, TextColor color, Object first, MathChatGame.MathEquationType equation) {
        return new MathChatGame.Parentheses(random, first, equation, color);
    }

    @Contract("_, _, _ -> new")
    static MathChatGame.@NotNull Parentheses parentheses(Random random, Object first, MathChatGame.MathEquationType equation) {
        return new MathChatGame.Parentheses(random, first, equation, MathChatGame.Formatter.randomColor(random));
    }

    @Contract(value = "_, _, _, _, _ -> new", pure = true)
    static MathChatGame.@NotNull Parentheses parentheses(Random random, TextColor color, Object first, MathChatGame.MathEquationType equation, Object second) {
        return new MathChatGame.Parentheses(random, first, equation, second, color);
    }

    @Contract(value = "_, _, _, _ -> new", pure = true)
    static MathChatGame.@NotNull Parentheses parentheses(Random random, Object first, MathChatGame.MathEquationType equation, Object second) {
        return new MathChatGame.Parentheses(random, first, equation, second);
    }

    @Contract(value = "_, _, _, _, _, _, _ -> new", pure = true)
    static MathChatGame.@NotNull Parentheses parentheses(Random random, TextColor color, Object first, MathChatGame.MathEquationType equation, Object second, MathChatGame.MathEquationType secondEquation, Object third) {
        return new MathChatGame.Parentheses(random, first, equation, second, secondEquation, third, color);
    }

    @Contract(value = "_, _, _, _, _, _ -> new", pure = true)
    static MathChatGame.@NotNull Parentheses parentheses(Random random, Object first, MathChatGame.MathEquationType equation, Object second, MathChatGame.MathEquationType secondEquation, Object third) {
        return new MathChatGame.Parentheses(random, first, equation, second, secondEquation, third);
    }

    @Contract(value = "_, _, _, _, _, _, _, _, _ -> new", pure = true)
    static MathChatGame.@NotNull Parentheses parentheses(Random random, TextColor color, Object first, MathChatGame.MathEquationType equation, Object second, MathChatGame.MathEquationType secondEquation, Object third, MathChatGame.MathEquationType thirdEquation, Object fourth) {
        return new MathChatGame.Parentheses(random, new Object[]{first, second, third, fourth}, new MathChatGame.MathEquationType[]{equation, secondEquation, thirdEquation}, color);
    }

    @Contract(value = "_, _, _, _, _, _, _, _ -> new", pure = true)
    static MathChatGame.@NotNull Parentheses parentheses(Random random, Object first, MathChatGame.MathEquationType equation, Object second, MathChatGame.MathEquationType secondEquation, Object third, MathChatGame.MathEquationType thirdEquation, Object fourth) {
        return new MathChatGame.Parentheses(random, new Object[]{first, second, third, fourth}, new MathChatGame.MathEquationType[]{equation, secondEquation, thirdEquation}, MathChatGame.Formatter.randomColor(random));
    }

    @Contract(value = "_, _, _, _, _, _, _, _, _, _, _ -> new", pure = true)
    static MathChatGame.@NotNull Parentheses parentheses(Random random, TextColor color, Object first, MathChatGame.MathEquationType equation, Object second, MathChatGame.MathEquationType secondEquation, Object third, MathChatGame.MathEquationType thirdEquation, Object fourth, MathChatGame.MathEquationType fourthEquation, Object fifth) {
        return new MathChatGame.Parentheses(random, new Object[]{first, second, third, fourth, fifth}, new MathChatGame.MathEquationType[]{equation, secondEquation, thirdEquation, fourthEquation}, color);
    }

    @Contract(value = "_, _, _, _, _, _, _, _, _, _ -> new", pure = true)
    static MathChatGame.@NotNull Parentheses parentheses(Random random, Object first, MathChatGame.MathEquationType equation, Object second, MathChatGame.MathEquationType secondEquation, Object third, MathChatGame.MathEquationType thirdEquation, Object fourth, MathChatGame.MathEquationType fourthEquation, Object fifth) {
        return new MathChatGame.Parentheses(random, new Object[]{first, second, third, fourth, fifth}, new MathChatGame.MathEquationType[]{equation, secondEquation, thirdEquation, fourthEquation}, MathChatGame.Formatter.randomColor(random));
    }
}
