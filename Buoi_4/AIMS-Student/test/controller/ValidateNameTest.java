package controller;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ValidateNameTest {
	//Bui Hoang Long - 20183943
		private PlaceOrderController placeOrderController;
		
		@BeforeEach
		void setUp() throws Exception {
			placeOrderController = new PlaceOrderController();
		}

		@ParameterizedTest
		@CsvSource({
			"nguyen lm,true",
			"longbh183943,false",
			"&#long,false",
			",false"
		})
		public void test(String name, boolean expected) {
			//when
			boolean isValid = placeOrderController.validateName(name);
			
			//then
			assertEquals(expected, isValid);
		}
}
