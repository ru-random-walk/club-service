-- создание таблицы 'club', если она не существует
create table if not exists club.club
(
    id   uuid default gen_random_uuid() primary key,
    name varchar not null
);

-- создание типа 'approvement_type', если он не существует
do
$$
    begin
        perform 1
        from pg_type
        where typname = 'approvement_type';
        if not found then
            create type club.approvement_type as enum ('MEMBERS_CONFIRM', 'FORM');
        end if;
    end
$$;

-- Автоматический каст строки в тип approvement_type
DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1
                       FROM pg_cast
                       WHERE castsource = 'character varying'::regtype
                         AND casttarget = 'club.approvement_type'::regtype) THEN
            CREATE CAST (character varying AS club.approvement_type) WITH INOUT AS IMPLICIT;
        END IF;
    END
$$;

-- создание таблицы 'approvement', если она не существует
create table if not exists club.approvement
(
    id      uuid default gen_random_uuid() primary key,
    club_id uuid                  not null,
    type    club.approvement_type not null,
    data    jsonb                 not null,
    foreign key (club_id) references club.club (id)
);

-- создание типа 'member_role', если он не существует
do
$$
    begin
        perform 1
        from pg_type
        where typname = 'member_role';
        if not found then
            create type club.member_role as enum ('ADMIN', 'USER');
        end if;
    end
$$;

-- Автоматический каст строки в тип member_role
DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1
                       FROM pg_cast
                       WHERE castsource = 'character varying'::regtype
                         AND casttarget = 'club.member_role'::regtype) THEN
            CREATE CAST (character varying AS club.member_role) WITH INOUT AS IMPLICIT;
        END IF;
    END
$$;

-- создание таблицы 'member', если она не существует
create table if not exists club.member
(
    id      uuid             not null,
    club_id uuid             not null,
    role    club.member_role not null,
    foreign key (club_id) references club.club (id),
    unique (id, club_id)
);

-- создание типа 'answer_status', если он не существует
do
$$
    begin
        perform 1
        from pg_type
        where typname = 'answer_status';
        if not found then
            create type club.answer_status as enum ('CREATED', 'SENT', 'IN_PROCESS', 'IN_REVIEW', 'FAILED', 'PASSED');
        end if;
    end
$$;

-- Автоматический каст строки в тип answer_status
DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1
                       FROM pg_cast
                       WHERE castsource = 'character varying'::regtype
                         AND casttarget = 'club.answer_status'::regtype) THEN
            CREATE CAST (character varying AS club.answer_status) WITH INOUT AS IMPLICIT;
        END IF;
    END
$$;

-- создание таблицы 'answer', если она не существует
create table if not exists club.answer
(
    id             uuid default gen_random_uuid() primary key,
    approvement_id uuid               not null,
    user_id        uuid               not null,
    data           jsonb,
    status         club.answer_status not null,
    foreign key (approvement_id) references club.approvement (id)
);