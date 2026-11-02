package com.example.taberna.eventbus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para validar la arquitectura base del EventBus.
 */
class EventBusTest {

    private EventBus bus;

    // Eventos ficticios mínimos para testing interno
    private static class EventoPruebaA implements Evento {
        private final String mensaje;
        public EventoPruebaA(String mensaje) { this.mensaje = mensaje; }
        public String getMensaje() { return mensaje; }
    }

    private static class EventoPruebaB implements Evento {
        private final int valor;
        public EventoPruebaB(int valor) { this.valor = valor; }
        public int getValor() { return valor; }
    }

    @BeforeEach
    void setUp() {
        bus = new EventBus();
    }

    @Test
    @DisplayName("Debe registrar suscriptores y despachar eventos correctamente")
    void testSuscribirYPublicar() {
        List<String> recibidos = new ArrayList<>();

        bus.suscribir(EventoPruebaA.class, evento -> recibidos.add(evento.getMensaje()));
        bus.publicar(new EventoPruebaA("Hola Gólem"));

        assertEquals(1, recibidos.size());
        assertEquals("Hola Gólem", recibidos.get(0));
    }

    @Test
    @DisplayName("Los suscriptores de un tipo de evento no deben recibir eventos de otro tipo")
    void testAislamientoDeEventos() {
        AtomicInteger conteoA = new AtomicInteger(0);
        AtomicInteger conteoB = new AtomicInteger(0);

        bus.suscribir(EventoPruebaA.class, e -> conteoA.incrementAndGet());
        bus.suscribir(EventoPruebaB.class, e -> conteoB.incrementAndGet());

        bus.publicar(new EventoPruebaA("Solo para A"));

        assertEquals(1, conteoA.get(), "El suscriptor de A debió recibir el evento");
        assertEquals(0, conteoB.get(), "El suscriptor de B NO debió recibir el evento");
    }

    @Test
    @DisplayName("Debe desuscribir un suscriptor específico para una clase de evento")
    void testDesuscribirPorClase() {
        AtomicInteger contador = new AtomicInteger(0);
        Suscriptor<EventoPruebaA> suscriptor = e -> contador.incrementAndGet();

        bus.suscribir(EventoPruebaA.class, suscriptor);
        assertEquals(1, bus.contarSuscriptores(EventoPruebaA.class));

        bus.publicar(new EventoPruebaA("Primero"));
        assertEquals(1, contador.get());

        bus.desuscribir(EventoPruebaA.class, suscriptor);
        assertEquals(0, bus.contarSuscriptores(EventoPruebaA.class));

        bus.publicar(new EventoPruebaA("Segundo"));
        assertEquals(1, contador.get(), "No debe incrementar tras desuscribirse");
    }

    @Test
    @DisplayName("La desuscripción global debe remover al suscriptor de todas las listas")
    void testDesuscribirGlobal() {
        Suscriptor<EventoPruebaA> subA = e -> {};
        Suscriptor<EventoPruebaB> subB = e -> {};

        bus.suscribir(EventoPruebaA.class, subA);
        bus.suscribir(EventoPruebaB.class, subB);

        assertEquals(1, bus.contarSuscriptores(EventoPruebaA.class));
        assertEquals(1, bus.contarSuscriptores(EventoPruebaB.class));

        bus.desuscribir(subA);

        assertEquals(0, bus.contarSuscriptores(EventoPruebaA.class));
        assertEquals(1, bus.contarSuscriptores(EventoPruebaB.class));

        bus.desuscribir(subB);
        assertEquals(0, bus.contarSuscriptores(EventoPruebaB.class));
    }

    @Test
    @DisplayName("Copia defensiva: desuscribirse dentro del callback no debe lanzar ConcurrentModificationException")
    void testDesuscripcionDuranteNotificacion() {
        AtomicBoolean ejecutado = new AtomicBoolean(false);
        java.util.concurrent.atomic.AtomicReference<Suscriptor<EventoPruebaA>> ref = new java.util.concurrent.atomic.AtomicReference<>();

        ref.set(evento -> {
            ejecutado.set(true);
            bus.desuscribir(EventoPruebaA.class, ref.get());
        });

        bus.suscribir(EventoPruebaA.class, ref.get());
        assertDoesNotThrow(() -> bus.publicar(new EventoPruebaA("Test concurrencia")));
        assertTrue(ejecutado.get());
        assertEquals(0, bus.contarSuscriptores(EventoPruebaA.class));
    }

    @Test
    @DisplayName("Debe rechazar nulos al suscribir y tolerar nulos al publicar y desuscribir")
    void testManejoDeNulos() {
        assertThrows(NullPointerException.class, () -> bus.suscribir(null, e -> {}));
        assertThrows(NullPointerException.class, () -> bus.suscribir(EventoPruebaA.class, null));

        assertDoesNotThrow(() -> bus.publicar(null));
        assertDoesNotThrow(() -> bus.desuscribir(null, null));
        assertDoesNotThrow(() -> bus.desuscribir((Suscriptor<?>) null));
    }
}
