package com.hacknite.model.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
public class StateResponse {
    List<PlanetResponse> planets;
    List<RocketResponse> rockets;
}
