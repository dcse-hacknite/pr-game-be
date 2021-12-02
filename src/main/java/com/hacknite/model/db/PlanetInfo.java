package com.hacknite.model.db;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class PlanetInfo {
    String name;
    Long score;

    public PlanetInfo(){}

    public PlanetInfo(String name, Long score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public Long getScore() {
        return score;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(Long score) {
        this.score = score;
    }
}
