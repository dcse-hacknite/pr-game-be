package com.hacknite.model.response;

import lombok.Value;

import java.util.List;

@Value
public class RocketResponse {
    String id;
    List<String> authorAvatars;
    Integer secondsRemaining;
    PositionResponse position;
    StatusType status;
}
