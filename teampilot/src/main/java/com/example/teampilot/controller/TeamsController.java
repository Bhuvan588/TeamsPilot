package com.example.teampilot.controller;

import com.example.teampilot.integration.teams.model.*;
import com.example.teampilot.integration.teams.service.TeamsService;

import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/teams")
public class TeamsController {

    private final TeamsService teamsService;

    public TeamsController(TeamsService teamsService) {
        this.teamsService = teamsService;
    }

    @GetMapping("/users")
    public List<TeamsUser> searchUsers(
            @RequestParam String name) {

        return teamsService.searchUsers(name);
    }

    @GetMapping("/messages")
    public List<ChatMessage> getMessages(
            @RequestParam String personId,
            @RequestParam Instant from,
            @RequestParam Instant to) {

        return teamsService.getMessages(
                personId,
                from,
                to
        );
    }

    @GetMapping("/documents/latest")
    public Optional<SharedDocument> getLatestDocument(
            @RequestParam String personId) {

        return teamsService.getLatestDocument(personId);
    }
}