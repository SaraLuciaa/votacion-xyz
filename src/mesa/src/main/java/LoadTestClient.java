import VotacionXYZ.VoteStationPrx;
import VotacionXYZ.Ciudadano;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

import java.io.FileReader;
import java.util.concurrent.*;
import java.util.*;
import java.time.Duration;
import java.time.Instant;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;

public class LoadTestClient {
    public static void main(String[] args) throws java.lang.Exception {
        final int CANDIDATO_ID = 1;
        final int MAX_VOTOS = 2000;

        // Leer ciudadanos.json
        Gson gson = new Gson();
        List<Ciudadano> ciudadanos = gson.fromJson(
                new FileReader("src/estacion/src/main/resources/ciudadanos.json"),
                new TypeToken<List<Ciudadano>>(){}.getType()
        );

        if (ciudadanos.isEmpty()) {
            System.out.println("No hay ciudadanos cargados.");
            return;
        }

        try (Communicator communicator = Util.initialize(args, "mesa.cfg")) {
            ObjectPrx base = communicator.stringToProxy(
                communicator.getProperties().getProperty("VoteStation"));
            VoteStationPrx voteStation = VoteStationPrx.checkedCast(base);

            if (voteStation == null) {
                System.err.println("Proxy inválido para VoteStation.");
                return;
            }

            ExecutorService pool = Executors.newFixedThreadPool(100);
            List<Future<Long>> latencias = new ArrayList<>();
            List<Integer> resultados = Collections.synchronizedList(new ArrayList<>());

            Instant inicio = Instant.now();

            for (int i = 0; i < Math.min(ciudadanos.size(), MAX_VOTOS); i++) {
                Ciudadano c = ciudadanos.get(i);
                final String documento = c.documento;

                latencias.add(pool.submit(() -> {
                    Instant t1 = Instant.now();
                    int code = voteStation.vote(documento, CANDIDATO_ID);
                    Instant t2 = Instant.now();
                    resultados.add(code);
                    return Duration.between(t1, t2).toMillis();
                }));
            }

            pool.shutdown();
            pool.awaitTermination(60, TimeUnit.SECONDS);

            Instant fin = Instant.now();
            long duracionTotalMs = Duration.between(inicio, fin).toMillis();

            long sumaLatencias = 0;
            int exitosos = 0;
            for (int i = 0; i < resultados.size(); i++) {
                if (resultados.get(i) == 0) exitosos++;
                sumaLatencias += latencias.get(i).get();
            }

            double rps = (exitosos * 1000.0) / duracionTotalMs;
            double latenciaPromedio = sumaLatencias * 1.0 / resultados.size();

            System.out.println("======= RESULTADOS DE PRUEBA =======");
            System.out.println("Votos enviados: " + resultados.size());
            System.out.println("Votos exitosos (ACK = 0): " + exitosos);
            System.out.println("Latencia promedio: " + latenciaPromedio + " ms");
            System.out.println("Tiempo total: " + duracionTotalMs + " ms");
            System.out.println("Throughput real: " + rps + " votos/segundo");
        }
    }
}
