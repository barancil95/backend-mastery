CREATE TABLE users(
    id BIGSERIAL PRIMARY KEY,
    full_name varchar(100) not null,
    email varchar(150) not null unique,
    password varchar(255) not null,
    role varchar(20) not null default 'ROLE_USER',
    created_at timestamp with time zone default CURRENT_TIMESTAMP not null,
    updated_at timestamp with time zone default CURRENT_TIMESTAMP not null
);