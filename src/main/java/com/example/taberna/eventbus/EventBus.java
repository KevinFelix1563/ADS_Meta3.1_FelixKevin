package com.example.taberna.eventbus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Núcleo del patrón de diseño <strong>Event Bus</strong> (Bus de Eventos) para el sistema
 * "El Gólem Alquímico".
 *
 * <p><strong>¿Qué es un Event Bus?</strong></p>
 * El Event Bus actúa como un intermediario centralizado que permite la comunicación desacoplada
 * (desacoplamiento espacial y temporal) entre componentes que generan sucesos (publicadores)
 * y componentes interesados en reaccionar ante ellos (suscriptores). Ninguno de los dos
 * conoce la existencia directa del otro; solo conocen el tipo de evento que intercambian.
 *
 * <p><strong>Estructura interna de almacenamiento y Genéricos:</strong></p>
 * <pre>{@code
 * Map<Class<? extends Evento>, List<Suscriptor<? extends Evento>>>
 * }</pre>
 * Esta estructura implementa lo que Joshua Bloch denomina en <em>Effective Java</em> un
 * <strong>Contenedor Heterogéneo Seguro de Tipos</strong> (Typesafe Heterogeneous Container):
 * <ul>
 *   <li><strong>Clave ({@code Class<? extends Evento>}):</strong> Representa el <em>Type Token</em>
 *       o metadato de tipo en tiempo de ejecución. Permite indexar listas de suscriptores según
 *       la clase exacta del evento que escuchan.</li>
 *   <li><strong>Valor ({@code List<Suscriptor<? extends Evento>>}):</strong> Lista de suscriptores
 *       capaces de procesar eventos que extienden {@link Evento}.</li>
 * </ul>
 *
 * <p><strong>Garantía de Manejo de Tipos (Type-Safety):</strong></p>
 * Aunque en tiempo de ejecución ocurre el fenómeno de <em>Type Erasure</em> (borrado de tipos)
 * y el compilador de Java requeriría un casteo no verificado al despachar, la seguridad de tipos
 * queda <strong>estrictamente garantizada por la firma del método {@link #suscribir}</strong>:
 * al obligar a que el primer argumento sea {@code Class<T>} y el segundo sea {@code Suscriptor<T>},
 * es físicamente imposible para el programador asociar un suscriptor a un tipo de evento incompatible.
 */
public class EventBus {

    /**
     * Tabla hash interna que asocia cada clase concreta de evento con su lista de suscriptores.
     */
    private final Map<Class<? extends Evento>, List<Suscriptor<? extends Evento>>> suscriptores;

    /**
     * Construye una nueva instancia de {@code EventBus}.
     */
    public EventBus() {
        this.suscriptores = new HashMap<>();
    }

    /**
     * Registra un nuevo suscriptor para escuchar eventos de una clase específica.
     *
     * <p><strong>Explicación de Genéricos para el Estudiante:</strong></p>
     * La firma utiliza una variable de tipo acotada: {@code <T extends Evento>}.
     * Esto significa que {@code T} debe ser forzosamente una clase que implemente {@link Evento}.
     * Al recibir conjuntamente {@code Class<T> tipoEvento} y {@code Suscriptor<T> suscriptor},
     * el compilador infiere que ambos deben compartir <strong>exactamente el mismo tipo T</strong>.
     *
     * <p>Por ejemplo: Si pasas {@code PocionCreadaEvento.class}, el parámetro del suscriptor
     * debe ser obligatoriamente un {@code Suscriptor<PocionCreadaEvento>}. Si intentaras pasar
     * un {@code Suscriptor<DanoRecibidoEvento>}, el código ni siquiera compilaría.</p>
     *
     * @param <T> Tipo específico del evento.
     * @param tipoEvento Token de clase que representa la categoría de evento a escuchar.
     * @param suscriptor Instancia del suscriptor que recibirá las notificaciones.
     * @throws NullPointerException si {@code tipoEvento} o {@code suscriptor} son nulos.
     */
    public <T extends Evento> void suscribir(Class<T> tipoEvento, Suscriptor<T> suscriptor) {
        Objects.requireNonNull(tipoEvento, "El tipo de evento no puede ser nulo.");
        Objects.requireNonNull(suscriptor, "El suscriptor no puede ser nulo.");

        // computeIfAbsent crea la lista de suscriptores si es la primera vez que se registra este tipo de evento.
        // Dado que T extends Evento, Suscriptor<T> es compatible con Suscriptor<? extends Evento>.
        this.suscriptores
                .computeIfAbsent(tipoEvento, clave -> new ArrayList<>())
                .add(suscriptor);
    }

    /**
     * Da de baja a un suscriptor para un tipo de evento específico, evitando que continúe
     * recibiendo notificaciones futuras.
     *
     * @param <T> Tipo específico del evento.
     * @param tipoEvento Token de clase del evento del cual se retira la suscripción.
     * @param suscriptor Instancia del suscriptor a remover.
     */
    public <T extends Evento> void desuscribir(Class<T> tipoEvento, Suscriptor<T> suscriptor) {
        if (tipoEvento == null || suscriptor == null) {
            return;
        }

        List<Suscriptor<? extends Evento>> lista = this.suscriptores.get(tipoEvento);
        if (lista != null) {
            lista.remove(suscriptor);

            // Buena práctica de ingeniería de software: liberar memoria eliminando la clave
            // si la lista de suscriptores ha quedado vacía.
            if (lista.isEmpty()) {
                this.suscriptores.remove(tipoEvento);
            }
        }
    }

    /**
     * Sobrecarga conveniente que remueve un suscriptor de <em>todas</em> las listas de eventos
     * en las que pudiera estar registrado.
     *
     * @param suscriptor El suscriptor que se desea remover completamente del bus.
     */
    public void desuscribir(Suscriptor<?> suscriptor) {
        if (suscriptor == null) {
            return;
        }

        // Remueve el suscriptor de todas las listas asociadas
        this.suscriptores.values().forEach(lista -> lista.remove(suscriptor));

        // Limpia cualquier entrada en el mapa cuya lista haya quedado vacía
        this.suscriptores.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    /**
     * Publica un evento en el bus, despachándolo a todos los suscriptores registrados
     * para el tipo concreto de dicho evento.
     *
     * <p><strong>Flujo de Ejecución y Decisiones de Diseño:</strong></p>
     * <ol>
     *   <li><strong>Identificación Dinámica:</strong> Mediante {@code evento.getClass()}
     *       obtenemos en tiempo de ejecución la clase exacta del objeto publicado.</li>
     *   <li><strong>Copia Defensiva (Snapshot):</strong> Antes de iterar sobre la lista de suscriptores,
     *       creamos una copia con {@code new ArrayList<>(lista)}. Esto previene la temida
     *       {@link java.util.ConcurrentModificationException} en caso de que durante el manejo
     *       del evento (en {@code onEvento}), algún suscriptor decida desuscribirse o registrar uno nuevo.</li>
     *   <li><strong>Despacho Seguro:</strong> Se delega al método auxiliar {@link #notificar(Suscriptor, Evento)}
     *       para invocar el método de callback.</li>
     * </ol>
     *
     * @param <T> Tipo genérico del evento publicado.
     * @param evento Instancia del evento a notificar.
     */
    public <T extends Evento> void publicar(T evento) {
        if (evento == null) {
            return;
        }

        // Recuperamos la lista de suscriptores para la clase concreta de este evento
        List<Suscriptor<? extends Evento>> lista = this.suscriptores.get(evento.getClass());

        if (lista != null && !lista.isEmpty()) {
            // Copia defensiva para proteger el ciclo de notificación contra modificaciones concurrentes
            List<Suscriptor<? extends Evento>> copiaDefensiva = new ArrayList<>(lista);
            for (Suscriptor<? extends Evento> suscriptor : copiaDefensiva) {
                notificar(suscriptor, evento);
            }
        }
    }

    /**
     * Método auxiliar privado para realizar el despacho y la invocación del suscriptor.
     *
     * <p><strong>¿Por qué se requiere {@code @SuppressWarnings("unchecked")} aquí?</strong></p>
     * En Java, el comodín {@code ? extends Evento} representa un tipo desconocido que hereda de {@code Evento}.
     * El compilador no puede verificar estáticamente que el objeto {@code suscriptor} almacenado en la lista
     * coincide con el tipo del parámetro {@code evento}.
     *
     * <p>Sin embargo, gracias a la invariante establecida en {@link #suscribir(Class, Suscriptor)},
     * sabemos que un suscriptor solo entra a la lista mapeada con la clave {@code evento.getClass()}
     * si fue registrado específicamente para ese tipo de evento. Por lo tanto, el casteo
     * {@code (Suscriptor<T>) suscriptor} es conceptual y matemáticamente seguro en ejecución.</p>
     *
     * @param <T> Tipo del evento.
     * @param suscriptor Suscriptor recuperado del mapa.
     * @param evento Evento a entregar.
     */
    @SuppressWarnings("unchecked")
    private <T extends Evento> void notificar(Suscriptor<? extends Evento> suscriptor, T evento) {
        ((Suscriptor<T>) suscriptor).onEvento(evento);
    }

    /**
     * Consulta la cantidad actual de suscriptores registrados para un tipo de evento determinado.
     * Útil para pruebas unitarias, monitoreo y depuración.
     *
     * @param tipoEvento Clase del evento a consultar.
     * @return El número de suscriptores registrados, o {@code 0} si no hay ninguno.
     */
    public int contarSuscriptores(Class<? extends Evento> tipoEvento) {
        if (tipoEvento == null) {
            return 0;
        }
        List<Suscriptor<? extends Evento>> lista = this.suscriptores.get(tipoEvento);
        return lista != null ? lista.size() : 0;
    }

    /**
     * Verifica si existen suscriptores activos para un tipo de evento determinado.
     *
     * @param tipoEvento Clase del evento a consultar.
     * @return {@code true} si hay al menos un suscriptor registrado; {@code false} en caso contrario.
     */
    public boolean tieneSuscriptores(Class<? extends Evento> tipoEvento) {
        return contarSuscriptores(tipoEvento) > 0;
    }

    /**
     * Elimina todos los suscriptores registrados en el bus de eventos.
     * Ideal para restablecer el estado del sistema entre pruebas o reinicios.
     */
    public void limpiar() {
        this.suscriptores.clear();
    }
}
