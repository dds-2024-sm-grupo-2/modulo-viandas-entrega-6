package ar.edi.itn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.model.Vianda;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersistenceIT {

    static EntityManagerFactory entityManagerFactory ;
    EntityManager entityManager ;

    @BeforeAll
    public static void setUpClass() throws Exception {
        entityManagerFactory = Persistence.createEntityManagerFactory("TPDDS");
    }
    @BeforeEach
    public void setup() throws Exception {
        entityManager = entityManagerFactory.createEntityManager();
    }

    @Test
    public void testConectar() {
// vacío, para ver que levante el ORM
    }

    @Test
    public void testGuardarYRecuperarVianda() throws Exception {
        Vianda vianda1 = new Vianda();
        vianda1.setCodigoQR("ABC12223"); //NO PUEDE SER EL MISMO
        vianda1.setFechaElaboracion(LocalDateTime.now());
        vianda1.setEstado(EstadoViandaEnum.EN_TRASLADO);
        vianda1.setColaboradorId(1L);
        vianda1.setHeladeraId(101);


        entityManager.getTransaction().begin();
        entityManager.persist(vianda1);
        entityManager.getTransaction().commit();
        entityManager.close();

        // Obtener el ID generado automáticamente
        Long viandaId = vianda1.getId();

        entityManager = entityManagerFactory.createEntityManager();
        Vianda vianda2 = entityManager.find(Vianda.class, viandaId);

        assertEquals(vianda1.getCodigoQR(), vianda2.getCodigoQR());
    }

}



