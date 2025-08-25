# Taskboard

Sistema de gerenciamento de tarefas estilo Kanban desenvolvido em **Java 24** com **Spring Boot** e **MySQL**.

---

## Funcionalidades
- Criar boards com colunas padrão: Inicial, Final e Cancelada
- Listar e selecionar boards
- Criar, listar, mover, cancelar, bloquear e desbloquear tarefas
- Persistência de dados com MySQL

---

## Tecnologias
- Java 24
- Spring Boot 3.5.5
- Spring Data JPA / Hibernate
- MySQL 8.0+
- HikariCP
- Maven

---

## Modelagem

**Board:** `id`, `name`, `columns` (List<BoardColumn>)  
**BoardColumn:** `id`, `name`, `type` (INITIAL, FINAL, CANCEL), `columnOrder`, `board`, `tasks` (List<Task>)  
**Task:** `id`, `title`, `description`, `blocked`, `blockReason`, `unblockReason`, `column` (BoardColumn)

---

## Estrutura do Projeto
taskboard/
├─ src/main/java/com/rian/taskboard/taskboard/
│ ├─ model/ # Entidades JPA
│ ├─ repository/ # Repositórios
│ ├─ service/ # Serviços
│ ├─ TaskboardApp.java # CLI Runner
│ └─ TaskboardApplication.java # Main
└─ src/main/resources/application.properties
