package com.example.taberna;

import com.example.taberna.dominio.actores.Alquimista;
import com.example.taberna.dominio.actores.Inventario;
import com.example.taberna.dominio.actores.Tabernero;
import com.example.taberna.dominio.eventos.IngredienteAgotadoEvento;
import com.example.taberna.dominio.eventos.PocionElaboradaEvento;
import com.example.taberna.eventbus.EventBus;
import com.example.taberna.eventbus.Suscriptor;

/**
 * Clase principal de simulación e integración para la Fase 2 (Incremento 2)
 * del sistema "El Gólem Alquímico".
 *
 * <p>Demuestra el desacoplamiento total entre el publicador ({@link Alquimista})
 * y los suscriptores ({@link Inventario} y {@link Tabernero}), orquestados por el
 * {@link EventBus} mediante referencias a métodos (<i>method references</i>).</p>
 */
public class TabernaSimulador {

    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println("  SIMULACIÓN: EL GÓLEM ALQUÍMICO - TABERNA (INCREMENTO 2)");
        System.out.println("=========================================================\n");

        // a) Instanciamos el Event Bus central
        EventBus bus = new EventBus();

        // b) Instanciamos los actores del dominio
        Inventario inventario = new Inventario();
        Tabernero tabernero = new Tabernero("Barliman");
        Alquimista alquimista = new Alquimista("Nicolas Flamel", bus);

        // c) Suscripción mediante referencias a métodos (::)
        // Nota pedagógica: Para poder desuscribir una referencia a método posteriormente,
        // guardamos la instancia de Suscriptor en una variable de referencia; de lo contrario,
        // cada uso de `tabernero::celebrarElaboracion` crearía una nueva instancia anónima en memoria.
        Suscriptor<PocionElaboradaEvento> celebracionTabernero = tabernero::celebrarElaboracion;

        bus.suscribir(PocionElaboradaEvento.class, inventario::registrarPocion);
        bus.suscribir(IngredienteAgotadoEvento.class, inventario::alertarFaltaIngrediente);
        bus.suscribir(PocionElaboradaEvento.class, celebracionTabernero);

        System.out.println("--- Ronda 1: Elaboración con todos los suscriptores activos ---");
        // d) El alquimista elabora la primera poción
        alquimista.elaborarPocion("Poción Curativa Mayor", "Raíz de Mandrágora");

        System.out.println("\n--- Ronda 2: Desuscripción del Tabernero ---");
        // e) Demostración de desuscripción
        System.out.println("[Sistema] El tabernero se retira a descansar (desuscribiendo)...");
        bus.desuscribir(PocionElaboradaEvento.class, celebracionTabernero);

        System.out.println("[Sistema] Elaborando una nueva poción...");
        alquimista.elaborarPocion("Elixir de Maná Legendario", "Polvo de Hada");

        System.out.println("\n=========================================================");
        System.out.println("  SIMULACIÓN COMPLETADA CON ÉXITO");
        System.out.println("=========================================================");
    }
}
