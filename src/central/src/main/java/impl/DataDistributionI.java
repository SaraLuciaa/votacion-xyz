package impl;

import VotacionXYZ.DataDistribution;
import com.zeroc.Ice.Current;

public class DataDistributionI implements DataDistribution {

    @Override  
    public void sendData(String mesaId, Current current) {
        System.out.println("Distribuyendo datos de la mesa: " + mesaId);
        // Aquí puedes agregar la lógica de difusión de datos
    }
}
