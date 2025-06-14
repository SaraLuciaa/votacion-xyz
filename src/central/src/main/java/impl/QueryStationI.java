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
        return dataManager.obtenerInfoCompletaPorCedula(document);
    }
}