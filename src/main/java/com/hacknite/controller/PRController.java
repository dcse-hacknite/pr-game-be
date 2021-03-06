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
import org.springframework.web.bind.annotation.*;

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
    @CrossOrigin(origins = "*")
    public StateResponse getEvents(StateResponse dto) {
        return dto;
    }

    //Endpoint for initial state of the PRs
    @GetMapping("/state")
    @CrossOrigin(origins = "*")
    public @ResponseBody
    StateResponse state() {
//        service.deleteAll();
        System.out.println("Calling state");
        StateResponse response = service.getCurrentState();
        if(response != null) {
            template.convertAndSend("/pr/events", response);
        }
        return response;
    }

    //Endpoint for accepting github's webhook requests
    @PostMapping("/git-event")
    public @ResponseBody StateResponse gitEvent(@RequestBody GitEventRequest request) {
        System.out.println("----------------------------REQUEST----------------------------");
        System.out.println(request.toString());
        StateResponse response = service.handleGitRequest(request);

        template.convertAndSend("/pr/events", response);
        return response;
    }
}

