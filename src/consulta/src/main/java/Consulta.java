import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

import VotacionXYZ.queryStationPrx;

public class Consulta {
    
    private static final int THREADS = 60;
    private static final int REQUESTS_PER_THREAD = 44;
    private static final String DOCUMENT_FILE = "documentos.txt";

    public static void main(String[] args) {
        List<String> documentos;
        try {
            documentos = Files.readAllLines(Paths.get(DOCUMENT_FILE));
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de documentos: " + e.getMessage());
            return;
        }

        if (documentos.isEmpty()) {
            System.err.println("El archivo de documentos está vacío.");
            return;
        } else {
            System.out.println("Se leyeron " + documentos.size() + " documentos:");
            documentos.stream().limit(5).forEach(System.out::println); 
        }


        ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        long[] totalLatency = {0};
        CountDownLatch latch = new CountDownLatch(THREADS);
        Random random = new Random();

        long startTime = System.nanoTime();

        for (int i = 0; i < THREADS; i++) {
            executor.submit(() -> {
                try (Communicator communicator = Util.initialize(new String[0], "consulta.cfg")) {
                    ObjectPrx base = communicator.stringToProxy("QueryService");
                    queryStationPrx query = queryStationPrx.checkedCast(base);

                    if (query == null) {
                        System.err.println("Proxy inválido en un hilo.");
                        latch.countDown();
                        return;
                    }

                    long localLatency = 0;
                    for (int j = 0; j < REQUESTS_PER_THREAD; j++) {
                        String doc = documentos.get(random.nextInt(documentos.size())).trim();
                        try {
                            long t1 = System.nanoTime();
                            query.query(doc);
                            long t2 = System.nanoTime();
                            localLatency += (t2 - t1);
                            successCount.incrementAndGet();
                        } catch (Exception e) {
                            errorCount.incrementAndGet();
                        }
                    }
                    synchronized (totalLatency) {
                        totalLatency[0] += localLatency;
                    }
                } catch (Exception e) {
                    System.err.println("Error en hilo de estación: " + e.getMessage());
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            System.err.println("Interrupción inesperada.");
        }

        executor.shutdown();
        long endTime = System.nanoTime();

        double durationSeconds = (endTime - startTime) / 1_000_000_000.0;
        double throughput = successCount.get() / durationSeconds;
        double avgLatency = (totalLatency[0] / 1_000_000.0) / successCount.get();

        System.out.println("====== RESULTADOS (múltiples estaciones) ======");
        System.out.printf("Consultas exitosas: %d%n", successCount.get());
        System.out.printf("Errores: %d%n", errorCount.get());
        System.out.printf("Duración total: %.2f s%n", durationSeconds);
        System.out.printf("Throughput: %.2f consultas/s%n", throughput);
        System.out.printf("Latencia promedio: %.2f ms%n", avgLatency);
    }
}