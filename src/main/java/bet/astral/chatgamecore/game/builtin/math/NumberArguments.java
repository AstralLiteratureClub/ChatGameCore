package bet.astral.chatgamecore.game.builtin.math;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

public interface NumberArguments {
    static double randomDouble(Random random){
        return randomDouble(random, -100, 100);
    }
    static double randomDouble(Random random, double min, double max){
        return random.nextDouble(min, max);
    }

    static int randomInteger(Random random) {
        return randomInteger(random, -100, 100);
    }
    static int randomInteger(@NotNull Random random, int min, int max){
        return random.nextInt(min, max + 1);
    }

    static double opposite(double value){
        return -value;
    }

    static double positive(double value){
        return value > 0 ? value : -value;
    }

    static double negative(double value) {
        return value < 0 ? -value : value;
    }
}
