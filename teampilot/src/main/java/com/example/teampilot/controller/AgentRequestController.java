package com.example.teampilot.controller;

import com.example.teampilot.dto.AgentRequestDto;
import com.example.teampilot.dto.AgentRequestResponse;
import com.example.teampilot.service.AgentRequestService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/agent/requests")
public class AgentRequestController {

    private final AgentRequestService service;

    public AgentRequestController(
            AgentRequestService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AgentRequestResponse createRequest(
            @Valid @RequestBody AgentRequestDto dto) {

        return service.createRequest(dto);
    }

    @GetMapping("/{id}")
    public AgentRequestResponse getRequest(
            @PathVariable UUID id) {

        return service.getRequest(id);
    }
}