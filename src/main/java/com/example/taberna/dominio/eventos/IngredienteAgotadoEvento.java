package com.example.taberna.dominio.eventos;

import com.example.taberna.eventbus.Evento;
import java.util.Objects;

/**
 * Evento de dominio emitido cuando las reservas de un ingrediente crítico se han agotado
 * durante el proceso alquímico en la taberna.
 *
 * <p>Al igual que {@link PocionElaboradaEvento}, se define como un {@code record} inmutable
 * para evitar efectos secundarios imprevistos en los suscriptores.</p>
 *
 * @param ingrediente Nombre del ingrediente que se ha terminado en el almacén.
 */
public record IngredienteAgotadoEvento(String ingrediente) implements Evento {

    public IngredienteAgotadoEvento {
        Objects.requireNonNull(ingrediente, "El nombre del ingrediente no puede ser nulo.");
    }
}
