package com.SAFE_Rescue.API_Turno.repository;

import com.SAFE_Rescue.API_Turno.modelo.UbicacionDTO;
import org.springframework.stereotype.Repository;


@Repository
public class UbicacionDTORepository {

    private UbicacionDTO ubicacionActual;

    public UbicacionDTO getUbicacion() {
        return ubicacionActual;
    }

    public void setUbicacion(UbicacionDTO nuevaUbicacion) {
        this.ubicacionActual = nuevaUbicacion;
    }

    public void clear() {
        this.ubicacionActual = null;
    }
}
