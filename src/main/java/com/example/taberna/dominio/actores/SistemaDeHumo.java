package com.example.taberna.dominio.actores;

import com.example.taberna.dominio.eventos.BandaTocandoEvent;
import java.util.Objects;

/**
 * Representa el Sistema de Efectos Especiales y Cañones de Humo de la taberna.
 *
 * <p><strong>Demostración del Principio Open/Closed (Abierto/Cerrado - OCP):</strong></p>
 * El principio Open/Closed establece que las entidades de software deben estar <em>abiertas para
 * la extensión, pero cerradas para la modificación</em>.
 *
 * <p>Gracias al patrón <strong>Event Bus</strong>:</p>
 * <ul>
 *   <li><strong>Cerrado a la modificación:</strong> Ni una sola línea de código en {@link com.example.taberna.eventbus.EventBus},
 *       {@link Banda} o los demás suscriptores tuvo que alterarse.</li>
 *   <li><strong>Abierto a la extensión:</strong> Se incorporó este nuevo actor y se conectó al bus
 *       mediante referencias a métodos, extendiendo la funcionalidad del sistema sin riesgo
 *       de introducir regresiones en el código existente.</li>
 * </ul>
 */
public class SistemaDeHumo {

    private static final String CANCION_DISPARADORA = "Through the Fire and Flames";
    private int disparosRealizados;

    public SistemaDeHumo() {
        this.disparosRealizados = 0;
    }

    /**
     * Callback invocado cuando la banda interpreta una canción.
     * Compatible con {@code Suscriptor<BandaTocandoEvent>}.
     *
     * <p>Si la canción interpretada es exactamente {@value #CANCION_DISPARADORA},
     * se activan los cañones de CO2 y humo en el escenario.</p>
     *
     * @param evento Evento con los detalles de la canción que la banda está tocando.
     */
    public void accionarHumo(BandaTocandoEvent evento) {
        Objects.requireNonNull(evento, "El evento musical no puede ser nulo.");

        if (CANCION_DISPARADORA.equalsIgnoreCase(evento.nombreCancion().trim())) {
            this.disparosRealizados++;
            System.out.println("[SistemaDeHumo] Despliegue de humo activado. Disparador: \""
                    + CANCION_DISPARADORA + "\".");
        }
    }

    public int getDisparosRealizados() {
        return disparosRealizados;
    }
}
