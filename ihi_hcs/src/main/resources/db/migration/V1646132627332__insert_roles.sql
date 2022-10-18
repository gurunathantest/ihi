insert into roles (id, name) values ('dcdf7439-265f-4821-8436-96e8f3c3a664', 'ROLE_USER');

insert into roles (id, name) values ('c1d438aa-8004-4e4d-9d05-215e477c77f7', 'ROLE_SUPER_ADMIN');

INSERT INTO users(id,first_name,last_name,email,password,username,mobile_num,status)
VALUES  ('e9af162b-eb95-45b0-9486-4aa5e7ac9057','Admin','','superadmin@yopmail.com','$2a$12$5tG/j0ESv/HPFFzBDMmTne4BYFxvrDth4vx1v6FVanP3QLRswUkE6',
'superadmin@yopmail.com',9876543210,'ACTIVE');

INSERT INTO user_roles(user_id, role_id)
VALUES ('e9af162b-eb95-45b0-9486-4aa5e7ac9057', 'c1d438aa-8004-4e4d-9d05-215e477c77f7');

