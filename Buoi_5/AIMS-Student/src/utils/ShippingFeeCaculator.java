package utils;

import entity.order.Order;
/**
 * 
 * @author LongBH_20183943
 *
 */
public interface ShippingFeeCaculator {
	
	/**
     * This method calculates the shipping fees of order
     * @param order
     * @return shippingFee
     */
    public int calculateShippingFee(Order order);
	
}
