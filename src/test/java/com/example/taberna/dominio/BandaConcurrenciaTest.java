package com.example.taberna.dominio;

import com.example.taberna.dominio.actores.Banda;
import com.example.taberna.dominio.actores.PanelLED;
import com.example.taberna.dominio.actores.SistemaDeSonido;
import com.example.taberna.dominio.eventos.BandaTocandoEvent;
import com.example.taberna.dominio.eventos.IngredienteAgotadoEvento;
import com.example.taberna.dominio.eventos.PocionElaboradaEvento;
import com.example.taberna.eventbus.EventBus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para validar la concurrencia y los nuevos componentes del Incremento 3.
 */
class BandaConcurrenciaTest {

    private EventBus bus;
    private Banda banda;
    private SistemaDeSonido sonido;
    private PanelLED panelLED;

    @BeforeEach
    void setUp() {
        bus = new EventBus();
        banda = new Banda("Los Trovadores", bus);
        sonido = new SistemaDeSonido();
        panelLED = new PanelLED();
    }

    @AfterEach
    void tearDown() {
        banda.detenerConcierto();
    }

    @Test
    @DisplayName("BandaTocandoEvent valida campos nulos y duración positiva")
    void testValidacionEventoBanda() {
        assertThrows(NullPointerException.class, () -> new BandaTocandoEvent(null, "Cancion", 120));
        assertThrows(NullPointerException.class, () -> new BandaTocandoEvent("Banda", null, 120));
        assertThrows(IllegalArgumentException.class, () -> new BandaTocandoEvent("Banda", "Cancion", 0));
        assertThrows(IllegalArgumentException.class, () -> new BandaTocandoEvent("Banda", "Cancion", -10));

        BandaTocandoEvent evento = new BandaTocandoEvent("Banda", "Cancion", 150);
        assertEquals("Banda", evento.nombreBanda());
        assertEquals("Cancion", evento.nombreCancion());
        assertEquals(150, evento.duracion());
    }

    @Test
    @DisplayName("Banda debe publicar periódicamente en un hilo concurrente")
    void testPublicacionConcurrenteBanda() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        CopyOnWriteArrayList<BandaTocandoEvent> eventosRecibidos = new CopyOnWriteArrayList<>();

        bus.suscribir(BandaTocandoEvent.class, evento -> {
            eventosRecibidos.add(evento);
            latch.countDown();
        });

        // Conectar el sistema de sonido y el panel LED
        bus.suscribir(BandaTocandoEvent.class, sonido::reproducirCancion);
        bus.suscribir(BandaTocandoEvent.class, panelLED::mostrarMusica);

        banda.iniciarConcierto(100, TimeUnit.MILLISECONDS);

        // Esperar a que se reciban al menos 2 eventos en el latch
        boolean completado = latch.await(2, TimeUnit.SECONDS);

        assertTrue(completado, "Debieron recibirse al menos 2 eventos de la banda en el tiempo límite");
        assertFalse(eventosRecibidos.isEmpty());
        assertTrue(banda.isTocando());

        banda.detenerConcierto();
        assertFalse(banda.isTocando());
    }

    @Test
    @DisplayName("PanelLED debe tolerar y procesar múltiples tipos de eventos sin errores")
    void testPanelLEDMultievento() {
        assertDoesNotThrow(() -> {
            panelLED.mostrarMusica(new BandaTocandoEvent("Banda", "Vals", 100));
            panelLED.mostrarPocion(new PocionElaboradaEvento("Poción X", "Alquimista"));
            panelLED.mostrarAlertaIngrediente(new IngredienteAgotadoEvento("Mandrágora"));
        });
    }

    @Test
    @DisplayName("SistemaDeSonido debe ajustar el volumen dentro del rango 0 a 100")
    void testAjusteVolumen() {
        sonido.ajustarVolumen(50);
        assertEquals(50, sonido.getVolumenPrincipal());

        sonido.ajustarVolumen(150);
        assertEquals(100, sonido.getVolumenPrincipal());

        sonido.ajustarVolumen(-20);
        assertEquals(0, sonido.getVolumenPrincipal());
    }
}
