CREATE SEQUENCE IF NOT EXISTS public.course_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE;

-- public.course definition

CREATE TABLE IF NOT EXISTS public.course
(
    id     int8        NOT NULL,
    "name" varchar(35) NOT NULL,
    CONSTRAINT course_pkey PRIMARY KEY (id),
    CONSTRAINT uk_4xqvdpkafb91tt3hsb67ga3fj UNIQUE (name)
);

CREATE SEQUENCE IF NOT EXISTS public.student_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1
    NO CYCLE;

-- public.student definition

CREATE TABLE IF NOT EXISTS public.student
(
    id        int8         NOT NULL,
    email     varchar(255) NOT NULL,
    firstname varchar(20)  NOT NULL,
    lastname  varchar(20)  NOT NULL,
    CONSTRAINT student_pkey PRIMARY KEY (id),
    CONSTRAINT uk_fe0i52si7ybu0wjedj6motiim UNIQUE (email)
);


-- public.student_course definition

CREATE TABLE IF NOT EXISTS public.student_course
(
    student_id int8 NOT NULL,
    course_id  int8 NOT NULL,
    CONSTRAINT student_course_pkey PRIMARY KEY (student_id, course_id),
    CONSTRAINT fkejrkh4gv8iqgmspsanaji90ws FOREIGN KEY (course_id) REFERENCES public.course (id),
    CONSTRAINT fkq7yw2wg9wlt2cnj480hcdn6dq FOREIGN KEY (student_id) REFERENCES public.student (id)
);
