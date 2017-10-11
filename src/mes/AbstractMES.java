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
    private class WrappedObserver{
        private int capacity;
        private ISCADAObserver so;
        public WrappedObserver(ISCADAObserver so){
            this.so=so;
            setCapacity(RMI_Constants.SCADA_CAPACITY);
        }
        public int getCapacity() {
            return capacity;
        }
        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }
        public void postOrder(OrderINFO orderINFO) throws RemoteException{
            this.so.postOrder(orderINFO);
            capacity-=orderINFO.getQuantity();
        }
    }
    private List<WrappedObserver> observers = new ArrayList<>();

    @Override
    public void addObserver(ISCADAObserver scada) throws RemoteException {
        observers.add(new WrappedObserver(scada));
    }
    public void executeOrder(OrderINFO orderINFO) throws ExceedsCapacityException{
        if(hasCapacity(orderINFO))
            throw new ExceedsCapacityException("Order quantity to large, accumulated capacity is below.");
        boolean capacityMatch=false;
        for (WrappedObserver wo: observers) {
            if(wo.getCapacity()>=orderINFO.getQuantity()){
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
    public boolean hasCapacity(OrderINFO orderINFO){
        int accumulated=0;
        for(WrappedObserver wo:observers){
            accumulated+=wo.getCapacity();
        }
        if (accumulated>=orderINFO.getQuantity())
            return true;
        else
            return false;
    }
    private void dividedExecution(OrderINFO orderINFO) throws ExceedsCapacityException {
        for(WrappedObserver wo:observers){
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
