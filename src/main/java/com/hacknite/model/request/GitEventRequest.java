package com.hacknite.model.request;

import lombok.Value;

@Value
public class GitEventRequest {
    ActionType action;
    GitEventDetailsRequest details;
}
