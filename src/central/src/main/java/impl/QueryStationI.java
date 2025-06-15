package impl;

import com.zeroc.Ice.Current;
import com.zeroc.Ice.Communicator;

import VotacionXYZ.queryStation;
import utils.DataManager;

public class QueryStationI implements queryStation {

    private final DataManager dataManager;

    public QueryStationI(Communicator communicator) {
        this.dataManager = DataManager.getInstance(communicator);
    }

    @Override
    public String query(String document, Current current) {
        String adapterName = current.adapter.toString();
        System.out.println("[" + adapterName + "] Atendiendo consulta de documento: " + document);
        System.out.println("tinitn");
        return dataManager.obtenerInfoCompletaPorCedula(document);
    }
}