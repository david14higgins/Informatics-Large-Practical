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

    private final String baseUrl;
    private final String date;

    public RestApiClient(String[] args) {
        if (args.length < 2){
            java.lang.System.err.println("Date and base URL must be provided");
            java.lang.System.exit(1);
        }

        String inputDate = args[0];
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            var localDate = LocalDate.parse(inputDate, formatter);
        } catch (DateTimeParseException e) {
            throw new RuntimeException(e);
        }
        this.date = inputDate;

        var baseUrl = args[1];
        if (baseUrl.endsWith("/") == false){
            baseUrl += "/";
        }

        try {
            var temp = new URL(baseUrl);
        } catch (Exception x) {
            java.lang.System.err.println("The URL is invalid: " + x);
            java.lang.System.exit(2);
        }
        this.baseUrl = baseUrl;
    }

    public Restaurant[] getRestaurants() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(new URL(baseUrl + RestApiUrl.RESTAURANTS_URL), Restaurant[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public NamedRegion[] getNoFlyZones() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(new URL(baseUrl + RestApiUrl.NO_FLY_ZONE_URL), NamedRegion[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public NamedRegion getCentralArea() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(new URL(baseUrl + RestApiUrl.CENTRAL_AREA_URL), NamedRegion.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Order[] getAllOrders() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(new URL(baseUrl + RestApiUrl.ORDERS_URL), Order[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Order[] getOrderByDate() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(new URL(baseUrl + RestApiUrl.ORDERS_URL + '/' + date), Order[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
