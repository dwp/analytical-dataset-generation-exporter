package org.apache.spark.metrics.source.adg.exporter;

import com.codahale.metrics.Gauge;

public class CollectionProcessingTimeGauge implements Gauge {

    @Override
    public Integer getValue() {
        return 1;
    }
    
}
