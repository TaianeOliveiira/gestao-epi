package br.com.epi.repository;

import br.com.epi.model.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColaboradorRepository extends JpaRepository<Colaborador, Long> {

    List<Colaborador> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

    List<Colaborador> findAllByOrderByNomeAsc();
}
