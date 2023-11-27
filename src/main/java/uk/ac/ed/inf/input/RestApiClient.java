package uk.ac.ed.inf.input;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.constant.RestApiUrl;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;

public class RestApiClient implements uk.ac.ed.inf.interfaces.RestApiClient {
    private final String baseUrl;
    private final String date;

    /**
     * Constructor for client checks CLI arguments are valid and gracefully terminates program if not
     * @param args Arguments to be validated
     */
    public RestApiClient(String[] args) {
        //Check two arguments passed
        if (args.length < 2){
            java.lang.System.err.println("Date and base URL must be provided");
            java.lang.System.exit(1);
        }

        //Check date is the valid YYYY-MM-DD format
        String inputDate = args[0];
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            @SuppressWarnings("unused") //Only var building object to test format is correct - will not be used
            var localDate = LocalDate.parse(inputDate, formatter);
        } catch (DateTimeParseException e) {
            throw new RuntimeException(e);
        }
        this.date = inputDate;

        //Add slash to URL if not already given
        var baseUrl = args[1];
        if (!baseUrl.endsWith("/")){
            baseUrl += "/";
        }

        //Check that a valid URL has been given
        try {
            @SuppressWarnings("unused") //Only building var object to test URL is valid - will not be used 
            var temp = new URL(baseUrl);
        } catch (Exception x) {
            java.lang.System.err.println("The URL is invalid: " + x);
            java.lang.System.exit(2);
        }
        this.baseUrl = baseUrl;
    }

    /**
     * @return Array of Restaurant objects from the rest API
     */
    public Restaurant[] getRestaurants() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(new URL(baseUrl + RestApiUrl.RESTAURANTS_URL), Restaurant[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return Array of NamedRegion objects representing the no-fly-zones from the rest API
     */
    public NamedRegion[] getNoFlyZones() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(new URL(baseUrl + RestApiUrl.NO_FLY_ZONE_URL), NamedRegion[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return NamedRegion object representing the central area from the rest API
     */
    public NamedRegion getCentralArea() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(new URL(baseUrl + RestApiUrl.CENTRAL_AREA_URL), NamedRegion.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return An array of Order objects for the date passed to the client from the rest API
     */
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
