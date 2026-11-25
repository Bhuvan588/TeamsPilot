package com.example.teampilot.analysis.controller;


import com.example.teampilot.analysis.model.DiscussionAnalysis;
import com.example.teampilot.analysis.service.ConversationAnalysisService;
import com.example.teampilot.integration.teams.model.ChatMessage;
import com.example.teampilot.integration.teams.service.TeamsService;

import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
public class ConversationAnalysisController {

    private final TeamsService teamsService;
    private final ConversationAnalysisService analysisService;

    public ConversationAnalysisController(
            TeamsService teamsService,
            ConversationAnalysisService analysisService) {

        this.teamsService = teamsService;
        this.analysisService = analysisService;
    }

    @GetMapping("/analysis")
    public DiscussionAnalysis analyzeConversation(
            @RequestParam String personId,
            @RequestParam Instant from,
            @RequestParam Instant to) {

        List<ChatMessage> messages =
                teamsService.getMessages(personId, from, to);

        return analysisService.analyze(messages);
    }
}
