package com.example.teampilot.integration.teams.service;

import com.example.teampilot.integration.teams.client.TeamsClient;
import com.example.teampilot.integration.teams.model.*;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class TeamsService {

    private final TeamsClient teamsClient;

    public TeamsService(TeamsClient teamsClient) {
        this.teamsClient = teamsClient;
    }

    public List<TeamsUser> searchUsers(String name) {
        return teamsClient.searchUsers(name);
    }

    public List<ChatMessage> getMessages(
            String personId,
            Instant from,
            Instant to) {

        if (from == null || to == null || from.isAfter(to)) {
            throw new IllegalArgumentException(
                    "Invalid date range"
            );
        }

        return teamsClient.getMessages(
                personId,
                from,
                to
        );
    }

    public Optional<SharedDocument> getLatestDocument(
            String personId) {

        return teamsClient.getLatestSharedDocument(personId);
    }
}