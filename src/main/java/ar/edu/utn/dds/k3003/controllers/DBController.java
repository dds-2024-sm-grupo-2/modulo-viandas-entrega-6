package ar.edu.utn.dds.k3003.controllers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

public class DBController implements Handler {

    private EntityManager entityManager;

    public DBController(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        entityManager.getTransaction().begin();
        try {
            List<String> tablas = entityManager.createNativeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema='public'").getResultList();
            for (String nombre : tablas) {
                Query query = entityManager.createNativeQuery("DELETE FROM " + nombre);
                query.executeUpdate();
            }
            context.result("Se borraron todos los datos");
        } catch (Exception e) {
            e.printStackTrace();
            entityManager.getTransaction().rollback();
            context.result("Ha ocurrido un error al intentar borrar los datos");
            context.status(500);
        } finally {
            entityManager.getTransaction().commit();
            entityManager.close();
        }
    }
}

