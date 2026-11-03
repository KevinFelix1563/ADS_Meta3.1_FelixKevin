package com.example.taberna.dominio.actores;

import com.example.taberna.dominio.eventos.IngredienteAgotadoEvento;
import com.example.taberna.dominio.eventos.PocionElaboradaEvento;

import java.util.Objects;

/**
 * Representa el Almacén / Inventario de la taberna.
 *
 * <p><strong>Patrón Observer y Referencias a Métodos:</strong></p>
 * Observa que esta clase <strong>no necesita implementar explícitamente {@code Suscriptor<T>}</strong>.
 * Gracias a que {@code Suscriptor<T>} es una {@link FunctionalInterface} con el método
 * {@code void onEvento(T evento)}, cualquier método de instancia que reciba como único parámetro
 * un subtipo de {@link com.example.taberna.eventbus.Evento} y retorne {@code void} es automáticamente
 * compatible con la interfaz mediante referencias a métodos (<i>method references</i>, {@code ::}).
 */
public class Inventario {

    public Inventario() {
        // Constructor por defecto
    }

    /**
     * Callback para reaccionar a la creación de una nueva poción.
     * Compatible con {@code Suscriptor<PocionElaboradaEvento>}.
     *
     * @param evento Evento con los detalles de la poción elaborada.
     */
    public void registrarPocion(PocionElaboradaEvento evento) {
        Objects.requireNonNull(evento, "El evento de poción elaborada no puede ser nulo.");
        System.out.println("[Inventario] Registrando +1 " + evento.nombrePocion() + " creada por " + evento.alquimista());
    }

    /**
     * Callback para reaccionar al aviso de ingrediente agotado.
     * Compatible con {@code Suscriptor<IngredienteAgotadoEvento>}.
     *
     * @param evento Evento con los detalles del ingrediente agotado.
     */
    public void alertarFaltaIngrediente(IngredienteAgotadoEvento evento) {
        Objects.requireNonNull(evento, "El evento de ingrediente agotado no puede ser nulo.");
        System.out.println("[Inventario] ALERTA: Comprar más " + evento.ingrediente());
    }
}
