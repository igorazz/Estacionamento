CREATE DATABASE sistema_estacionamento;
USE sistema_estacionamento;

CREATE TABLE clientes_mensalistas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    telefone VARCHAR(15),
    data_vencimento DATE,
    status_pagamento ENUM('EM_DIA', 'ATRASADO') DEFAULT 'EM_DIA'
);

CREATE TABLE veiculos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    placa VARCHAR(8) UNIQUE NOT NULL,clientes_mensalistasmodelo
    modelo VARCHAR(50),
    cor VARCHAR(20),
    tipo ENUM('CARRO', 'MOTO'),
    id_mensalista INT,
    FOREIGN KEY (id_mensalista) REFERENCES clientes_mensalistas(id)
);

CREATE TABLE transacoes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    placa_veiculo VARCHAR(8) NOT NULL,
    horario_entrada DATETIME NOT NULL,
    horario_saida DATETIME,
    valor_total DECIMAL(10, 2) DEFAULT 0.00,
    tipo_cliente ENUM('AVULSO', 'MENSALISTA') NOT NULL,
    status_transacao ENUM('ABERTA', 'CONCLUIDA') DEFAULT 'ABERTA'	
);


USE sistema_estacionamento;
ALTER TABLE veiculos ADD COLUMN data_entrada DATETIME DEFAULT CURRENT_TIMESTAMP;

SET SQL_SAFE_UPDATES = 0;
DELETE FROM veiculos;
SET SQL_SAFE_UPDATES = 1;

SELECT * FROM veiculos;