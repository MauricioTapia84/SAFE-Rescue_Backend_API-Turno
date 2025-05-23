package com.SAFE_Rescue.API_Turno.repository;

import com.SAFE_Rescue.API_Turno.modelo.VehiculoDTO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class VehiculoDTORepository {

    private List<VehiculoDTO> listaVehiculos = new ArrayList<>();

    public List<VehiculoDTO> obtenerVehiculos() {
        return listaVehiculos;
    }

    public void agregarBombero(VehiculoDTO vehiculo) {
        listaVehiculos.add(vehiculo);
    }
}
