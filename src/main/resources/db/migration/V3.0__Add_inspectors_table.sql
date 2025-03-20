-- создание типа 'confirmation_status', если он не существует
do
$$
    begin
        perform 1
        from pg_type
        where typname = 'confirmation_status';
        if not found then
            create type club.confirmation_status as enum ('WAITING', 'APPLIED', 'REJECT');
        end if;
    end
$$;

-- Автоматический каст строки в тип confirmation_status
DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1
                       FROM pg_cast
                       WHERE castsource = 'character varying'::regtype
                         AND casttarget = 'club.confirmation_status'::regtype) THEN
            CREATE CAST (character varying AS club.confirmation_status) WITH INOUT AS IMPLICIT;
        END IF;
    END
$$;

-- создание таблицы 'confirmation', если она не существует
create table if not exists club.confirmation
(
    id              uuid default gen_random_uuid()  primary key,
    approver_id     uuid                            not null,
    user_id uuid                                    not null,
    status          club.confirmation_status        not null,
    foreign key (approver_id) references club.user (id),
    foreign key (user_id) references club.user (id)
);