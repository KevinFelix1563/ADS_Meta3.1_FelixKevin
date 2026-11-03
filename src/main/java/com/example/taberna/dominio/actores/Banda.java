package com.example.taberna.dominio.actores;

import com.example.taberna.dominio.eventos.BandaTocandoEvent;
import com.example.taberna.eventbus.EventBus;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Representa a la Banda musical de la taberna.
 *
 * <p><strong>Rol en el Patrón:</strong> Publicador Concurrente (Asynchronous Publisher).</p>
 *
 * <p><strong>Estrategia de Concurrencia:</strong></p>
 * En lugar de bloquear el hilo principal de la aplicación con {@code Thread.sleep()},
 * la banda gestiona un {@link ScheduledExecutorService} interno con un hilo dedicado (pool de 1 hilo).
 * Este planificador dispara de forma periódica la publicación de un {@link BandaTocandoEvent} en el
 * {@link EventBus}, simulando el paso del tiempo mientras el resto de la taberna (como el
 * alquimista elaborando pociones) continúa operando en paralelo sin interferencias.
 */
public class Banda implements AutoCloseable {

    private final String nombre;
    private final EventBus bus;
    private final List<Cancion> repertorio;
    private final AtomicInteger indiceCancion;

    private ScheduledExecutorService scheduler;
    private volatile boolean tocando;

    /**
     * Registro auxiliar inmutable para estructurar el repertorio musical.
     */
    public record Cancion(String titulo, int duracionSegundos) {}

    /**
     * Construye una nueva Banda con su nombre y el bus de eventos inyectado.
     *
     * @param nombre Nombre de la agrupación (ej. "Los Bardos del Caldero").
     * @param bus Bus central de eventos.
     */
    public Banda(String nombre, EventBus bus) {
        this.nombre = Objects.requireNonNull(nombre, "El nombre de la banda no puede ser nulo.");
        this.bus = Objects.requireNonNull(bus, "El EventBus no puede ser nulo.");
        this.indiceCancion = new AtomicInteger(0);
        this.tocando = false;

        // Repertorio inicial de música tabernera
        this.repertorio = List.of(
                new Cancion("La Balada del Gólem de Arcilla", 180),
                new Cancion("El Vals de la Mandrágora", 210),
                new Cancion("Ritmo y Fuego Alquímico", 195),
                new Cancion("Brindis con Elixir de Dragón", 240)
        );
    }

    /**
     * Inicia la actuación de la banda en un hilo concurrente independiente.
     *
     * @param periodo Intervalo de tiempo entre canciones.
     * @param unidad Unidad de tiempo del intervalo.
     */
    public synchronized void iniciarConcierto(long periodo, TimeUnit unidad) {
        if (this.tocando) {
            System.out.println("[Banda: " + this.nombre + "] La banda ya se encuentra en ejecución.");
            return;
        }

        this.tocando = true;
        // Creamos un ScheduledExecutorService con hilo tipo Daemon o hilo gestionado
        this.scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "Hilo-Banda-" + this.nombre);
            thread.setDaemon(true); // Permite que la JVM finalice sin trabas si el programa termina
            return thread;
        });

        System.out.println("[Banda: " + this.nombre + "] Inicio de presentación en escenario.");

        // Programamos la ejecución periódica
        this.scheduler.scheduleAtFixedRate(this::tocarSiguientePieza, 0, periodo, unidad);
    }

    /**
     * Método invocado periódicamente por el hilo del scheduler.
     * Selecciona la siguiente canción y publica el evento en el bus.
     */
    private void tocarSiguientePieza() {
        if (!this.tocando) {
            return;
        }

        // Selección cíclica de canción del repertorio
        int indice = this.indiceCancion.getAndIncrement() % this.repertorio.size();
        Cancion cancionActual = this.repertorio.get(indice);

        BandaTocandoEvent evento = new BandaTocandoEvent(
                this.nombre,
                cancionActual.titulo(),
                cancionActual.duracionSegundos()
        );

        System.out.println("[Banda: " + this.nombre + "] Iniciando ejecución de pista: \""
                + cancionActual.titulo() + "\"");

        // Publicación del evento hacia el EventBus
        this.bus.publicar(evento);
    }

    /**
     * Detiene la actuación de la banda y apaga ordenadamente el hilo planificador.
     */
    public synchronized void detenerConcierto() {
        if (!this.tocando) {
            return;
        }

        this.tocando = false;
        System.out.println("[Banda: " + this.nombre + "] Finalización de presentación musical.");

        if (this.scheduler != null && !this.scheduler.isShutdown()) {
            this.scheduler.shutdown();
            try {
                if (!this.scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                    this.scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                this.scheduler.shutdownNow();
            }
        }
    }

    public boolean isTocando() {
        return tocando;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public void close() {
        detenerConcierto();
    }
}
