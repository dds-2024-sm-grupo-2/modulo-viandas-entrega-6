package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import ar.edu.utn.dds.k3003.model.Vianda;
import ar.edu.utn.dds.k3003.repositories.ViandaMapper;
import ar.edu.utn.dds.k3003.repositories.ViandaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.*;

public class Fachada implements ar.edu.utn.dds.k3003.facades.FachadaViandas {
    private EntityManagerFactory entityManagerFactory;
    private final ViandaMapper viandaMapper;
    private final ViandaRepository viandaRepository;
    private FachadaHeladeras heladerasProxy;
    public Fachada(EntityManagerFactory entityManagerFactory){
        this.entityManagerFactory = entityManagerFactory;
        this.viandaRepository = new ViandaRepository();
        this.viandaMapper = new ViandaMapper();
    }
    public Fachada() {
        this.viandaRepository = new ViandaRepository();
        this.viandaMapper = new ViandaMapper();
    }

    @Override
    public ViandaDTO agregar(ViandaDTO viandaDTO) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        viandaRepository.setEntityManager(entityManager);
        viandaRepository.getEntityManager().getTransaction().begin();
        Vianda vianda = new Vianda(viandaDTO.getCodigoQR(), viandaDTO.getFechaElaboracion(), viandaDTO.getEstado(), viandaDTO.getColaboradorId(), viandaDTO.getHeladeraId());
        vianda = this.viandaRepository.save(vianda);
        viandaRepository.getEntityManager().getTransaction().commit();
        viandaRepository.getEntityManager().close();
        return viandaMapper.map(vianda);
    }

    @Override
    public ViandaDTO modificarEstado(String qr, EstadoViandaEnum nuevoEstado) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        viandaRepository.setEntityManager(entityManager);
        viandaRepository.getEntityManager().getTransaction().begin();
        Vianda vianda = viandaRepository.findByQR(qr);
        if (vianda != null) {
            vianda.setEstado(nuevoEstado);
            viandaRepository.save(vianda);
            entityManager.getTransaction().commit();
            entityManager.close();
            return viandaMapper.map(vianda);
        } else {
            entityManager.getTransaction().rollback();
            entityManager.close();
            throw new IllegalArgumentException("No se encontró la vianda con el código QR.");
        }
    }
    @Override
    public List<ViandaDTO> viandasDeColaborador(Long colaboradorId, Integer mes, Integer anio) throws NoSuchElementException {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        viandaRepository.setEntityManager(entityManager);
        TypedQuery<Vianda> query = entityManager.createQuery(
                "SELECT v FROM Vianda v WHERE v.colaboradorId = :colaboradorId AND FUNCTION('MONTH', v.fechaElaboracion) = :mes AND FUNCTION('YEAR', v.fechaElaboracion) = :anio",
                Vianda.class
        );
        query.setParameter("colaboradorId", colaboradorId);
        query.setParameter("mes", mes);
        query.setParameter("anio", anio);
        List<Vianda> viandas = query.getResultList();
        entityManager.close();

        if (viandas.isEmpty()) {
            throw new NoSuchElementException("No se encontraron viandas para el colaborador y fecha especificados.");
        }

        List<ViandaDTO> viandasDeColaborador = new ArrayList<>();
        for (Vianda vianda : viandas) {
            viandasDeColaborador.add(viandaMapper.map(vianda));
        }

        return viandasDeColaborador;
    }
    @Override
    public ViandaDTO buscarXQR(String qr) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        viandaRepository.setEntityManager(entityManager);
        Vianda vianda = viandaRepository.findByQR(qr);
        entityManager.close();
        if (vianda != null) {
            return viandaMapper.map(vianda);
        } else {
            return null;
        }
    }

    @Override
    public void setHeladerasProxy(FachadaHeladeras heladerasProxy) {
        this.heladerasProxy = heladerasProxy;
    }

    @Override
    public boolean evaluarVencimiento(String qr) throws NoSuchElementException {
        ViandaDTO vianda = buscarXQR(qr);
        if (vianda == null) {
            throw new NoSuchElementException("No se encontró la vianda con el codigoo QR: " + qr);
        }
        List<TemperaturaDTO> temperaturas = heladerasProxy.obtenerTemperaturas(vianda.getHeladeraId());

        boolean vencida = false;
        for (TemperaturaDTO temperatura : temperaturas) {
            if (temperatura.getTemperatura() > 4) {
                vencida = true;
                break;
            }
        }
        return vencida;
    }

    @Override
    public ViandaDTO modificarHeladera(String codigoQR, int nuevoIdHeladera) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        viandaRepository.setEntityManager(entityManager);
        entityManager.getTransaction().begin();
        Vianda vianda = viandaRepository.findByQR(codigoQR);
        if (vianda == null) {
            entityManager.getTransaction().rollback();
            entityManager.close();
            throw new NoSuchElementException("No se encontró una vianda con el código QR especificado.");
        }
        vianda.setHeladeraId(nuevoIdHeladera);
        viandaRepository.save(vianda);
        entityManager.getTransaction().commit();
        entityManager.close();
        return viandaMapper.map(vianda);
    }

}

