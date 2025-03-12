package Services;

import com.Entities.Orders;
import com.Entities.Side;

import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.List;
import java.util.Queue;

public class OrderService {

    public void addBuyOrder(Hashtable<BigDecimal, Queue<Orders>> orderTable, List<Orders> orderDetails){
        // Determine the order type:
        for (Orders order : orderDetails) {
                // TODO: Buy orders functionality goes here
            /* Key for hashtable = price,
            Reason - different orders = same price.
            Check if it exists, if not, crashout */
                if(order.Price.compareTo(BigDecimal.ZERO) > 0) {
                    System.out.println("The order you are trying to place doesn't contain a price");
                }

                // Determine whether to add a new item in hashtable
                // TODO: YOU ARE HERE
                if(orderTable.containsKey(order.Price)){
                    // TODO Add order to Queue
                    orderTable.get(order.Price).add(order);
                }else{
                    // TODO Add price to hashtable and order to Queue
                    // orderTable.put(order.Price, order);
                }
        }
    }

    public void addSellOrder(Hashtable<BigDecimal, Queue<Orders>> orderTable, List<Orders> orderDetails){
        for (Orders order : orderDetails) {
            if(order.Price.compareTo(BigDecimal.ZERO) > 0) {
                System.out.println("The order you are trying to place doesn't contain a price");
            }
            if(orderTable.containsKey(order.Price)){
                orderTable.get(order.Price).add(order);
            }
            else{

            }
        }
    }
}
