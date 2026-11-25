package com.example.teampilot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AgentRequestDto(
        @NotBlank(message = "Instruction cannot be empty")
        @Size(max = 5000)
        String instruction
) {
}
