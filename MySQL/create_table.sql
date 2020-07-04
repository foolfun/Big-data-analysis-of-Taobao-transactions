use dbtaobao;
create table user_log(
    user_id varchar(10)	not null,
    item_id varchar(10) not null,
    cat_id varchar(10) not null,
		merchant_id varchar(10) not null,
    brand_id varchar(10) not null,
		month_ varchar(10) not null,
    day_ varchar(10) not null,
		action varchar(10) not null,
    age_range varchar(10) not null,
		gender varchar(10) not null,
    province varchar(10) not null
		);
