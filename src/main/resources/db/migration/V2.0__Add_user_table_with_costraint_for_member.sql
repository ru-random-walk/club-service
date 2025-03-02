-- Создание таблицы user (если не существует)
create table if not exists club.user
(
    id       uuid default gen_random_uuid() primary key,
    fullname varchar
);

-- Добавление внешнего ключа в таблицу 'member', если он еще не существует
DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1
                       FROM information_schema.table_constraints
                       WHERE constraint_name = 'member_id_fkey'
                         AND table_name = 'member'
                         AND table_schema = 'club') THEN
            ALTER TABLE club.member
                ADD CONSTRAINT member_id_fkey FOREIGN KEY (id) REFERENCES club.user (id);
        END IF;
    END
$$;