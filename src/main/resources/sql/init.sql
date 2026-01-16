SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;


CREATE DATABASE IF NOT EXISTS fundicion_lara_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE fundicion_lara_db;


CREATE TABLE users
(
    user_id     integer PRIMARY KEY AUTO_INCREMENT,
    name        varchar(50) NOT NULL,
    last_name   varchar(50) NOT NULL COMMENT 'Apellido paterno',
    mother_last_name varchar(50) NOT NULL COMMENT 'Apellido materno',
    email       varchar(255) UNIQUE NOT NULL,
    password    varchar(255)        NOT NULL,
    role        varchar(50)        NOT NULL COMMENT 'ADMIN | CONSULTA',
    update_Date datetime            NOT NULL,
    created_at  datetime            NOT NULL
);

INSERT INTO users (SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;


CREATE DATABASE IF NOT EXISTS fundicion_lara_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE fundicion_lara_db;


CREATE TABLE users
(
    user_id     integer PRIMARY KEY AUTO_INCREMENT,
    name        varchar(50) NOT NULL,
    last_name   varchar(50) NOT NULL COMMENT 'Apellido paterno',
    mother_last_name varchar(50) NOT NULL COMMENT 'Apellido materno',
    email       varchar(255) UNIQUE NOT NULL,
    password    varchar(255)        NOT NULL,
    role        varchar(50)        NOT NULL COMMENT 'ADMIN | CONSULTA',
    update_Date datetime            NOT NULL,
    created_at  datetime            NOT NULL
);

INSERT INTO users (
    name,
    last_name,
    mother_last_name,
    email,
    password,
    role,
    update_date,
    created_at
) VALUES (
             'Roberto',
             'Aguilar',
             'Vazquez',
             'roberto.aav.23@gmail.com',
             '$2a$10$cndpB8I6KxxCrNlcmp.PcOgjQwLjHdZKPrbrEnp8OK331ub1KwK52',
             'ADMIN',
             NOW(),
             NOW()
         );

CREATE TABLE products
(
    product_id     integer PRIMARY KEY AUTO_INCREMENT,
    name           varchar(100) NOT NULL,
    client         varchar(100),
    description    varchar(200),
    unidad         varchar(10),
    stock          integer,
    purchase_price decimal(10, 2)      NOT NULL COMMENT 'Precio compra',
    selling_price  decimal(10, 2)      NOT NULL COMMENT 'Precio venta',
    avatar         varchar(255) COMMENT 'Imagen del producto',
    status         varchar(10),
    update_Date    datetime            NOT NULL,
    created_at     datetime            NOT NULL
);

CREATE TABLE transactions
(
    transaction_id       integer PRIMARY KEY AUTO_INCREMENT,
    order_transaction_id integer,
    amount               decimal(10, 2) NOT NULL,
    description          varchar(200),
    invoice_number       varchar(100),
    issuer_rfc           varchar(50),
    type                 varchar(50)    NOT NULL COMMENT 'VENTA | COMPRA | GASTOS',
    operation_date       date           NOT NULL,
    status               varchar(10)    NOT NULL,
    update_Date          datetime       NOT NULL,
    created_at           datetime       NOT NULL
);

CREATE TABLE order_transactions
(
    order_transaction_id integer PRIMARY KEY AUTO_INCREMENT,
    extra_amount         decimal(10, 2) NOT NULL,
    description          varchar(200),
    product_id           integer        NOT NULL,
    item_count           integer        NOT NULL,
    method_payment       varchar(50),
    invoice_number       varchar(100),
    client               varchar(100),
    amount_paid          decimal(10, 2) NOT NULL,
    payment_status       varchar(10)    NOT NULL COMMENT 'PAGADO | PENDIENTE | NO PAGADO',
    delivery_status      varchar(10)    NOT NULL COMMENT 'PENDIENTE | EN TRANCITO | ENTREGADO | CANCELADO | EN ESPERA ',
    selling_price        decimal(10, 2) COMMENT 'Precio venta',
    purchase_price       decimal(10, 2) NOT NULL COMMENT 'Precio compra',
    register_in_sales BOOLEAN NULL COMMENT 'Indica si también se registra en ventas',
    operation_date       date           NULL,
    update_Date          datetime       NOT NULL,
    created_at           datetime       NOT NULL
);

ALTER TABLE order_transactions
    ADD FOREIGN KEY (product_id) REFERENCES products (product_id);


name,
    last_name,
    mother_last_name,
    email,
    password,
    role,
    update_date,
    created_at
) VALUES (
             'Roberto',
             'Aguilar',
             'Vazquez',
             'roberto.aav.23@gmail.com',
             '$2a$10$cndpB8I6KxxCrNlcmp.PcOgjQwLjHdZKPrbrEnp8OK331ub1KwK52',
             'ADMIN',
             NOW(),
             NOW()
         );

CREATE TABLE products
(
    product_id     integer PRIMARY KEY AUTO_INCREMENT,
    name           varchar(100) NOT NULL,
    client         varchar(100),
    description    varchar(200),
    unidad         varchar(10),
    stock          integer,
    purchase_price decimal(10, 2)      NOT NULL COMMENT 'Precio compra',
    selling_price  decimal(10, 2)      NOT NULL COMMENT 'Precio venta',
    avatar         varchar(255) COMMENT 'Imagen del producto',
    status         varchar(10),
    update_Date    datetime            NOT NULL,
    created_at     datetime            NOT NULL
);

CREATE TABLE transactions
(
    transaction_id       integer PRIMARY KEY AUTO_INCREMENT,
    order_transaction_id integer,
    amount               decimal(10, 2) NOT NULL,
    description          varchar(200),
    invoice_number       varchar(100),
    issuer_rfc           varchar(50),
    type                 varchar(50)    NOT NULL COMMENT 'VENTA | COMPRA | GASTOS',
    operation_date       date           NOT NULL,
    status               varchar(10)    NOT NULL,
    update_Date          datetime       NOT NULL,
    created_at           datetime       NOT NULL
);

CREATE TABLE order_transactions
(
    order_transaction_id integer PRIMARY KEY AUTO_INCREMENT,
    extra_amount         decimal(10, 2) NOT NULL,
    description          varchar(200),
    product_id           integer        NOT NULL,
    item_count           integer        NOT NULL,
    method_payment       varchar(50),
    invoice_number       varchar(100),
    client               varchar(100),
    amount_paid          decimal(10, 2) NOT NULL,
    payment_status       varchar(10)    NOT NULL COMMENT 'PAGADO | PENDIENTE | NO PAGADO',
    delivery_status      varchar(10)    NOT NULL COMMENT 'PENDIENTE | EN TRANCITO | ENTREGADO | CANCELADO | EN ESPERA ',
    selling_price        decimal(10, 2) COMMENT 'Precio venta',
    purchase_price       decimal(10, 2) NOT NULL COMMENT 'Precio compra',
    register_in_sales BOOLEAN NULL COMMENT 'Indica si también se registra en ventas',
    operation_date       date           NULL,
    update_Date          datetime       NOT NULL,
    created_at           datetime       NOT NULL
);

ALTER TABLE order_transactions
    ADD FOREIGN KEY (product_id) REFERENCES products (product_id);

