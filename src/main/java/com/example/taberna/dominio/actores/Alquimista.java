package com.example.taberna.dominio.actores;

import com.example.taberna.dominio.eventos.IngredienteAgotadoEvento;
import com.example.taberna.dominio.eventos.PocionElaboradaEvento;
import com.example.taberna.eventbus.EventBus;

import java.util.Objects;

/**
 * Representa al Alquimista en el sistema "El Gólem Alquímico", quien asume el rol de
 * <strong>Publicador (Publisher)</strong> en el patrón Event Bus.
 *
 * <p><strong>Patrón de Inyección de Dependencias:</strong></p>
 * En lugar de que el alquimista cree o acceda a un {@link EventBus} globalmente (por ejemplo,
 * mediante un Singleton estático), la instancia de {@code EventBus} se inyecta a través de su
 * constructor. Esto favorece la inversión de control, la modularidad y facilita las pruebas unitarias.
 */
public class Alquimista {

    private final String nombre;
    private final EventBus bus;

    /**
     * Construye un nuevo Alquimista con su nombre y el bus de eventos inyectado.
     *
     * @param nombre Nombre del alquimista (ej. "Nicolas Flamel").
     * @param bus Instancia del {@link EventBus} a través del cual publicará sus eventos.
     */
    public Alquimista(String nombre, EventBus bus) {
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo.");
        this.bus = Objects.requireNonNull(bus, "El EventBus no puede ser nulo.");
    }

    /**
     * Simula el proceso alquímico de preparación de una poción.
     *
     * <p><strong>Flujo del Publicador:</strong></p>
     * <ol>
     *   <li>Imprime en la consola el inicio del proceso.</li>
     *   <li>Emite el evento {@link PocionElaboradaEvento} para notificar a los interesados (inventario, tabernero, etc.).</li>
     *   <li>Evalúa una condición de consumo y, si el ingrediente clave se desgasta, emite {@link IngredienteAgotadoEvento}.</li>
     * </ol>
     *
     * @param pocion Nombre de la poción que se va a elaborar.
     * @param ingredienteClave Ingrediente principal consumido en la receta.
     */
    public void elaborarPocion(String pocion, String ingredienteClave) {
        Objects.requireNonNull(pocion, "La poción no puede ser nula.");

        // a) Mensaje de actividad en consola
        System.out.println("[Alquimista " + this.nombre + "] elaborando " + pocion + "...");

        // b) Notificación de elaboración exitosa a través del bus
        this.bus.publicar(new PocionElaboradaEvento(pocion, this.nombre));

        // c) Condición de simulación: si se utilizó un ingrediente clave, notificamos su agotamiento
        if (ingredienteClave != null && !ingredienteClave.isBlank()) {
            this.bus.publicar(new IngredienteAgotadoEvento(ingredienteClave));
        }
    }

    public String getNombre() {
        return nombre;
    }
}
