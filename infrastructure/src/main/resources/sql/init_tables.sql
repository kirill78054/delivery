CREATE TABLE delivery_transport
(
    id    UUID NOT NULL,
    name  VARCHAR NOT NULL,
    speed VARCHAR NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE delivery_courier_status
(
    name VARCHAR NOT NULL,
    PRIMARY KEY (name)
);

CREATE TABLE delivery_courier
(
    id             UUID NOT NULL,
    name           VARCHAR NOT NULL,
    transport_id   UUID NOT NULL,
    location_x     INTEGER NOT NULL,
    location_y     INTEGER NOT NULL,
    courier_status VARCHAR NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE delivery_order
(
    id           UUID NOT NULL,
    location_x   INTEGER NOT NULL,
    location_y   INTEGER NOT NULL,
    order_status VARCHAR NOT NULL,
    courier_id   UUID,
    PRIMARY KEY (id)
);

CREATE TABLE delivery_order_status
(
    name VARCHAR NOT NULL,
    PRIMARY KEY (name)
);
