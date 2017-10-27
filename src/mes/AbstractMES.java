package mes;

import dto.OrderINFO;
import scada.ISCADAObserver;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public abstract class AbstractMES extends UnicastRemoteObject implements IMESServer{
    protected AbstractMES() throws RemoteException {
        super();
    }

    private List<ISCADAObserver> observers = new ArrayList<>();

    @Override
    public void addObserver(ISCADAObserver scada) throws RemoteException {
        observers.add(scada);
    }
    public void executeOrder(OrderINFO orderINFO) throws ExceedsCapacityException, RemoteException {
        if(!hasCapacity(orderINFO))
            throw new ExceedsCapacityException("Order quantity to large, accumulated capacity is below.");
        boolean capacityMatch=false;
        for (ISCADAObserver wo: observers) {
            if(wo.getCapacity() >= orderINFO.getQuantity()){
                try {
                    capacityMatch=true;
                    wo.postOrder(orderINFO);
                    break;
                } catch (RemoteException e) {
                    System.err.println("Error posting order to SCADA. Deleting observer.");
                    observers.remove(wo);
                    capacityMatch=false;
                    executeOrder(orderINFO);
                }
            }
        }
        if(!capacityMatch)
            dividedExecution(orderINFO);
    }
    public boolean hasCapacity(OrderINFO orderINFO) throws RemoteException {
        int accumulated=0;
        for(ISCADAObserver wo:observers){
            accumulated+=wo.getCapacity();
        }
        if (accumulated>=orderINFO.getQuantity())
            return true;
        else
            return false;
    }
    private void dividedExecution(OrderINFO orderINFO) throws ExceedsCapacityException, RemoteException {
        for(ISCADAObserver wo:observers){
            try {
                if((orderINFO.getQuantity()-wo.getCapacity())<0)
                    wo.postOrder(new OrderINFO(orderINFO.getArticleNumber(),orderINFO.getQuantity(), orderINFO.getOrderID()));
                else {
                    orderINFO.setQuantity(orderINFO.getQuantity()-wo.getCapacity());
                    wo.postOrder(new OrderINFO(orderINFO.getArticleNumber(), wo.getCapacity(), orderINFO.getOrderID()));
                }
            } catch (RemoteException e) {
                System.err.println("Error posting order to SCADA, deleting observer.");
                observers.remove(wo);
                executeOrder(orderINFO);
            }
        }
    }

}
