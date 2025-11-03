create table tbl_post_file(
    id bigint generated always as identity primary key,
    post_file_name varchar(255) not null,
    post_file_path varchar(255) not null,
    post_file_size varchar(255) not null,
    created_datetime timestamp default now(),
    updated_datetime timestamp default now(),
    post_id bigint not null,
    constraint fk_post_file_post foreign key (post_id)
                          references tbl_post(id)
);