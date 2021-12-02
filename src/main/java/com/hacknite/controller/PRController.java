package com.hacknite.controller;

import com.hacknite.dto.EventDto;
import com.hacknite.dto.MessageDto;
import com.hacknite.model.request.GitEventRequest;
import com.hacknite.model.response.*;
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

    //Websocket for subscribing to events
    // Takes messages from /app/connect. (Note the Spring adds the /app prefix for us).
    @MessageMapping("/connect")
    // Sends the return value of this method to /pr/events
    @SendTo("/pr/events")
    public EventDto getEvents(EventDto dto) {
        return dto;
    }

    //Endpoint for initial state of the PRs
    @GetMapping("/state")
    public @ResponseBody
    StateResponse state() {
        List<RocketResponse> rockets = Arrays.asList(
                new RocketResponse(1L, Arrays.asList("avatar-url"), 100,
                    new PositionResponse(Math.random(), Math.random()), StatusType.FLYING),
                new RocketResponse(2L, Arrays.asList("avatar-url2"), 120,
                    new PositionResponse(Math.random(), Math.random()), StatusType.PROBLEM),
                new RocketResponse(3L, Arrays.asList("avatar-url3"), 150,
                        new PositionResponse(Math.random(), Math.random()), StatusType.FLYING),
                new RocketResponse(4L, Arrays.asList("avatar-url4"), 80,
                        new PositionResponse(Math.random(), Math.random()), StatusType.MUTINY));

        EventDto event = new EventDto();
        event.setEvent("Event happened");
        template.convertAndSend("/pr/events", event);

        return new StateResponse(Arrays.asList(new PlanetResponse("Mars", 100L)), rockets);
    }

    //Endpoint for accepting github's webhook requests
    @PostMapping("/git-event")
    public @ResponseBody StateResponse gitEvent(@RequestBody GitEventRequest request) {

        List<RocketResponse> rockets = Arrays.asList(
                new RocketResponse(1L, Arrays.asList("avatar-url"), 100,
                        new PositionResponse(Math.random(), Math.random()), StatusType.FLYING),
                new RocketResponse(2L, Arrays.asList("avatar-url2"), 120,
                        new PositionResponse(Math.random(), Math.random()), StatusType.PROBLEM),
                new RocketResponse(3L, Arrays.asList("avatar-url3"), 150,
                        new PositionResponse(Math.random(), Math.random()), StatusType.FLYING),
                new RocketResponse(4L, Arrays.asList("avatar-url4"), 80,
                        new PositionResponse(Math.random(), Math.random()), StatusType.MUTINY));
//        template.convertAndSend("/pr/events", event);
        return new StateResponse(Arrays.asList(new PlanetResponse("Mars", 100L)), rockets);
    }
}

