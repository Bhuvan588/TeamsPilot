package com.example.teampilot.integration.teams.client;


import com.example.teampilot.integration.teams.model.ChatMessage;
import com.example.teampilot.integration.teams.model.SharedDocument;
import com.example.teampilot.integration.teams.model.TeamsUser;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TeamsClient {

    List<TeamsUser> searchUsers(String name);

    List<ChatMessage> getMessages(
            String personId,
            Instant from,
            Instant to
    );

    Optional<SharedDocument> getLatestSharedDocument(
            String personId
    );
}