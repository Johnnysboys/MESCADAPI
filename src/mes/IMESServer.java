package mes;

import dto.OrderINFO;
import scada.ISCADAObserver;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface IMESServer extends Remote{
    void addObserver(ISCADAObserver scada) throws RemoteException;
    void alertPlanted(OrderINFO o) throws RemoteException;
    void alertLost(OrderINFO o,int amountLost ) throws RemoteException;
    void alertHarvest(OrderINFO o) throws RemoteException;
}
