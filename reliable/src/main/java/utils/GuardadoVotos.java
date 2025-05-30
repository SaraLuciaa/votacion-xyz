package utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import VotacionXYZ.Message;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GuardadoVotos {
    private static final File FILE = new File("reliable/src/main/resources/votos_pendientes.json");
    private final List<Message> pendingMessages = new ArrayList<>();
    private final Logger log = AppLogger.get();
    private final Gson gson;

    public GuardadoVotos() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        load();
    }

    private void load() {
        if (!FILE.exists()) return;
        try (Reader reader = new FileReader(FILE)) {
            Type listType = new TypeToken<ArrayList<Message>>() {}.getType();
            List<Message> loaded = gson.fromJson(reader, listType);
            if (loaded != null) pendingMessages.addAll(loaded);
            log.info("Cargados " + pendingMessages.size() + " votos pendientes.");
        } catch (IOException e) {
            log.severe("Error al cargar votos: " + e.getMessage());
        }
    }

    private void save() {
        try (Writer writer = new FileWriter(FILE)) {
            gson.toJson(pendingMessages, writer);
            log.info("Guardados " + pendingMessages.size() + " votos.");
        } catch (IOException e) {
            log.severe("Error al guardar votos: " + e.getMessage());
        }
    }

    public synchronized void add(Message msg) {
        pendingMessages.add(msg);
        save();
    }

    public synchronized void remove(String id) {
        pendingMessages.removeIf(m -> m.id.equals(id));
        save();
    }

    public synchronized List<Message> getAll() {
        return new ArrayList<>(pendingMessages);
    }
}