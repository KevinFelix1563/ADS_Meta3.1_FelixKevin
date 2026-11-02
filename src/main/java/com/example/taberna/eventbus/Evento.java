package com.example.taberna.eventbus;

/**
 * Interfaz marcadora para todos los eventos del sistema "El Gólem Alquímico".
 *
 * <p><strong>¿Qué es una interfaz marcadora (Marker Interface)?</strong></p>
 * Una interfaz marcadora es una interfaz en Java que no define métodos ni constantes
 * (está vacía por diseño). Ejemplos clásicos de la biblioteca estándar de Java son
 * {@link java.io.Serializable} y {@link Cloneable}. Su propósito no es definir
 * un contrato de comportamiento, sino establecer una <em>marca de tipo</em> o identidad
 * taxonómica que el compilador y la máquina virtual (JVM) puedan reconocer.
 *
 * <p><strong>Rol en el Event Bus y Manejo de Tipos:</strong></p>
 * Al obligar a que cualquier mensaje o suceso que viaje por el {@link EventBus}
 * implemente {@code Evento}, establecemos un límite superior en el sistema de genéricos:
 * {@code ? extends Evento}. Esto impide que clases arbitrarias que no corresponden al
 * dominio de eventos (por ejemplo, {@code String}, {@code Integer} o modelos de datos no relacionados)
 * sean enviadas o registradas en el bus, otorgando seguridad de tipos en tiempo de compilación.
 */
public interface Evento {
    // Interfaz marcadora: intencionalmente vacía.
}
