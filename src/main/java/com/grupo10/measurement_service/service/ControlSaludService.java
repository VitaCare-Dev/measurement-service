package com.grupo10.measurement_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import com.grupo10.measurement_service.exception.BusinessLogicException;
import com.grupo10.measurement_service.model.ControlSalud;
import com.grupo10.measurement_service.repository.ControlSaludRepository;

/**
 * Servicio para la gestión de controles de salud.
 * Actúa como entidad padre de todas las mediciones (glucosa, lípidos y vitales),
 * centralizando los datos comunes como el paciente, la fecha y las notas.
 */
@Service
public class ControlSaludService {

    private final ControlSaludRepository controlSaludRepository;

    public ControlSaludService(ControlSaludRepository controlSaludRepository) {
        this.controlSaludRepository = controlSaludRepository;
    }

    /**
     * Crea un nuevo control de salud con la fecha y hora actuales.
     * Es invocado internamente por los servicios de medición antes de persistir cada registro.
     *
     * @param idPaciente identificador del paciente al que pertenece el control
     * @param notas      observaciones opcionales del profesional de salud
     * @return el control de salud creado y persistido
     * @throws BusinessLogicException si el ID del paciente es nulo
     */
    public ControlSalud crearControl(Long idPaciente, String notas) {
        if (idPaciente == null) {
            throw new BusinessLogicException("El ID del paciente es obligatorio");
        }

        ControlSalud controlSalud = new ControlSalud();
        controlSalud.setIdPaciente(idPaciente);
        controlSalud.setFechaHora(LocalDateTime.now());
        controlSalud.setNotas(notas);

        return controlSaludRepository.save(controlSalud);
    }

    /**
     * Obtiene el historial completo de controles de salud de un paciente,
     * ordenado de más reciente a más antiguo.
     *
     * @param idPaciente identificador del paciente
     * @return lista de controles de salud; lista vacía si el paciente no tiene registros
     * @throws BusinessLogicException si el ID del paciente es nulo
     */
    public List<ControlSalud> obtenerHistorialPorPaciente(Long idPaciente) {
        if (idPaciente == null) {
            throw new BusinessLogicException("El ID del paciente es obligatorio");
        }

        return controlSaludRepository.findByIdPacienteOrderByFechaHoraDesc(idPaciente);
    }

}
