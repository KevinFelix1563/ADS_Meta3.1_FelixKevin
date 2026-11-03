package com.example.taberna.dominio;

import com.example.taberna.dominio.actores.Alquimista;
import com.example.taberna.dominio.actores.Inventario;
import com.example.taberna.dominio.actores.Tabernero;
import com.example.taberna.dominio.eventos.IngredienteAgotadoEvento;
import com.example.taberna.dominio.eventos.PocionElaboradaEvento;
import com.example.taberna.eventbus.EventBus;
import com.example.taberna.eventbus.Suscriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para validar los eventos de dominio y actores del Incremento 2.
 */
class DominioEventosTest {

    private EventBus bus;
    private Inventario inventario;
    private Tabernero tabernero;
    private Alquimista alquimista;

    @BeforeEach
    void setUp() {
        bus = new EventBus();
        inventario = new Inventario();
        tabernero = new Tabernero("Barliman");
        alquimista = new Alquimista("Nicolas Flamel", bus);
    }

    @Test
    @DisplayName("Debe publicar PocionElaboradaEvento e IngredienteAgotadoEvento y ser recibidos por los actores")
    void testFlujoElaboracionPocion() {
        List<PocionElaboradaEvento> pocionesRecibidas = new ArrayList<>();
        List<IngredienteAgotadoEvento> ingredientesAgotados = new ArrayList<>();

        bus.suscribir(PocionElaboradaEvento.class, pocionesRecibidas::add);
        bus.suscribir(IngredienteAgotadoEvento.class, ingredientesAgotados::add);

        // Suscripción con method references tal como en el diseño
        bus.suscribir(PocionElaboradaEvento.class, inventario::registrarPocion);
        bus.suscribir(PocionElaboradaEvento.class, tabernero::celebrarElaboracion);
        bus.suscribir(IngredienteAgotadoEvento.class, inventario::alertarFaltaIngrediente);

        alquimista.elaborarPocion("Poción de Resistencia al Fuego", "Obsidiana Líquida");

        assertEquals(1, pocionesRecibidas.size());
        assertEquals("Poción de Resistencia al Fuego", pocionesRecibidas.get(0).nombrePocion());
        assertEquals("Nicolas Flamel", pocionesRecibidas.get(0).alquimista());

        assertEquals(1, ingredientesAgotados.size());
        assertEquals("Obsidiana Líquida", ingredientesAgotados.get(0).ingrediente());
    }

    @Test
    @DisplayName("Desuscribir al tabernero debe evitar que reciba eventos posteriores")
    void testDesuscripcionTabernero() {
        List<String> celebraciones = new ArrayList<>();
        Suscriptor<PocionElaboradaEvento> subTabernero = e -> celebraciones.add(e.nombrePocion());

        bus.suscribir(PocionElaboradaEvento.class, subTabernero);

        alquimista.elaborarPocion("Poción A", "Ingrediente A");
        assertEquals(1, celebraciones.size());

        bus.desuscribir(PocionElaboradaEvento.class, subTabernero);

        alquimista.elaborarPocion("Poción B", "Ingrediente B");
        assertEquals(1, celebraciones.size(), "El tabernero no debió recibir la segunda poción");
    }
}
