package br.com.epi.repository;

import br.com.epi.model.Emprestimo;
import br.com.epi.model.StatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long>, JpaSpecificationExecutor<Emprestimo> {

    List<Emprestimo> findAllByOrderByIdDesc();

    long countByStatus(StatusEmprestimo status);
}
