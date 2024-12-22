package bet.astral.chatgamecore.game.builtin.math;

import bet.astral.chatgamecore.game.ChatGame;
import bet.astral.chatgamecore.game.GameData;
import bet.astral.chatgamecore.game.RunData;
import bet.astral.chatgamecore.game.State;
import bet.astral.chatgamecore.messenger.GameTranslations;
import bet.astral.messenger.v2.component.ComponentType;
import bet.astral.messenger.v2.info.MessageInfoBuilder;
import bet.astral.messenger.v2.placeholder.Placeholder;
import bet.astral.messenger.v2.placeholder.collection.PlaceholderList;
import bet.astral.messenger.v2.translation.TranslationKey;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public abstract class MathChatGame extends ChatGame {
    public static void runTest(){
        Random random = new Random();
        Variable variable = new Variable(random, "J", -80.25);
        Parentheses parentheses = new Parentheses(
                random,
                variable,
                MathEquationType.POWER,
                new Parentheses(
                        random,
                        variable,
                        MathEquationType.MINUS,
                        variable
                )
        );

        System.out.println(parentheses.displayFirst());
        System.out.println(parentheses.calculate());
    }

    public static final Set<String> CANNOT_CALCULATE = Set.of("Cannot calculate", "No possible answer", "No answer", "NaN", "Not a Number", "nan");
    public static final Set<String> INFINITE = Set.of("Infinity", "Infinite");
    public static final Set<String> NEGATIVE_INFINITE = Set.of("-Infinity", "-Infinite");
    private final Parentheses parentheses;
    private final List<Variable> variables;
    private final double answer;

    private static Set<String> possibleAnswers(Parentheses parentheses){
        Set<String> answers = new HashSet<>();
        double answer = parentheses.calculate();
        String formatted = Formatter.format(answer);
        if (Double.isInfinite(answer)){
            answers.addAll(INFINITE);
            answers.addAll(NEGATIVE_INFINITE);
        } else if (Double.isNaN(answer)){
            answers.addAll(CANNOT_CALCULATE);
        } else if (formatted.contains(".")) {
            String split = formatted.split("\\.")[1];
            if (split.length()>1){
                answers.add(formatted);
                answers.add(formatted.replace("\\.", ","));
            } else {
                answers.add(formatted);
                answers.add(formatted+"0");
                answers.add(formatted.replace("\\.", ","));
                answers.add((formatted+"0").replace("\\.", ","));
            }
        } else {
            answers.add(formatted);
            answers.add(formatted+".0");
            answers.add(formatted+".00");
            answers.add(formatted+",0");
            answers.add(formatted+",00");
        }
        return answers;
    }

    public MathChatGame(@NotNull Parentheses parentheses, @NotNull List<Variable> variables, @NotNull GameData gameData, @NotNull RunData runData) {
        super(possibleAnswers(parentheses), gameData, runData);
        this.parentheses = parentheses;
        this.variables = variables != null ? variables : Collections.emptyList();
        answer = parentheses.calculate();
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
        display.append(parentheses.displayFirst());
        return display.toString();
    }

    @Override
    public PlaceholderList getPlaceholders(State state) {
        PlaceholderList placeholders = new PlaceholderList();
        if (variables != null && !variables.isEmpty()) {
            TextComponent.Builder builder = Component.text();
            for (int i = 0; i < variables.size(); i++) {
                Variable variable = variables.get(i);
                MessageInfoBuilder messagebuilder;
                if (i == variables.size() - 1) {
                    messagebuilder = new MessageInfoBuilder(GameTranslations.MATH_VALUE_VARIABLE);
                } else {
                    messagebuilder = new MessageInfoBuilder(GameTranslations.MATH_VALUE_VARIABLE_COMMA);
                }

                messagebuilder.withPlaceholders(
                                Placeholder.of("value", Formatter.formatComponent(
                                        variable.value,
                                        variable.color
                                )),
                                Placeholder.of("name", Component.text(variable.name, variable.color)),
                                Placeholder.of("default-render", variable.displayVariableAndValueComponent())
                        );
                //Component info; = infoBuilder.build().parseAsComponent(getMessenger(), ComponentType.CHAT);
                Component info = getMessenger().disablePrefixForNextParse().parseComponent(messagebuilder.build(), ComponentType.CHAT);
                builder.append(info);
            }
            placeholders.add("variables", builder);
        }

        placeholders.add("answer", Formatter.formatDouble(answer));
        placeholders.add("equation", parentheses.displayComponentFirst());
        placeholders.add("winner", getWinner() != null ? getWinner().getName() : "No Winner");
        placeholders.add("seconds", getTimeSinceStartDuration().toSeconds());
        return placeholders;
    }

    @Override
    public TranslationKey getTranslation(State state) {
        return switch (state){
            case CREATED -> {
                if (!variables.isEmpty()) {
                    yield GameTranslations.MATH_STATE_CREATED_HAS_VARIABLES;
                } else {
                    yield GameTranslations.MATH_STATE_CREATED;
                }
            }
            case ENDED_PLAYER_GUESSED -> GameTranslations.MATH_STATE_WON;
            case ENDED_NOBODY_GUESSED -> GameTranslations.MATH_STATE_NO_WINNER;
        };
    }

    public interface Value {
        Component displayComponentFirst();
        Component displayComponent();
        String displayFirst();
        String display();
        double calculate();
        Random getRandom();
        TextColor getColor();
    }

    public static class Variable implements Value{
        private final Random random;
        private final String name;
        private final Object value;
        private final TextColor color;
        public Variable(Random random, String name, Object value) {
            this.random = random;
            this.name = name;
            this.value = value;
            this.color = Formatter.randomColor(random);
        }

        public Variable(Random random, String name, Object value, TextColor color) {
            this.random = random;
            this.name = name;
            this.value = value;
            this.color = color;
        }

        public static String generateVariableName(List<Variable> values) {
            Set<String> names = values.stream().map(variable -> variable.name).collect(Collectors.toSet());

            String suffix = names.size() > 25 ? "" + names.size() / 25 : "";
            String name = suffix + RandomStringUtils.randomAlphabetic(1);

            while (names.contains(name)) {
                name = suffix + RandomStringUtils.randomAlphabetic(1);
            }

            return name;
        }

        @Override
        public Component displayComponentFirst() {
            return Component.text(name, color);
        }

        @Override
        public Component displayComponent() {
            return displayComponentFirst();
        }

        @Override
        public String displayFirst() {
            return name;
        }

        @Override
        public String display() {
            return displayFirst();
        }

        public Component displayVariableAndValueComponent() {
            return Component.text(name, Formatter.brighter(color)).appendSpace().append(Component.text("=", color)).appendSpace().append(Formatter.formatComponentFirst(value, color));
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

        @Override
        public TextColor getColor() {
            return null;
        }
    }

    public static class Parentheses implements Value {
        private final Random random;
        private final Object[] values;
        private final MathEquationType[] equationTypes;
        private final TextColor color;

        public Parentheses(Random random, Object value1, MathEquationType equationType, Object value2) {
            this.random = random;
            this.color = Formatter.randomColor(random);
            this.values = new Object[]{value1, value2};
            this.equationTypes = new MathEquationType[]{equationType};
        }
        public Parentheses(Random random, Object value1, MathEquationType equationType, Object value2, TextColor color) {
            this.random = random;
            this.color = color;
            this.values = new Object[]{value1, value2};
            this.equationTypes = new MathEquationType[]{equationType};
        }
        public Parentheses(Random random, Object value1, MathEquationType equationType1, Object value2, MathEquationType equationType2, Object value3) {
            this.random = random;
            this.color = Formatter.randomColor(random);
            this.values = new Object[]{value1, value2, value3};
            this.equationTypes = new MathEquationType[]{equationType1, equationType2};
        }

        public Parentheses(Random random, Object value1, MathEquationType equationType1, Object value2, MathEquationType equationType2, Object value3, TextColor color) {
            this.random = random;
            this.color = color;
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
        public TextColor getColor() {
            return color;
        }

        @Override
        public Component displayComponentFirst() {
            TextComponent.Builder builder = Component.text();
            for (int i = 0; i < values.length; i++) {
                boolean power = false;
                TextColor renderColor = color;

                if (i > 0) {
                    power = MathEquationType.POWER == equationTypes[i -1];
                    if (power){
                        renderColor = Formatter.randomColor(random);
                    }

                    builder.append(Component.text(" ")).append(Component.text(equationTypes[i - 1].sign, renderColor)).append(Component.text(" "));
                    if (power){
                        builder.append(Component.text("(", renderColor));
                    }
                }

                builder.append(Formatter.formatComponent(values[i], renderColor));

                if (power){
                    builder.append(Component.text(")", renderColor));
                }
            }
            return builder.build();
        }

        @Override
        public Component displayComponent() {
            return Component.text("(", color).append(displayComponentFirst()).append(Component.text(")", color));
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
                return value2 == 0 ? Double.POSITIVE_INFINITY : value1 / value2;
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

        public static TextColor brighter(TextColor color, double brighter){
            return TextColor
                            .color(
                                    (int) (color.red() + (color.red() * brighter)),
                                    (int) (color.green() + (color.green() * brighter)),
                                    (int) (color.blue() + (color.blue() * brighter))
                            );
        }
        public static TextColor brighter(TextColor color){
            return brighter(color, 0.35);
        }

        public static TextColor randomColor(Random random){
            return NamedTextColor.nearestTo(TextColor.color(random.nextInt(0, 256), random.nextInt(0, 256), random.nextInt(0, 256)));
        }
        public static Component formatComponentFirst(Object object, TextColor numberColor){
            if (object instanceof Value value){
                return value.displayComponentFirst();
            }
            return Component.text(formatDouble((((Number) object).doubleValue())), brighter(numberColor));
        }
        public static Component formatComponent(Object object, TextColor numberColor){
            if (object instanceof Value value){
                return value.displayComponent();
            }
            return Component.text(formatDouble((((Number) object).doubleValue())), brighter(numberColor));
        }

        public static String formatDouble(double number) {
            return DECIMAL_FORMAT.format(number);
        }

        public static double simpleDouble(double number){
            if (Double.isInfinite(number)){
                if (number < 0){
                    return Double.NEGATIVE_INFINITY;
                } else {
                    return Double.POSITIVE_INFINITY;
                }
            }
            return Double.parseDouble(formatDouble(number));
        }
    }
}
