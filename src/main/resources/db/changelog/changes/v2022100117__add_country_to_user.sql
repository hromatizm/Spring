ALTER TABLE ulab_edu.person
    ADD COLUMN country varchar(50) not null DEFAULT 'Not defined';

comment on column ulab_edu.person.country is 'Город проживания';