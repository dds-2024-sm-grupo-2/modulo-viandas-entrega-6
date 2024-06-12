package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.json.JSONObject;
import java.util.NoSuchElementException;

public class ModificarHeladeraController implements Handler {
    private Fachada fachada;

    public ModificarHeladeraController(Fachada fachada) {
        this.fachada = fachada;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        String qr = ctx.pathParam("qr");

        try {
            String body = ctx.body();
            JSONObject jsonBody = new JSONObject(body);
            int heladeraId = jsonBody.getInt("heladeraId");
            ViandaDTO viandaActualizada = fachada.modificarHeladera(qr, heladeraId);
            ctx.status(200).json(viandaActualizada);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Formato incorrecto para heladeraId");
        } catch (NoSuchElementException e) {
            ctx.status(404).result("Vianda no encontrada");
        }
    }
}
