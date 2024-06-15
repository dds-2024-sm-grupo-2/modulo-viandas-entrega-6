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
@Entity
@Table(name = "viandas")
public class Vianda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Para generación automática de IDs
    private Long id;

    @Column(name = "codigo_qr")
    private String codigoQR;

    @Column(name = "fecha_elaboracion")
    private LocalDateTime fechaElaboracion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoViandaEnum estado;

    @Column(name = "colaborador_id")
    private Long colaboradorId;

    @Column(name = "heladera_id")
    private Integer heladeraId;


    public Vianda(String codigoQR, LocalDateTime fechaElaboracion, EstadoViandaEnum estado, Long colaboradorId, Integer heladeraId) {
        this.codigoQR = codigoQR;
        this.fechaElaboracion = fechaElaboracion;
        this.estado = estado;
        this.colaboradorId = colaboradorId;
        this.heladeraId = heladeraId;
    }

    public Vianda() {

    }
}
