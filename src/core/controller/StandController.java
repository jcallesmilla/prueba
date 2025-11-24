package core.controller;

import core.model.Stand;
import core.model.interfaces.IStand;
import core.model.storage.StandStorage;
import core.controller.util.Response;
import core.controller.util.Status;
import java.util.ArrayList;
import java.util.List;

public class StandController {

    private final StandStorage standStorage;

    public StandController(StandStorage standStorage) {
        this.standStorage = standStorage;
    }

    public Response<IStand> crearStand(String idTexto, String precioTexto) {
        if (idTexto == null || precioTexto == null || idTexto.isEmpty() || precioTexto.isEmpty()) {
            return new Response<>(Status.BAD_REQUEST, "Todos los campos del stand son obligatorios.");
        }
        long id;
        double precio;
        try {
            id = Long.parseLong(idTexto);
            precio = Double.parseDouble(precioTexto);
        } catch (NumberFormatException e) {
            return new Response<>(Status.BAD_REQUEST, "El ID debe ser numérico y el precio debe ser un número válido.");
        }
        if (id < 0 || String.valueOf(id).length() > 15) {
            return new Response<>(Status.BAD_REQUEST,
                    "El ID del stand debe ser mayor o igual a 0 y con máximo 15 dígitos.");
        }
        if (precio <= 0) {
            return new Response<>(Status.BAD_REQUEST, "El precio del stand debe ser mayor que 0.");
        }
        if (standStorage.existeId(id)) {
            return new Response<>(Status.BAD_REQUEST, "Ya existe un stand con ese ID.");
        }
        IStand stand = new Stand(id, precio);
        standStorage.guardar(stand);
        return new Response<>(Status.CREATED, "Stand creado correctamente.", stand);
    }

    public Response<List<IStand>> obtenerStands() {
        List<IStand> copias = new ArrayList<>();
        for (IStand stand : standStorage.obtenerOrdenados()) {
            copias.add(stand.copiar());
        }
        return new Response<>(Status.OK, "Listado de stands obtenido.", copias);
    }
}