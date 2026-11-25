package com.example.teampilot.service;


import com.example.teampilot.dto.AgentRequestDto;
import com.example.teampilot.dto.AgentRequestResponse;
import com.example.teampilot.entity.AgentRequest;
import com.example.teampilot.exception.ResourceNotFoundException;
import com.example.teampilot.repository.AgentRequestRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AgentRequestService {

    private final AgentRequestRepository repository;

    public AgentRequestService(AgentRequestRepository repository) {
        this.repository = repository;
    }

    public AgentRequestResponse createRequest(AgentRequestDto dto)
    {
        AgentRequest request = new AgentRequest(dto.instruction());

        AgentRequest savedRequest = repository.save(request);

        return new AgentRequestResponse(
                savedRequest.getId(),
                savedRequest.getStatus(),
                "Your status has been accepted....",
                savedRequest.getCreatedAt()
        );
    }
    public AgentRequestResponse getRequest(UUID id) {

        AgentRequest request = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Agent request not found: " + id
                        )
                );

        return new AgentRequestResponse(
                request.getId(),
                request.getStatus(),
                "Request status retrieved",
                request.getCreatedAt()
        );
    }
}
