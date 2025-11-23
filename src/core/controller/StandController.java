package core.controller;

import core.model.Stand;
import core.model.storage.StandStorage;
import core.util.Response;
import core.util.StatusCode;
import java.util.ArrayList;
import java.util.List;


public class StandController {

    private final StandStorage standStorage;

    public StandController(StandStorage standStorage) {
        this.standStorage = standStorage;
    }

    public Response<Stand> crearStand(String idTexto, String precioTexto) {
        if (idTexto == null || precioTexto == null || idTexto.isEmpty() || precioTexto.isEmpty()) {
            return new Response<>(StatusCode.ERROR_VALIDACION, "Todos los campos del stand son obligatorios.");
        }
        long id;
        double precio;
        try {
            id = Long.parseLong(idTexto);
            precio = Double.parseDouble(precioTexto);
        } catch (NumberFormatException e) {
            return new Response<>(StatusCode.ERROR_VALIDACION, "El ID debe ser numérico y el precio debe ser un número válido.");
        }
        if (id < 0 || String.valueOf(id).length() > 15) {
            return new Response<>(StatusCode.ERROR_VALIDACION, "El ID del stand debe ser mayor o igual a 0 y con máximo 15 dígitos.");
        }
        if (precio <= 0) {
            return new Response<>(StatusCode.ERROR_VALIDACION, "El precio del stand debe ser mayor que 0.");
        }
        if (standStorage.existeId(id)) {
            return new Response<>(StatusCode.ERROR_DUPLICADO, "Ya existe un stand con ese ID.");
        }
        Stand stand = new Stand(id, precio);
        standStorage.guardar(stand);
        return new Response<>(StatusCode.SUCCESS, "Stand creado correctamente.", stand);
    }

    public Response<List<Stand>> obtenerStands() {
        List<Stand> copias = new ArrayList<>();
        for (Stand stand : standStorage.obtenerOrdenados()) {
            copias.add(stand.copiar());
        }
        return new Response<>(StatusCode.SUCCESS, "Listado de stands obtenido.", copias);
    }
}
