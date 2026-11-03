package com.example.taberna.dominio.actores;

import com.example.taberna.dominio.eventos.BandaTocandoEvent;
import java.util.Objects;

/**
 * Representa el Sistema de Sonido y Amplificación de la taberna.
 *
 * <p><strong>Rol en el Patrón:</strong> Suscriptor (Subscriber).</p>
 * Escucha exclusivamente los eventos de tipo {@link BandaTocandoEvent} para ecualizar
 * y amplificar las melodías emitidas por la banda en vivo.
 */
public class SistemaDeSonido {

    private int volumenPrincipal;

    public SistemaDeSonido() {
        this.volumenPrincipal = 80; // Volumen base al 80%
    }

    /**
     * Callback para reaccionar ante una nueva pieza musical interpretada por la banda.
     * Compatible con {@code Suscriptor<BandaTocandoEvent>}.
     *
     * @param evento Información de la banda, canción y duración.
     */
    public void reproducirCancion(BandaTocandoEvent evento) {
        Objects.requireNonNull(evento, "El evento musical no puede ser nulo.");
        System.out.println("[SistemaDeSonido] 🔊 [Volumen " + this.volumenPrincipal + "%] "
                + "Amplificando \"" + evento.nombreCancion() + "\" interpretada por "
                + evento.nombreBanda() + " (Duración: " + evento.duracion() + "s)");
    }

    public void ajustarVolumen(int nuevoVolumen) {
        this.volumenPrincipal = Math.clamp(nuevoVolumen, 0, 100);
        System.out.println("[SistemaDeSonido] Volumen reajustado a: " + this.volumenPrincipal + "%");
    }

    public int getVolumenPrincipal() {
        return volumenPrincipal;
    }
}
