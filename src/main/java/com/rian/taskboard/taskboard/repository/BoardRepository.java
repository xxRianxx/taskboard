package com.rian.taskboard.taskboard.repository;

import com.rian.taskboard.taskboard.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    // Aqui podemos adicionar consultas personalizadas depois, se precisar
}
