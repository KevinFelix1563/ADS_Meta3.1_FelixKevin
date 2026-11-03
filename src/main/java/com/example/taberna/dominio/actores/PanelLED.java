package com.example.taberna.dominio.actores;

import com.example.taberna.dominio.eventos.BandaTocandoEvent;
import com.example.taberna.dominio.eventos.IngredienteAgotadoEvento;
import com.example.taberna.dominio.eventos.PocionElaboradaEvento;

import java.util.Objects;

/**
 * Representa el Panel LED informativo instalado en el centro de la taberna.
 *
 * <p><strong>Rol en el Patrón:</strong> Suscriptor Multievento (Multi-Event Subscriber).</p>
 *
 * <p>A diferencia de suscriptores monotemáticos, el {@code PanelLED} demuestra cómo un
 * mismo componente puede registrarse a <strong>múltiples tipos de eventos</strong> disjuntos
 * sin acoplar los emisores entre sí. Dispone de métodos independientes adaptados
 * para cada tipo de suceso.</p>
 */
public class PanelLED {

    public PanelLED() {
        // Constructor por defecto
    }

    /**
     * Muestra anuncios de música en vivo en la marquesina LED.
     * Compatible con {@code Suscriptor<BandaTocandoEvent>}.
     */
    public void mostrarMusica(BandaTocandoEvent evento) {
        Objects.requireNonNull(evento, "El evento de banda no puede ser nulo.");
        System.out.println("[PanelLED] 🌟 [CARTEL LUMINOSO] >> En el escenario: "
                + evento.nombreBanda() + " tocando \"" + evento.nombreCancion() + "\" <<");
    }

    /**
     * Muestra anuncios de nuevas pociones disponibles en la barra.
     * Compatible con {@code Suscriptor<PocionElaboradaEvento>}.
     */
    public void mostrarPocion(PocionElaboradaEvento evento) {
        Objects.requireNonNull(evento, "El evento de poción no puede ser nulo.");
        System.out.println("[PanelLED] 🧪 [OFERTA TABERNA] >> ¡Poción fresca en barra: "
                + evento.nombrePocion() + " por " + evento.alquimista() + "! <<");
    }

    /**
     * Muestra avisos urgentes en rojo ante la falta de suministros críticos.
     * Compatible con {@code Suscriptor<IngredienteAgotadoEvento>}.
     */
    public void mostrarAlertaIngrediente(IngredienteAgotadoEvento evento) {
        Objects.requireNonNull(evento, "El evento de ingrediente no puede ser nulo.");
        System.out.println("[PanelLED] 🚨 [ALERTA CRÍTICA] >> SE AGOTÓ: "
                + evento.ingrediente().toUpperCase() + " (Favor de no pedir bebidas con este ingrediente) <<");
    }
}
