package uk.ac.ed.inf.output;

import java.io.File;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import uk.ac.ed.inf.constant.OutputPath;
import uk.ac.ed.inf.interfaces.OutputWriter;
import uk.ac.ed.inf.routing.MoveInfo;
import uk.ac.ed.inf.ilp.data.LngLat;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DroneGeoJsonWriter implements OutputWriter {

    private final ArrayList<MoveInfo> routeMoves;

    public DroneGeoJsonWriter(ArrayList<MoveInfo> routeMoves) {
        this.routeMoves = routeMoves;
    }

    @Override
    public void writeToFile(String fileName) {
        JSONArray coordinates = new JSONArray();
        //Add source position from the first move and then just destinations positions after that
        if(!routeMoves.isEmpty()) {
            MoveInfo firstMove = routeMoves.get(0);
            LngLat firstMoveSource = firstMove.getSourceToDestinationPair().sourceLngLat();
            JSONArray firstCoordinate = new JSONArray();
            firstCoordinate.put(firstMoveSource.lng());
            firstCoordinate.put(firstMoveSource.lat());
            coordinates.put(firstCoordinate);
        }

        //Now add destinations from every non hover move
        for (MoveInfo moveInfo : routeMoves) {
            if(moveInfo.getAngle() != 999) {
                LngLat position = moveInfo.getSourceToDestinationPair().destinationLngLat();
                JSONArray coordinate = new JSONArray();
                coordinate.put(position.lng());
                coordinate.put(position.lat());
                coordinates.put(coordinate);
            }
        }

        // Create a LineString
        JSONObject lineString = new JSONObject();
        lineString.put("type", "LineString");
        lineString.put("coordinates", coordinates);

        JSONObject properties = new JSONObject();
        properties.put("name", "Sample Line");

        JSONObject feature = new JSONObject();
        feature.put("type", "Feature");
        feature.put("geometry", lineString);
        feature.put("properties", properties);

        // Creating a Feature Collection
        JSONArray features = new JSONArray();
        features.put(feature);

        JSONObject featureCollection = new JSONObject();
        featureCollection.put("type", "FeatureCollection");
        featureCollection.put("features", features);

        String outputDirectory = OutputPath.PATH;
        Path outputPath = Paths.get(outputDirectory, fileName + ".geojson");

        // Writing to a GeoJSON file
//        try (FileWriter file = new FileWriter(outputPath.toString())) {
//            file.write(featureCollection.toString());
//            System.out.println("GeoJSON file created successfully.");
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.out.println("An error occurred while creating the GeoJSON file.");
//        }
        try {
            File outputFile = new File("resultfiles/" + fileName + ".geojson");
            outputFile.getParentFile().mkdirs();
            FileWriter outputFileWriter = new FileWriter(outputFile);
            outputFileWriter.write(featureCollection.toString());
            outputFileWriter.close();
            System.out.println("GeoJSON file created successfully.");
        } catch (IOException e) {
            java.lang.System.err.println("An error occurred while creating the drone GeoJSON file.");
            java.lang.System.exit(2);
        }
    }
}
