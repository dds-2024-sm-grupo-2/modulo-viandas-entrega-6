package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import io.javalin.http.Context;
import io.javalin.http.Handler;

public class ListaViandaQRController implements Handler {
    private Fachada fachada;

    public ListaViandaQRController(Fachada fachada) {
        super();
        this.fachada = fachada;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        String qr = ctx.pathParam("qr");

        try {
            ViandaDTO vianda = fachada.buscarXQR(qr);
            ctx.json(vianda);

        } catch (IllegalArgumentException e) {
            //código 404 y enviar  mensaje de error
            ctx.status(404).result("Vianda no encontrada con el codigo QR especificado: " + qr);
        }
    }
}


