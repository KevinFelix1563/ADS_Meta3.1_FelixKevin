package com.example.taberna;

import com.example.taberna.dominio.actores.Alquimista;
import com.example.taberna.dominio.actores.Banda;
import com.example.taberna.dominio.actores.Inventario;
import com.example.taberna.dominio.actores.PanelLED;
import com.example.taberna.dominio.actores.SistemaDeHumo;
import com.example.taberna.dominio.actores.SistemaDeSonido;
import com.example.taberna.dominio.actores.Tabernero;
import com.example.taberna.dominio.eventos.BandaTocandoEvent;
import com.example.taberna.dominio.eventos.IngredienteAgotadoEvento;
import com.example.taberna.dominio.eventos.PocionElaboradaEvento;
import com.example.taberna.eventbus.EventBus;
import com.example.taberna.eventbus.Suscriptor;

import java.util.concurrent.TimeUnit;

/**
 * Simulador principal de "El Gólem Alquímico" - Fase 4 (Incremento 4).
 *
 * <p><strong>Demostración del Principio Open/Closed (OCP):</strong></p>
 * En este incremento se incorpora el nuevo suscriptor {@link SistemaDeHumo}
 * para reaccionar exclusivamente cuando suena "Through the Fire and Flames".
 * Se demuestra que la arquitectura basada en {@link EventBus} permite extender el
 * comportamiento del sistema agregando nuevos componentes sin alterar la lógica interna
 * de los publicadores existentes ({@link Alquimista}, {@link Banda}) ni la infraestructura
 * del bus.
 */
public class TabernaSimulador {

    public static void main(String[] args) {
        System.out.println("[INFO] Sistema de eventos inicializado.");

        // 1. Instanciamos el Bus central de eventos
        EventBus bus = new EventBus();

        // 2. Instanciamos los actores de la taberna (existentes y nuevo)
        Inventario inventario = new Inventario();
        Tabernero tabernero = new Tabernero("Barliman");
        Alquimista alquimista = new Alquimista("Nicolas Flamel", bus);
        SistemaDeSonido sonido = new SistemaDeSonido();
        PanelLED panelLED = new PanelLED();
        SistemaDeHumo sistemaDeHumo = new SistemaDeHumo(); // Nuevo suscriptor OCP
        Banda banda = new Banda("Los Bardos del Caldero", bus);

        // 3. Suscripciones al EventBus mediante referencias a métodos (::)
        // Subsistema de Alquimia
        bus.suscribir(PocionElaboradaEvento.class, inventario::registrarPocion);
        bus.suscribir(IngredienteAgotadoEvento.class, inventario::alertarFaltaIngrediente);

        Suscriptor<PocionElaboradaEvento> celebracionTabernero = tabernero::celebrarElaboracion;
        bus.suscribir(PocionElaboradaEvento.class, celebracionTabernero);

        // Subsistema de Música y Efectos de Escenario
        bus.suscribir(BandaTocandoEvent.class, sonido::reproducirCancion);
        bus.suscribir(BandaTocandoEvent.class, sistemaDeHumo::accionarHumo); // Suscripción del nuevo componente

        // Suscriptor Multievento
        bus.suscribir(BandaTocandoEvent.class, panelLED::mostrarMusica);
        bus.suscribir(PocionElaboradaEvento.class, panelLED::mostrarPocion);
        bus.suscribir(IngredienteAgotadoEvento.class, panelLED::mostrarAlertaIngrediente);

        System.out.println("[INFO] Iniciando componentes de ejecución concurrente.");
        banda.iniciarConcierto(800, TimeUnit.MILLISECONDS);

        try {
            // Breve espera para que la banda arranque y suene la primera pieza del repertorio
            Thread.sleep(400);

            System.out.println("[INFO] Ejecución de actividades en hilo principal.");
            alquimista.elaborarPocion("Poción Curativa Mayor", "Raíz de Mandrágora");

            Thread.sleep(900);

            System.out.println("[INFO] Ejecución de pista especial solicitada.");
            System.out.println("[Banda: " + banda.getNombre() + "] Iniciando ejecución de pista: \"Through the Fire and Flames\"");
            bus.publicar(new BandaTocandoEvent(banda.getNombre(), "Through the Fire and Flames", 440));

            Thread.sleep(800);

            System.out.println("[INFO] Actualización de suscripciones activas.");
            bus.desuscribir(PocionElaboradaEvento.class, celebracionTabernero);
            alquimista.elaborarPocion("Elixir de Fuerza de Titán", "Pelo de Minotauro");

            Thread.sleep(900);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[ERROR] Proceso interrumpido.");
        } finally {
            System.out.println("[INFO] Deteniendo componentes concurrentes.");
            banda.detenerConcierto();
        }

        System.out.println("[INFO] Cierre del sistema completado.");
    }
}
