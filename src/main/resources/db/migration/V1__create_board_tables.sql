-- ========================================
-- V1__create_board_tables.sql
-- Criação das tabelas do Taskboard
-- ========================================

-- ========================================
-- Tabela: board
-- ========================================
CREATE TABLE board (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(120) NOT NULL
);

-- ========================================
-- Tabela: board_column
-- ========================================
CREATE TABLE board_column (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              name VARCHAR(120) NOT NULL,
                              column_order INT NOT NULL,
                              type VARCHAR(20) NOT NULL,
                              board_id BIGINT NOT NULL,
                              CONSTRAINT fk_board_column_board FOREIGN KEY (board_id) REFERENCES board(id) ON DELETE CASCADE
);

-- Índices para melhorar busca e ordenação
CREATE INDEX idx_board_column_order ON board_column(board_id, column_order);
CREATE INDEX idx_board_column_type ON board_column(type);

-- ========================================
-- Tabela: task
-- ========================================
CREATE TABLE task (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      title VARCHAR(200) NOT NULL,
                      description TEXT,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      blocked BOOLEAN DEFAULT FALSE,
                      block_reason VARCHAR(255),
                      unblock_reason VARCHAR(255),
                      column_id BIGINT NOT NULL,
                      CONSTRAINT fk_task_board_column FOREIGN KEY (column_id) REFERENCES board_column(id) ON DELETE CASCADE
);

-- Índices para facilitar buscas
CREATE INDEX idx_task_column ON task(column_id);
CREATE INDEX idx_task_title ON task(title);
