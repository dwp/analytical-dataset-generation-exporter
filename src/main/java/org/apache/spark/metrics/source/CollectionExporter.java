package org.apache.spark.metrics.source;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import org.apache.spark.metrics.source.Source;

public class CollectionExporter implements Source {

    MetricRegistry metricRegistry;
    Gauge collectionProcessingTimeGauge;

    public CollectionExporter() {
        metricRegistry = new MetricRegistry();
        collectionProcessingTimeGauge = new CollectionProcessingTimeGauge();
        metricRegistry.register("all_collection_processing_time", collectionProcessingTimeGauge);
    }

    @Override
    public String sourceName() {
        return String.format("adg");
    }

    @Override
    public MetricRegistry metricRegistry() {
        return metricRegistry;
    }
    
}
