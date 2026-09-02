package com.robot.arm.domain.ports.in;

import com.robot.arm.domain.model.AutomatonResult;

public interface ProcessSequenceUseCase {
    AutomatonResult process(String sequence);
}