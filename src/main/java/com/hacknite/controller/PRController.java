package com.hacknite.controller;

import com.hacknite.dto.EventDto;
import com.hacknite.dto.MessageDto;
import com.hacknite.model.request.GitEventRequest;
import com.hacknite.model.response.*;
import com.hacknite.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

@Controller
public class PRController {

    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private StateService service;

    //Websocket for subscribing to events
    // Takes messages from /app/connect. (Note the Spring adds the /app prefix for us).
    @MessageMapping("/connect")
    // Sends the return value of this method to /pr/events
    @SendTo("/pr/events")
    public StateResponse getEvents(StateResponse dto) {
        return dto;
    }

    //Endpoint for initial state of the PRs
    @GetMapping("/state")
    public @ResponseBody
    StateResponse state() {
        System.out.println("Calling state");
        return service.getCurrentState();
    }

    //Endpoint for accepting github's webhook requests
    @PostMapping("/git-event")
    public @ResponseBody StateResponse gitEvent(@RequestBody GitEventRequest request) {
        StateResponse response = service.handleGitRequest(request);

        template.convertAndSend("/pr/events", response);
        return response;
    }
}

