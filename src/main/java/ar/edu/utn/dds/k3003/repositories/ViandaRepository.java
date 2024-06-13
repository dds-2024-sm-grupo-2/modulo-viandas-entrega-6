//package ar.edu.utn.dds.k3003.repositories;
//
//import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
//import ar.edu.utn.dds.k3003.model.Vianda;
//import java.util.*;
//import java.util.concurrent.atomic.AtomicLong;
//
//public class ViandaRepository {
//    private static AtomicLong seqId = new AtomicLong();
//    private Collection<Vianda> viandas;
//
//    public ViandaRepository() {
//        this.viandas = new ArrayList<>();
//    }
//
//    public Vianda save(Vianda vianda) {
//        if (Objects.isNull(vianda.getId())) {
//            vianda.setId(seqId.getAndIncrement());
//            this.viandas.add(vianda);
//        }
//        return vianda;
//    }
//
//    public Vianda findByQR(String qr) {
//        Optional<Vianda> viandaOptional = this.viandas.stream().filter(x -> x.getCodigoQR().equals(qr)).findFirst();
//        return viandaOptional.orElse(null); // Devuelve null si no se encuentra la vianda
//    }
//
//    public Collection<Vianda> getViandas() {
//        return this.viandas;
//    }
//
//    public Vianda findById(Long id) {
//        Optional<Vianda> first = this.viandas.stream().filter(x -> x.getId().equals(id)).findFirst();
//        return first.orElseThrow(() -> new NoSuchElementException(
//                String.format("No hay una vianda de id: %s", id)
//        ));
//    }
//
//    public void update(Vianda vianda) {
//        String qr = vianda.getCodigoQR();
//        Optional<Vianda> viandaOptional = this.viandas.stream().filter(x -> x.getCodigoQR().equals(qr)).findFirst();
//        if (viandaOptional.isPresent()) {
//            Vianda existingVianda = viandaOptional.get();
//            existingVianda.setColaboradorId(vianda.getColaboradorId());
//            existingVianda.setHeladeraId(vianda.getHeladeraId());
//            existingVianda.setEstado(vianda.getEstado());
//            existingVianda.setFechaElaboracion(vianda.getFechaElaboracion());
//        } else {
//            throw new NoSuchElementException("No se encontró la vianda a actualizar");
//        }
//    }
//
//}

package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Vianda;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ViandaRepository {
    public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
}
    public EntityManager getEntityManager() {
        return entityManager;
    }
    private EntityManager entityManager;

    public ViandaRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
    }
    public ViandaRepository(){
    }

public Vianda save(Vianda vianda) {
    EntityTransaction transaction = entityManager.getTransaction();
    boolean isNewTransaction = false;

    try {
        if (!transaction.isActive()) {
            transaction.begin();
            isNewTransaction = true;
        }

        if (vianda.getId() == null) {
            entityManager.persist(vianda);
        } else {
            entityManager.merge(vianda);
        }

        if (isNewTransaction) {
            transaction.commit();
        }

        return vianda;
    } catch (Exception e) {
        if (isNewTransaction && transaction.isActive()) {
            transaction.rollback();
        }
        throw e;
    }
}


    public Vianda findByQR(String qr) {
        TypedQuery<Vianda> query = entityManager.createQuery("SELECT v FROM Vianda v WHERE v.codigoQR = :qr", Vianda.class);
        query.setParameter("qr", qr);
        List<Vianda> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    public List<Vianda> getViandas() {
        TypedQuery<Vianda> query = entityManager.createQuery("SELECT v FROM Vianda v", Vianda.class);
        return query.getResultList();
    }

    public Vianda findById(Long id) {
        Vianda vianda = entityManager.find(Vianda.class, id);
        if (vianda == null) {
            throw new NoSuchElementException(String.format("No hay una vianda de id: %s", id));
        }
        return vianda;
    }

    public void update(Vianda vianda) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Vianda existingVianda = entityManager.find(Vianda.class, vianda.getId());
            if (existingVianda != null) {
                existingVianda.setColaboradorId(vianda.getColaboradorId());
                existingVianda.setHeladeraId(vianda.getHeladeraId());
                existingVianda.setEstado(vianda.getEstado());
                existingVianda.setFechaElaboracion(vianda.getFechaElaboracion());
                entityManager.merge(existingVianda);
            } else {
                throw new NoSuchElementException("No se encontró la vianda a actualizar");
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}
