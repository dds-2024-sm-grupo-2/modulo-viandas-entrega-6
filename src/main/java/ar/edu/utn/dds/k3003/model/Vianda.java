//package ar.edu.utn.dds.k3003.model;
//
//import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.time.LocalDateTime;
//
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor  // Agrega constructor sin argumentos
//
//public class Vianda {
//    private Long id;
//    private String codigoQR;
//    private LocalDateTime fechaElaboracion;
//    private EstadoViandaEnum estado;
//    private Long colaboradorId;
//    private Integer heladeraId;
//
//    public Vianda(String codigoQR, LocalDateTime fechaElaboracion, EstadoViandaEnum estado, Long colaboradorId, Integer heladeraId) {
//        this.codigoQR = codigoQR;
//        this.fechaElaboracion = fechaElaboracion;
//        this.estado = estado;
//        this.colaboradorId = colaboradorId;
//        this.heladeraId = heladeraId;
//
//
//    }
//
//}
package ar.edu.utn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "viandas")
public class Vianda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Para generación automática de IDs
    private Long id;

    @Column(name = "codigo_qr", nullable = false, unique = true)
    private String codigoQR;

    @Column(name = "fecha_elaboracion", nullable = false)
    private LocalDateTime fechaElaboracion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoViandaEnum estado;

    @Column(name = "colaborador_id", nullable = false)
    private Long colaboradorId;

    @Column(name = "heladera_id", nullable = false)
    private Integer heladeraId;



    public Vianda(String codigoQR, LocalDateTime fechaElaboracion, EstadoViandaEnum estado, Long colaboradorId, Integer heladeraId) {
        this.codigoQR = codigoQR;
        this.fechaElaboracion = fechaElaboracion;
        this.estado = estado;
        this.colaboradorId = colaboradorId;
        this.heladeraId = heladeraId;
    }
}
