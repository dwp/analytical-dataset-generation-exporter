package org.apache.spark.metrics.source.adg.exporter;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;

import com.codahale.metrics.Gauge;

public class CollectionProcessingTimeGauge implements Gauge {

    private static String COLLECTION_PROCESSING_TIME_FILE = "/opt/emr/metrics/processing_times.csv";
    private static String ALL_COLLECTIONS_ID = "all_collections";

    @Override
    public Integer getValue() {
        try {
            FileReader fr = new FileReader(COLLECTION_PROCESSING_TIME_FILE);
            BufferedReader br = new BufferedReader(fr);
            return extractValue(br);
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
            return 0;
        }
    }

    private Integer extractValue(BufferedReader br) {
        String line;
        while ((line = br.readLine()) != null) {
            if line.contains(",") {
                String[] kvp = line.split(",");
                if (kvp[0].equals(ALL_COLLECTIONS_ID)) {
                    return Integer.parseInt(kvp[1]);
                }
            }
        }
        return 0;
    }
}


