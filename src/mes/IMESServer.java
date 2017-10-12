package mes;

import dto.OrderINFO;
import scada.ISCADAObserver;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IMESServer extends Remote{
    /**
     * Connecting party must register as an observer
     * @param scada
     * @throws RemoteException
     */
    void addObserver(ISCADAObserver scada) throws RemoteException;

    /**
     * Alert to MES that order has been planted.
     * @param o
     * @throws RemoteException
     */
    void alertPlanted(OrderINFO o) throws RemoteException;

    /**
     * Alert discarded always signals that a single quantity has been lost in production.
     * @param o
     * @throws RemoteException
     */
    void alertDiscarded(OrderINFO o) throws RemoteException;

    /**
     * Alert harvest always signals that a single quantity has ben harvested.
     * @param o
     * @throws RemoteException
     */
    void alertHarvest(OrderINFO o) throws RemoteException;
}
