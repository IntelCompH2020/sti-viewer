--
-- PostgreSQL database dump
--

-- Dumped from database version 9.4.26
-- Dumped by pg_dump version 14.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

--
-- Name: bookmark; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.bookmark (
    id uuid NOT NULL,
    name character varying(500) NOT NULL,
    value text NOT NULL,
    user_id uuid NOT NULL,
    type character varying(200) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    is_active character varying(20) NOT NULL,
    hash_code character varying(200) NOT NULL
);


--
-- Name: data_access_request; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.data_access_request (
    id uuid NOT NULL,
    tenant_id uuid NOT NULL,
    status character varying(100) NOT NULL,
    user_id uuid NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    config json
);


--
-- Name: data_group_request; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.data_group_request (
    id uuid NOT NULL,
    tenant_id uuid NOT NULL,
    status character varying(100) NOT NULL,
    user_id uuid,
    group_hash character varying(500) NOT NULL,
    config json,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    is_active character varying(20) NOT NULL,
    name character varying(500) NOT NULL
);


--
-- Name: in_app_notification; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.in_app_notification (
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    is_active character varying NOT NULL,
    type uuid NOT NULL,
    read_time timestamp without time zone,
    tracking_state character varying NOT NULL,
    priority character varying NOT NULL,
    subject character varying,
    body character varying,
    extra_data character varying,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    tenant_id uuid NOT NULL
);


--
-- Name: indicator; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.indicator (
    id uuid NOT NULL,
    code character varying(200) NOT NULL,
    name character varying(500) NOT NULL,
    description character varying,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    is_active character varying(100) NOT NULL,
    config json
);


--
-- Name: indicator_access; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.indicator_access (
    id uuid NOT NULL,
    tenant_id uuid NOT NULL,
    indicator_id uuid NOT NULL,
    user_id uuid,
    config json,
    created_at timestamp without time zone NOT NULL,
    is_active character varying(100) NOT NULL,
    updated_at timestamp without time zone NOT NULL
);


--
-- Name: notification; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.notification (
    id uuid NOT NULL,
    user_id uuid,
    tenant_id uuid NOT NULL,
    type uuid,
    contact_type_hint character varying(200),
    contact_hint character varying(200),
    notified_at timestamp without time zone,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    is_active character varying(100) NOT NULL,
    data text,
    notify_state character varying(200),
    notified_with character varying(200),
    retry_count integer,
    tracking_data text,
    provenance_ref character varying(200),
    tracking_state numeric NOT NULL,
    tracking_process numeric NOT NULL
);


--
-- Name: queue_inbox; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.queue_inbox (
    id uuid NOT NULL,
    queue character varying(50) NOT NULL,
    exchange character varying(50) NOT NULL,
    route character varying(50) NOT NULL,
    application_id character varying(100) NOT NULL,
    message_id uuid NOT NULL,
    message json NOT NULL,
    retry_count integer,
    tenant_id uuid,
    is_active character varying(20) NOT NULL,
    status character varying(50) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);


--
-- Name: queue_outbox; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.queue_outbox (
    id uuid NOT NULL,
    exchange character varying(200) NOT NULL,
    route character varying(200) NOT NULL,
    message_id uuid NOT NULL,
    message json NOT NULL,
    notify_status character varying(100) NOT NULL,
    retry_count integer NOT NULL,
    published_at timestamp without time zone,
    confirmed_at timestamp without time zone,
    tenant_id uuid,
    is_active character varying(100) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);


--
-- Name: scheduled_event; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.scheduled_event (
    id uuid NOT NULL,
    key character varying(500) NOT NULL,
    key_type character varying(200) NOT NULL,
    event_type character varying(200) NOT NULL,
    run_at timestamp without time zone NOT NULL,
    creator uuid,
    data json,
    retry_count integer NOT NULL,
    is_active character varying(20) NOT NULL,
    status character varying(100) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);


--
-- Name: tenant; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tenant (
    id uuid NOT NULL,
    name character varying(500) NOT NULL,
    is_active character varying(100) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    config json,
    code character varying(200) NOT NULL
);


--
-- Name: tenant_configuration; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tenant_configuration (
    id uuid NOT NULL,
    tenant_id uuid NOT NULL,
    type character varying NOT NULL,
    value character varying NOT NULL,
    is_active character varying NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);


--
-- Name: tenant_request; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tenant_request (
    id uuid NOT NULL,
    message character varying,
    status character varying(100) NOT NULL,
    email character varying(200) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    for_user_id uuid NOT NULL,
    assigned_tenant_id uuid
);


--
-- Name: tenant_user; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tenant_user (
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    tenant_id uuid NOT NULL,
    is_active character varying(100) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);


--
-- Name: user; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public."user" (
    id uuid NOT NULL,
    first_name character varying(200) NOT NULL,
    last_name character varying(200) NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    is_active character varying(100) NOT NULL,
    timezone character varying(200) NOT NULL,
    culture character varying(200) NOT NULL,
    language character varying(200) NOT NULL,
    subject_id character varying(150) NOT NULL
);


