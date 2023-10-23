package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;



public class RestApiClient {

    public Restaurant[] getRestaurants() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(new URL(RestApiUrl.RESTAURANTS_URL), Restaurant[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public NamedRegion[] getNoFlyZones() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(new URL(RestApiUrl.NO_FLY_ZONE_URL), NamedRegion[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public NamedRegion getCentralArea() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(new URL(RestApiUrl.CENTRAL_AREA_URL), NamedRegion.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Order[] getAllOrders() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(new URL(RestApiUrl.ORDERS_URL), Order[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Order[] getOrderByDate(String date) {
        //Validate date provided - might be best to do this elsewhere?
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new RuntimeException(e);
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String fullUrl = RestApiUrl.ORDERS_URL + '/' + date;
        try {
            return mapper.readValue(new URL(fullUrl), Order[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
