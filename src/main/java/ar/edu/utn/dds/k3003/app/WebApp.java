package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clients.HeladerasProxy;
import ar.edu.utn.dds.k3003.controllers.*;
import ar.edu.utn.dds.k3003.facades.dtos.Constants;
import io.javalin.Javalin;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.micrometer.MicrometerPlugin;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmHeapPressureMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class WebApp {

    public static void main(String[] args) {

        var env = System.getenv();

        // Variables de entorno
//        var URL_HELADERAS = env.get("URL_HELADERAS");
        var TOKEN = env.get("TOKEN");

        final var registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        registry.config().commonTags("app", "metrics-sample");

        try (var jvmGcMetrics = new JvmGcMetrics();
             var jvmHeapPressureMetrics = new JvmHeapPressureMetrics()) {
            jvmGcMetrics.bindTo(registry);
            jvmHeapPressureMetrics.bindTo(registry);
        }
        new JvmMemoryMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);
        new FileDescriptorMetrics().bindTo(registry);

        var objectMapper = createObjectMapper();
        var fachada = new Fachada();
        // Obtengo el puerto de la variable de entorno, si no existe, uso el 8080
        var port = Integer.parseInt(env.getOrDefault("PORT", "8080"));

        fachada.setHeladerasProxy(new HeladerasProxy(objectMapper));

        fachada.setRegistry(registry);
        final var micrometerPlugin = new MicrometerPlugin(config -> config.registry = registry);



        var viandaController = new ViandaController(fachada);
        var app = Javalin.create(config -> { config.registerPlugin(micrometerPlugin); }).start(port);

        // Home
        app.get("/", ctx -> ctx.result("Modulo Viandas - Diseño de Sistemas K3003 - UTN FRBA"));

        // APIs
        app.post("/viandas", viandaController::agregar);
        app.get("/viandas/{qr}", viandaController::obtenerqr);
        app.get("/viandas/{qr}/vencida", viandaController::obtenervencida);
        app.patch("/viandas/{qr}",viandaController::modificarhl);
        app.get("/viandas/search/findByColaboradorIdAndAnioAndMes", viandaController::obtenerviancolab);
        app.delete("/viandas", viandaController::eliminar);
        app.patch("/viandas/{qr}/estado",viandaController::modificarEstadoVianda);
        app.get("/metrics", ctx -> {
            var auth = ctx.header("Authorization");
            if (auth != null && auth.equals("Bearer " + TOKEN)) {
                ctx.contentType("text/plain; version=0.0.4")
                        .result(registry.scrape());
            } else {
                ctx.status(401).json("{\"error\": \"Unauthorized access\"}");
            }
        });

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