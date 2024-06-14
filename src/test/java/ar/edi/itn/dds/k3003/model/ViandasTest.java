package ar.edi.itn.dds.k3003.model;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;


@ExtendWith(MockitoExtension.class)
public class ViandasTest {
    private static final Long colaborador_id = 10L;
    private static final Integer heladera_id = 10;
    private FachadaViandas instancia;
    private ViandaDTO vianda;
    private final LocalDateTime now = LocalDateTime.now();
    @Mock
    private FachadaHeladeras fachadaHeladeras;

    @BeforeEach
    void setUp() {
        instancia = new Fachada();
        instancia.setHeladerasProxy(fachadaHeladeras);
        vianda = new ViandaDTO("Qr", now, EstadoViandaEnum.PREPARADA, colaborador_id, heladera_id);
    }

    @Test
    @DisplayName("Agregar vianda a un colaborador")
    void testAgregarViandaAColaborador() {
        ViandaDTO viandaAgregada = instancia.agregar(vianda);
        Assertions.assertNotNull(viandaAgregada.getId(), "Cuando se agrega una vianda se le debe asignar un id.");
    }

    @Test
    @DisplayName("Contar viandas de colaborador en un mes y año específicos")
    void testContarViandasDeColaborador() {
        instancia.agregar(vianda);
        instancia.agregar(new ViandaDTO("otroQr", now, EstadoViandaEnum.RETIRADA, colaborador_id, heladera_id));
        Assertions.assertEquals(2, instancia.viandasDeColaborador(colaborador_id, now.getMonthValue(), now.getYear()).size());
        //expectea dos por que ya se agrego una en el metodo agregar()
    }

    @Test
    @DisplayName("Buscar vianda por código QR")
    void testBuscarViandaPorQR() {
        instancia.agregar(vianda);
        ViandaDTO viandaEncontrada = instancia.buscarXQR(vianda.getCodigoQR());
        Assertions.assertEquals(vianda.getColaboradorId(), viandaEncontrada.getColaboradorId(), "Al buscarXQR no se retorna la vianda correcta.");
    }

    @Test
    @DisplayName("Modificar estado de vianda")
    void testModificarEstado() {
        instancia.agregar(vianda);
        instancia.modificarEstado(vianda.getCodigoQR(), EstadoViandaEnum.EN_TRASLADO);
        Assertions.assertEquals(EstadoViandaEnum.EN_TRASLADO, instancia.buscarXQR(vianda.getCodigoQR()).getEstado(), "Se cambió el estado de una vianda pero no parece haberse guardado");
    }

    @Test
    @DisplayName("Modificar heladera de vianda")
    void testModificarHeladera() {
        instancia.agregar(vianda);
        int heladeraDestino = 2;
        instancia.modificarHeladera(vianda.getCodigoQR(), heladeraDestino);
        Assertions.assertEquals(heladeraDestino, instancia.buscarXQR(vianda.getCodigoQR()).getHeladeraId(), "No funcionó cambiar la vianda de heladera");
    }


    @Test
    @DisplayName("Evaluar vencimiento de vianda")
    void testEvaluarVencimiento() {
        ViandaDTO viandaAgregada = instancia.agregar(vianda);
        Mockito.when(fachadaHeladeras.obtenerTemperaturas(heladera_id)).thenReturn(List.of(new TemperaturaDTO(7, heladera_id, now), new TemperaturaDTO(10, heladera_id, now)));
        Assertions.assertTrue(instancia.evaluarVencimiento(viandaAgregada.getCodigoQR()), "La heladera tiene una temperatura mayor a 5 grados, así que debería considerar a la vianda como vencida");
    }
}
