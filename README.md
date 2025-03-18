# ReadME for STD_FX_Assessment

For simplicity I made use of a runtime solution, so that when you run the application there is consistency in the data I am using.

For the orderbook I made use of 2 HashMaps, as they gave me the ability to store a BigDecimal as a key with a Queue, as FIFO is important, as the value.
I also made use of a global OrderIdMap, which game me a quick and easy way to find an order as the Price (key) is linked to an Order.

For ease of use, I made use of a combination of LinkedLists and ArrayLists, to store the difference between buy orders and sell orders.

## Code setup:
### OrderService:
Methods:
- getAllOrders()
- initialiseOrders()
- handleOrders(List<Orders> ordersToHandle)
  
Purpose:
- These methods are there to setup the order book, the orderbook contains 10 orders that are predefined.
- initialiseOrders is a check method. This methods sorts valid from invalid orders and ensures that the orderbook contains only valid orders.
- handleOrdes(...) further splits all the valid orders in the respective buy- and sell orders lists.
- Note that it is also clear straight afterwards, as new orders also make use of this method, hence keeping it will create duplicate entries in the orderbook

Methods:
- setupOrderBook(HashMap<BigDecimal, Queue<Orders>> orderTable, List<Orders> orderDetails)
- addSellOrder(HashMap<BigDecimal, Queue<Orders>> orderTable, List<Orders> orderDetails)
- addBuyOrder(HashMap<BigDecimal, Queue<Orders>> orderTable, List<Orders> orderDetails)
- addOrderNoCheck(HashMap<BigDecimal, Queue<Orders>> orderTable, List<Orders> orderDetails)

Purpose:
- setupOrderBook(...) is used to add a new order to the order book. The initial process is to go through each order being added and to attempt to match the order to an existing order in the order book, matching will be explained later.
- If the match isn't a success, a new order is added to the orderbook hashmap
- addBuyOrder(...) and addSellOrder(...) call the setupOrderBook function, purely for separation of concerns
- addOrderNoCheck(...) if a new order is a partialfill, then this method will be called, and the partial order will be added to the order book

Methods:
- getOrderById(int id)
- removeOrderById(int id)
- modifyOrderById(int id, int quantity)

Purpose:
- getOrderById(...) is for retrieving an order, makes use of the orderIdMap for speed
- removeOrderById(...) removes order form queue as well as from orderIdMap.
- If an order is the only one in the key, the entire key-value pair is removed from the hash map
- modifyOrderById(...) updates quantity of order as well as lastUpdated (added attribute) and removes it from queue. After it's been removed, it appends it to the queue again as it loses priority

Methods:
- printOrderBook(OrderBook orderBookToPrint)
- cleanUpOrderBook()

Purpose:
- printOrderBook(...) print order book in predefined format
- Format e.g.:
KEY: 1  --> 1: 3, 2:10
Key: Price --> orderId: orderQuantity
- cleanUpOrderBook() removes empty key-value pairs


### MatchingEngineService:

Methods:
- fulfillOrder(Orders incomingOrder)
- matchingOrder(Orders incomingOrder)

Purpose:
- matchingOrder(...):
Returns the queue in the orderbook hashmap, if no queue exists, it just adds the order to the orderbook
- fulfillOrder(...):
Makes use of matchingOrder(...) to determine if the order needs to be matched or added.
For this explanation, it needs to be matched!

A difference is calculated to determine if the order is a full match or a partial match or a combination of the 2.
SCENARIOS:
- If the order is a full match, the order, both incomingOrder and the first order in the queue is marked as Completed(added attribute)
- If the order is a partial match (incoming order has fewer quantity than the existing one), just the incoming order is marked as Completed and the existing orders quantity is updated to the remaining difference
- If the order is a partial match (incoming order has more quantity than the existing one), the loop will complete until either of the following scenarios:
1) The remaining quantity from the incoming order is 0 OR
2) The remaining quanityt from the incoming order is > 0 and there are no items left in the queue, then the remaining quantity and incoming order gets added to the orderbook.

All 3 these scenarios (marked by 'SCENARIOS') are handled in helper methods:
- matchEquals(Orders incomingOrder, Orders targetOrder, int quantityRemaining)
- matchLessThan(Orders incomingOrder, Orders targetOrder)
- matchMoreThan(Orders targetOrder, int quantityRemaining)

At the end of the matching process, the following method is called:
- removeCompletedOrders(Queue<Orders> targetQueue)
This method removes completed orders from the orderbook to ensure an updated representation of the order book.
It is split away from the fulfillOrders(...) method as updating a hashMap in realtime will cause a ConcurrentModificationException.


## Summary of DataStructures:
Maps:
Map
HashMap

Queues & Lists:
Queue
List
LinkedList
ArrayList

Sets:
Set
