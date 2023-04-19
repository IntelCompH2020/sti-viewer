-- Table: public.dynamic_page_content

-- DROP TABLE IF EXISTS public.dynamic_page_content;

CREATE TABLE public.dynamic_page_content
(
    id uuid NOT NULL,
    page_id uuid NOT NULL,
    language character varying(50) COLLATE pg_catalog."default" NOT NULL,
    title character varying(500) COLLATE pg_catalog."default" NOT NULL,
    content text COLLATE pg_catalog."default",
    is_active character varying(100) COLLATE pg_catalog."default" NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    CONSTRAINT dynamic_page_content_pkey PRIMARY KEY (id),
    CONSTRAINT dynamic_page_content_page_id_fkey FOREIGN KEY (page_id)
        REFERENCES public.dynamic_page (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

Update public.version_info 
set	version = '02.01.002', released_at = '2023-03-31 00:00:00.000', deployed_at = now() at time zone 'utc', description = 'Add dynamic_page_content Table'
where key = 'StiViewer.Web.db';