CREATE TABLE users
(
    id           BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    username     VARCHAR(100) NOT NULL UNIQUE,
    email        VARCHAR(255) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20)  NOT NULL UNIQUE,
    role         VARCHAR(50)  NOT NULL DEFAULT 'CUSTOMER',
    created_at   TIMESTAMP             DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE books
(
    id                  BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title               VARCHAR(255)   NOT NULL,
    author              VARCHAR(255)   NOT NULL,
    description         TEXT,
    genre               VARCHAR(100),
    age_group           VARCHAR(50)    NOT NULL,
    language            VARCHAR(50)    NOT NULL,
    price               DECIMAL(10, 2) NOT NULL,
    stock_quantity      INT            NOT NULL DEFAULT 0,
    discount_percentage DECIMAL(5, 2)           DEFAULT 0 CHECK (discount_percentage >= 0 AND discount_percentage <= 100),
    created_at          TIMESTAMP               DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE orders
(
    id           BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id      BIGINT                              NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    status       VARCHAR(50)                         NOT NULL,
    total_amount DECIMAL(10, 2)                      NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE order_items
(
    id       BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    order_id BIGINT         NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    book_id  BIGINT         NOT NULL REFERENCES books (id),
    quantity INT            NOT NULL,
    price    DECIMAL(10, 2) NOT NULL
);

CREATE TABLE cart_items
(
    id       BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id  BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    book_id  BIGINT NOT NULL REFERENCES books (id),
    quantity INT    NOT NULL DEFAULT 1,
    UNIQUE (user_id, book_id)
);

CREATE TABLE payments
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    order_id   BIGINT                              NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    amount     DECIMAL(10, 2)                      NOT NULL,
    method     VARCHAR(50)                         NOT NULL,
    status     VARCHAR(50)                         NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE reviews
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id    BIGINT                              NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    book_id    BIGINT                              NOT NULL REFERENCES books (id) ON DELETE CASCADE,
    rating     INT                                 NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment    TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UNIQUE (user_id, book_id)
);

CREATE TABLE wishlist_items
(
    id       BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id  BIGINT                              NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    book_id  BIGINT                              NOT NULL REFERENCES books (id),
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    UNIQUE (user_id, book_id)
);

CREATE TABLE promocodes
(
    id                  BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    code                VARCHAR(50)                         NOT NULL UNIQUE,
    discount_percentage DECIMAL(5, 2)                       NOT NULL CHECK (discount_percentage > 0 AND discount_percentage <= 100),
    start_date          TIMESTAMP                           NOT NULL,
    end_date            TIMESTAMP                           NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);
