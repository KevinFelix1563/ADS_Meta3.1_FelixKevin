package com.example.taberna;

import com.example.taberna.dominio.actores.Alquimista;
import com.example.taberna.dominio.actores.Banda;
import com.example.taberna.dominio.actores.Inventario;
import com.example.taberna.dominio.actores.PanelLED;
import com.example.taberna.dominio.actores.SistemaDeSonido;
import com.example.taberna.dominio.actores.Tabernero;
import com.example.taberna.dominio.eventos.BandaTocandoEvent;
import com.example.taberna.dominio.eventos.IngredienteAgotadoEvento;
import com.example.taberna.dominio.eventos.PocionElaboradaEvento;
import com.example.taberna.eventbus.EventBus;
import com.example.taberna.eventbus.Suscriptor;

import java.util.concurrent.TimeUnit;

/**
 * Simulador principal de "El Gólem Alquímico" - Fase 3 (Incremento 3).
 *
 * <p><strong>Demostración de Concurrencia y Desacoplamiento:</strong></p>
 * <ol>
 *   <li>La {@link Banda} opera como publicador asíncrono en un hilo secundario (pool dedicado),
 *       emitiendo periódicamente {@link BandaTocandoEvent}.</li>
 *   <li>El {@link SistemaDeSonido} y el {@link PanelLED} reaccionan a la música en tiempo real.</li>
 *   <li>En paralelo, el hilo principal ejecuta las labores alquímicas del {@link Alquimista},
 *       notificando al {@link Inventario}, al {@link Tabernero} y al {@link PanelLED}.</li>
 *   <li>Ambos subsistemas coexisten sin conocerse, coordinados exclusivamente a través del {@link EventBus}.</li>
 * </ol>
 */
public class TabernaSimulador {

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("  SIMULACIÓN CONCURRENTE: EL GÓLEM ALQUÍMICO (INCREMENTO 3)");
        System.out.println("=================================================================\n");

        // 1. Instanciamos el Bus central de eventos
        EventBus bus = new EventBus();

        // 2. Instanciamos los actores de la taberna
        Inventario inventario = new Inventario();
        Tabernero tabernero = new Tabernero("Barliman");
        Alquimista alquimista = new Alquimista("Nicolas Flamel", bus);
        SistemaDeSonido sonido = new SistemaDeSonido();
        PanelLED panelLED = new PanelLED();
        Banda banda = new Banda("Los Bardos del Caldero", bus);

        // 3. Suscripciones mediante referencias a métodos (::)
        // Suscriptores de Alquimia e Inventario
        bus.suscribir(PocionElaboradaEvento.class, inventario::registrarPocion);
        bus.suscribir(IngredienteAgotadoEvento.class, inventario::alertarFaltaIngrediente);

        // Suscripción del Tabernero (guardamos referencia para permitir desuscripción limpia)
        Suscriptor<PocionElaboradaEvento> celebracionTabernero = tabernero::celebrarElaboracion;
        bus.suscribir(PocionElaboradaEvento.class, celebracionTabernero);

        // Suscriptores de Música y Sonido
        bus.suscribir(BandaTocandoEvent.class, sonido::reproducirCancion);

        // Suscriptor Multievento: Panel LED escucha múltiples tipos de eventos
        bus.suscribir(BandaTocandoEvent.class, panelLED::mostrarMusica);
        bus.suscribir(PocionElaboradaEvento.class, panelLED::mostrarPocion);
        bus.suscribir(IngredienteAgotadoEvento.class, panelLED::mostrarAlertaIngrediente);

        System.out.println(">>> 1. INICIANDO LA BANDA EN HILO PARALELO (CONCURRENCIA) <<<");
        // Arranca la banda emitiendo una canción cada 900 milisegundos en segundo plano
        banda.iniciarConcierto(900, TimeUnit.MILLISECONDS);

        try {
            // Pausa breve para permitir que la banda inicie y suene la primera canción
            Thread.sleep(300);

            System.out.println("\n>>> 2. ACTIVIDAD EN HILO PRINCIPAL: ELABORACIÓN DE POCIONES <<<");
            alquimista.elaborarPocion("Poción Curativa Mayor", "Raíz de Mandrágora");

            // Simulamos tiempo de cocción en caldero mientras la banda continúa en paralelo
            Thread.sleep(1000);

            System.out.println("\n>>> 3. DESUSCRIPCIÓN DINÁMICA DEL TABERNERO <<<");
            System.out.println("[Sistema] El tabernero va a atender mesas (desuscribiendo de pociones)...");
            bus.desuscribir(PocionElaboradaEvento.class, celebracionTabernero);

            alquimista.elaborarPocion("Elixir de Fuerza de Titán", "Pelo de Minotauro");

            // Permitimos que la música suene un momento más
            Thread.sleep(1200);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Simulación interrumpida.");
        } finally {
            System.out.println("\n>>> 4. CIERRE CONTROLADO DE HILOS Y CONCIERTO <<<");
            banda.detenerConcierto();
        }

        System.out.println("\n=================================================================");
        System.out.println("  SIMULACIÓN CONCURRENTE FINALIZADA SATISFACTORIAMENTE");
        System.out.println("=================================================================");
    }
}
