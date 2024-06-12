package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor  // Agrega constructor sin argumentos

public class Vianda {
    private Long id;
    private String codigoQR;
    private LocalDateTime fechaElaboracion;
    private EstadoViandaEnum estado;
    private Long colaboradorId;
    private Integer heladeraId;

    public Vianda(String codigoQR, LocalDateTime fechaElaboracion, EstadoViandaEnum estado, Long colaboradorId, Integer heladeraId) {
        this.codigoQR = codigoQR;
        this.fechaElaboracion = fechaElaboracion;
        this.estado = estado;
        this.colaboradorId = colaboradorId;
        this.heladeraId = heladeraId;


    }

}
