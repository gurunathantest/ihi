CREATE TABLE IF NOT EXISTS users
(
    id text  NOT NULL,
    first_name text ,
    last_name text ,
    email text ,
    password text ,
    username text ,
    mobile_num text,
    status text,
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT email_unique_key UNIQUE (email),
    CONSTRAINT username_unique_key UNIQUE (username)
)
TABLESPACE pg_default;

	
CREATE TABLE IF NOT EXISTS refresh_token
(
    id text  NOT NULL,
    user_id text  NOT NULL,
    token text ,
    expiry_date time without time zone,
    CONSTRAINT refresh_token_pkey PRIMARY KEY (id),
    CONSTRAINT userkey FOREIGN KEY (user_id)
        REFERENCES users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
TABLESPACE pg_default;
	
CREATE TABLE IF NOT EXISTS roles
(
    id text  NOT NULL,
    name text ,
    CONSTRAINT roles_pkey PRIMARY KEY (id)
)
TABLESPACE pg_default;

	
CREATE TABLE IF NOT EXISTS user_roles
(
    user_id text  NOT NULL,
    role_id text  NOT NULL,
    CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id),
    CONSTRAINT role_foreign_key FOREIGN KEY (role_id)
        REFERENCES roles (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT user_foreign_key FOREIGN KEY (user_id)
        REFERENCES users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
TABLESPACE pg_default;

	