--
-- Name: user_contact_info; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_contact_info (
    type character varying(100) NOT NULL,
    value character varying NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    is_active character varying(100) NOT NULL,
    tenant_id uuid NOT NULL,
    user_id uuid NOT NULL
);


--
-- Name: user_invitation; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_invitation (
    id uuid NOT NULL,
    tenant_id uuid NOT NULL,
    token character varying(1000) NOT NULL,
    email character varying(200) NOT NULL,
    is_consumed boolean NOT NULL,
    expires_at timestamp without time zone NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at bigint NOT NULL
);


--
-- Name: user_notification_preference; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_notification_preference (
    user_id uuid NOT NULL,
    type uuid NOT NULL,
    channel character varying NOT NULL,
    ordinal numeric NOT NULL,
    created_at timestamp without time zone NOT NULL,
    tenant_id uuid NOT NULL
);


--
-- Name: user_settings; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_settings (
    id uuid NOT NULL,
    key character varying(500) NOT NULL,
    entity_type character varying(100) NOT NULL,
    entity_id uuid,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    type character varying(200) NOT NULL,
    value text NOT NULL,
    name character varying(500) NOT NULL
);


--
-- Name: version_info; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.version_info (
    key character varying(300) NOT NULL,
    version character varying(300) NOT NULL,
    released_at timestamp without time zone NOT NULL,
    deployed_at timestamp without time zone NOT NULL,
    description character varying(300) NOT NULL
);


--
-- Name: bookmark bookmark_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bookmark
    ADD CONSTRAINT bookmark_pkey PRIMARY KEY (id);


--
-- Name: data_access_request data_access_request_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.data_access_request
    ADD CONSTRAINT data_access_request_pkey PRIMARY KEY (id);


--
-- Name: data_group_request data_group_request_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.data_group_request
    ADD CONSTRAINT data_group_request_pkey PRIMARY KEY (id);


--
-- Name: in_app_notification in_app_notification_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.in_app_notification
    ADD CONSTRAINT in_app_notification_pkey PRIMARY KEY (id);


--
-- Name: indicator_access indicator_access_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.indicator_access
    ADD CONSTRAINT indicator_access_pkey PRIMARY KEY (id);


--
-- Name: indicator_access indicator_access_tenant_id_indicator_id_user_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.indicator_access
    ADD CONSTRAINT indicator_access_tenant_id_indicator_id_user_id_key UNIQUE (tenant_id, indicator_id, user_id);


--
-- Name: indicator indicator_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.indicator
    ADD CONSTRAINT indicator_pkey PRIMARY KEY (id);


--
-- Name: notification notification_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notification
    ADD CONSTRAINT notification_pkey PRIMARY KEY (id);


--
-- Name: queue_inbox queue_inbox_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.queue_inbox
    ADD CONSTRAINT queue_inbox_pkey PRIMARY KEY (id);


--
-- Name: queue_outbox queue_outbox_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.queue_outbox
    ADD CONSTRAINT queue_outbox_pkey PRIMARY KEY (id);


--
-- Name: scheduled_event scheduled_event_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.scheduled_event
    ADD CONSTRAINT scheduled_event_pkey PRIMARY KEY (id);


--
-- Name: tenant_configuration tenant_configuration_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tenant_configuration
    ADD CONSTRAINT tenant_configuration_pkey PRIMARY KEY (id);


--
-- Name: tenant tenant_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tenant
    ADD CONSTRAINT tenant_pkey PRIMARY KEY (id);


--
-- Name: tenant_request tenant_request_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tenant_request
    ADD CONSTRAINT tenant_request_pkey PRIMARY KEY (id);


--
-- Name: tenant_user tenant_user_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tenant_user
    ADD CONSTRAINT tenant_user_pkey PRIMARY KEY (id);


--
-- Name: user_contact_info user_contact_info_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_contact_info
    ADD CONSTRAINT user_contact_info_pkey PRIMARY KEY (user_id, type);


--
-- Name: user_invitation user_invitation_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_invitation
    ADD CONSTRAINT user_invitation_pkey PRIMARY KEY (id);


--
-- Name: user_notification_preference user_notification_preference_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_notification_preference
    ADD CONSTRAINT user_notification_preference_pkey PRIMARY KEY (user_id, type, channel);


--
-- Name: user user_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);


--
-- Name: user_settings user_settings_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_settings
    ADD CONSTRAINT user_settings_pkey PRIMARY KEY (id);


--
-- Name: user user_subject_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public."user"
    ADD CONSTRAINT user_subject_id_key UNIQUE (subject_id);


--
-- Name: version_info version_info_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.version_info
    ADD CONSTRAINT version_info_pkey PRIMARY KEY (key);


--
-- Name: bookmark bookmark_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.bookmark
    ADD CONSTRAINT bookmark_user_id_fkey FOREIGN KEY (user_id) REFERENCES public."user"(id);


--
-- Name: data_access_request data_access_request_tenant_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.data_access_request
    ADD CONSTRAINT data_access_request_tenant_id_fkey FOREIGN KEY (tenant_id) REFERENCES public.tenant(id);


