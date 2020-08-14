package org.apache.spark.metrics.source.adg.exporter;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.codahale.metrics.Gauge;

public class CollectionSizeGauge implements Gauge {

    private static String COLLECTION_SIZE_FILE = "/opt/emr/metrics/collection_size.csv";
    private String collection;

    public CollectionSizeGauge(String collection) {
        this.collection = collection;
    }

    @Override
    public Integer getValue() {
        try {
            FileReader fr = new FileReader(COLLECTION_SIZE_FILE);
            BufferedReader br = new BufferedReader(fr);
            return extractValue(br);
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
            return 0;
        }
        catch (IOException e) {
            System.out.println(e.toString());
            return 0;
        }
    }

    private Integer extractValue(BufferedReader br) {
        String line;
        try {
            while ((line = br.readLine()) != null) {
                if (line.contains(",")) {
                    String[] kvp = line.split(",");
                    if (kvp[0].equals(this.collection)) {
                        return Integer.parseInt(kvp[1]);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.toString());
            return 0;
        }
        return 0;
    }
}


