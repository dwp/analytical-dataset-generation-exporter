package org.apache.spark.metrics.source.adg.exporter;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import org.apache.spark.metrics.source.Source;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;
import com.amazonaws.services.secretsmanager.model.InvalidRequestException;
import com.amazonaws.services.secretsmanager.model.InvalidParameterException;
import com.amazonaws.services.secretsmanager.model.DecryptionFailureException;
import com.amazonaws.services.secretsmanager.model.InternalServiceErrorException;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class CollectionExporter implements Source {

    MetricRegistry metricRegistry;
    Gauge collectionProcessingTimeGauge;
    Gauge InputCollectionSizeGauge;
    Gauge OutputCollectionSizeGauge;
    ArrayList<String> collections;

    public CollectionExporter() {
        metricRegistry = new MetricRegistry();
        collections = getMetricList();
        for (String collection : collections) {
            collectionProcessingTimeGauge = new CollectionProcessingTimeGauge(collection);
            InputCollectionSizeGauge = new InputCollectionSizeGauge(collection);
            OutputCollectionSizeGauge = new OutputCollectionSizeGauge(collection);
            metricRegistry.register(collection + "_processing_time", collectionProcessingTimeGauge);
            metricRegistry.register(collection + "_input_collection_size", InputCollectionSizeGauge);
            metricRegistry.register(collection + "_output_collection_size", OutputCollectionSizeGauge);
        }
    }

    @Override
    public String sourceName() {
        return String.format("adg");
    }

    @Override
    public MetricRegistry metricRegistry() {
        return metricRegistry;
    }

    private ArrayList<String> getMetricList() {
        ArrayList<String> metrics = new ArrayList<String>();
        Gson gson = new Gson();
        JsonElement jsonElement = new JsonParser().parse(getSecret());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        jsonObject = jsonObject.getAsJsonObject("collections_all");
        Set<Map.Entry<String, JsonElement>> entries = jsonObject.entrySet();
        for(Map.Entry<String, JsonElement> entry: entries) {
            String key = entry.getKey();
            key = key.replace("db.", "");
            key = key.replace(".", "_");
            key = key.replace("-", "_");
            key = key.toLowerCase();
            metrics.add(key);
        }
        metrics.add("all_collections");
        return metrics;
    }

    private String getSecret() {
        String secretName = "/concourse/dataworks/adg/full";
        String region = "eu-west-2";
        AWSSecretsManager client  = AWSSecretsManagerClientBuilder.standard().withRegion(region).build();
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(secretName);
        GetSecretValueResult getSecretValueResult = null;
        String secret;
        try {
            getSecretValueResult = client.getSecretValue(getSecretValueRequest);
        } catch (DecryptionFailureException e) {
            throw e;
        } catch (InternalServiceErrorException e) {
            throw e;
        } catch (InvalidParameterException e) {
            throw e;
        } catch (InvalidRequestException e) {
            throw e;
        } catch (ResourceNotFoundException e) {
            throw e;
        }
        if (getSecretValueResult.getSecretString() != null) {
            return getSecretValueResult.getSecretString();
        }
        else {
            byte[] arry = new byte[getSecretValueResult.getSecretBinary().remaining()];
            getSecretValueResult.getSecretBinary().get(arry);
            secret = new String(arry, StandardCharsets.UTF_8);
            return secret;
        }
    }
}
