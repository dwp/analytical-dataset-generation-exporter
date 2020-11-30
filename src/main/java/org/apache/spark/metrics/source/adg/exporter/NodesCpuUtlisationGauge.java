package org.apache.spark.metrics.source.adg.exporter;

import com.codahale.metrics.Gauge;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class NodesCpuUtlisationGauge implements Gauge {

    private static String CPU_UTILISATION_FILE = "/opt/emr/metrics/adg_nodes_cpu_utilisation.csv";
    private String collection;

    public NodesCpuUtlisationGauge(String nodeType) {
        this.nodeType = nodeType;
    }

    @Override
    public Integer getValue() {
        try {
            FileReader fr = new FileReader(CPU_UTILISATION_FILE);
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


