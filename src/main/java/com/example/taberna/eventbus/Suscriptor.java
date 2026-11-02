package com.example.taberna.eventbus;

/**
 * Interfaz genérica para los observadores o suscriptores (listeners) en el patrón Event Bus.
 *
 * <p><strong>Manejo de Genéricos ({@code <T extends Evento>}):</strong></p>
 * El parámetro de tipo acotado superiormente (<em>bounded type parameter</em>)
 * {@code <T extends Evento>} restringe el tipo de datos que este suscriptor puede manejar:
 * solo clases o interfaces que implementen {@link Evento}.
 * De esta manera, el compilador previene errores conceptuales como intentar crear un
 * {@code Suscriptor<String>}.
 *
 * <p><strong>Diseño Funcional ({@link FunctionalInterface}):</strong></p>
 * Al poseer un único método abstracto (SAM - Single Abstract Method), esta interfaz
 * califica como una interfaz funcional. Esto permite a los estudiantes y desarrolladores
 * registrar manejadores tanto mediante clases tradicionales como de forma elegante
 * a través de lambdas y referencias a métodos:
 * <pre>{@code
 * // Ejemplo usando una lambda:
 * eventBus.suscribir(MiEvento.class, evento -> {
 *     System.out.println("Evento recibido: " + evento);
 * });
 *
 * // O usando una referencia a método:
 * eventBus.suscribir(MiEvento.class, this::procesarEvento);
 * }</pre>
 *
 * @param <T> El tipo específico de evento que este suscriptor tiene la capacidad de procesar.
 */
@FunctionalInterface
public interface Suscriptor<T extends Evento> {

    /**
     * Método invocado cuando se publica en el {@link EventBus} un evento del tipo {@code T}
     * para el cual este suscriptor se encuentra registrado.
     *
     * @param evento Instancia del evento que contiene el contexto y los datos asociados al suceso.
     */
    void onEvento(T evento);
}
