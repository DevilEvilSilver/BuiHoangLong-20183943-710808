package utils;

import java.util.Random;
import java.util.logging.Logger;

import controller.PlaceOrderController;
import entity.order.Order;
/**
 * 
 * @author LongBH_20183943
 *
 */
public class RushShippingFee implements ShippingFeeCaculator {
	
	/**
     * Just for logging purpose
     */
	private static Logger LOGGER = utils.Utils.getLogger(PlaceOrderController.class.getName());

	/**
     * This method calculates the shipping fees of order
     * @param order
     * @return shippingFee
     */
    public int calculateShippingFee(Order order){
    	 int fees;
    	 // New shipping fee calculator
         if (order.getAmount() > 100) {
         	fees = 0;
         }
         else {
         	Random rand = new Random();
             fees = (int)( ( (rand.nextFloat()*10)/100 ) * order.getAmount() );
         }
         // Rush order fee for each media
         for (int i = 0; i < order.getlstOrderMedia().size(); i++) {
			fees += 10;
         }
         order.setShippingFees(fees);
         LOGGER.info("Order Amount: " + order.getAmount() + " -- Shipping Fees: " + fees);
         return fees;
    }
	
}
