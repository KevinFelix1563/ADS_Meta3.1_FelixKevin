package com.example.taberna.dominio.actores;

import com.example.taberna.dominio.eventos.PocionElaboradaEvento;

import java.util.Objects;

/**
 * Representa al Tabernero (ej. "Barliman"), quien atiende a los clientes y celebra
 * en voz alta las creaciones del alquimista.
 *
 * <p>Al igual que {@link Inventario}, esta clase actúa como un suscriptor desacoplado
 * cuyos métodos se conectan al {@link com.example.taberna.eventbus.EventBus} mediante
 * referencias a métodos de instancia.</p>
 */
public class Tabernero {

    private final String nombre;

    /**
     * Construye un nuevo Tabernero con su nombre.
     *
     * @param nombre Nombre del tabernero.
     */
    public Tabernero(String nombre) {
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo.");
    }

    /**
     * Anuncia a viva voz a la taberna la preparación de una nueva poción.
     *
     * @param evento Evento con los detalles de la poción elaborada.
     */
    public void celebrarElaboracion(PocionElaboradaEvento evento) {
        Objects.requireNonNull(evento, "El evento de poción elaborada no puede ser nulo.");
        System.out.println("[Tabernero: " + this.nombre + "] Anuncio al público: Nueva poción disponible en barra - "
                + evento.nombrePocion() + ".");
    }

    public String getNombre() {
        return nombre;
    }
}
