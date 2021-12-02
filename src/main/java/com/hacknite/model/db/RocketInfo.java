package com.hacknite.model.db;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEnum;
import com.hacknite.model.response.PositionResponse;
import com.hacknite.model.response.StatusType;

import java.util.List;

@DynamoDBDocument
public class RocketInfo {
    String id;
    List<String> authorAvatars;
    Integer secondsRemaining;
    PositionInfo position;
    StatusType status;

    public RocketInfo(){}

    public RocketInfo(String id, List<String> authorAvatars, Integer secondsRemaining, PositionInfo position, StatusType status) {
        this.id = id;
        this.authorAvatars = authorAvatars;
        this.secondsRemaining = secondsRemaining;
        this.position = position;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public List<String> getAuthorAvatars() {
        return authorAvatars;
    }

    public Integer getSecondsRemaining() {
        return secondsRemaining;
    }

    public PositionInfo getPosition() {
        return position;
    }

    @DynamoDBTypeConvertedEnum
    @DynamoDBAttribute(attributeName="Status")
    public StatusType getStatus() {
        return status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAuthorAvatars(List<String> authorAvatars) {
        this.authorAvatars = authorAvatars;
    }

    public void setSecondsRemaining(Integer secondsRemaining) {
        this.secondsRemaining = secondsRemaining;
    }

    public void setPosition(PositionInfo position) {
        this.position = position;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }
}
