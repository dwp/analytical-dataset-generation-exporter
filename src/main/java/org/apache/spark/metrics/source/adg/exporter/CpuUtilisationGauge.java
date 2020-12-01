package org.apache.spark.metrics.source.adg.exporter;

import com.codahale.metrics.Gauge;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CpuUtilisationGauge implements Gauge {


    private String nodeType;
    public CpuUtilisationGauge(String nodeType) {
        this.nodeType = nodeType;
    }
    private String CPU_UTILISATION_FILE = "/opt/emr/metrics/adg_" + this.nodeType + "_cpu_utilisation.csv";


    @Override
    public Double getValue() {
        try {
            FileReader fr = new FileReader(CPU_UTILISATION_FILE);
            BufferedReader br = new BufferedReader(fr);
            System.out.println("got some cpu value");
            return extractValue(br);
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
            return 0.00;
        }
        catch (IOException e) {
            System.out.println(e.toString());
            return 0.00;
        }
    }

    private Double extractValue(BufferedReader br) {
        String line;
        try {
            while ((line = br.readLine()) != null) {
                if (line.contains(",")) {
                    String[] kvp = line.split(",");
                    if (kvp[0].equals(this.nodeType)) {
                        System.out.println("got the integer value");
                        return Double.parseDouble(kvp[1]);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.toString());
            return 0.00;
        }
        return 0.00;
    }
}


