package com.example.teampilot.integration.teams.client;

import com.example.teampilot.integration.teams.model.*;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@Profile("local")
public class MockTeamsClient implements TeamsClient {

    private final List<TeamsUser> users;

    private final List<ChatMessage> messages;

    private final List<SharedDocument> documents;

    public MockTeamsClient() {

        Instant now = Instant.now();

        users = List.of(
                new TeamsUser(
                        "user-101",
                        "Rahul Sharma",
                        "rahul@example.com"
                ),
                new TeamsUser(
                        "user-102",
                        "Priya Mehta",
                        "priya@example.com"
                )
        );

        messages = List.of(

                new ChatMessage(
                        "msg-1",
                        "chat-101",
                        "user-101",
                        "Rahul Sharma",
                        "We should migrate the database to PostgreSQL.",
                        now.minusSeconds(20*60*60)
                ),

                new ChatMessage(
                        "msg-2",
                        "chat-101",
                        "user-102",
                        "Priya Mehta",
                        "Agreed. We should first test migration in staging.",
                        now.minusSeconds(20*60*60)
                ),

                new ChatMessage(
                        "msg-3",
                        "chat-101",
                        "user-101",
                        "Rahul Sharma",
                        "I'll prepare the migration plan by Friday.",
                        now.minusSeconds(20*60*60)
                ),

                new ChatMessage(
                        "msg-4",
                        "chat-101",
                        "user-102",
                        "Priya Mehta",
                        "Let's also evaluate database indexing.",
                        now.minusSeconds(20*60*60)
                )
        );

        documents = List.of(
                new SharedDocument(
                        "doc-101",
                        "Database_Migration_Plan.pdf",
                        "user-101",
                        "Rahul Sharma",
                        "chat-101",
                        "https://example.com/database-migration.pdf",
                        "application/pdf",
                        now.minusSeconds(20*60*60)
                )
        );
    }

    @Override
    public List<TeamsUser> searchUsers(String name) {

        return users.stream()
                .filter(user ->
                        user.displayName()
                                .toLowerCase()
                                .contains(name.toLowerCase())
                )
                .toList();
    }

    @Override
    public List<ChatMessage> getMessages(
            String personId,
            Instant from,
            Instant to) {

        // Identify chats in which the requested person participates.
        var chatIds = messages.stream()
                .filter(message ->
                        message.senderId().equals(personId))
                .map(ChatMessage::chatId)
                .collect(java.util.stream.Collectors.toSet());

        // Return all messages in those chats.
        return messages.stream()
                .filter(message ->
                        chatIds.contains(message.chatId()))
                .filter(message ->
                        !message.createdAt().isBefore(from))
                .filter(message ->
                        !message.createdAt().isAfter(to))
                .sorted(java.util.Comparator.comparing(
                        ChatMessage::createdAt))
                .toList();
    }

    @Override
    public Optional<SharedDocument> getLatestSharedDocument(
            String personId) {

        return documents.stream()
                .filter(document ->
                        document.sharedByUserId().equals(personId)
                )
                .max((a, b) ->
                        a.sharedAt().compareTo(b.sharedAt())
                );
    }
}