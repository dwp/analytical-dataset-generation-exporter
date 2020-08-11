package org.apache.spark.metrics.source.adg.exporter;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import org.apache.spark.metrics.source.Source;

import java.util.ArrayList;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;
import com.amazonaws.services.secretsmanager.model.InvalidRequestException;
import com.amazonaws.services.secretsmanager.model.InvalidParameterException;
import com.amazonaws.services.secretsmanager.model.DecryptionFailureException;
import com.amazonaws.services.secretsmanager.model.InternalServiceErrorException;

import org.json.JSONObject;

public class CollectionExporter implements Source {

    MetricRegistry metricRegistry;
    Gauge collectionProcessingTimeGauge;
    ArrayList<String> collections;

    public CollectionExporter() {
        metricRegistry = new MetricRegistry();
        collections = getMetricList();
        for (String collection : collections) {
            collectionProcessingTimeGauge = new CollectionProcessingTimeGauge(collection);
            metricRegistry.register(collection, collectionProcessingTimeGauge);
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
        JSONObject obj = new JSONObject(getSecret());
        obj = obj.getJSONObject("collections_all");
        Iterator<String> keys = obj.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            key = key.replaceAll("db.", "");
            key = key.replaceAll(".", "_");
            key = key.replaceAll("-", "_");
            key = key.toLowerCase();
            metrics.add(key);
        }
        return metrics;
    }

    private String getSecret() {

        String secretName = "/concourse/dataworks/adg";
        String region = "eu-west-2";

        AWSSecretsManager client  = AWSSecretsManagerClientBuilder.standard().withRegion(region).build();
        
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(secretName);
        GetSecretValueResult getSecretValueResult = null;

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
            String secret = new String(Base64.getDecoder().decode(getSecretValueResult.getSecretBinary()).array());
            return secret;
        }
    }
    
}
