package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clients.HeladerasProxy;
import ar.edu.utn.dds.k3003.controllers.*;
import ar.edu.utn.dds.k3003.facades.dtos.Constants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class WebApp {
    public static void main(String[] args) {
        var env = System.getenv();
        var objectMapper = createObjectMapper();
        Fachada fachada = new Fachada();
        fachada.setHeladerasProxy(new HeladerasProxy(objectMapper));

        // Variables de entorno
        var URL_VIANDAS = env.get("URL_VIANDAS");
        var URL_HELADERAS = env.get("URL_HELADERAS");

        var port = Integer.parseInt(env.getOrDefault("PORT", "8080"));

        var app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> {
                configureObjectMapper(mapper);
            }));
        }).start(port);

        app.get("/", ctx -> ctx.result("Servicio de Viandas!!!"));
        app.post("/viandas", new AltaViandaController(fachada)); // Postea una vianda
        app.get("/viandas/{qr}", new ListaViandaQRController(fachada)); // Devuelve vianda por QR y mensaje 404 si no encuentra
        app.get("/viandas/{qr}/vencida", new ListaViandaVencidaController(fachada)); // Devuelve true si la vianda está vencida, false si no se encuentra o no está vencida
        app.patch("/viandas/{qr}", new ModificarHeladeraController(fachada)); // Modifica UNICAMENTE la heladeraId de una vianda por QR mediante un PATCH
        app.get("/viandas/search/findByColaboradorIdAndAnioAndMes", new ViandasColaboradorController(fachada)); // Devuelve las viandas de un colaborador para un mes y año especificados
    }

    public static ObjectMapper createObjectMapper() {
        var objectMapper = new ObjectMapper();
        configureObjectMapper(objectMapper);
        return objectMapper;
    }
    public static void configureObjectMapper(ObjectMapper objectMapper) {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        var sdf = new SimpleDateFormat(Constants.DEFAULT_SERIALIZATION_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setDateFormat(sdf);
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