--
-- Name: data_access_request data_access_request_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.data_access_request
    ADD CONSTRAINT data_access_request_user_id_fkey FOREIGN KEY (user_id) REFERENCES public."user"(id);


--
-- Name: data_group_request data_group_request_tenant_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.data_group_request
    ADD CONSTRAINT data_group_request_tenant_id_fkey FOREIGN KEY (tenant_id) REFERENCES public.tenant(id);


--
-- Name: data_group_request data_group_request_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.data_group_request
    ADD CONSTRAINT data_group_request_user_id_fkey FOREIGN KEY (user_id) REFERENCES public."user"(id) NOT VALID;


--
-- Name: indicator_access indicator_access_indicator_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.indicator_access
    ADD CONSTRAINT indicator_access_indicator_id_fkey FOREIGN KEY (indicator_id) REFERENCES public.indicator(id);


--
-- Name: indicator_access indicator_access_tenant_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.indicator_access
    ADD CONSTRAINT indicator_access_tenant_id_fkey FOREIGN KEY (tenant_id) REFERENCES public.tenant(id);


--
-- Name: indicator_access indicator_access_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.indicator_access
    ADD CONSTRAINT indicator_access_user_id_fkey FOREIGN KEY (user_id) REFERENCES public."user"(id);


--
-- Name: notification notification_tenant_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notification
    ADD CONSTRAINT notification_tenant_id FOREIGN KEY (tenant_id) REFERENCES public.tenant(id);


--
-- Name: in_app_notification notification_tenant_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.in_app_notification
    ADD CONSTRAINT notification_tenant_id FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) NOT VALID;


--
-- Name: notification notification_user_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.notification
    ADD CONSTRAINT notification_user_id FOREIGN KEY (user_id) REFERENCES public."user"(id);


--
-- Name: in_app_notification notification_user_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.in_app_notification
    ADD CONSTRAINT notification_user_id FOREIGN KEY (user_id) REFERENCES public."user"(id) NOT VALID;


--
-- Name: queue_inbox queue_inbox_tenant_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.queue_inbox
    ADD CONSTRAINT queue_inbox_tenant_id_fkey FOREIGN KEY (tenant_id) REFERENCES public.tenant(id);


--
-- Name: queue_outbox queue_outbox_tenant_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.queue_outbox
    ADD CONSTRAINT queue_outbox_tenant_id_fkey FOREIGN KEY (tenant_id) REFERENCES public.tenant(id);


--
-- Name: scheduled_event scheduled_event_creator_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.scheduled_event
    ADD CONSTRAINT scheduled_event_creator_fkey FOREIGN KEY (creator) REFERENCES public."user"(id);


--
-- Name: tenant_configuration tenant_configuration_tenant_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tenant_configuration
    ADD CONSTRAINT tenant_configuration_tenant_id FOREIGN KEY (tenant_id) REFERENCES public.tenant(id);


--
-- Name: tenant_request tenant_request_assigned_tenant _id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tenant_request
    ADD CONSTRAINT "tenant_request_assigned_tenant _id_fkey" FOREIGN KEY (assigned_tenant_id) REFERENCES public.tenant(id) NOT VALID;


--
-- Name: tenant_request tenant_request_for_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tenant_request
    ADD CONSTRAINT tenant_request_for_user_id_fkey FOREIGN KEY (for_user_id) REFERENCES public."user"(id) NOT VALID;


--
-- Name: tenant_user tenant_user_tenant_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tenant_user
    ADD CONSTRAINT tenant_user_tenant_id_fkey FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) NOT VALID;


--
-- Name: tenant_user tenant_user_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tenant_user
    ADD CONSTRAINT tenant_user_user_id_fkey FOREIGN KEY (user_id) REFERENCES public."user"(id) NOT VALID;


--
-- Name: user_contact_info user_contact_info_tenant_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_contact_info
    ADD CONSTRAINT user_contact_info_tenant_id_fkey FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) NOT VALID;


--
-- Name: user_contact_info user_contact_info_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_contact_info
    ADD CONSTRAINT user_contact_info_user_id_fkey FOREIGN KEY (user_id) REFERENCES public."user"(id);


--
-- Name: user_invitation user_invitation_tenant_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_invitation
    ADD CONSTRAINT user_invitation_tenant_id_fkey FOREIGN KEY (tenant_id) REFERENCES public.tenant(id);


--
-- Name: user_notification_preference user_notification_preference_tenant_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_notification_preference
    ADD CONSTRAINT user_notification_preference_tenant_id FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) NOT VALID;


--
-- Name: user_notification_preference user_notification_preference_user_id; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_notification_preference
    ADD CONSTRAINT user_notification_preference_user_id FOREIGN KEY (user_id) REFERENCES public."user"(id);





--
-- PostgreSQL database dump complete
--

INSERT INTO public.version_info(
	key, version, released_at, deployed_at, description)
	VALUES ('StiViewer.Web.db', '01.00.000', '2023-01-01 00:00:00.000', now() at time zone 'utc', 'Init script');