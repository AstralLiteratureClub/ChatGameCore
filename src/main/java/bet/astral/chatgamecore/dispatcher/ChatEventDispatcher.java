package bet.astral.chatgamecore.dispatcher;

import bet.astral.chatgamecore.game.*;
import bet.astral.chatgamecore.internal.ChatListener;
import bet.astral.chatgamecore.internal.ConnectionListener;
import bet.astral.more4j.tuples.Pair;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ChatEventDispatcher {
    private final Map<UUID, ChatGame> runningById = new HashMap<>();
    private final Map<Class<? extends ChatGame>, ChatGame> runningByClass = new HashMap<>();
    private final Map<Class<? extends ChatGame>, Method> createMethods = new HashMap<>();
    private final Map<Class<? extends ChatGame>, GameData> gameDataByClass = new HashMap<>();
    private final Map<String, Class<? extends ChatGame>> byName = new HashMap<>();
    @Getter
    private ChatGame latest;

    public <T extends ChatGame> void dispatchAnswer(Player player, UUID game, String answer) {
        ChatGame chatGame = runningById.get(game);
        if (game != null && chatGame.getState() == State.CREATED){
            chatGame.guess(player, answer);
        }
    }
    public <T extends ChatGame> void dispatchAnswer(Player player, Class<T> gameClass, String answer){
        T game = getLatest(gameClass);
        if (game != null && game.getState() == State.CREATED){
            game.guess(player, answer);
        }
    }

    public void dispatchAnswer(Player player, String answer){
        ChatGame latest = getLatest();
        if (latest != null && latest.getState()==State.CREATED){
            latest.guess(player, answer);
        }
    }

    public <T extends ChatGame> T getLatest(Class<T> clazz, UUID uniqueId){
        //noinspection unchecked
        return (T) runningById.get(uniqueId);
    }

    public <T extends ChatGame> T getLatest(Class<T> clazz){
        //noinspection unchecked
        return (T) runningByClass.get(clazz);
    }

    public <T extends ChatGame> Class<T> getByName(String name){
        //noinspection unchecked
        return (Class<T>) byName.get(name.toLowerCase());
    }

    public <T extends ChatGame> List<Pair<Class<T>, String>> getRegistred(){
        List<Pair<Class<T>, String>> list = new LinkedList<>();
        for (String value : byName.keySet()){
            list.add(Pair.immutable(getByName(value), value));
        }
        return list;
    }

    public <T extends ChatGame> void run(Class<T> clazz, RunData runData) throws IllegalStateException, IllegalArgumentException {
        T currentGame = getLatest(clazz);
        if (currentGame != null && currentGame.getState()==State.CREATED){
            throw new IllegalStateException("Found already a game running in the threads.");
        }

        Method method = createMethods.get(clazz);
        if (method == null){
            throw new IllegalArgumentException("Couldn't find a game registered for class "+ clazz.getName());
        }
        GameData gameData = gameDataByClass.get(clazz);
        try {
            method.setAccessible(true);
            //noinspection unchecked
            latest = (T) method.invoke(null, gameData, runData);
            runningByClass.put(clazz, latest);
            runningById.put(gameData.getUniqueId(), latest);
            latest.launch();
        } catch (IllegalAccessException e) {
            throw new RuntimeException("wtf..?", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Illegal create method formatting! Format: method(GameData.class, RunData.class)", e);
        }
    }

    public <T extends ChatGame> void register(Class<T> clazz) throws IllegalStateException, ClassCastException {
        unregister(clazz);
        Method method = Arrays.stream(clazz.getMethods()).filter(m->m.isAnnotationPresent(Create.class)).findFirst().orElse(null);
        if (method == null){
            method = Arrays.stream(clazz.getDeclaredMethods()).filter(m->m.isAnnotationPresent(Create.class)).findFirst().orElse(null);
        }


        if (method == null){
            throw new IllegalStateException("Couldn't find create method for class " + clazz.getName());
        }
        Create create = method.getAnnotation(Create.class);

        //
        // Get the default game data
        //
        Field dateField = Arrays.stream(clazz.getFields()).filter(m->m.isAnnotationPresent(GameData.DefaultData.class)).findFirst().orElse(null);
        if (dateField==null) {
            dateField = Arrays.stream(clazz.getDeclaredFields()).filter(m -> m.isAnnotationPresent(GameData.DefaultData.class)).findFirst().orElse(null);
        }
        if (dateField == null){
            throw new IllegalStateException("Couldn't find game data field for class " + clazz.getName());
        }
        dateField.setAccessible(true);

        try {
            GameData data = (GameData) dateField.get(null);
            gameDataByClass.put(clazz, data);
            byName.put(create.name().toLowerCase(), clazz);
            createMethods.put(clazz, method);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("wtf?", e);
        }
    }
    public <T extends ChatGame> void unregister(Class<T> chatGame) {
        T game = getLatest(chatGame);
        runningByClass.remove(chatGame);
        Set<UUID> mapped = runningById.entrySet().stream().filter(entry->chatGame.isInstance(entry.getValue())).map(entry->entry.getKey()).collect(Collectors.toSet());
        if (!mapped.isEmpty()){
            mapped.forEach(runningById::remove);
        }

        createMethods.remove(chatGame);

        if (game == null){
            return;
        }
        if (game.getState()!= State.CREATED){
            return;
        }
        game.end();
    }

    public void registerListeners(JavaPlugin plugin){
        plugin.getServer().getPluginManager().registerEvents(new ConnectionListener(this), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ChatListener(this), plugin);
    }

    @ApiStatus.Internal
    public void onJoin(@NotNull Player player) {
        ChatGame latest = getLatest();
        if (latest != null && latest.getState()==State.CREATED){
            latest.onJoin(player);
        }
    }
}
