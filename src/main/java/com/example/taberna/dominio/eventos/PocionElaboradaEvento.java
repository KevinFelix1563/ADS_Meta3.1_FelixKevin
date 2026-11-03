package com.example.taberna.dominio.eventos;

import com.example.taberna.eventbus.Evento;
import java.util.Objects;

/**
 * Evento de dominio emitido cuando un alquimista completa la elaboración de una poción.
 *
 * <p><strong>¿Por qué usar un {@code record} para eventos de dominio?</strong></p>
 * En arquitectura orientada a eventos (EDA - Event-Driven Architecture), los eventos representan
 * hechos inmutables que ocurrieron en el pasado.
 * Los {@code record} de Java son ideales porque:
 * <ul>
 *   <li>Son <strong>inmutables</strong> por definición (sus campos son {@code private final}).</li>
 *   <li>Garantizan <strong>seguridad en hilos (thread-safety)</strong> ya que su estado no puede mutar tras ser publicado.</li>
 *   <li>Generan automáticamente métodos canónicos: componentes de acceso ({@code nombrePocion()}, {@code alquimista()}),
 *       {@code equals()}, {@code hashCode()} y un {@code toString()} informativo.</li>
 * </ul>
 *
 * @param nombrePocion Nombre descriptivo de la poción fabricada.
 * @param alquimista Nombre del alquimista que la creó.
 */
public record PocionElaboradaEvento(String nombrePocion, String alquimista) implements Evento {

    public PocionElaboradaEvento {
        Objects.requireNonNull(nombrePocion, "El nombre de la poción no puede ser nulo.");
        Objects.requireNonNull(alquimista, "El nombre del alquimista no puede ser nulo.");
    }
}
