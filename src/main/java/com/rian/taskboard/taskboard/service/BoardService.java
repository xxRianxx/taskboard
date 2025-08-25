package com.rian.taskboard.taskboard.service;

import com.rian.taskboard.taskboard.model.Board;
import com.rian.taskboard.taskboard.model.BoardColumn;
import com.rian.taskboard.taskboard.model.ColumnType;
import com.rian.taskboard.taskboard.repository.BoardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    // =========================
    // Criar um novo board
    // =========================
    @Transactional
    public Board createBoard(String name) {
        Board board = new Board();
        board.setName(name);

        // Criar colunas padrão
        List<BoardColumn> columns = new ArrayList<>();

        BoardColumn initial = new BoardColumn();
        initial.setName("Coluna Inicial");
        initial.setType(ColumnType.INITIAL);
        initial.setColumnOrder(1);
        initial.setBoard(board);

        BoardColumn finalColumn = new BoardColumn();
        finalColumn.setName("Coluna Final");
        finalColumn.setType(ColumnType.FINAL);
        finalColumn.setColumnOrder(2);
        finalColumn.setBoard(board);

        BoardColumn cancelColumn = new BoardColumn();
        cancelColumn.setName("Coluna Cancelada");
        cancelColumn.setType(ColumnType.CANCEL);
        cancelColumn.setColumnOrder(3);
        cancelColumn.setBoard(board);

        columns.add(initial);
        columns.add(finalColumn);
        columns.add(cancelColumn);

        board.setColumns(columns);

        // Salvar no banco
        return boardRepository.save(board);
    }

    // =========================
    // Persistir alterações em um board existente
    // =========================
    @Transactional
    public Board saveBoard(Board board) {
        return boardRepository.save(board);
    }

    // =========================
    // Listar todos os boards
    // =========================
    public List<Board> listBoards() {
        return boardRepository.findAll();
    }

    // =========================
    // Buscar board por ID
    // =========================
    public Optional<Board> getBoard(Long id) {
        return boardRepository.findById(id);
    }

    // =========================
    // Excluir board
    // =========================
    @Transactional
    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }
}
