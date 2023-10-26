package uk.ac.ed.inf;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GeoJsonWriter {
    public void writeToGeoJson(ArrayList<RouteNode> path, String fileName) {
        JSONArray coordinates = new JSONArray();
        for (RouteNode routeNode : path) {
            JSONArray coordinate = new JSONArray();
            coordinate.put(routeNode.getPosition().lng());
            coordinate.put(routeNode.getPosition().lat());
            coordinates.put(coordinate);
        }

        // Creating a LineString feature
        JSONObject geometry = new JSONObject();
        geometry.put("type", "LineString");
        geometry.put("coordinates", coordinates);

        JSONObject properties = new JSONObject();
        properties.put("name", "Sample Line");

        JSONObject feature = new JSONObject();
        feature.put("type", "Feature");
        feature.put("geometry", geometry);
        feature.put("properties", properties);

        // Creating a Feature Collection
        JSONArray features = new JSONArray();
        features.put(feature);

        JSONObject featureCollection = new JSONObject();
        featureCollection.put("type", "FeatureCollection");
        featureCollection.put("features", features);

        String outputDirectory = "PizzaDronz/resultfiles";
        Path outputPath = Paths.get(outputDirectory, fileName + ".geojson");

        // Writing to a GeoJSON file
        try (FileWriter file = new FileWriter(outputPath.toString())) {
            file.write(featureCollection.toString());
            System.out.println("GeoJSON file created successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while creating the GeoJSON file.");
        }
    }
}
