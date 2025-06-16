package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Logger;

public class Loader<T> {
    private final File file;
    private final List<T> data = new ArrayList<>();
    private final Gson gson;
    private final Type listType;
    private static final Logger log = AppLogger.get();

    /**
     * Constructor genérico
     * @param filePath Ruta al archivo JSON
     * @param clazz Tipo de clase a cargar (por ejemplo, Message.class)
     */
    public Loader(String filePath, Class<T> clazz) {
        this.file = new File(filePath);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.listType = TypeToken.getParameterized(List.class, clazz).getType();
        load();
    }

    private void load() {
        if (!file.exists()) {
            try {
                if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs(); 
                }
                try (Writer writer = new FileWriter(file)) {
                    gson.toJson(Collections.emptyList(), writer);
                }
                log.info("Archivo creado vacío: " + file.getPath());
            } catch (IOException e) {
                log.severe("No se pudo crear el archivo: " + file.getPath() + " - " + e.getMessage());
            }
            return;
        }
    
        try (Reader reader = new FileReader(file)) {
            List<T> loaded = gson.fromJson(reader, listType);
            if (loaded != null) data.addAll(loaded);
            log.info("Cargados " + data.size() + " elementos desde " + file.getName());
        } catch (IOException e) {
            log.severe("Error al leer " + file.getName() + ": " + e.getMessage());
        }
    }
    

    private void save() {
        try (Writer writer = new FileWriter(file)) {
            gson.toJson(data, writer);
            log.info("Guardados " + data.size() + " elementos en " + file.getName());
        } catch (IOException e) {
            log.severe("Error al guardar en " + file.getName() + ": " + e.getMessage());
        }
    }

    public synchronized void add(T item) {
        data.add(item);
        save();
    }

    public synchronized void removeById(String id) {
        boolean removed = data.removeIf(item -> {
            try {
                return id.equals(item.getClass().getField("id").get(item));
            } catch (Exception e) {
                log.warning("No se pudo acceder al campo 'id' para eliminar: " + e.getMessage());
                return false;
            }
        });
        if (removed) save();
    }

    public synchronized List<T> getAll() {
        return new ArrayList<>(data);
    }
}