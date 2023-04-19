CREATE TABLE public.dynamic_page
(
    id uuid NOT NULL,
    creator_id uuid,
    visibility character varying(200) COLLATE pg_catalog."default" NOT NULL,
    "order" integer NOT NULL,
    config json,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    type character varying(200) COLLATE pg_catalog."default" NOT NULL,
    is_active character varying(100) COLLATE pg_catalog."default" NOT NULL,
    default_language character varying(50) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT dynamic_page_pkey PRIMARY KEY (id),
    CONSTRAINT dynamic_page_creator_id_fke FOREIGN KEY (creator_id)
        REFERENCES public."user" (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

Update public.version_info 
set	version = '02.01.001', released_at = '2023-03-31 00:00:00.000', deployed_at = now() at time zone 'utc', description = 'Add dynamic_page Table'
where key = 'StiViewer.Web.db';