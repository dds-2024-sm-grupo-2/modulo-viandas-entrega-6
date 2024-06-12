package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import ar.edu.utn.dds.k3003.model.Vianda;
import ar.edu.utn.dds.k3003.repositories.ViandaMapper;
import ar.edu.utn.dds.k3003.repositories.ViandaRepository;

import java.time.LocalDateTime;
import java.util.*;

public class Fachada implements ar.edu.utn.dds.k3003.facades.FachadaViandas {
    private final ViandaMapper viandaMapper;
    private final ViandaRepository viandaRepository;
    private FachadaHeladeras heladerasProxy;

    public Fachada() {
        this.viandaRepository = new ViandaRepository();
        this.viandaMapper = new ViandaMapper();
    }

    @Override
    public ViandaDTO agregar(ViandaDTO viandaDTO) {
        Vianda vianda = new Vianda(viandaDTO.getCodigoQR(), viandaDTO.getFechaElaboracion(), viandaDTO.getEstado(), viandaDTO.getColaboradorId(), viandaDTO.getHeladeraId());
        vianda = this.viandaRepository.save(vianda);
        return viandaMapper.map(vianda);
    }

    @Override
    public ViandaDTO modificarEstado(String qr, EstadoViandaEnum nuevoEstado) {
        Vianda vianda = viandaRepository.findByQR(qr);
        if (vianda != null) {
            vianda.setEstado(nuevoEstado);
            viandaRepository.save(vianda);
            return viandaMapper.map(vianda);
        } else {
            throw new IllegalArgumentException("No se encontró la vianda con el código QR.");
        }
    }
    @Override
    public List<ViandaDTO> viandasDeColaborador(Long colaboradorId, Integer mes, Integer anio) throws NoSuchElementException {
        List<ViandaDTO> viandasDeColaborador = new ArrayList<>();

        for (Vianda vianda : this.viandaRepository.getViandas()) {
            LocalDateTime fechaVianda = vianda.getFechaElaboracion();
            if (vianda.getColaboradorId().equals(colaboradorId) &&
                    fechaVianda.getMonthValue() == mes &&
                    fechaVianda.getYear() == anio) {
                viandasDeColaborador.add(viandaMapper.map(vianda));
            }
        }

        if (viandasDeColaborador.isEmpty()) {
            throw new NoSuchElementException("No se encontraron viandas para el colaborador y fecha especificados.");
        }

        return viandasDeColaborador;
    }

    @Override
    public ViandaDTO buscarXQR(String qr) {
        Vianda vianda = viandaRepository.findByQR(qr);
        if (vianda != null) {
            return viandaMapper.map(vianda);
        } else {
            return null;
        }
    }

    @Override
    public void setHeladerasProxy(FachadaHeladeras heladerasProxy) {
        this.heladerasProxy = heladerasProxy;
    }

    @Override
    public boolean evaluarVencimiento(String qr) throws NoSuchElementException {
        ViandaDTO vianda = buscarXQR(qr);
        if (vianda == null) {
            throw new NoSuchElementException("No se encontró la vianda con el codigoo QR: " + qr);
        }
        List<TemperaturaDTO> temperaturas = heladerasProxy.obtenerTemperaturas(vianda.getHeladeraId());

        boolean vencida = false;
        for (TemperaturaDTO temperatura : temperaturas) {
            if (temperatura.getTemperatura() > 4) {
                vencida = true;
                break;
            }
        }
        return vencida;
    }


    @Override
    public ViandaDTO modificarHeladera(String codigoQR, int nuevoIdHeladera) {
        Vianda vianda = viandaRepository.findByQR(codigoQR);
        if (vianda == null) {
            throw new NoSuchElementException("No se encontró una vianda con el código QR especificado.");
        }
        vianda.setHeladeraId(nuevoIdHeladera);
        return viandaMapper.map(vianda);
    }
}

