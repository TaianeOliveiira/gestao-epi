package br.com.epi.repository;

import br.com.epi.model.Epi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EpiRepository extends JpaRepository<Epi, Long> {

    List<Epi> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);

    List<Epi> findAllByOrderByNomeAsc();

    List<Epi> findByAtivoTrueOrderByNomeAsc();
}
