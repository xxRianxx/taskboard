package com.rian.taskboard.taskboard;

import com.rian.taskboard.taskboard.model.Board;
import com.rian.taskboard.taskboard.model.BoardColumn;
import com.rian.taskboard.taskboard.model.ColumnType;
import com.rian.taskboard.taskboard.model.Task;
import com.rian.taskboard.taskboard.service.BoardService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class TaskboardApp implements CommandLineRunner {

    private final BoardService boardService;

    public TaskboardApp(BoardService boardService) {
        this.boardService = boardService;
    }

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n===== MENU PRINCIPAL =====");
            System.out.println("1 - Criar novo board");
            System.out.println("2 - Listar boards");
            System.out.println("3 - Selecionar board");
            System.out.println("4 - Excluir board");
            System.out.println("5 - Sair");
            System.out.print("Escolha uma opção: ");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    System.out.print("Digite o nome do board: ");
                    String name = scanner.nextLine();
                    Board newBoard = boardService.createBoard(name);
                    System.out.println("Board criado com ID: " + newBoard.getId());
                    break;
                case "2":
                    List<Board> boards = boardService.listBoards();
                    System.out.println("Boards existentes:");
                    for (Board b : boards) {
                        System.out.println(b.getId() + " - " + b.getName());
                    }
                    break;
                case "3":
                    System.out.print("Digite o ID do board para selecionar: ");
                    Long selectId = Long.parseLong(scanner.nextLine());
                    Optional<Board> selected = boardService.getBoard(selectId);
                    if (selected.isPresent()) {
                        Board board = selected.get();
                        System.out.println("Board selecionado: " + board.getName());
                        openBoardMenu(scanner, board);
                    } else {
                        System.out.println("Board não encontrado.");
                    }
                    break;
                case "4":
                    System.out.print("Digite o ID do board para excluir: ");
                    Long deleteId = Long.parseLong(scanner.nextLine());
                    boardService.deleteBoard(deleteId);
                    System.out.println("Board excluído.");
                    break;
                case "5":
                    running = false;
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }

        scanner.close();
    }

    private void openBoardMenu(Scanner scanner, Board board) {
        boolean inBoard = true;

        while (inBoard) {
            System.out.println("\n--- BOARD: " + board.getName() + " ---");
            System.out.println("1 - Criar card");
            System.out.println("2 - Listar cards");
            System.out.println("3 - Mover card para próxima coluna");
            System.out.println("4 - Cancelar card");
            System.out.println("5 - Bloquear card");
            System.out.println("6 - Desbloquear card");
            System.out.println("7 - Voltar ao menu principal");
            System.out.print("Escolha uma opção: ");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    System.out.print("Título do card: ");
                    String title = scanner.nextLine();
                    System.out.print("Descrição do card: ");
                    String desc = scanner.nextLine();
                    createTask(board, title, desc);
                    break;
                case "2":
                    listTasks(board);
                    break;
                case "3":
                    moveTask(scanner, board);
                    break;
                case "4":
                    cancelTask(scanner, board);
                    break;
                case "5":
                    blockTask(scanner, board);
                    break;
                case "6":
                    unblockTask(scanner, board);
                    break;
                case "7":
                    inBoard = false;
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private void createTask(Board board, String title, String description) {
        Optional<BoardColumn> initialColumn = board.getColumns().stream()
                .filter(c -> c.getType() == ColumnType.INITIAL)
                .findFirst();

        if (initialColumn.isPresent()) {
            Task task = new Task();
            task.setTitle(title);
            task.setDescription(description);
            task.setBlocked(false);
            task.setColumn(initialColumn.get());

            initialColumn.get().getTasks().add(task);
            boardService.createBoard(board.getName()); // salvar alterações

            System.out.println("Task '" + title + "' criada na coluna inicial.");
        } else {
            System.out.println("Erro: coluna inicial não encontrada.");
        }
    }

    private void listTasks(Board board) {
        for (BoardColumn column : board.getColumns()) {
            System.out.println("\nColuna: " + column.getName() + " (" + column.getType() + ")");
            if (column.getTasks().isEmpty()) {
                System.out.println("  Nenhuma task.");
            } else {
                for (Task task : column.getTasks()) {
                    System.out.println("  [" + task.getId() + "] " + task.getTitle() +
                            (task.isBlocked() ? " [BLOQUEADA]" : ""));
                }
            }
        }
    }

    private void moveTask(Scanner scanner, Board board) {
        System.out.print("Digite o ID da task para mover: ");
        Long taskId = Long.parseLong(scanner.nextLine());

        Task taskToMove = findTaskById(board, taskId);
        if (taskToMove == null) {
            System.out.println("Task não encontrada.");
            return;
        }

        if (taskToMove.isBlocked()) {
            System.out.println("Task bloqueada. Desbloqueie antes de mover.");
            return;
        }

        BoardColumn currentColumn = taskToMove.getColumn();
        List<BoardColumn> cols = board.getColumns();
        int index = cols.indexOf(currentColumn);

        if (currentColumn.getType() == ColumnType.FINAL) {
            System.out.println("Task já está na coluna final.");
            return;
        }

        if (index < cols.size() - 1) {
            BoardColumn nextColumn = cols.get(index + 1);
            if (nextColumn.getType() != ColumnType.CANCEL) {
                currentColumn.getTasks().remove(taskToMove);
                nextColumn.getTasks().add(taskToMove);
                taskToMove.setColumn(nextColumn);
                boardService.createBoard(board.getName()); // salvar alterações
                System.out.println("Task movida para coluna: " + nextColumn.getName());
            }
        }
    }

    private void cancelTask(Scanner scanner, Board board) {
        System.out.print("Digite o ID da task para cancelar: ");
        Long taskId = Long.parseLong(scanner.nextLine());

        Task taskToCancel = findTaskById(board, taskId);
        if (taskToCancel == null) {
            System.out.println("Task não encontrada.");
            return;
        }

        Optional<BoardColumn> cancelColumn = board.getColumns().stream()
                .filter(c -> c.getType() == ColumnType.CANCEL)
                .findFirst();

        if (cancelColumn.isPresent()) {
            BoardColumn currentColumn = taskToCancel.getColumn();
            currentColumn.getTasks().remove(taskToCancel);
            cancelColumn.get().getTasks().add(taskToCancel);
            taskToCancel.setColumn(cancelColumn.get());
            boardService.createBoard(board.getName()); // salvar alterações
            System.out.println("Task cancelada.");
        } else {
            System.out.println("Coluna de cancelamento não encontrada.");
        }
    }

    private void blockTask(Scanner scanner, Board board) {
        System.out.print("Digite o ID da task para bloquear: ");
        Long taskId = Long.parseLong(scanner.nextLine());
        System.out.print("Digite o motivo do bloqueio: ");
        String reason = scanner.nextLine();

        Task task = findTaskById(board, taskId);
        if (task != null) {
            task.setBlocked(true);
            task.setBlockReason(reason);
            boardService.createBoard(board.getName()); // salvar alterações
            System.out.println("Task bloqueada: " + reason);
        } else {
            System.out.println("Task não encontrada.");
        }
    }

    private void unblockTask(Scanner scanner, Board board) {
        System.out.print("Digite o ID da task para desbloquear: ");
        Long taskId = Long.parseLong(scanner.nextLine());
        System.out.print("Digite o motivo do desbloqueio: ");
        String reason = scanner.nextLine();

        Task task = findTaskById(board, taskId);
        if (task != null) {
            task.setBlocked(false);
            task.setBlockReason(reason);
            boardService.createBoard(board.getName()); // salvar alterações
            System.out.println("Task desbloqueada: " + reason);
        } else {
            System.out.println("Task não encontrada.");
        }
    }

    private Task findTaskById(Board board, Long taskId) {
        for (BoardColumn column : board.getColumns()) {
            for (Task task : column.getTasks()) {
                if (task.getId().equals(taskId)) {
                    return task;
                }
            }
        }
        return null;
    }
}
