package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;
import java.time.DayOfWeek;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class OrderValidator implements OrderValidation{

    /**
     * Applies a series of tests to check if the Order is valid
     * Chose not to split this into separate methods since lots of checks share data and computations
     *
     * @param orderToValidate The order we wish to validate
     * @param definedRestaurants The restaurants we are considering
     * @return The order with its order status and validation code updated
     */
    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {

        //Check card number is valid (use length and regex)
        CreditCardInformation cardInfo = orderToValidate.getCreditCardInformation();
        String cardNumber = cardInfo.getCreditCardNumber();
        if(!(cardNumber.length() == 16 && cardNumber.matches("\\d+"))) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
            return orderToValidate;
        }

        //Check expiry date is valid
        //Create formatter for extracting expiry month and year
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth expiryDate = YearMonth.parse(cardInfo.getCreditCardExpiry(), formatter);
        YearMonth currentYearMonth = YearMonth.now();
        if(expiryDate.isBefore(currentYearMonth)) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            return orderToValidate;
        }

        //Check CVV is valid (use length and regex)
        String cvv = cardInfo.getCvv();
        if(!(cvv.length() == 3 && cvv.matches("\\d+"))) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
            return orderToValidate;
        }

        //Check order total is correct, that is the sum of the pizzas and the delivery charge
        int orderSumInPence = 0;
        for (Pizza pizza : orderToValidate.getPizzasInOrder()) {
            orderSumInPence += pizza.priceInPence();
        }
        //Delivery fee
        orderSumInPence += SystemConstants.ORDER_CHARGE_IN_PENCE;
        if(orderSumInPence != orderToValidate.getPriceTotalInPence()) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
            return orderToValidate;
        }

        //Check all pizzas are defined
        //Assume all are defined and then look for a counterexample
        boolean allDefined = true;
        for (Pizza orderPizza : orderToValidate.getPizzasInOrder()) {
            //Assume it is undefined and look until it is found
            boolean thisPizzaDefined = false;
            for (Restaurant restaurant : definedRestaurants) {
                for (Pizza restaurantPizza : restaurant.menu()) {
                    if (orderPizza.name().equals(restaurantPizza.name()) &&
                            orderPizza.priceInPence() == restaurantPizza.priceInPence()) {
                        thisPizzaDefined = true;
                        break;
                    }
                }
            }
            if (!thisPizzaDefined) {
                allDefined = false;
                break;
            }
        }
        if (!allDefined) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
            return orderToValidate;
        }

        //Check pizza count not exceeded
        if(orderToValidate.getPizzasInOrder().length > 4) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
            return orderToValidate;
        }

        //Check all pizzas are from the same restaurant
        Restaurant firstPizzaRestaurant = null;
        if (orderToValidate.getPizzasInOrder().length > 0) {
            //Get restaurant of first pizza
            Pizza firstPizza = orderToValidate.getPizzasInOrder()[0];
            for (Restaurant restaurant : definedRestaurants) {
                for(Pizza pizza : restaurant.menu()) {
                    if (firstPizza.name().equals(pizza.name()) && firstPizza.priceInPence() == pizza.priceInPence()) {
                        firstPizzaRestaurant = restaurant;
                        break;
                    }
                }
            }
            //Can now check that all subsequent pizzas come from this restaurant's menu
            //Assume all pizzas are on the menu and look for a counterexample
            boolean allPizzasOnMenu = true;
            for (int i = 1; i < orderToValidate.getPizzasInOrder().length; i++) {
                //Assume it hasn't been found and search for it
                boolean pizzaFound = false;
                Pizza nextPizzaOnOrder = orderToValidate.getPizzasInOrder()[i];
                for (Pizza pizzaOnMenu : firstPizzaRestaurant.menu()) {
                    if (nextPizzaOnOrder.name().equals(pizzaOnMenu.name()) &&
                            nextPizzaOnOrder.priceInPence() == pizzaOnMenu.priceInPence()) {
                        pizzaFound = true;
                        break;
                    }
                }
                if(!pizzaFound) {
                    allPizzasOnMenu = false;
                    break;
                }
            }
            if(!allPizzasOnMenu) {
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
                orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
                return orderToValidate;
            }
        }

        //Check restaurant is open
        //Assume restaurant is closed and search to see if it is open
        boolean restaurantOpen = false;
        DayOfWeek dayOfOrder = orderToValidate.getOrderDate().getDayOfWeek();
        for (DayOfWeek day : firstPizzaRestaurant.openingDays()) {
            if(day.getValue() == dayOfOrder.getValue()) {
                restaurantOpen = true;
                break;
            }
        }
        if(!restaurantOpen) {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
            return orderToValidate;
        }

        //If this code is reached, then the order is valid and ready for delivery
        orderToValidate.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);
        orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        return orderToValidate;
    }
}
