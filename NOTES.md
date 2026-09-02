### Notas Importantes
1. Lenguaje de Programación a emplear Java 21
2. Requisito, mecanismo para mover una pinza robotica sobre una matriz con ejes X Y que pueda realizar la acción de tomar o soltar algo
3. Al AFD (también toca hacer grafo) debe permitir validar la secuencia de las coordenadas y las asociaciones logicas (el brazo agarra o suelta algo)
4. Software visual que simule el movimiento del brazo y sus estados (por definir)
5. Los 5 paramatros de los automatas se tienen que redactar primero antes de pasar a construcción de código o grafos

## Planteamiento del Ejercicio

# AFD — Control de Brazo Robótico $3\times3$ (2 Piezas) — Versión Autobucle
---

## Definición Formal

$$\mathcal{M} = (Q, \Sigma, \delta, q_0, F)$$

---

## 1. $Q$ — Conjunto Finito de Estados

Un estado $q \in Q$ representa una configuración válida del sistema mediante la tupla:

$$q = (x, y, G, P_1, P_2)$$

donde:
- $x, y \in \{0, 1, 2\}$: Coordenadas cartesianas de la garra en la cuadrícula $3\times3$.
- $G \in \{0, 1, 2\}$: Estado de la garra ($0$: Vacía, $1$: Sosteniendo Pieza 1, $2$: Sosteniendo Pieza 2).
- $P_1 \in (\{0,1,2\} \times \{0,1,2\}) \cup \{\text{held}\}$: Posición de la Pieza 1 en el tablero o $\text{held}$ si la lleva la garra.
- $P_2 \in (\{0,1,2\} \times \{0,1,2\}) \cup \{\text{held}\}$: Posición de la Pieza 2 en el tablero o $\text{held}$ si la lleva la garra.

$$Q = \left\{ (x, y, G, P_1, P_2) \;\middle|\; x,y \in \{0,1,2\},\; G \in \{0,1,2\},\; P_1, P_2 \in (\{0,1,2\}^2 \cup \{\text{held}\}) \right\}$$

> **Principio de Exclusión Espacial:** $P_1 \neq P_2$ cuando ambas piezas están apoyadas en la matriz.

A partir de las distintas combinaciones de la tupla en el conjunto de estados podemos encontrar los siguientes estados:

```java
	Q0_INITIAL("q0: Inicio"),
    Q1_MOVING("q1: Movimiento"),
    Q2_HOLDING("q2: Con Pieza"),
    Q3_RELEASED("q3: Garra Vacía");
```

---

## 2. $\Sigma$ — Alfabeto Mixto

$$\Sigma = \{U, D, L, R, +, -\}$$

| Símbolo | Significado | Acción de Transición |
| :---: | :--- | :--- |
| `U` | Mover Arriba | $y \leftarrow \min(2, y+1)$ |
| `D` | Mover Abajo | $y \leftarrow \max(0, y-1)$ |
| `L` | Mover Izquierda | $x \leftarrow \max(0, x-1)$ |
| `R` | Mover Derecha | $x \leftarrow \min(2, x+1)$ |
| `+` | Tomar Pieza | Toma la pieza si $G=0$ y coincide la casilla. Si no hay pieza o $G \neq 0$, **mantiene el estado**. |
| `-` | Soltar Pieza | Deposita la pieza si la casilla está libre. **Si está ocupada por la otra pieza o $G=0$, mantiene el estado**. |

---

## 3. $q_0$ — Estado Inicial

$$q_0 = (0, 0, 0, (1,1), (2,2))$$

- **Garra:** En el origen $(0,0)$ y desocupada ($G=0$).
- **Pieza 1:** En la casilla central $(1,1)$.
- **Pieza 2:** En la casilla $(2,2)$.

---

## 4. $F$ — Estados de Aceptación

$$F = Q$$

Todos los estados son de aceptación. Toda cadena $w \in \Sigma^*$ es aceptada. Una cadena solo se rechaza si contiene símbolos fuera de $\Sigma$.

---

## 5. $\delta$ — Función de Transición Completa

### Movimiento (Con rebote/clamp en los bordes $0..2$):
$$\delta((x,y,G,P_1,P_2), U) = (x,\ \min(2, y+1),\ G,\ P_1,\ P_2)$$
$$\delta((x,y,G,P_1,P_2), D) = (x,\ \max(0, y-1),\ G,\ P_1,\ P_2)$$
$$\delta((x,y,G,P_1,P_2), L) = (\max(0, x-1),\ y,\ G,\ P_1,\ P_2)$$
$$\delta((x,y,G,P_1,P_2), R) = (\min(2, x+1),\ y,\ G,\ P_1,\ P_2)$$

### Agarra ($+$):
1. **Garra vacía sobre $P_1$ ($P_1 = (x,y)$):** $\delta((x,y,0,(x,y),P_2), +) = (x,y,1,\text{held},P_2)$
2. **Garra vacía sobre $P_2$ ($P_2 = (x,y)$):** $\delta((x,y,0,P_1,(x,y)), +) = (x,y,2,P_1,\text{held})$
3. **Casilla vacía o garra ya ocupada:** $\delta((x,y,G,P_1,P_2), +) = (x,y,G,P_1,P_2) \quad \text{(Autobucle)}$

### Soltar ($-$) — Protección de Superposición:
1. **Garra desocupada ($G=0$):** $\delta((x,y,0,P_1,P_2), -) = (x,y,0,P_1,P_2) \quad \text{(Autobucle)}$
2. **Lleva Pieza 1 ($G=1$):**
    - **Casilla Libre ($P_2 \neq (x,y)$):** $\delta((x,y,1,\text{held},P_2), -) = (x,y,0,(x,y),P_2)$
    - **Casilla Ocupada por Pieza 2 ($P_2 = (x,y)$):** $\delta((x,y,1,\text{held},(x,y)), -) = (x,y,1,\text{held},(x,y)) \quad \text{\textbf{(Autobucle: No la suelta)}}$
3. **Lleva Pieza 2 ($G=2$):**
    - **Casilla Libre ($P_1 \neq (x,y)$):** $\delta((x,y,2,P_1,\text{held}), -) = (x,y,0,P_1,(x,y))$
    - **Casilla Ocupada por Pieza 1 ($P_1 = (x,y)$):** $\delta((x,y,2,(x,y),\text{held}), -) = (x,y,2,(x,y),\text{held}) \quad \text{\textbf{(Autobucle: No la suelta)}}$



> [!tip] Tabla resumen de $\delta$ para $+$ y $-$
>
> | Estado antes | Símbolo | Condición | Estado después |
> |---|---|---|---|
> | garra vacía, pieza en $P_1$ o $P_2$) | `+` | garra sobre alguna pieza | garra llena (`held`) q2|
> | garra vacía o llena | `U, D, L, R` | — | garra se moviliza q1 |
> | garra vacía, pieza en otra celda | `+` | — | igual, queda en q3 |
> | garra llena | `+` | — | igual, queda en q2 |
> | garra llena | `-` | — | pieza cae en $(x,y)$, garra vacía, queda en q3 |
> | garra vacía | `-` | — | igual, queda en q3 |

---
