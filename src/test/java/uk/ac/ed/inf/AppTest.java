package uk.ac.ed.inf;

import junit.framework.TestCase;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.*;

import java.time.DayOfWeek;
import java.time.LocalDate;


public class AppTest 
    extends TestCase
{
    // *************** LngLatHandler Tests ***************
    public void testDistanceTo1() {
        LngLatHandler lngLatHandler = new LngLatHandler();
        LngLat startPos = new LngLat(20, 20);
        LngLat endPos = new LngLat(20, 40);
        double distance = lngLatHandler.distanceTo(startPos, endPos);
        assertEquals(20.0, distance);
    }

    public void testDistanceTo2() {
        LngLatHandler lngLatHandler = new LngLatHandler();
        LngLat startPos = new LngLat(20, 20);
        LngLat endPos = new LngLat(20, -20);
        double distance = lngLatHandler.distanceTo(startPos, endPos);
        assertEquals(40.0, distance);
    }

    public void testDistanceTo3() {
        LngLatHandler lngLatHandler = new LngLatHandler();
        LngLat startPos = new LngLat(0, 0);
        LngLat endPos = new LngLat(50, 120);
        double distance = lngLatHandler.distanceTo(startPos, endPos);
        assertEquals(130.0, distance);
    }

    public void testIsCloseTo1() {
        LngLatHandler lngLatHandler = new LngLatHandler();
        LngLat startPos = new LngLat(0, 0);
        LngLat otherPos = new LngLat(0, 0.00015);
        boolean isCloseTo = lngLatHandler.isCloseTo(startPos, otherPos);
        assertFalse(isCloseTo);
    }

    public void testIsCloseTo2() {
        LngLatHandler lngLatHandler = new LngLatHandler();
        LngLat startPos = new LngLat(0, 0);
        LngLat otherPos = new LngLat(0, 0.00014);
        boolean isCloseTo = lngLatHandler.isCloseTo(startPos, otherPos);
        assertTrue(isCloseTo);
    }

    public void testIsCloseTo3() {
        LngLatHandler lngLatHandler = new LngLatHandler();
        LngLat startPos = new LngLat(0, 0);
        LngLat otherPos = new LngLat(-0.00001, 0.00005);
        boolean isCloseTo = lngLatHandler.isCloseTo(startPos, otherPos);
        assertTrue(isCloseTo);
    }


    public void testIsInRegion1() {
        LngLatHandler lngLatHandler = new LngLatHandler();

        LngLat vertex1 = new LngLat(-3.192473,55.946233);
        LngLat vertex2 = new LngLat(-3.184319,55.946233);
        LngLat vertex3 = new LngLat(-3.184319,55.942617);
        LngLat vertex4 = new LngLat(-3.192473,55.942617);
        LngLat[] vertices = new LngLat[] {vertex1, vertex2, vertex3, vertex4};

        NamedRegion testRegion = new NamedRegion("Test Region", vertices);
        LngLat testPoint = new LngLat(-3.19240, 55.946);

        boolean isInRegion = lngLatHandler.isInRegion(testPoint, testRegion);
        assertTrue(isInRegion);
    }

    public void testIsInRegion2() {
        LngLatHandler lngLatHandler = new LngLatHandler();

        LngLat vertex1 = new LngLat(-3.192473,55.946233);
        LngLat vertex2 = new LngLat(-3.184319,55.946233);
        LngLat vertex3 = new LngLat(-3.184319,55.942617);
        LngLat vertex4 = new LngLat(-3.192473,55.942617);
        LngLat[] vertices = new LngLat[] {vertex1, vertex2, vertex3, vertex4};

        NamedRegion testRegion = new NamedRegion("Test Region", vertices);
        LngLat testPoint = new LngLat(-3.192473,55.946233);

        boolean isInRegion = lngLatHandler.isInRegion(testPoint, testRegion);
        assertTrue(isInRegion);
    }

    public void testIsInRegion3() {
        LngLatHandler lngLatHandler = new LngLatHandler();

        LngLat vertex1 = new LngLat(-3.192473,55.946233);
        LngLat vertex2 = new LngLat(-3.184319,55.946233);
        LngLat vertex3 = new LngLat(-3.184319,55.942617);
        LngLat vertex4 = new LngLat(-3.192473,55.942617);
        LngLat[] vertices = new LngLat[] {vertex1, vertex2, vertex3, vertex4};

        NamedRegion testRegion = new NamedRegion("Test Region", vertices);
        LngLat testPoint = new LngLat(-3.192473,55.943);

        boolean isInRegion = lngLatHandler.isInRegion(testPoint, testRegion);
        assertTrue(isInRegion);
    }

    public void testIsInRegion4() {
        LngLatHandler lngLatHandler = new LngLatHandler();

        LngLat vertex1 = new LngLat(-3.192473,55.946233);
        LngLat vertex2 = new LngLat(-3.184319,55.946233);
        LngLat vertex3 = new LngLat(-3.184319,55.942617);
        LngLat vertex4 = new LngLat(-3.192473,55.942617);
        LngLat[] vertices = new LngLat[] {vertex1, vertex2, vertex3, vertex4};

        NamedRegion testRegion = new NamedRegion("Test Region", vertices);
        LngLat testPoint = new LngLat(-3.192474,55.946233);

        boolean isInRegion = lngLatHandler.isInRegion(testPoint, testRegion);
        assertFalse(isInRegion);
    }

    // *************** OrderValidator Tests ***************

    public void testCreditCardNumberTooShort() {
        Order order = getValidOrder();
        Restaurant[] restaurants = new Restaurant[] {getValidRestaurant()};
        OrderValidator orderValidator = new OrderValidator();

        //Make credit card number invalid
        order.setCreditCardInformation(new CreditCardInformation("1111", "10/25", "123"));
        //Test
        order = orderValidator.validateOrder(order, restaurants);
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, order.getOrderValidationCode());
    }

    public void testCreditCardNumberTooLong() {
        Order order = getValidOrder();
        Restaurant[] restaurants = new Restaurant[] {getValidRestaurant()};
        OrderValidator orderValidator = new OrderValidator();

        //Make credit card number invalid
        order.setCreditCardInformation(new CreditCardInformation("11111111111111111", "10/25", "123"));
        //Test
        order = orderValidator.validateOrder(order, restaurants);
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, order.getOrderValidationCode());
    }

    public void testCreditCardNumberContainsNonDigit() {
        Order order = getValidOrder();
        Restaurant[] restaurants = new Restaurant[] {getValidRestaurant()};
        OrderValidator orderValidator = new OrderValidator();

        //Make credit card number invalid
        order.setCreditCardInformation(new CreditCardInformation("111111111a111111", "10/25", "123"));
        //Test
        order = orderValidator.validateOrder(order, restaurants);
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, order.getOrderValidationCode());
    }

    public void testCreditCardExpired() {
        Order order = getValidOrder();
        Restaurant[] restaurants = new Restaurant[] {getValidRestaurant()};
        OrderValidator orderValidator = new OrderValidator();

        //Make expiry date in the past
        order.setCreditCardInformation(new CreditCardInformation("1111111111111111", "10/20", "123"));
        //Test
        order = orderValidator.validateOrder(order, restaurants);
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, order.getOrderValidationCode());
    }

    public void testCreditCardCvvTooShort() {
        Order order = getValidOrder();
        Restaurant[] restaurants = new Restaurant[] {getValidRestaurant()};
        OrderValidator orderValidator = new OrderValidator();

        //Make CVV invalid
        order.setCreditCardInformation(new CreditCardInformation("1111111111111111", "10/25", "12"));
        //Test
        order = orderValidator.validateOrder(order, restaurants);
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.CVV_INVALID, order.getOrderValidationCode());
    }

    public void testCreditCardCvvTooLong() {
        Order order = getValidOrder();
        Restaurant[] restaurants = new Restaurant[] {getValidRestaurant()};
        OrderValidator orderValidator = new OrderValidator();

        //Make CVV invalid
        order.setCreditCardInformation(new CreditCardInformation("1111111111111111", "10/25", "1234"));
        //Test
        order = orderValidator.validateOrder(order, restaurants);
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.CVV_INVALID, order.getOrderValidationCode());
    }

    public void testCreditCardCvvContainsNonDigit() {
        Order order = getValidOrder();
        Restaurant[] restaurants = new Restaurant[] {getValidRestaurant()};
        OrderValidator orderValidator = new OrderValidator();

        //Make CVV invalid
        order.setCreditCardInformation(new CreditCardInformation("1111111111111111", "10/25", "1!3"));
        //Test
        order = orderValidator.validateOrder(order, restaurants);
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.CVV_INVALID, order.getOrderValidationCode());
    }

    public void testOrderTotalIncorrect() {
        Order order = getValidOrder();
        Restaurant[] restaurants = new Restaurant[] {getValidRestaurant()};
        OrderValidator orderValidator = new OrderValidator();

        //Make order total invalid (total should be 2500)
        order.setPriceTotalInPence(2000);
        //Test
        order = orderValidator.validateOrder(order, restaurants);
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.TOTAL_INCORRECT, order.getOrderValidationCode());
    }

    public void testPizzaIsNotDefined() {
        Order order = getValidOrder();
        Restaurant[] restaurants = new Restaurant[] {getValidRestaurant()};
        OrderValidator orderValidator = new OrderValidator();

        //Add an invalid pizza to the order
        Pizza newPizza = new Pizza("UndefinedPizza", 0);
        Pizza[] definedPizzas = getValidPizzas1();
        order.setPizzasInOrder(new Pizza[] {definedPizzas[0], definedPizzas[1], definedPizzas[2], newPizza});
        //Test
        order = orderValidator.validateOrder(order, restaurants);
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED, order.getOrderValidationCode());
    }

    public void testMaxPizzaCountExceeded() {
        Order order = getValidOrder();
        Restaurant[] restaurants = new Restaurant[] {getValidRestaurant()};
        OrderValidator orderValidator = new OrderValidator();

        //Add more than 5 pizzas to an order
        Pizza[] validPizzas = getValidPizzas1();
        Pizza[] newOrder = new Pizza[] {validPizzas[0], validPizzas[1], validPizzas[2], validPizzas[0], validPizzas[1]};
        order.setPizzasInOrder(newOrder);
        //Also need to update order total so this doesn't fail order total test (just calculated from my prices further down)
        order.setPriceTotalInPence(4000);
        //Test
        order = orderValidator.validateOrder(order, restaurants);
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED, order.getOrderValidationCode());
    }

    public void testPizzasFromMultipleRestaurants() {
        Order order = getValidOrder();
        OrderValidator orderValidator = new OrderValidator();

        //Define two restuarants
        DayOfWeek[] openDays = new DayOfWeek[] {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY};
        Restaurant restaurantOne = new Restaurant("Restaurant One", new LngLat(50, 50), openDays, getValidPizzas1());
        Restaurant restaurantTwo = new Restaurant("Restaurant Two", new LngLat(100, 50), openDays, getValidPizzas2());
        Restaurant[] restaurants = new Restaurant[] {restaurantOne, restaurantTwo};

        //Create an order with pizzas from both restaurants
        Pizza[] restaurantOnePizzas = restaurantOne.menu();
        Pizza[] restaurantTwoPizzas = restaurantTwo.menu();
        order.setPizzasInOrder(new Pizza[] {restaurantOnePizzas[0], restaurantOnePizzas[1], restaurantTwoPizzas[0]});
        //Update price total too (800 + 700 + 800 + 100 = 2400)
        order.setPriceTotalInPence(2400);
        //Test
        order = orderValidator.validateOrder(order, restaurants);
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, order.getOrderValidationCode());
    }

    public void testRestaurantClosed() {
        Order order = getValidOrder();
        Restaurant[] restaurants = new Restaurant[] {getValidRestaurant()};
        OrderValidator orderValidator = new OrderValidator();

        //Change order to date when restaurant is closed (e.g. last saturday)
        order.setOrderDate(LocalDate.of(2023, 10, 7));
        //Test
        order = orderValidator.validateOrder(order, restaurants);
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        assertEquals(OrderValidationCode.RESTAURANT_CLOSED, order.getOrderValidationCode());
    }

    public void testValidOrder() {
        Order order = getValidOrder();
        Restaurant[] restaurants = new Restaurant[] {getValidRestaurant()};
        OrderValidator orderValidator = new OrderValidator();

        //Test
        order = orderValidator.validateOrder(order, restaurants);
        assertEquals(OrderStatus.VALID_BUT_NOT_DELIVERED, order.getOrderStatus());
        assertEquals(OrderValidationCode.NO_ERROR, order.getOrderValidationCode());
    }

    //Produces a fully valid order to be used in testing
    private Order getValidOrder() {
        String orderNumber = "19514FE0";
        LocalDate orderDate = LocalDate.of(2023, 10, 9);
        OrderStatus orderStatus = OrderStatus.UNDEFINED;
        int orderPriceTotalInPence = 2500;
        Pizza[] pizzasInOrder = getValidPizzas1();
        CreditCardInformation creditCardInformation = new CreditCardInformation("1111111111111111", "10/25", "123");
        return new Order(orderNumber, orderDate, orderPriceTotalInPence, pizzasInOrder, creditCardInformation);
    }

    //Returns a pepperoni, margherita and chicken pizza
    private Pizza[] getValidPizzas1() {
        Pizza pepperoni = new Pizza("Pepperoni", 800);
        Pizza margherita = new Pizza("Margherita", 700);
        Pizza chicken = new Pizza("Chicken", 900);
        return new Pizza[] {pepperoni, margherita, chicken};
    }

    private Pizza[] getValidPizzas2() {
        Pizza vegan = new Pizza("Vegan", 800);
        Pizza vegetarian = new Pizza("Vegetarian", 700);
        Pizza bbq = new Pizza("BBQ", 900);
        return new Pizza[] {vegan, vegetarian, bbq};
    }

    private Restaurant getValidRestaurant() {
        String name = "Rudy's";
        LngLat location = new LngLat(50, 50);
        DayOfWeek[] openingDays = new DayOfWeek[] {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY};
        return new Restaurant(name, location, openingDays, getValidPizzas1());
    }
}
