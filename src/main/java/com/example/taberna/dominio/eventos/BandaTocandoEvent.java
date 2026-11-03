package com.example.taberna.dominio.eventos;

import com.example.taberna.eventbus.Evento;
import java.util.Objects;

/**
 * Evento de dominio emitido periódicamente y de manera asíncrona cuando una banda
 * musical está tocando en la taberna.
 *
 * <p><strong>Inmutabilidad con Records:</strong></p>
 * Al ser un {@code record}, todos sus componentes son inmutables ({@code final}),
 * lo cual resulta indispensable para entornos multihilo donde publicadores y
 * suscriptores operan en hilos diferentes.
 *
 * @param nombreBanda Nombre del grupo musical en escena (ej. "Los Bardos del Caldero").
 * @param nombreCancion Título de la pieza musical interpretada.
 * @param duracion Duración estimada de la canción en segundos.
 */
public record BandaTocandoEvent(String nombreBanda, String nombreCancion, int duracion) implements Evento {

    public BandaTocandoEvent {
        Objects.requireNonNull(nombreBanda, "El nombre de la banda no puede ser nulo.");
        Objects.requireNonNull(nombreCancion, "El nombre de la canción no puede ser nulo.");
        if (duracion <= 0) {
            throw new IllegalArgumentException("La duración debe ser mayor a cero.");
        }
    }
}
