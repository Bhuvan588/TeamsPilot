package com.example.teampilot.entity;


import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="agent_requests")
public class AgentRequest {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false,length = 5000)
    private String instruction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    private Instant createdAt;

    private Instant updatedAt;

    protected AgentRequest() {
    }

    public AgentRequest(String instruction) {
        this.instruction = instruction;
        this.status = RequestStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getInstruction() {
        return instruction;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void updateStatus(RequestStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }
}
