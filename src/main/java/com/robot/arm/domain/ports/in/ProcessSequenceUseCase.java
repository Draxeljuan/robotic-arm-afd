package com.robot.arm.domain.ports.in;

import com.robot.arm.domain.model.AutomatonResult;

/**
 * Puerto de entrada del dominio: procesar una cadena w y obtener el resultado
 * formal (aceptada + traza de estados, o rechazada).
 */
public interface ProcessSequenceUseCase {
    AutomatonResult process(String input);
}
