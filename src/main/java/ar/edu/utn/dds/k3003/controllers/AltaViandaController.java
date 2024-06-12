package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

public class AltaViandaController implements Handler {

    private final Fachada fachada;

    public AltaViandaController(Fachada fachada) {
        this.fachada = fachada;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        ViandaDTO viandaDTO = ctx.bodyAsClass(ViandaDTO.class);
        ViandaDTO viandaAgregada = fachada.agregar(viandaDTO);
        ctx.status(HttpStatus.OK);
        ctx.json(viandaAgregada);
    }
}
