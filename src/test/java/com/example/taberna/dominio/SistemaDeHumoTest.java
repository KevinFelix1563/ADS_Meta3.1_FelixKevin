package com.example.taberna.dominio;

import com.example.taberna.dominio.actores.SistemaDeHumo;
import com.example.taberna.dominio.eventos.BandaTocandoEvent;
import com.example.taberna.eventbus.EventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para validar SistemaDeHumo y la demostración de OCP.
 */
class SistemaDeHumoTest {

    private EventBus bus;
    private SistemaDeHumo humo;

    @BeforeEach
    void setUp() {
        bus = new EventBus();
        humo = new SistemaDeHumo();
        bus.suscribir(BandaTocandoEvent.class, humo::accionarHumo);
    }

    @Test
    @DisplayName("Debe disparar humo únicamente si la canción es 'Through the Fire and Flames'")
    void testDisparoHumoConCancionCorrecta() {
        assertEquals(0, humo.getDisparosRealizados());

        // Otra canción no dispara humo
        bus.publicar(new BandaTocandoEvent("Los Bardos", "El Vals de la Mandrágora", 200));
        assertEquals(0, humo.getDisparosRealizados());

        // La canción detonadora activa el humo
        bus.publicar(new BandaTocandoEvent("Los Bardos", "Through the Fire and Flames", 440));
        assertEquals(1, humo.getDisparosRealizados());

        // Tolerancia a mayúsculas/minúsculas o espacios extra
        bus.publicar(new BandaTocandoEvent("DragonForce", "  through the fire and flames  ", 420));
        assertEquals(2, humo.getDisparosRealizados());
    }
}
