package utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Logger;

/** Registra qué documento ya votó y en qué mesa */
public class VotosRealizadosStore {

    private static final File FILE =
            new File("estacion/src/main/resources/votos_realizados.json");

    private final Map<String, Integer> votos = new HashMap<>();   // documento → mesaId
    private final Gson   gson = new GsonBuilder().setPrettyPrinting().create();
    private final Logger log  = AppLogger.get();

    public VotosRealizadosStore() {
        cargar();
    }

    private void cargar() {
        if (!FILE.exists()) return;           // si no existe, no se crea nada aún
        try (Reader r = new FileReader(FILE)) {
            Type type = new TypeToken<Map<String,Integer>>(){}.getType();
            Map<String,Integer> datos = gson.fromJson(r, type);
            if (datos != null) votos.putAll(datos);
            log.info("Cargados " + votos.size() + " votos realizados.");
        } catch (IOException e) {
            log.severe("Error al cargar votos realizados: " + e.getMessage());
        }
    }

    private void guardar() {
        try {
            FILE.getParentFile().mkdirs();    // asegura directorio
            try (Writer w = new FileWriter(FILE)) {
                gson.toJson(votos, w);
            }
            log.info("Guardados " + votos.size() + " votos realizados.");
        } catch (IOException e) {
            log.severe("Error al guardar votos realizados: " + e.getMessage());
        }
    }

    /** true → el ciudadano YA votó */
    public synchronized boolean yaVoto(String documento) {
        return votos.containsKey(documento);
    }

    /** registra el voto y lo persiste */
    public synchronized void registrar(String documento, int mesaId) {
        votos.put(documento, mesaId);
        guardar();
    }
}
