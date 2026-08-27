# AFD - Brazo Robótico 3x3 (v2)

Proyecto Java 21 (Swing) con arquitectura hexagonal que modela, mediante un
Autómata Finito Determinista, el movimiento de una pinza robótica en una
cuadrícula 3x3 con una sola pieza.

## Requisitos

- JDK 21
- Maven 3.9+

## Ejecutar

```bash
mvn clean package
java -jar target/robotic-arm-afd.jar
```

O directamente en desarrollo:

```bash
mvn clean compile exec:java -Dexec.mainClass="com.robot.arm.Main"
```

(si no tiene el plugin `exec`, use la opción del `jar` ejecutable de arriba)

## Uso

1. Escriba una cadena usando únicamente los símbolos `U D L R + -`
   (mayúsculas o minúsculas).
2. Presione **Enter**. El programa valida primero que todos los símbolos
   pertenezcan a Σ; si alguno no pertenece, se muestra únicamente
   **"La cadena no pertenece."** (sin detalles de error).
3. Si la cadena es válida, se anima automáticamente paso a paso (no se mueve
   mientras usted escribe).
4. `+` toma la pieza si la garra está vacía y está exactamente sobre ella; en
   cualquier otro caso no hace nada (autobucle) y la garra queda vacía.
5. `-` suelta la pieza en la posición actual si la garra la sostiene; en
   cualquier otro caso no hace nada (autobucle) y la garra queda vacía.
6. Al llegar a un borde, seguir avanzando en la misma dirección deja la garra
   en el borde (autobucle / clamp), no produce error.

## Estructura (arquitectura hexagonal)

```
com.robot.arm
├── Main.java
├── domain
│   ├── model        (Position, Symbol, RobotState, AutomatonResult)
│   ├── ports.in      (ProcessSequenceUseCase)
│   └── service        (TransitionEngine: implementa δ)
└── infrastructure
    └── gui           (RobotArmFrame, GridPanel)
```

El dominio (`domain`) no depende de Swing ni de ningún framework: podría
reutilizarse con otra interfaz (web, consola, etc.) sin modificarlo.
