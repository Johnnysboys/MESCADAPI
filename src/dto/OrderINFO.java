package dto;

import java.io.Serializable;

public class OrderINFO implements Serializable {
    private int articleNumber;
    private int quantity;
    private String orderID;

    public OrderINFO(int articleNumber, int quantity, String orderID){
        this.articleNumber=articleNumber;
        this.quantity=quantity;
        this.orderID=orderID;
    }


    public int getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(int articleNumber) {
        this.articleNumber = articleNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }
}
