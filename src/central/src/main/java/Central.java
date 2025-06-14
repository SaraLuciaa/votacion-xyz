public class Central {

    public static void main(String[] args) {
        try {
            Communicator communicator = Util.initialize(args);

            
            String index = communicator.getProperties().getProperty("Central.Index");
            if (index == null || index.isEmpty()) {
                index = "1"; 
            }

            ObjectAdapter adapter = communicator.createObjectAdapter("CentralAdapter");

            RmReceiver receiver = new RmReceiverI();
            adapter.add(receiver, Util.stringToIdentity("RMService-" + index));

            queryStation query = new QueryStationI(communicator);
            adapter.add(query, Util.stringToIdentity("QueryService-" + index));

            adapter.activate();
            System.out.println("Servidor central [" + index + "] en ejecución...");

            communicator.waitForShutdown();

        } catch (Exception e) {
            System.err.println("Error en el servidor central: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
