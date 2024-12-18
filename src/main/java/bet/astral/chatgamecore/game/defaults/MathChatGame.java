package bet.astral.chatgamecore.game.defaults;

import bet.astral.chatgamecore.game.ChatGame;
import bet.astral.chatgamecore.game.Create;
import bet.astral.chatgamecore.game.State;
import bet.astral.messenger.v2.Messenger;
import bet.astral.messenger.v2.placeholder.collection.PlaceholderList;
import bet.astral.messenger.v2.translation.TranslationKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class MathChatGame extends ChatGame {
    public static void main(String[] args) {
        Random random = new Random(100);

        for (int i = 0; i < 10; i++) {
            final MathChatGame game = create(random, null, 1);
            System.out.println();
            System.out.println(game.display() + " = " + game.getCorrectAnswer());
        }
    }

    @Create
    public static MathChatGame create(Random random, Messenger messenger, int ticks) {
        List<Variable> variables = new ArrayList<>();
        Parentheses parentheses = (Parentheses) assignValue(variables, random, ValueType.PARENTHESES);
        return new MathChatGame(parentheses, variables, messenger, ticks);
    }

    private static Object assignValue(List<Variable> usedVariables, Random random) {
        return assignValue(usedVariables, random, -323, 323);
    }
    private static Object assignValue(List<Variable> usedVariables, Random random, ValueType valueType) {
        return assignValue(usedVariables, random, -323, 323, valueType);
    }

    private static Object assignValue(List<Variable> usedVariables, Random random, double min, double max) {
        ValueType type = ValueType.random(random);
        return assignValue(usedVariables, random, min, max, type);
    }
    private static Object assignValue(List<Variable> usedVariables, Random random, double min, double max, ValueType type) {
        switch (type) {
            case PARENTHESES -> {
                double difficulty = random.nextDouble();
                if (difficulty > 0.8) {
                    Object value1 = assignValue(usedVariables, random);
                    MathEquationType equationType = MathEquationType.random(random);
                    Object value2 = assignValueForType(usedVariables, random, equationType);

                    MathEquationType equationType2 = MathEquationType.random(random);
                    Object value3 = assignValueForType(usedVariables, random, equationType2);

                    return new Parentheses(
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
                    return new Parentheses(
                            random,
                            value1,
                            MathEquationType.random(random),
                            value2
                    );
                } else {
                    return new Parentheses(
                            random,
                            assignSimpleValue(random),
                            MathEquationType.randomSimple(random),
                            assignSimpleValue(random)
                    );
                }
            }
            case VARIABLE -> {
                Variable variable = new Variable(random, Variable.generateVariableName(usedVariables), assignValue(usedVariables, random));
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
        return 0;
    }

    private static Object assignValueForType(List<Variable> variables, Random random, MathEquationType type) {
        if (type == MathEquationType.POWER) {
            return assignValue(variables, random, -5, 5);
        } else if (type == MathEquationType.DIVIDE || type == MathEquationType.MULTIPLY) {
            return assignValue(variables, random, -25, 25);
        } else {
            return assignValue(variables, random);
        }
    }

    private static Object assignSimpleValue(Random random) {
        return random.nextBoolean() ? random.nextDouble(-100, 100) : random.nextInt(-100, 100);
    }

    private final Parentheses parentheses;
    private final List<Variable> variables;

    public MathChatGame(Parentheses parentheses, List<Variable> variables, Messenger messenger, int ticks) {
        super("" + parentheses.calculate(), false, messenger, ticks);
        this.parentheses = parentheses;
        this.variables = variables;
    }

    public String display() {
        StringBuilder display = new StringBuilder();
        for (Variable variable : variables){
            if (!display.isEmpty()){
                display.append("\n");
            }
            display.append(variable.displayVariableAndValue());
        }
        if (!display.isEmpty()){
            display.append("\n");
        }
        display.append(parentheses.display());
        return display.toString();
    }

    @Override
    public PlaceholderList getPlaceholders(State state) {
        return null;
    }

    @Override
    public TranslationKey getTranslation(State state) {
        return null;
    }

    @Override
    public void rewardPlayer(Player player) {

    }

    @Override
    public void nobodyGuessedCorrectly() {

    }

    public interface Value {
        Component displayComponentFirst(TextColor color);
        Component displayComponent(TextColor color);
        String displayFirst();
        String display();
        double calculate();
        Random getRandom();
    }

    public static class Variable implements Value{
        private final Random random;
        private final String name;
        private final Object value;

        public Variable(Random random, String name, Object value) {
            this.random = random;
            this.name = name;
            this.value = value;
        }

        public static String generateVariableName(List<Variable> values) {
            Set<String> names = new HashSet<>(values.stream().map(variable -> variable.name).collect(Collectors.toSet()));

            String suffix = names.size() > 25 ? "" + names.size() / 25 : "";
            String name = suffix + RandomStringUtils.randomAlphabetic(1);

            while (names.contains(name)) {
                name = suffix + RandomStringUtils.randomAlphabetic(1);
            }

            return name;
        }

        @Override
        public Component displayComponentFirst(TextColor color) {
            return Component.text(name, color);
        }

        @Override
        public Component displayComponent(TextColor color) {
            return displayComponentFirst(color);
        }

        @Override
        public String displayFirst() {
            return name;
        }

        @Override
        public String display() {
            return displayFirst();
        }

        public Component displayVariableAndValueComponent(TextColor color) {
            if (value instanceof Value valueObj){
                return Component.text(name, color).append(Component.text("=", color)).append(valueObj.displayComponentFirst(color));
            }
            return Component.text(name, color).append(Component.text("=", color)).append(Component.text(Formatter.formatDouble(((Number) value).doubleValue())));
        }

        public String displayVariableAndValue(){
            if (value instanceof Value valueObj){
                if (value instanceof Variable variable){
                    return name + " = " + variable.displayVariableAndValue();
                }
                return name+" = "+valueObj.display();
            }
            return name+" = "+ Formatter.formatDouble(((Number) value).doubleValue());
        }

        @Override
        public double calculate() {
            if (value instanceof Value objVal){
                return objVal.calculate();
            }
            return Formatter.simpleDouble(((Number) value).doubleValue());
        }

        @Override
        public Random getRandom() {
            return random;
        }
    }

    public static class Parentheses implements Value {
        private final Random random;
        private final Object[] values;
        private final MathEquationType[] equationTypes;

        public Parentheses(Random random, Object value1, MathEquationType equationType, Object value2) {
            this.random = random;
            this.values = new Object[]{value1, value2};
            this.equationTypes = new MathEquationType[]{equationType};
        }

        public Parentheses(Random random, Object value1, MathEquationType equationType1, Object value2, MathEquationType equationType2, Object value3) {
            this.random = random;
            this.values = new Object[]{value1, value2, value3};
            this.equationTypes = new MathEquationType[]{equationType1, equationType2};
        }

        public double calculate() {
            List<Double> resolvedValues = new ArrayList<>();
            for (Object value : values) {
                if (value instanceof Value val) {
                    resolvedValues.add(val.calculate());
                } else if (value instanceof Number number) {
                    resolvedValues.add(Formatter.simpleDouble(number.doubleValue()));
                }
            }

            List<MathEquationType> remainingTypes = new ArrayList<>(Arrays.asList(equationTypes));
            for (MathEquationType priority : MathEquationType.PRIORITY_ORDER) {
                for (int i = 0; i < remainingTypes.size(); i++) {
                    if (remainingTypes.get(i) == priority) {
                        double result = priority.calculate(resolvedValues.get(i), resolvedValues.get(i + 1));
                        if (Double.isNaN(result)){
                            resolvedValues.set(i, result);
                        } else {
                            resolvedValues.set(i, Formatter.simpleDouble(result));
                        }
                        resolvedValues.remove(i + 1);
                        remainingTypes.remove(i);
                        i--;
                    }
                }
            }

            return Formatter.simpleDouble(resolvedValues.getFirst());
        }

        @Override
        public Random getRandom() {
            return random;
        }

        @Override
        public Component displayComponentFirst(TextColor color) {
            return null;
        }

        @Override
        public Component displayComponent(TextColor color) {
            TextComponent.Builder builder = Component.text();
            builder.append(Component.text("(", color));
            for (int i = 0; i < values.length; i++) {
                if (i > 0) {
                    builder.append(Component.text(" ")).append(Component.text(equationTypes[i - 1].sign)).append(Component.text(" "));
                }
                builder.append(Formatter.formatComponent(values[i], getRandom()));
            }
            builder.append(Component.text(")", color));
            return builder.build();
        }

        @Override
        public String displayFirst() {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                if (i > 0) {
                    builder.append(" ").append(equationTypes[i - 1].sign).append(" ");
                }
                builder.append(Formatter.format(values[i]));
            }
            return builder.toString();
        }

        public String display() {
            StringBuilder builder = new StringBuilder();
            builder.append("(");
            for (int i = 0; i < values.length; i++) {
                if (i > 0) {
                    builder.append(" ").append(equationTypes[i - 1].sign).append(" ");
                }
                builder.append(Formatter.format(values[i]));
            }
            builder.append(")");
            return builder.toString();
        }

        @Override
        public String toString() {
            return display();
        }
    }

    public enum ValueType {
        INTEGER, DOUBLE, PARENTHESES, VARIABLE;

        public static ValueType random(Random random) {
            return values()[random.nextInt(values().length)];
        }
    }

    public enum MathEquationType {
        PLUS('+') {
            @Override
            public double calculate(double value1, double value2) {
                return value1 + value2;
            }
        },
        MINUS('-') {
            @Override
            public double calculate(double value1, double value2) {
                return value1 - value2;
            }
        },
        MULTIPLY('*') {
            @Override
            public double calculate(double value1, double value2) {
                return value1 * value2;
            }
        },
        DIVIDE('/') {
            @Override
            public double calculate(double value1, double value2) {
                return value2 == 0 ? Double.POSITIVE_INFINITY : value1 / value2; // Updated division by zero handling.
            }
        },
        POWER('^') {
            @Override
            public double calculate(double value1, double value2) {
                return Math.pow(value1, value2);
            }
        };

        public static final List<MathEquationType> PRIORITY_ORDER = List.of(POWER, MULTIPLY, DIVIDE, PLUS, MINUS);

        private final char sign;

        MathEquationType(char sign) {
            this.sign = sign;
        }

        public abstract double calculate(double value1, double value2);

        public static MathEquationType random(Random random) {
            return values()[random.nextInt(values().length)]; // Fixed bounds to include all types.
        }

        public static MathEquationType randomSimple(Random random) {
            return values()[random.nextInt(PLUS.ordinal() + 1)]; // Bounds for simpler operations.
        }
    }

    public static class Formatter {
        private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.##");

        public static String format(Object object){
            if (object instanceof Value value){
                return value.display();
            }
            return formatDouble(((Number) object).doubleValue());
        }

        public static Component formatComponent(Object object, Random random) {
            return formatComponent(object,
                    NamedTextColor.nearestTo(TextColor.color(random.nextInt(0, 256), random.nextInt(0, 256), random.nextInt(0, 256))));
        }
        public static Component formatComponent(Object object, TextColor color){
            if (object instanceof Value value){
                return value.displayComponent(color);
            }
            return Component.text((((Number) object).doubleValue()), color);
        }

        public static String formatDouble(double number) {
            return DECIMAL_FORMAT.format(number);
        }

        public static double simpleDouble(double number){
            return Double.parseDouble(formatDouble(number));
        }
    }
}
