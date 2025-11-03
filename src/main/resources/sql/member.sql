create type status as enum('active', 'inactive');
create type member_role as enum('admin', 'member');

create table tbl_member(
    id bigint generated always as identity primary key,
    member_name varchar(255) not null,
    member_email varchar(255) unique,
    member_password varchar(255),
    member_status status default 'active',
    member_role member_role default 'member',
    sns_email varchar(255),
    provider varchar(255),
    profile_url varchar(255),
    created_date timestamp default now(),
    updated_date timestamp default now()
);

select * from tbl_member;











