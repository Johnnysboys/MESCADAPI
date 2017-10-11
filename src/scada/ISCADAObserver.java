package scada;

import dto.OrderINFO;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ISCADAObserver extends Remote{
    void postOrder(OrderINFO o) throws RemoteException;
}
