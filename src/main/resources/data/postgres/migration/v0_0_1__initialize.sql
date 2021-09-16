/*
* Engine : POSTGRES
* Version : 0.0.1
* Description
* STRUCTURE
*/

create table address (
  id  bigserial not null,
  address1 varchar(255),
   address2 varchar(255),
    address3 varchar(255),
     email varchar(255),
      postal_code varchar(255),
       primary key (id));


 create table contact (
    id  bigserial not null,
     address1 varchar(255),
      address2 varchar(255),
       address3 varchar(255),
        age int4,
         email varchar(255),
          name varchar(255),
           note varchar(4000),
            phone varchar(255),
             postal_code varchar(255),
              primary key (id));
