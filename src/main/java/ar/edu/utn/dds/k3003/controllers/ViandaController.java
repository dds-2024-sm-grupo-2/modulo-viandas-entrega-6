package ar.edu.utn.dds.k3003.controllers;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.RutaDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.json.JSONObject;

import java.util.List;
import java.util.NoSuchElementException;

public class ViandaController {

    private final Fachada fachada;

    public ViandaController(Fachada fachada) {
        this.fachada = fachada;
    }

    public void agregar(Context ctx) throws Exception {
        ViandaDTO viandaDTO = ctx.bodyAsClass(ViandaDTO.class);
        ViandaDTO viandaAgregada = fachada.agregar(viandaDTO);
        ctx.status(HttpStatus.OK);
        ctx.json(viandaAgregada);
    }

    public void obtenerqr(Context ctx) throws Exception {
        String qr = ctx.pathParam("qr");

        try {
            ViandaDTO vianda = fachada.buscarXQR(qr);
            ctx.json(vianda);

        } catch (IllegalArgumentException e) {
            //código 404 y enviar  mensaje de error
            ctx.status(404).result("Vianda no encontrada con el codigo QR especificado: " + qr);
        }
    }

    public void obtenervencida(Context ctx) throws Exception {
        String qr = ctx.pathParam("qr");

        JSONObject resultado = new JSONObject();  // Objeto JSON para el resultado

        try {
            boolean viandaVencida = fachada.evaluarVencimiento(qr);
            if (viandaVencida == Boolean.TRUE) {
            ViandaDTO vianda = fachada.modificarEstado(qr, EstadoViandaEnum.VENCIDA);
            resultado.put("resultado", viandaVencida);
            ctx.result(resultado.toString()).contentType("application/json");
            }
        } catch (NoSuchElementException e) {
            resultado.put("error", "No se encontró la vianda con el código QR: " + qr);
            ctx.status(404).result(resultado.toString());
        }
    }

    public void modificarhl(Context ctx) throws Exception {
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


    public void obtenerviancolab(Context ctx) throws Exception {
        try {
            Long colaboradorId = Long.valueOf(ctx.queryParam("colaboradorId"));
            Integer mes = Integer.valueOf(ctx.queryParam("mes"));
            Integer anio = Integer.valueOf(ctx.queryParam("anio"));

            List<ViandaDTO> viandas = fachada.viandasDeColaborador(colaboradorId, mes, anio);

            if (viandas.isEmpty()) {
                ctx.status(404).result("No se encontraron viandas para el colaborador y fecha especificados.");
            } else {
                ViandaDTO vianda = viandas.get(0); // Obtiene el primer elemento de la lista
                ctx.json(vianda);
            }
        } catch (NumberFormatException e) {
            ctx.status(400).result("Los parametros de la solicitud deben ser numeros enteros.");
        } catch (NoSuchElementException e) {
            ctx.status(404).result("No se encontraron viandas para el colaborador y fecha especificados.");
        }
    }

    public void eliminar(Context ctx) {
        fachada.viandaRepository.borrarTodo();
        ctx.status(HttpStatus.NO_CONTENT);
    }
}