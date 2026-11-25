package com.example.teampilot.repository;

import com.example.teampilot.entity.AgentRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AgentRequestRepository extends JpaRepository<AgentRequest, UUID> {
}
