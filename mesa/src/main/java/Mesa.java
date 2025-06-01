import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Util;

import VotacionXYZ.*;

public class Mesa {

    public static void main(String[] args) {
        List<String> params = new ArrayList<>();
        try (Communicator communicator = Util.initialize(args, "mesaVotacion.cfg", params)) {
            
            //System.out.println("[MESA] Inicializando communicator con mesaVotacion.cfg");
            
            // Verificar propiedades cargadas
            //System.out.println("[MESA] Propiedades cargadas:");
            //System.out.println("  AckService.Proxy: " + communicator.getProperties().getProperty("AckService.Proxy"));
            //System.out.println("  RMSender.Proxy: " + communicator.getProperties().getProperty("RMSender.Proxy"));
            //System.out.println("  RMService.Proxy: " + communicator.getProperties().getProperty("RMService.Proxy"));
            
            // Crear proxy ACK con validación
            //System.out.println("[MESA] Creando proxy AckService...");
            AckServicePrx ackProxy = null;
            try {
                ackProxy = AckServicePrx.checkedCast(
                    communicator.propertyToProxy("AckService.Proxy"));
                
                if (ackProxy == null) {
                    System.err.println("[MESA] ERROR: No se pudo crear AckServicePrx - proxy es null");
                    System.err.println("[MESA] Verifique que el servicio 'reliable' esté ejecutándose");
                    System.exit(1);
                }
                
                System.out.println("[MESA] Proxy ACK creado exitosamente: " + ackProxy.ice_getIdentity().name);
                
            } catch (Exception e) {
                System.err.println("[MESA] ERROR al crear AckServicePrx: " + e.getMessage());
                System.err.println("[MESA] Verifique que:");
                System.err.println("  1. El servicio 'reliable' esté ejecutándose");
                System.err.println("  2. La configuración AckService.Proxy sea correcta");
                e.printStackTrace();
                System.exit(1);
            }

            // Crear proxy RMSender con validación
            //System.out.println("[MESA] Creando proxy RMSender...");
            RmSenderPrx sender = null;
            try {
                sender = RmSenderPrx.checkedCast(
                    communicator.propertyToProxy("RMSender.Proxy"));
                
                if (sender == null) {
                    System.err.println("[MESA] ERROR: No se pudo crear RmSenderPrx - proxy es null");
                    System.err.println("[MESA] Verifique que el servicio 'reliable' esté ejecutándose");
                    System.exit(1);
                }
                
                System.out.println("[MESA] Proxy RMSender creado exitosamente");
                
            } catch (Exception e) {
                System.err.println("[MESA] ERROR al crear RmSenderPrx: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }

            // Crear proxy RMReceiver con validación
            System.out.println("[MESA] Creando proxy RMService...");
            RmReceiverPrx receiver = null;
            try {
                receiver = RmReceiverPrx.uncheckedCast(
                    communicator.propertyToProxy("RMService.Proxy"));
                
                if (receiver == null) {
                    System.err.println("[MESA] ERROR: No se pudo crear RmReceiverPrx - proxy es null");
                    System.err.println("[MESA] Verifique que el servicio 'estacion' esté ejecutándose");
                    System.exit(1);
                }
                
                System.out.println("[MESA] Proxy RMService creado exitosamente");
                
            } catch (Exception e) {
                System.err.println("[MESA] ERROR al crear RmReceiverPrx: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }

            // Configurar conexión
            //System.out.println("[MESA] Configurando conexión sender -> receiver");
            try {
                sender.setServerProxy(receiver);
                System.out.println("[MESA] Conexión configurada exitosamente");
            } catch (Exception e) {
                System.err.println("[MESA] ERROR al configurar conexión: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
            
            // Inicializar servicio de votación
            System.out.println("[MESA] Inicializando servicio de votación...");
            Votacion service = new Votacion(sender, ackProxy);
            
            //System.out.println("[MESA] ¡Sistema listo! Iniciando interfaz de votación...");
            start(service);
        
            communicator.waitForShutdown();
            
        } catch (Exception e) {
            System.err.println("[MESA] Error general en la mesa: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void start(Votacion service) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    SISTEMA DE VOTACIÓN ELECTRÓNICA");
        System.out.println("=".repeat(50));
        System.out.println("Mesa de votación lista y operativa");
        System.out.println("Siga las instrucciones para emitir su voto");

        boolean votando = true;
        Scanner scanner = new Scanner(System.in);

        while (votando) {
            try {
                System.out.println("\n" + "=".repeat(30));
                System.out.println("   LISTA DE CANDIDATOS");
                System.out.println("=".repeat(30));
                
                String[] lista = service.listarCandidatos(null);
                for (String item : lista) {
                    System.out.println("  " + item);
                }
                
                System.out.println("\nOpciones:");
                System.out.println("Ingrese el número del candidato (1-" + lista.length + ")");
                System.out.println("Ingrese 0 para salir");
                System.out.print("\nSeleccion: ");
                
                int numero = scanner.nextInt();
                
                if (numero == 0) {
                    System.out.println("\n¡Gracias por usar el sistema de votación!");
                    votando = false;
                    continue;
                }
                
                if (numero < 1 || numero > lista.length) {
                    System.out.println(" Numero invalido. Seleccione un candidato de la lista.");
                    continue;
                }
                
                System.out.println("\n¿Confirma su voto por el candidato " + numero + "? (s/n): ");
                String respuesta = scanner.next();
                
                if (respuesta.equalsIgnoreCase("s") || respuesta.equalsIgnoreCase("si")) {
                    try {
                        service.registrarVoto(numero);
                        System.out.println("¡Voto registrado exitosamente!");
                        System.out.println("Su voto ha sido enviado al sistema central.");
                    } catch (Exception e) {
                        System.err.println("Error al registrar el voto: " + e.getMessage());
                    }
                } else {
                    System.out.println("Voto cancelado.");
                }
                
            } catch (Exception e) {
                System.err.println("Error en la interfaz de votación: " + e.getMessage());
                scanner.nextLine(); 
            }
        }
        scanner.close();
    }
}
