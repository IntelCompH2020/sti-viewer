-- Table: public.dynamic_page_content

-- DROP TABLE IF EXISTS public.dynamic_page_content;

CREATE TABLE public.external_token
(
    id uuid NOT NULL,
    token character varying(512) COLLATE pg_catalog."default" NOT NULL,
    definition json,
    expires_at timestamp without time zone NOT NULL,
    owner_id uuid,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    is_active character varying(100) COLLATE pg_catalog."default" NOT NULL,
    tenant_id uuid NOT NULL,
    name character varying(1024) COLLATE pg_catalog."default" NOT NULL,
    type character varying(100) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT external_token_pkey PRIMARY KEY (id),
    CONSTRAINT external_token_token_key UNIQUE (token),
    CONSTRAINT external_token_owner_id_fkey FOREIGN KEY (owner_id)
        REFERENCES public."user" (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT external_token_tenant_id_fkey FOREIGN KEY (tenant_id)
        REFERENCES public.tenant (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

Update public.version_info 
set	version = '02.02.001', released_at = '2023-06-30 00:00:00.000', deployed_at = now() at time zone 'utc', description = 'Add external_token Table'
where key = 'StiViewer.Web.db';