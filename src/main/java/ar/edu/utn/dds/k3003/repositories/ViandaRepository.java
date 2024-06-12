package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.model.Vianda;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class ViandaRepository {
    private static AtomicLong seqId = new AtomicLong();
    private Collection<Vianda> viandas;

    public ViandaRepository() {
        this.viandas = new ArrayList<>();
    }

    public Vianda save(Vianda vianda) {
        if (Objects.isNull(vianda.getId())) {
            vianda.setId(seqId.getAndIncrement());
            this.viandas.add(vianda);
        }
        return vianda;
    }

    public Vianda findByQR(String qr) {
        Optional<Vianda> viandaOptional = this.viandas.stream().filter(x -> x.getCodigoQR().equals(qr)).findFirst();
        return viandaOptional.orElse(null); // Devuelve null si no se encuentra la vianda
    }

    public Collection<Vianda> getViandas() {
        return this.viandas;
    }

    public Vianda findById(Long id) {
        Optional<Vianda> first = this.viandas.stream().filter(x -> x.getId().equals(id)).findFirst();
        return first.orElseThrow(() -> new NoSuchElementException(
                String.format("No hay una vianda de id: %s", id)
        ));
    }

    public void update(Vianda vianda) {
        String qr = vianda.getCodigoQR();
        Optional<Vianda> viandaOptional = this.viandas.stream().filter(x -> x.getCodigoQR().equals(qr)).findFirst();
        if (viandaOptional.isPresent()) {
            Vianda existingVianda = viandaOptional.get();
            existingVianda.setColaboradorId(vianda.getColaboradorId());
            existingVianda.setHeladeraId(vianda.getHeladeraId());
            existingVianda.setEstado(vianda.getEstado());
            existingVianda.setFechaElaboracion(vianda.getFechaElaboracion());
        } else {
            throw new NoSuchElementException("No se encontró la vianda a actualizar");
        }
    }

}