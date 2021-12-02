package com.hacknite.model.db;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class PositionInfo {
    Double distance;
    Double order;

    public PositionInfo(){}

    public PositionInfo(Double distance, Double order) {
        this.distance = distance;
        this.order = order;
    }

    public Double getDistance() {
        return distance;
    }

    public Double getOrder() {
        return order;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public void setOrder(Double order) {
        this.order = order;
    }
}
