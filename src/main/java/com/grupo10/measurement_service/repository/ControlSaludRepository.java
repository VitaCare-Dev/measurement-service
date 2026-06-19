package com.grupo10.measurement_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.grupo10.measurement_service.model.ControlSalud;
import java.util.List;

public interface ControlSaludRepository extends JpaRepository<ControlSalud, Long> {
    
    List<ControlSalud> findByIdPacienteOrderByFechaHoraDesc(Long idPaciente);
}
