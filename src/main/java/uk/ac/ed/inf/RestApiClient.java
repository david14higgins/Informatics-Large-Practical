package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;
import java.net.URL;


public class RestApiClient {

    public void getRestaurants() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        try {
            Restaurant[] restaurants = mapper.readValue(new URL(RestApiUrl.RESTAURANTS_URL), Restaurant[].class);
            for (Restaurant restaurant : restaurants) {
                System.out.println(restaurant.name());
            }
            System.out.println("read all restaurants");
            System.out.println(restaurants.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
