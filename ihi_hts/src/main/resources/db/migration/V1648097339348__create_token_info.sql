CREATE TABLE IF NOT EXISTS public.token_info
(
    id text,
    user_id text NOT NULL,
    token_id text,
    created_time timestamp without time zone,
    CONSTRAINT pk_token_info_id PRIMARY KEY (id)
        INCLUDE(id),
    CONSTRAINT fk_user_id FOREIGN KEY (user_id)
        REFERENCES public.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);