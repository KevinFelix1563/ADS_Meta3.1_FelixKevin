# El Gólem Alquímico - Arquitectura Event Bus

Este proyecto implementa el sistema reactivo y distribuido de eventos para **"El Gólem Alquímico"**, ambientado en una taberna fantástica. La solución fue desarrollada siguiendo el patrón arquitectónico **Event Bus** (Publicador-Suscriptor / Observer desacoplado) sobre **Java 21**, estructurado modularmente para garantizar alta cohesión y bajo acoplamiento entre actores.

---

## 🛠️ Requisitos y Tecnologías

* **Lenguaje:** Java 21 (LTS)
* **Gestor de Construcción:** Apache Maven con Maven Wrapper (`./mvnw`)
* **Framework de Pruebas:** JUnit Jupiter 5.12.1 con Surefire 3.2.5
* **Herramienta de Diagramado:** PlantUML (`diagramas.puml`)

---

## 🚀 Compilación y Ejecución

El proyecto incluye el envoltorio de Maven (`mvnw`), por lo que no se requiere una instalación global previa de Maven.

### 1. Compilación del proyecto
Para compilar todas las clases del código fuente y validar la modularidad de Java:
```bash
./mvnw clean compile
```

### 2. Ejecución de la suite de pruebas unitarias
Para correr las pruebas automatizadas de concurrencia, despacho y manejo de tipos:
```bash
./mvnw test
```

### 3. Ejecución de la simulación interactiva
Para ejecutar la simulación completa de la taberna con publicadores en paralelo y efectos de escenario:
```bash
./mvnw exec:java -Dexec.mainClass="com.example.taberna.TabernaSimulador"
```

---

## 🏛️ Demostración del Principio Open/Closed (OCP)

El **Principio Abierto/Cerrado (Open/Closed Principle)** establece que las entidades de software deben estar *abiertas a la extensión pero cerradas a la modificación*.

En este sistema, el cumplimiento estricto del principio se evidenció a través de las siguientes decisiones de diseño:

1. **Desacoplamiento Estructural:**
   Se implementó una infraestructura centralizada en `EventBus` que actúa como mediador tipado (`Map<Class<? extends Evento>, List<Suscriptor<? extends Evento>>>`). Mediante esta estructura, los publicadores (`Alquimista`, `Banda`) no poseen referencias a los suscriptores (`Inventario`, `Tabernero`, `SistemaDeSonido`, `PanelLED`, `SistemaDeHumo`), ni viceversa.

2. **Extensión sin Modificación:**
   En el Incremento 4 se introdujo el componente `SistemaDeHumo`, encargado de disparar cañones de humo y efectos de niebla cuando se detecta la interpretación de *"Through the Fire and Flames"*. Para lograr esta integración:
   * **No se alteró** la lógica interna ni una sola línea de código en `Banda.java`.
   * **No se modificó** el algoritmo de enrutamiento ni la estructura de `EventBus.java`.
   * **Se extendió** el comportamiento del sistema mediante la creación de la nueva clase suscriptora y su registro al bus mediante referencias a métodos (`bus.suscribir(BandaTocandoEvent.class, sistemaDeHumo::accionarHumo)`).

3. **Inmutabilidad y Concurrencia:**
   Se modelaron los eventos (`PocionElaboradaEvento`, `IngredienteAgotadoEvento`, `BandaTocandoEvent`) como registros inmutables (`record`). De este modo, se garantizó la seguridad en entornos multihilo sin recurrir a bloqueos sincronizados innecesarios durante la lectura de eventos.

---

## 📁 Estructura de Paquetes

```text
src/main/java/com/example/taberna/
├── TabernaSimulador.java               # Punto de entrada principal y simulación de la taberna
├── dominio/
│   ├── actores/
│   │   ├── Alquimista.java             # Publicador de pociones e ingredientes
│   │   ├── Banda.java                  # Publicador asíncrono concurrente de música
│   │   ├── Inventario.java             # Suscriptor de stock y abastecimiento
│   │   ├── PanelLED.java               # Suscriptor multievento para marquesina luminosa
│   │   ├── SistemaDeHumo.java          # Suscriptor de efectos especiales (Demostración OCP)
│   │   ├── SistemaDeSonido.java        # Suscriptor de amplificación y audio
│   │   └── Tabernero.java              # Suscriptor de anuncios al público
│   └── eventos/
│       ├── BandaTocandoEvent.java      # Record inmutable de música en vivo
│       ├── IngredienteAgotadoEvento.java # Record inmutable de falta de ingredientes
│       └── PocionElaboradaEvento.java  # Record inmutable de poción fabricada
└── eventbus/
    ├── EventBus.java                   # Núcleo del bus con contenedor heterogéneo tipado
    ├── Evento.java                     # Interfaz marcadora base para todos los eventos
    └── Suscriptor.java                 # Interfaz funcional genérica <T extends Evento>
```

---

## 📊 Diagramas UML

Las especificaciones formales de los diagramas de clases, secuencia y componentes se encuentran documentadas en el archivo [`diagramas.puml`](diagramas.puml) ubicado en la raíz del proyecto.
