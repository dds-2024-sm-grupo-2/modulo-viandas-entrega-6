package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clients.HeladerasProxy;
import ar.edu.utn.dds.k3003.controllers.*;
import ar.edu.utn.dds.k3003.facades.dtos.Constants;
import io.javalin.Javalin;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class WebApp {

    public static void main(String[] args) {

        var env = System.getenv();

        // Variables de entorno
        var URL_VIANDAS = env.get("URL_VIANDAS");
        var URL_HELADERAS = env.get("URL_HELADERAS");
        var URL_COLABORADORES = env.get("URL_COLABORADORES");
        var URL_LOGISTICA = env.get("URL_LOGISTICA");


        var objectMapper = createObjectMapper();
        var fachada = new Fachada();

        // Obtengo el puerto de la variable de entorno, si no existe, uso el 8080
        var port = Integer.parseInt(env.getOrDefault("PORT", "8080"));

        fachada.setHeladerasProxy(new HeladerasProxy(objectMapper));
        var viandaController = new ViandaController(fachada);
        var app = Javalin.create().start(port);

        // Home
        app.get("/", ctx -> ctx.result("Modulo Viandas - Diseño de Sistemas K3003 - UTN FRBA"));

        // APIs
        app.post("/viandas", viandaController::agregar);
        app.get("/viandas/{qr}", viandaController::obtenerqr);
        app.get("/viandas/{qr}/vencida", viandaController::obtenervencida);
        app.patch("/viandas/{qr}",viandaController::modificarhl);
        app.get("/viandas/search/findByColaboradorIdAndAnioAndMes", viandaController::obtenerviancolab);
        app.delete("/viandas", viandaController::eliminar);

    }

    public static ObjectMapper createObjectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        var sdf = new SimpleDateFormat(Constants.DEFAULT_SERIALIZATION_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setDateFormat(sdf);
        return objectMapper;
    }
}

//para probar en postman
//    {
//            "id": 0,
//            "codigoQR": "codigoQR",
//            "fechaElaboracion": "2024-05-09T10:30:00Z",
//            "estado": "PREPARADA",
//            "colaboradorId": 10,
//            "heladeraId": 10
//            }
//POST http://localhost:8080/viandas/
//GET http://localhost:8080/viandas/hhh
//viandas vencidas
//PATCH http://localhost:8080/viandas/hhh body raw json { "heladeraId": 10 }
//GET http://localhost:8080/viandas/search/findByColaboradorIdAndAnioAndMes?colaboradorId=10&anio=2024&mes=5