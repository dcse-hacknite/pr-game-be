package com.hacknite.controller;

import com.hacknite.dto.EventDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    String state() {
        return "state";
    }

    //Endpoint for accepting github's webhook requests
    @PostMapping("/pullRequest")
    public void pullRequest() {
        //Accept data, store it
//        EventDto event = new EventDto("Test");
        //Send result event to subscribers
//        template.convertAndSend("/pr/events", event);
    }
}

