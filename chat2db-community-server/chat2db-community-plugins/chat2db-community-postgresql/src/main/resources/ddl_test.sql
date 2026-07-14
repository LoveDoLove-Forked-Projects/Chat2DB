DROP TYPE IF EXISTS "position_level";
CREATE TYPE "position_level" AS ENUM ('junior', 'mid-level', 'senior', 'manager');

DROP TYPE IF EXISTS "position_level";
CREATE TYPE "position_level" AS ENUM ('junior', 'mid-level', 'senior', 'manager');
DROP TYPE IF EXISTS "position_level2'";
CREATE TYPE "position_level2'" AS ENUM ('junior', 'mid-level', 'senior', 'manager');

DROP TYPE IF EXISTS "inventory_item";
CREATE TYPE "inventory_item" AS (
    name text,
    supplier_id integer,
    price numeric
);

DROP TYPE IF EXISTS "inventory_item3";
CREATE TYPE "inventory_item3" AS (
    name character varying(255)
);

DROP TYPE IF EXISTS "inventory_item4";
CREATE TYPE "inventory_item4" AS (
    name character varying(255)
);

DROP TYPE IF EXISTS "inventory_item2";
CREATE TYPE "inventory_item2" AS (
    name text,
    supplier_id integer,
    price numeric
);

DROP SEQUENCE IF EXISTS "numeric_test_id_seq";
CREATE SEQUENCE "numeric_test_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test_column_17_seq";
CREATE SEQUENCE "numeric_test_column_17_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 32767
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test_column_18_seq";
CREATE SEQUENCE "numeric_test_column_18_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 9223372036854775807
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test_smallint_col_seq";
CREATE SEQUENCE "numeric_test_smallint_col_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 32767
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test_int_col_seq";
CREATE SEQUENCE "numeric_test_int_col_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test3_id_seq";
CREATE SEQUENCE "numeric_test3_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test_column_19_seq";
CREATE SEQUENCE "numeric_test_column_19_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test_column_20_seq";
CREATE SEQUENCE "numeric_test_column_20_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "foreign_sub_table_sub_id_seq";
CREATE SEQUENCE "foreign_sub_table_sub_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "text_types_test_id_seq";
CREATE SEQUENCE "text_types_test_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "bytea_test_id_seq";
CREATE SEQUENCE "bytea_test_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "datetime_test_id_seq";
CREATE SEQUENCE "datetime_test_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "boolean_tests_id_seq";
CREATE SEQUENCE "boolean_tests_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "employee_id_seq";
CREATE SEQUENCE "employee_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "employee2_id_seq";
CREATE SEQUENCE "employee2_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "geometric_shapes_id_seq";
CREATE SEQUENCE "geometric_shapes_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "network_addresses_id_seq";
CREATE SEQUENCE "network_addresses_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "bit_types_id_seq";
CREATE SEQUENCE "bit_types_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test_bigint_col_seq";
CREATE SEQUENCE "numeric_test_bigint_col_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 9223372036854775807
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test_column_21_seq";
CREATE SEQUENCE "numeric_test_column_21_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test_column_24_seq";
CREATE SEQUENCE "numeric_test_column_24_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "test_comment_table_id_seq";
CREATE SEQUENCE "test_comment_table_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "test_comment_table2_id_seq";
CREATE SEQUENCE "test_comment_table2_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "test_constraint_table_employee_id_seq";
CREATE SEQUENCE "test_constraint_table_employee_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "foreign_table_department_id_seq";
CREATE SEQUENCE "foreign_table_department_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "test_constraint_index_table2_employee_id_seq";
CREATE SEQUENCE "test_constraint_index_table2_employee_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test3_smallint_col_seq";
CREATE SEQUENCE "numeric_test3_smallint_col_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 32767
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test4_id_seq";
CREATE SEQUENCE "numeric_test4_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test4_smallint_col_seq";
CREATE SEQUENCE "numeric_test4_smallint_col_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 32767
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test4_int_col_seq";
CREATE SEQUENCE "numeric_test4_int_col_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test4_bigint_col_seq";
CREATE SEQUENCE "numeric_test4_bigint_col_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 9223372036854775807
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test4_column_17_seq";
CREATE SEQUENCE "numeric_test4_column_17_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 32767
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test4_column_18_seq";
CREATE SEQUENCE "numeric_test4_column_18_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 9223372036854775807
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test2_id_seq";
CREATE SEQUENCE "numeric_test2_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test2_smallint_col_seq";
CREATE SEQUENCE "numeric_test2_smallint_col_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 32767
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test2_int_col_seq";
CREATE SEQUENCE "numeric_test2_int_col_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test2_bigint_col_seq";
CREATE SEQUENCE "numeric_test2_bigint_col_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 9223372036854775807
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test2_column_17_seq";
CREATE SEQUENCE "numeric_test2_column_17_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 32767
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test2_column_18_seq";
CREATE SEQUENCE "numeric_test2_column_18_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 9223372036854775807
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test2_column_19_seq";
CREATE SEQUENCE "numeric_test2_column_19_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test2_column_20_seq";
CREATE SEQUENCE "numeric_test2_column_20_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test2_column_21_seq";
CREATE SEQUENCE "numeric_test2_column_21_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test2_column_24_seq";
CREATE SEQUENCE "numeric_test2_column_24_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test2_column_25_seq";
CREATE SEQUENCE "numeric_test2_column_25_seq"
 START WITH 100
 INCREMENT BY 2
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test4_column_19_seq";
CREATE SEQUENCE "numeric_test4_column_19_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test4_column_20_seq";
CREATE SEQUENCE "numeric_test4_column_20_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test4_column_21_seq";
CREATE SEQUENCE "numeric_test4_column_21_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test4_column_24_seq";
CREATE SEQUENCE "numeric_test4_column_24_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test4_column_25_seq";
CREATE SEQUENCE "numeric_test4_column_25_seq"
 START WITH 100
 INCREMENT BY 2
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "orders_order_id_seq";
CREATE SEQUENCE "orders_order_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "user_data_user_id_seq";
CREATE SEQUENCE "user_data_user_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test_column_25_seq";
CREATE SEQUENCE "numeric_test_column_25_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test3_int_col_seq";
CREATE SEQUENCE "numeric_test3_int_col_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test3_bigint_col_seq";
CREATE SEQUENCE "numeric_test3_bigint_col_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 9223372036854775807
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test3_column_17_seq";
CREATE SEQUENCE "numeric_test3_column_17_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 32767
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test3_column_18_seq";
CREATE SEQUENCE "numeric_test3_column_18_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 9223372036854775807
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test3_column_19_seq";
CREATE SEQUENCE "numeric_test3_column_19_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test3_column_20_seq";
CREATE SEQUENCE "numeric_test3_column_20_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test3_column_21_seq";
CREATE SEQUENCE "numeric_test3_column_21_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test3_column_24_seq";
CREATE SEQUENCE "numeric_test3_column_24_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test3_column_25_seq";
CREATE SEQUENCE "numeric_test3_column_25_seq"
 START WITH 100
 INCREMENT BY 2
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test_column_26_seq";
CREATE SEQUENCE "numeric_test_column_26_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 32767
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "test_int_table_column_4_seq";
CREATE SEQUENCE "test_int_table_column_4_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 32767
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "test_int_table_column_5_seq";
CREATE SEQUENCE "test_int_table_column_5_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "test_int_table_column_6_seq";
CREATE SEQUENCE "test_int_table_column_6_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 9223372036854775807
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "my_sequence";
CREATE SEQUENCE "my_sequence"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 9223372036854775807
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "my_table_column_3_seq";
CREATE SEQUENCE "my_table_column_3_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "original_table_id_seq";
CREATE SEQUENCE "original_table_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "xmltypestest_id_seq";
CREATE SEQUENCE "xmltypestest_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "xmltypestestchanges_changeid_seq";
CREATE SEQUENCE "xmltypestestchanges_changeid_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "departments_id_seq";
CREATE SEQUENCE "departments_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "employees_id_seq";
CREATE SEQUENCE "employees_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "users_user_id_seq";
CREATE SEQUENCE "users_user_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "products_product_id_seq";
CREATE SEQUENCE "products_product_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "my_sequence2";
CREATE SEQUENCE "my_sequence2"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 10000
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "my_sequence3";
CREATE SEQUENCE "my_sequence3"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 9223372036854775801
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test7_id_seq";
CREATE SEQUENCE "numeric_test7_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test8_id_seq";
CREATE SEQUENCE "numeric_test8_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
NO CYCLE
;

DROP SEQUENCE IF EXISTS "numeric_test9_id_seq";
CREATE SEQUENCE "numeric_test9_id_seq"
 START WITH 1
 INCREMENT BY 1
 MAXVALUE 2147483647
 MINVALUE 1
 CACHE 1
 CYCLE
;


DROP TABLE IF EXISTS "employee2";
create table "employee2"
(
	id  	integer default nextval('employee2_id_seq'::regclass) not null,
	first_name  	varchar(50),
	last_name  	varchar(50),
	"position"  	"position_level2'",
	hire_date  	date,
	constraint employee2_pkey primary key (id)

) tablespace pg_default;


alter table "employee2" owner to ali_dbhub;

grant select on "employee2" to only_read;


-- ----------------------------
-- Records of employee2
-- ----------------------------

DROP TABLE IF EXISTS "datetime_test";
create table "datetime_test"
(
	id  	integer default nextval('datetime_test_id_seq'::regclass) not null,
	date_col  	date default '2024-06-15'::date,
	time_without_tz_col  	time(3) default '20:24:00'::time without time zone,
	time_with_tz_col  	time(3) with time zone,
	timestamp_without_tz_col  	timestamp(3),
	timestamp_with_tz_col  	timestamp(3) with time zone,
	interval_col  	interval,
	column_8  	time,
	column_9  	time(0),
	column_10  	interval(4),
	column_11  	interval(0),
	constraint datetime_test_pkey primary key (id)

) tablespace pg_default;

comment on table datetime_test is '时间类型测试表';

alter table "datetime_test" owner to ali_dbhub;

grant select on "datetime_test" to only_read;


-- ----------------------------
-- Records of datetime_test
-- ----------------------------
INSERT INTO datetime_test (id,date_col,time_without_tz_col,time_with_tz_col,timestamp_without_tz_col,timestamp_with_tz_col,interval_col,column_8,column_9,column_10,column_11)  VALUES ('1','2024-06-15','20:24:00','04:24:00','2024-06-15 20:24:00','2024-06-16 04:24:00','0 years 0 mons 2 days 10 hours 0 mins 0.0 secs','20:24:00','20:24:00','0 years 0 mons 2 days 10 hours 0 mins 0.0 secs','0 years 0 mons 2 days 0 hours 0 mins 0.0 secs');
INSERT INTO datetime_test (id,date_col,time_without_tz_col,time_with_tz_col,timestamp_without_tz_col,timestamp_with_tz_col,interval_col,column_8,column_9,column_10,column_11)  VALUES ('2','2024-06-27','20:24:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO datetime_test (id,date_col,time_without_tz_col,time_with_tz_col,timestamp_without_tz_col,timestamp_with_tz_col,interval_col,column_8,column_9,column_10,column_11)  VALUES ('3','2024-06-27','20:24:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);

DROP TABLE IF EXISTS "geometric_shapes";
create table "geometric_shapes"
(
	id  	integer default nextval('geometric_shapes_id_seq'::regclass) not null,
	point_shape  	point,
	line_shape  	line,
	lseg_shape  	lseg,
	box_shape  	box,
	closed_path  	path,
	open_path  	path,
	polygon_shape  	polygon,
	circle_shape  	circle,
	constraint geometric_shapes_pkey primary key (id)

) tablespace pg_default;

comment on table geometric_shapes is '空间类型测试';

alter table "geometric_shapes" owner to ali_dbhub;

grant select on "geometric_shapes" to only_read;


-- ----------------------------
-- Records of geometric_shapes
-- ----------------------------

DROP TABLE IF EXISTS "network_addresses";
create table "network_addresses"
(
	id  	integer default nextval('network_addresses_id_seq'::regclass) not null,
	ipv4_cidr  	cidr,
	ipv6_cidr  	cidr,
	ipv4_inet  	inet,
	ipv6_inet  	inet,
	mac_address  	macaddr,
	extended_mac_address  	macaddr8,
	constraint network_addresses_pkey primary key (id)

) tablespace pg_default;

comment on table network_addresses is '网络类型测试';

alter table "network_addresses" owner to ali_dbhub;

grant select on "network_addresses" to only_read;


-- ----------------------------
-- Records of network_addresses
-- ----------------------------

DROP TABLE IF EXISTS "bytea_test";
create table "bytea_test"
(
	id  	integer default nextval('bytea_test_id_seq'::regclass) not null,
	binary_data  	bytea,
	text_data  	text,
	constraint bytea_test_pkey primary key (id)

) tablespace pg_default;

comment on table bytea_test is '测试二进制类型';

alter table "bytea_test" owner to ali_dbhub;

grant select on "bytea_test" to only_read;


-- ----------------------------
-- Records of bytea_test
-- ----------------------------

DROP TABLE IF EXISTS "xml_type";
create table "xml_type"
(
	column_1  	xml
) tablespace pg_default;

comment on table xml_type is 'xml类型测试表';

alter table "xml_type" owner to ali_dbhub;

grant select on "xml_type" to only_read;


-- ----------------------------
-- Records of xml_type
-- ----------------------------
INSERT INTO xml_type (column_1)  VALUES ('
<book>
    <title>PostgreSQL Guide</title>
    <author>John Smith</author>
    <year>2023</year>
</book>');
INSERT INTO xml_type (column_1)  VALUES ('
       <order>
           <id>12345</id>
           <customer>Jane Doe</customer>
           <items>
               <item>
                   <name>Laptop</name>
                   <price>1200.00</price>
               </item>
               <item>
                   <name>Mouse</name>
                   <price>25.00</price>
               </item>
           </items>
       </order>');

DROP TABLE IF EXISTS "json_types";
create table "json_types"
(
	column_1  	json,
	column_2  	jsonb,
	column_3  	jsonpath
) tablespace pg_default;

comment on table json_types is 'json类型测试';

alter table "json_types" owner to ali_dbhub;

grant select on "json_types" to only_read;


-- ----------------------------
-- Records of json_types
-- ----------------------------
INSERT INTO json_types (column_1,column_2,column_3)  VALUES ('{"name": "John", "age": 30, "city": "New York"}','{"age": 25, "city": "San Francisco", "name": "Jane"}',NULL);
INSERT INTO json_types (column_1,column_2,column_3)  VALUES ('24',NULL,NULL);
INSERT INTO json_types (column_1,column_2,column_3)  VALUES ('3213',NULL,NULL);
INSERT INTO json_types (column_1,column_2,column_3)  VALUES ('{
  "name": "John",
  "age": 30,
  "city": "New York"
}','{"age": 25, "city": "San Francisco", "name": "Jane"}',NULL);
INSERT INTO json_types (column_1,column_2,column_3)  VALUES ('24',NULL,NULL);
INSERT INTO json_types (column_1,column_2,column_3)  VALUES ('3213',NULL,NULL);

DROP TABLE IF EXISTS "cities_parent_table";
create table "cities_parent_table"
(
	name  	text,
	population  	double precision,
	altitude  	integer
) tablespace pg_default;

comment on table cities_parent_table is '测试父子表关系父表';

alter table "cities_parent_table" owner to ali_dbhub;

grant select on "cities_parent_table" to only_read;


-- ----------------------------
-- Records of cities_parent_table
-- ----------------------------

DROP TABLE IF EXISTS "array_type";
create table "array_type"
(
	name  	text,
	pay_by_quarter  	integer[],
	schedule  	text[],
	column_2  	char[],
	column_3  	integer[],
	column_4  	json[],
	column_5  	json[],
	column_8  	character varying[],
	column_9  	bit[],
	column_10  	bit varying[],
	column_11  	smallint[],
	column_12  	bigint[],
	column_13  	jsonb[],
	column_14  	jsonpath[],
	column_15  	time without time zone[],
	column_16  	timestamp without time zone[],
	column_17  	time with time zone[],
	column_18  	timestamp with time zone[],
	column_19  	point[],
	column_20  	path[],
	column_21  	line[],
	column_22  	lseg[],
	column_23  	box[],
	column_24  	polygon[],
	column_25  	circle[],
	column_26  	cidr[],
	column_27  	inet[],
	column_28  	macaddr[],
	column_29  	macaddr8[],
	column_30  	xml[],
	column_31  	tsvector[],
	column_32  	tsquery[]
) tablespace pg_default;

comment on table array_type is '测试数组类型';
comment on column array_type.column_11 is 'klnl''';

alter table "array_type" owner to ali_dbhub;

grant select on "array_type" to only_read;


-- ----------------------------
-- Records of array_type
-- ----------------------------

DROP TABLE IF EXISTS "capitals_sub_table";
create table "capitals_sub_table"
(
	state  	char(2),
	column_5  	integer
)  inherits ("cities_parent_table")
 tablespace pg_default;

comment on table capitals_sub_table is '测试表继承关系子表';

alter table "capitals_sub_table" owner to ali_dbhub;

grant select on "capitals_sub_table" to only_read;


-- ----------------------------
-- Records of capitals_sub_table
-- ----------------------------

DROP TABLE IF EXISTS "text_search_type";
create table "text_search_type"
(
	column_1  	tsquery,
	column_2  	tsvector
) tablespace pg_default;

comment on table text_search_type is '文本搜索类型测试表';

alter table "text_search_type" owner to ali_dbhub;

grant select on "text_search_type" to only_read;


-- ----------------------------
-- Records of text_search_type
-- ----------------------------

DROP TABLE IF EXISTS "test_comment_table2";
create table "test_comment_table2"
(
	id  	integer default nextval('test_comment_table2_id_seq'::regclass) not null,
	first_name  	varchar(50) not null,
	last_name  	varchar(50) not null,
	email  	varchar(100),
	hire_date  	date not null,
	department_id  	integer,
	constraint test_comment_table2_pkey primary key (id),
	constraint test_comment_table2_email_key unique (email)

) tablespace pg_default;
CREATE INDEX idx_employees2_email ON test_comment_table2 USING btree (email);

comment on table test_comment_table2 is '员工信息表，存储所有员工的详细信息''';
comment on column test_comment_table2.id is '''员工的唯一标识符';
comment on column test_comment_table2.first_name is '员工的名字';
comment on column test_comment_table2.last_name is '员工的姓氏';
comment on column test_comment_table2.email is '员工的工作电子邮件地址';
comment on column test_comment_table2.hire_date is '员工入职公司的日期';
comment on column test_comment_table2.department_id is '员工所属部门的ID';
comment on index idx_employees2_email is '用于快速查找员工的电子邮件地址';

alter table "test_comment_table2" owner to ali_dbhub;

grant select on "test_comment_table2" to only_read;


-- ----------------------------
-- Records of test_comment_table2
-- ----------------------------

DROP TABLE IF EXISTS "foreign_table";
create table "foreign_table"
(
	department_id  	integer default nextval('foreign_table_department_id_seq'::regclass) not null,
	department_name  	varchar(100) not null,
	constraint foreign_table_pkey primary key (department_id),
	constraint foreign_table_department_name_key unique (department_name)

) tablespace pg_default;


alter table "foreign_table" owner to ali_dbhub;

grant select on "foreign_table" to only_read;


-- ----------------------------
-- Records of foreign_table
-- ----------------------------

DROP TABLE IF EXISTS "test_constraint_index_table2";
create table "test_constraint_index_table2"
(
	employee_id  	integer default nextval('test_constraint_index_table2_employee_id_seq'::regclass) not null,
	first_name  	varchar(50) not null,
	last_name  	varchar(50) not null,
	email  	varchar(100),
	hire_date  	date not null,
	age  	integer,
	department_id  	integer,
	level  	integer not null,
	"position"  	varchar(50) not null,
	constraint test_constraint_table2_pkey primary key (employee_id),
	constraint test_constraint_table2_email_key unique (email),
	constraint unique_first_last2_name unique (first_name, last_name),
	constraint fk_department2_id foreign key (department_id) references foreign_table(department_id),
	constraint test_constraint_table_hire_date2_check check ((hire_date <= current_date)),
	constraint test_constraint_table_age2_check check (((age >= 18) and (age <= 65)))

) tablespace pg_default;
CREATE INDEX idx_level_department2_id ON test_constraint_index_table2 USING btree (level, department_id DESC);
CREATE INDEX idx_first_last_name2 ON test_constraint_index_table2 USING btree (first_name, last_name);
CREATE INDEX idx_position_department2_id ON test_constraint_index_table2 USING btree ("position", department_id);

comment on column test_constraint_index_table2.employee_id is '员工ID';
comment on column test_constraint_index_table2.first_name is '员工的名字';
comment on column test_constraint_index_table2.last_name is '员工的姓氏';
comment on column test_constraint_index_table2.email is '员工的电子邮件';
comment on column test_constraint_index_table2.hire_date is '员工的入职日期';
comment on column test_constraint_index_table2.age is '员工的年龄';
comment on column test_constraint_index_table2.department_id is '部门ID';
comment on index idx_level_department2_id is '根据级别和部门ID（降序）的复合索引，优化特定排序和查询';
comment on index idx_first_last_name2 is '基于名字和姓氏的复合唯一性索引，确保姓名组合的唯一性';
comment on index idx_position_department2_id is '职位与部门ID的复合索引，用于加速涉及职位和部门的查询';

alter table "test_constraint_index_table2" owner to ali_dbhub;

grant select on "test_constraint_index_table2" to only_read;


-- ----------------------------
-- Records of test_constraint_index_table2
-- ----------------------------

DROP TABLE IF EXISTS "test_constraint_index_table";
create table "test_constraint_index_table"
(
	employee_id  	integer default nextval('test_constraint_table_employee_id_seq'::regclass) not null,
	first_name  	varchar(50) not null,
	last_name  	varchar(50) not null,
	email  	varchar(100),
	hire_date  	date not null,
	age  	integer,
	department_id  	integer,
	level  	integer not null,
	"position"  	varchar(50) not null,
	constraint test_constraint_table_pkey primary key (employee_id),
	constraint test_constraint_table_email_key unique (email),
	constraint unique_first_last_name unique (first_name, last_name),
	constraint fk_department_id foreign key (department_id) references foreign_table(department_id),
	constraint test_constraint_table_hire_date_check check ((hire_date <= current_date)),
	constraint test_constraint_table_age_check check (((age >= 18) and (age <= 65)))

) tablespace pg_default;
CREATE INDEX idx_level_department_id ON test_constraint_index_table USING btree (level, department_id DESC);
CREATE INDEX idx_first_last_name ON test_constraint_index_table USING btree (first_name, last_name);
CREATE INDEX idx_position_department_id ON test_constraint_index_table USING btree ("position", department_id);

comment on table test_constraint_index_table is '约束以及索引测试表';
comment on column test_constraint_index_table.employee_id is '员工ID';
comment on column test_constraint_index_table.first_name is '员工的名字';
comment on column test_constraint_index_table.last_name is '员工的姓氏';
comment on column test_constraint_index_table.email is '员工的电子邮件';
comment on column test_constraint_index_table.hire_date is '员工的入职日期';
comment on column test_constraint_index_table.age is '员工的年龄';
comment on column test_constraint_index_table.department_id is '部门ID';
comment on index idx_level_department_id is '根据级别和部门ID（降序）的复合索引，优化特定排序和查询';
comment on index idx_first_last_name is '基于名字和姓氏的复合唯一性索引，确保姓名组合的唯一性';
comment on index idx_position_department_id is '职位与部门ID的复合索引，用于加速涉及职位和部门的查询';

alter table "test_constraint_index_table" owner to ali_dbhub;

grant select on "test_constraint_index_table" to only_read;


-- ----------------------------
-- Records of test_constraint_index_table
-- ----------------------------

DROP TABLE IF EXISTS "foreign_parent_table";
create table "foreign_parent_table"
(
	id1  	integer not null,
	id2  	integer not null,
	data  	varchar(255),
	constraint foreign_parent_table_pkey primary key (id1, id2)

) tablespace pg_default;

comment on table foreign_parent_table is '测试复合主键以及复合外键约束';

alter table "foreign_parent_table" owner to ali_dbhub;

grant select on "foreign_parent_table" to only_read;


-- ----------------------------
-- Records of foreign_parent_table
-- ----------------------------

DROP TABLE IF EXISTS "foreign_sub_table";
create table "foreign_sub_table"
(

	sub_id  	integer default nextval('foreign_sub_table_sub_id_seq'::regclass) not null,
	parent_id1  	integer not null,
	parent_id2  	integer not null,
	sub_data  	varchar(255),
	constraint foreign_sub_table_pkey primary key (sub_id),
	constraint foreign_sub_table_parent_id1_parent_id2_fkey foreign key (parent_id1, parent_id2) references foreign_parent_table(id1, id2)

) tablespace pg_default;


alter table "foreign_sub_table" owner to ali_dbhub;

grant select on "foreign_sub_table" to only_read;


-- ----------------------------
-- Records of foreign_sub_table
-- ----------------------------

DROP TABLE IF EXISTS "measurement";
create table "measurement"
(
	city_id  	integer not null,
	logdate  	date not null,
	peaktemp  	integer,
	unitsales  	integer
)partition by range (logdate);

create table "measurement_y2006m02"
partition of measurement
for values from ('2006-02-01') to ('2006-03-01');

create table "measurement_y2006m03"
partition of measurement
for values from ('2006-03-01') to ('2006-04-01');

create table "measurement_y2007m11"
partition of measurement
for values from ('2007-11-01') to ('2007-12-01');

create table "measurement_y2008m01"
partition of measurement
for values from ('2008-01-01') to ('2008-02-01');

create table "measurement_y2008m03"
partition of measurement
for values from ('2008-03-01') to ('2008-04-01');

create table "measurement_y2008m06"
partition of measurement
for values from ('2008-07-01') to ('2008-08-01');

create table "measurement_y2008m010"
partition of measurement
for values from ('2008-10-01') to ('2008-11-01');

create table "measurement_y2018m06"
partition of measurement
for values from ('2018-07-01') to ('2018-08-01');

create table "measurement_y2019m06"
partition of measurement
for values from ('2019-07-01') to ('2019-08-01');

comment on table measurement is '范围分区表主表';

alter table "measurement" owner to ali_dbhub;

grant select on "measurement" to only_read;


-- ----------------------------
-- Records of measurement
-- ----------------------------

DROP TABLE IF EXISTS "test_comment_table";
create table "test_comment_table"
(
	id  	integer default nextval('test_comment_table_id_seq'::regclass) not null,
	first_name  	varchar(50) not null,
	last_name  	varchar(50) not null,
	email  	varchar(100),
	hire_date  	date not null,
	department_id  	integer,
	constraint test_comment_table_pkey primary key (id),
	constraint test_comment_table_email_key unique (email)

) tablespace pg_default;
CREATE INDEX idx_employees_email ON test_comment_table USING btree (email);

comment on table test_comment_table is '注释测试表';
comment on column test_comment_table.id is '''员工的唯一标识符';
comment on column test_comment_table.first_name is '员工的名字';
comment on column test_comment_table.last_name is '员工的姓氏';
comment on column test_comment_table.email is '员工的工作电子邮件地址';
comment on column test_comment_table.hire_date is '员工入职公司的日期';
comment on column test_comment_table.department_id is '员工所属部门的ID';
comment on index idx_employees_email is '用于快速查找员工的电子邮件地址';

alter table "test_comment_table" owner to ali_dbhub;

grant select on "test_comment_table" to only_read;


-- ----------------------------
-- Records of test_comment_table
-- ----------------------------

DROP TABLE IF EXISTS "measurement2";
create table "measurement2"
(
	city_id  	integer not null,
	logdate  	date not null,
	peaktemp  	integer,
	unitsales  	integer
)partition by range (logdate);

create table "measurement2_y2006m02"
partition of measurement2
for values from ('2006-02-01') to ('2006-03-01');

create table "measurement2_y2006m03"
partition of measurement2
for values from ('2006-03-01') to ('2006-04-01');

create table "measurement2_y2007m11"
partition of measurement2
for values from ('2007-11-01') to ('2007-12-01');

create table "measurement2_y2008m01"
partition of measurement2
for values from ('2008-01-01') to ('2008-02-01');

create table "measurement2_y2026m02"
partition of measurement2
for values from ('2025-02-01') to ('2026-03-01');

create table "measurement2_y2028m02"
partition of measurement2
for values from ('2027-02-01') to ('2028-03-01');

create table "measurement2_y2030m02"
partition of measurement2
for values from ('2030-02-01') to ('2030-03-01');

comment on table measurement2 is '范围分区表主表';

alter table "measurement2" owner to ali_dbhub;

grant select on "measurement2" to only_read;


-- ----------------------------
-- Records of measurement2
-- ----------------------------

DROP TABLE IF EXISTS "numeric_test3";
create table "numeric_test3"
(
	id  	integer default nextval('numeric_test3_id_seq'::regclass) not null,
	smallint_col  	smallint default nextval('numeric_test3_smallint_col_seq'::regclass) not null,
	int_col  	integer default nextval('numeric_test3_int_col_seq'::regclass) not null,
	bigint_col  	bigint default nextval('numeric_test3_bigint_col_seq'::regclass) not null,
	numeric_default_col  	numeric,
	numeric_prec10_scale2_col  	numeric(10,2),
	numeric_prec20_scale5_col  	numeric(20,5),
	numeric_max_prec_col  	numeric(1000),
	numeric_nan_col  	numeric,
	column_10  	numeric(1,1),
	column_11  	numeric(1),
	column_12  	numeric(1),
	column_13  	numeric(5,2),
	column_14  	numeric(1000,78),
	column_15  	numeric,
	column_16  	double precision,
	column_17  	smallint default nextval('numeric_test3_column_17_seq'::regclass) not null,
	column_18  	bigint default nextval('numeric_test3_column_18_seq'::regclass) not null,
	column_19  	integer generated by default as identity,
	column_22  	money,
	column_23  	real,
	column_20  	integer generated by default as identity,
	column_21  	integer generated by default as identity,
	column_24  	integer generated by default as identity,
	column_25  	integer generated by default as identity (start with 100 increment by 2),
	constraint numeric3_test_pkey primary key (id)

)  inherits ("cities_parent_table")
  with (autovacuum_enabled=true,autovacuum_vacuum_threshold=50)
 tablespace pg_default;


alter table "numeric_test3" owner to ali_dbhub;

grant select on "numeric_test3" to only_read;


-- ----------------------------
-- Records of numeric_test3
-- ----------------------------

DROP TABLE IF EXISTS "numeric_test2";
create table "numeric_test2"
(
	id  	integer default nextval('numeric_test2_id_seq'::regclass) not null,
	smallint_col  	smallint default nextval('numeric_test2_smallint_col_seq'::regclass) not null,
	int_col  	integer default nextval('numeric_test2_int_col_seq'::regclass) not null,
	bigint_col  	bigint default nextval('numeric_test2_bigint_col_seq'::regclass) not null,
	numeric_default_col  	numeric,
	numeric_prec10_scale2_col  	numeric(10,2),
	numeric_prec20_scale5_col  	numeric(20,5),
	numeric_max_prec_col  	numeric(1000),
	numeric_nan_col  	numeric,
	column_10  	numeric(1,1),
	column_11  	numeric(1),
	column_12  	numeric(1),
	column_13  	numeric(5,2),
	column_14  	numeric(1000,78),
	column_15  	numeric,
	column_16  	double precision,
	column_17  	smallint default nextval('numeric_test2_column_17_seq'::regclass) not null,
	column_18  	bigint default nextval('numeric_test2_column_18_seq'::regclass) not null,
	column_19  	integer generated by default as identity,
	column_22  	money,
	column_23  	real,
	column_20  	integer generated by default as identity,
	column_21  	integer generated by default as identity,
	column_24  	integer generated by default as identity,
	column_25  	integer generated by default as identity (start with 100 increment by 2),
	constraint numeric2_test_pkey primary key (id)

) tablespace pg_default;


alter table "numeric_test2" owner to ali_dbhub;

grant select on "numeric_test2" to only_read;


-- ----------------------------
-- Records of numeric_test2
-- ----------------------------

DROP TABLE IF EXISTS "user_data";
create table "user_data"
(
	user_id  	integer default nextval('user_data_user_id_seq'::regclass) not null,
	user_name  	text not null,
	constraint user_data_pkey primary key (user_id)

)partition by hash (user_id);

create table "user_data_p0"
partition of user_data
for values with (modulus 4, remainder 0);

create table "user_data_p1"
partition of user_data
for values with (modulus 4, remainder 1);

create table "user_data_p2"
partition of user_data
for values with (modulus 4, remainder 2);

create table "user_data_p3"
partition of user_data
for values with (modulus 4, remainder 3);

comment on table user_data is '哈希分区';

alter table "user_data" owner to ali_dbhub;


-- ----------------------------
-- Records of user_data
-- ----------------------------
INSERT INTO user_data (user_id,user_name)  VALUES ('1','Alice');
INSERT INTO user_data (user_id,user_name)  VALUES ('3','Charlie');
INSERT INTO user_data (user_id,user_name)  VALUES ('5','Eve');
INSERT INTO user_data (user_id,user_name)  VALUES ('2','Bob');
INSERT INTO user_data (user_id,user_name)  VALUES ('4','Diana');

DROP TABLE IF EXISTS "numeric_test4";
create table "numeric_test4"
(
	id  	integer default nextval('numeric_test4_id_seq'::regclass) not null,
	smallint_col  	smallint default nextval('numeric_test4_smallint_col_seq'::regclass) not null,
	int_col  	integer default nextval('numeric_test4_int_col_seq'::regclass) not null,
	bigint_col  	bigint default nextval('numeric_test4_bigint_col_seq'::regclass) not null,
	numeric_default_col  	numeric,
	numeric_prec10_scale2_col  	numeric(10,2),
	numeric_prec20_scale5_col  	numeric(20,5),
	numeric_max_prec_col  	numeric(1000),
	numeric_nan_col  	numeric,
	column_10  	numeric(1,1),
	column_11  	numeric(1),
	column_12  	numeric(1),
	column_13  	numeric(5,2),
	column_14  	numeric(1000,78),
	column_15  	numeric,
	column_16  	double precision,
	column_17  	smallint default nextval('numeric_test4_column_17_seq'::regclass) not null,
	column_18  	bigint default nextval('numeric_test4_column_18_seq'::regclass) not null,
	column_19  	integer generated by default as identity,
	column_22  	money,
	column_23  	real,
	column_20  	integer generated by default as identity,
	column_21  	integer generated by default as identity,
	column_24  	integer generated by default as identity,
	column_25  	integer generated by default as identity (start with 100 increment by 2),
	constraint numeric_test4_pkey primary key (id)

)  inherits ("cities_parent_table")
  with (autovacuum_enabled=true,autovacuum_vacuum_threshold=50)
 tablespace pg_default;


alter table "numeric_test4" owner to ali_dbhub;

grant select on "numeric_test4" to only_read;


-- ----------------------------
-- Records of numeric_test4
-- ----------------------------

DROP TABLE IF EXISTS "orders";
create table "orders"
(
	order_id  	integer default nextval('orders_order_id_seq'::regclass) not null,
	customer_id  	integer not null,
	order_status  	text not null,
	column_4  	integer,
	constraint orders_pkey primary key (order_id, order_status)

)partition by list (order_status);

create table "orders_pending"
partition of orders
for values in ('pending');

create table "orders_completed"
partition of orders
for values in ('completed');

create table "orders_cancelled"
partition of orders
for values in ('cancelled');

comment on table orders is '列表分区';

alter table "orders" owner to ali_dbhub;

grant select on "orders" to only_read;


-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO orders (order_id,customer_id,order_status,column_4)  VALUES ('4','4','cancelled',NULL);
INSERT INTO orders (order_id,customer_id,order_status,column_4)  VALUES ('2','2','completed',NULL);
INSERT INTO orders (order_id,customer_id,order_status,column_4)  VALUES ('1','1','pending',NULL);
INSERT INTO orders (order_id,customer_id,order_status,column_4)  VALUES ('3','3','pending',NULL);

DROP TABLE IF EXISTS "empty";
create table "empty"
(
) tablespace pg_default;


-- ----------------------------
-- Records of empty
-- ----------------------------

DROP TABLE IF EXISTS "numeric_test";
create table "numeric_test"
(
	id  	integer default nextval('numeric_test_id_seq'::regclass) not null,
	smallint_col  	smallint default nextval('numeric_test_smallint_col_seq'::regclass) not null,
	int_col  	integer default nextval('numeric_test_int_col_seq'::regclass) not null,
	bigint_col  	bigint default nextval('numeric_test_bigint_col_seq'::regclass) not null,
	numeric_default_col  	numeric,
	numeric_prec10_scale2_col  	numeric(10,2),
	numeric_prec20_scale5_col  	numeric(20,5),
	numeric_max_prec_col  	numeric(1000),
	numeric_nan_col  	numeric default 47467.88 not null,
	column_10  	numeric(1,1),
	column_11  	numeric(1),
	column_12  	numeric(1),
	column_13  	numeric(5,2),
	column_14  	numeric(1000,78),
	column_15  	numeric,
	column_16  	double precision default 67.88,
	column_17  	smallint default nextval('numeric_test_column_17_seq'::regclass) not null,
	column_18  	bigint default nextval('numeric_test_column_18_seq'::regclass) not null,
	column_19  	integer generated by default as identity,
	column_22  	money,
	column_23  	real default 6,
	column_20  	integer generated by default as identity,
	column_21  	integer generated by default as identity,
	column_24  	integer generated by default as identity,
	column_25  	integer generated by default as identity,
	column_26  	smallint default nextval('numeric_test_column_26_seq'::regclass) not null,
	constraint numeric_test_pkey primary key (id)

) tablespace pg_default;
CREATE UNIQUE INDEX numeric_test_smallint_col_uindex ON numeric_test USING btree (smallint_col);

comment on table numeric_test is '数字测试表‘、@#￥！￥！%&！……#%&……%*%“@%@#%!$"''/-=·';
comment on column numeric_test.bigint_col is 'asfa''';

alter table "numeric_test" owner to ali_dbhub;

grant select on "numeric_test" to only_read;


-- ----------------------------
-- Records of numeric_test
-- ----------------------------
INSERT INTO numeric_test (id,smallint_col,int_col,bigint_col,numeric_default_col,numeric_prec10_scale2_col,numeric_prec20_scale5_col,numeric_max_prec_col,numeric_nan_col,column_10,column_11,column_12,column_13,column_14,column_15,column_16,column_17,column_18,column_19,column_22,column_23,column_20,column_21,column_24,column_25,column_26)  VALUES ('1','1','1','1',123.45,12345.67,123456789012345.12345,12345678901234567890,47467.88,0.1,1,2,123.45,123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890.123456789012345678901234567890123456789012345678900000000000000000000000000000,123.45,'67.88','1','1','100','$100.00','6.0','1','100','2','100','1');
INSERT INTO numeric_test (id,smallint_col,int_col,bigint_col,numeric_default_col,numeric_prec10_scale2_col,numeric_prec20_scale5_col,numeric_max_prec_col,numeric_nan_col,column_10,column_11,column_12,column_13,column_14,column_15,column_16,column_17,column_18,column_19,column_22,column_23,column_20,column_21,column_24,column_25,column_26)  VALUES ('2','2','2','2',NULL,NULL,NULL,NULL,47467.88,NULL,NULL,NULL,NULL,NULL,NULL,'67.88','2','2','101','$66.80','6.0','2','101','3','101','2');
INSERT INTO numeric_test (id,smallint_col,int_col,bigint_col,numeric_default_col,numeric_prec10_scale2_col,numeric_prec20_scale5_col,numeric_max_prec_col,numeric_nan_col,column_10,column_11,column_12,column_13,column_14,column_15,column_16,column_17,column_18,column_19,column_22,column_23,column_20,column_21,column_24,column_25,column_26)  VALUES ('3','3','3','3',NULL,NULL,NULL,NULL,47467.88,NULL,NULL,NULL,NULL,NULL,NULL,'67.88','3','3','102','$67.90','6.0','3','102','4','102','3');

DROP TABLE IF EXISTS "empty2";
create table "empty2"
(
) tablespace pg_default;


-- ----------------------------
-- Records of empty2
-- ----------------------------

DROP TABLE IF EXISTS "text_types_test";
create table "text_types_test"
(
	id  	integer default nextval('text_types_test_id_seq'::regclass) not null,
	char_varying_col  	varchar(50),
	varchar_col  	varchar(100),
	text_col  	text,
	char_col  	char(2) default 'b'::bpchar,
	char_no_col  	char,
	var_col  	varchar,
	var2_col  	varchar(255),
	name_col  	name,
	name_no_col  	name,
	chara_col  	char(2),
	chara_no_col  	char,
	column_13  	varchar(1),
	constraint text_types_test_pkey primary key (id)

) tablespace pg_default;

comment on table text_types_test is '字符串类型测试表';

alter table "text_types_test" owner to ali_dbhub;

grant select on "text_types_test" to only_read;


-- ----------------------------
-- Records of text_types_test
-- ----------------------------

DROP TABLE IF EXISTS "boolean_tests";
create table "boolean_tests"
(
	id  	integer default nextval('boolean_tests_id_seq'::regclass) not null,
	bool_column  	boolean,
	bool_text_column  	text,
	bool_numeric_column  	integer,
	constraint boolean_tests_pkey primary key (id)

) tablespace pg_default;

comment on table boolean_tests is '测试布尔类型';

alter table "boolean_tests" owner to ali_dbhub;

grant select on "boolean_tests" to only_read;


-- ----------------------------
-- Records of boolean_tests
-- ----------------------------

DROP TABLE IF EXISTS "employee";
create table "employee"
(
	id  	integer default nextval('employee_id_seq'::regclass) not null,
	first_name  	varchar(50),
	last_name  	varchar(50),
	"position"  	"position_level",
	hire_date  	date,
	constraint employee_pkey primary key (id)

) tablespace pg_default;

comment on table employee is '测试自定义类型';

alter table "employee" owner to ali_dbhub;

grant select on "employee" to only_read;


-- ----------------------------
-- Records of employee
-- ----------------------------

DROP TABLE IF EXISTS "bit_types";
create table "bit_types"
(
	id  	integer default nextval('bit_types_id_seq'::regclass) not null,
	fixed_bit_column  	bit(8),
	varying_bit_column  	bit varying(16),
	unlimited_varying_bit_column  	bit varying,
	column_5  	bit,
	column_6  	bit,
	column_7  	bit varying(1),
	constraint bit_types_pkey primary key (id)

) tablespace pg_default;

comment on table bit_types is '测试位串类型';

alter table "bit_types" owner to ali_dbhub;

grant select on "bit_types" to only_read;


-- ----------------------------
-- Records of bit_types
-- ----------------------------

DROP TABLE IF EXISTS "combination_type_table";
create table "combination_type_table"
(
	item  	"inventory_item",
	count  	integer
) tablespace pg_default;

comment on table combination_type_table is '测试组合类型';

alter table "combination_type_table" owner to ali_dbhub;

grant select on "combination_type_table" to only_read;


-- ----------------------------
-- Records of combination_type_table
-- ----------------------------

DROP TABLE IF EXISTS "capitals_sub_table2";
create table "capitals_sub_table2"
(
	state  	char(2),
	column_5  	integer
)  inherits ("cities_parent_table")
 tablespace pg_default;


alter table "capitals_sub_table2" owner to ali_dbhub;

grant select on "capitals_sub_table2" to only_read;


-- ----------------------------
-- Records of capitals_sub_table2
-- ----------------------------

DROP TABLE IF EXISTS "test_int_table";
create table "test_int_table"
(
	column_1  	smallint,
	column_2  	integer,
	column_3  	bigint,
	column_4  	smallint default nextval('test_int_table_column_4_seq'::regclass) not null,
	column_5  	integer default nextval('test_int_table_column_5_seq'::regclass) not null,
	column_6  	bigint default nextval('test_int_table_column_6_seq'::regclass) not null
) tablespace pg_default;


alter table "test_int_table" owner to ali_dbhub;


-- ----------------------------
-- Records of test_int_table
-- ----------------------------

DROP TABLE IF EXISTS "v_coldef";
create table "v_coldef"
(
	data_type  	text
) tablespace pg_default;


alter table "v_coldef" owner to ali_dbhub;


-- ----------------------------
-- Records of v_coldef
-- ----------------------------
INSERT INTO v_coldef (data_type)  VALUES ('integer');

DROP TABLE IF EXISTS "my_table";
create table "my_table"
(
	id  	integer default nextval('my_sequence'::regclass) not null,
	column1  	varchar(255),
	column2  	integer,
	column_3  	integer default nextval('my_table_column_3_seq'::regclass) not null,
	constraint my_table_pkey primary key (id)

) tablespace pg_default;


alter table "my_table" owner to ali_dbhub;


-- ----------------------------
-- Records of my_table
-- ----------------------------

DROP TABLE IF EXISTS "original_table";
create table "original_table"
(
	id  	integer default nextval('original_table_id_seq'::regclass) not null,
	name  	varchar(100) not null,
	created_at  	timestamp default CURRENT_TIMESTAMP,
	constraint original_table_pkey primary key (id)

) tablespace pg_default;


alter table "original_table" owner to ali_dbhub;


-- ----------------------------
-- Records of original_table
-- ----------------------------

DROP TABLE IF EXISTS "new_table";
create table "new_table"
(
	id  	integer default nextval('original_table_id_seq'::regclass) not null,
	name  	varchar(100) not null,
	created_at  	timestamp default CURRENT_TIMESTAMP,
	constraint new_table_pkey primary key (id)

) tablespace pg_default;


alter table "new_table" owner to ali_dbhub;


-- ----------------------------
-- Records of new_table
-- ----------------------------

DROP TABLE IF EXISTS "bit_types2";
create table "bit_types2"
(
	id  	integer default nextval('bit_types_id_seq'::regclass) not null,
	fixed_bit_column  	bit(8),
	varying_bit_column  	bit varying(16),
	unlimited_varying_bit_column  	bit varying,
	column_5  	bit,
	column_6  	bit,
	column_7  	bit varying(1),
	constraint bit_types2_pkey primary key (id)

) tablespace pg_default;

comment on table bit_types2 is '测试位串类型';

alter table "bit_types2" owner to ali_dbhub;


-- ----------------------------
-- Records of bit_types2
-- ----------------------------

DROP TABLE IF EXISTS "xmltypestestchanges";
create table "xmltypestestchanges"
(
	changeid  	integer default nextval('xmltypestestchanges_changeid_seq'::regclass) not null,
	id  	integer,
	changetype  	text,
	xmlcolumn  	xml,
	changedate  	timestamp,
	constraint xmltypestestchanges_pkey primary key (changeid)

) tablespace pg_default;


alter table "xmltypestestchanges" owner to ali_dbhub;


-- ----------------------------
-- Records of xmltypestestchanges
-- ----------------------------
INSERT INTO xmltypestestchanges (changeid,id,changetype,xmlcolumn,changedate)  VALUES ('1','1','INSERT','<root><element>New Value</element></root>','2024-07-21 09:09:47');

DROP TABLE IF EXISTS "xmltypestest";
create table "xmltypestest"
(
	id  	integer default nextval('xmltypestest_id_seq'::regclass) not null,
	xmlcolumn  	xml,
	constraint xmltypestest_pkey primary key (id)

) tablespace pg_default;


alter table "xmltypestest" owner to ali_dbhub;


-- ----------------------------
-- Records of xmltypestest
-- ----------------------------
INSERT INTO xmltypestest (id,xmlcolumn)  VALUES ('1','<root><element>New Value</element></root>');

DROP TABLE IF EXISTS "departments";
create table "departments"
(
	id  	integer default nextval('departments_id_seq'::regclass) not null,
	department_name  	text not null,
	constraint departments_pkey primary key (id)

) tablespace pg_default;


alter table "departments" owner to ali_dbhub;


-- ----------------------------
-- Records of departments
-- ----------------------------
INSERT INTO departments (id,department_name)  VALUES ('1','HR');
INSERT INTO departments (id,department_name)  VALUES ('2','Engineering');
INSERT INTO departments (id,department_name)  VALUES ('3','Marketing');

DROP TABLE IF EXISTS "employees";
create table "employees"
(
	id  	integer default nextval('employees_id_seq'::regclass) not null,
	name  	text not null,
	department_id  	integer,
	hire_date  	date default CURRENT_DATE not null,
	status  	text default 'active'::text not null,
	constraint employees_pkey primary key (id),
	constraint employees_department_id_fkey foreign key (department_id) references departments(id)

) tablespace pg_default;


alter table "employees" owner to ali_dbhub;


-- ----------------------------
-- Records of employees
-- ----------------------------
INSERT INTO employees (id,name,department_id,hire_date,status)  VALUES ('1','Alice','1','2022-01-15','active');
INSERT INTO employees (id,name,department_id,hire_date,status)  VALUES ('2','Bob','2','2023-03-20','active');
INSERT INTO employees (id,name,department_id,hire_date,status)  VALUES ('3','Charlie','3','2021-11-05','inactive');
INSERT INTO employees (id,name,department_id,hire_date,status)  VALUES ('4','David','2','2022-07-12','active');

DROP TABLE IF EXISTS "users";
create table "users"
(
	user_id  	integer default nextval('users_user_id_seq'::regclass) not null,
	username  	varchar(255) not null,
	email  	varchar(255) not null,
	created_at  	timestamp default CURRENT_TIMESTAMP not null,
	constraint users_pkey primary key (user_id)

) tablespace pg_default;


alter table "users" owner to ali_dbhub;


-- ----------------------------
-- Records of users
-- ----------------------------

DROP TABLE IF EXISTS "products";
create table "products"
(
	product_id  	integer default nextval('products_product_id_seq'::regclass) not null,
	name  	varchar(255) not null,
	price  	numeric(10,2) not null,
	category  	varchar(255) not null,
	constraint products_pkey primary key (product_id)

) tablespace pg_default;


alter table "products" owner to ali_dbhub;


-- ----------------------------
-- Records of products
-- ----------------------------
DROP VIEW IF EXISTS "active_employees";
CREATE OR REPLACE VIEW "active_employees" AS  SELECT employees.id,
    employees.name,
    employees.department_id,
    employees.hire_date
   FROM employees
  WHERE (employees.status = 'active'::text);

DROP VIEW IF EXISTS "active_employees_with_departments";
CREATE OR REPLACE VIEW "active_employees_with_departments" AS  SELECT e.id,
    e.name,
    d.department_name,
    e.hire_date
   FROM (employees e
     JOIN departments d ON ((e.department_id = d.id)))
  WHERE (e.status = 'active'::text);

DROP VIEW IF EXISTS "employee_names";
CREATE OR REPLACE VIEW "employee_names" AS  SELECT employees.id,
    employees.name
   FROM employees;

DROP PROCEDURE IF EXISTS insertxmlrecord;
CREATE OR REPLACE PROCEDURE insertxmlrecord(xml_data xml)
 LANGUAGE plpgsql
AS $procedure$
BEGIN
    INSERT INTO XmlTypesTest (XmlColumn)
    VALUES (xml_data);
END;
$procedure$
;


DROP FUNCTION IF EXISTS log_xml_changes;
CREATE OR REPLACE FUNCTION log_xml_changes()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
BEGIN
    IF TG_OP = 'INSERT' THEN
        INSERT INTO XmlTypesTestChanges (Id, ChangeType, XmlColumn, ChangeDate)
        VALUES (NEW.Id, 'INSERT', NEW.XmlColumn, NOW());
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO XmlTypesTestChanges (Id, ChangeType, XmlColumn, ChangeDate)
        VALUES (NEW.Id, 'UPDATE', NEW.XmlColumn, NOW());
    END IF;
    RETURN NEW;
END;
$function$
;


DROP FUNCTION IF EXISTS getxmlelementvalue;
CREATE OR REPLACE FUNCTION getxmlelementvalue(xml_data xml, element_name text)
 RETURNS text
 LANGUAGE plpgsql
AS $function$
DECLARE
    element_value TEXT;
BEGIN
    SELECT xpath('/root/element/text()', xml_data)::TEXT INTO element_value;
    RETURN element_value;
END;
$function$
;


DROP FUNCTION IF EXISTS employee_names_insert_trigger;
CREATE OR REPLACE FUNCTION employee_names_insert_trigger()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
BEGIN
    INSERT INTO employees (id, name) VALUES (NEW.id, NEW.name);
    RETURN NEW;
END;
$function$
;


CREATE CONSTRAINT TRIGGER "RI_ConstraintTrigger_a_25153" AFTER UPDATE ON foreign_table FROM test_constraint_index_table2 NOT DEFERRABLE INITIALLY IMMEDIATE FOR EACH ROW EXECUTE FUNCTION "RI_FKey_noaction_upd"();

CREATE CONSTRAINT TRIGGER "RI_ConstraintTrigger_a_25152" AFTER DELETE ON foreign_table FROM test_constraint_index_table2 NOT DEFERRABLE INITIALLY IMMEDIATE FOR EACH ROW EXECUTE FUNCTION "RI_FKey_noaction_del"();

CREATE CONSTRAINT TRIGGER "RI_ConstraintTrigger_a_25128" AFTER UPDATE ON foreign_table FROM test_constraint_index_table NOT DEFERRABLE INITIALLY IMMEDIATE FOR EACH ROW EXECUTE FUNCTION "RI_FKey_noaction_upd"();

CREATE CONSTRAINT TRIGGER "RI_ConstraintTrigger_a_25127" AFTER DELETE ON foreign_table FROM test_constraint_index_table NOT DEFERRABLE INITIALLY IMMEDIATE FOR EACH ROW EXECUTE FUNCTION "RI_FKey_noaction_del"();

CREATE CONSTRAINT TRIGGER "RI_ConstraintTrigger_c_25155" AFTER UPDATE ON test_constraint_index_table2 FROM foreign_table NOT DEFERRABLE INITIALLY IMMEDIATE FOR EACH ROW EXECUTE FUNCTION "RI_FKey_check_upd"();

CREATE CONSTRAINT TRIGGER "RI_ConstraintTrigger_c_25154" AFTER INSERT ON test_constraint_index_table2 FROM foreign_table NOT DEFERRABLE INITIALLY IMMEDIATE FOR EACH ROW EXECUTE FUNCTION "RI_FKey_check_ins"();

CREATE CONSTRAINT TRIGGER "RI_ConstraintTrigger_c_25130" AFTER UPDATE ON test_constraint_index_table FROM foreign_table NOT DEFERRABLE INITIALLY IMMEDIATE FOR EACH ROW EXECUTE FUNCTION "RI_FKey_check_upd"();

CREATE CONSTRAINT TRIGGER "RI_ConstraintTrigger_c_25129" AFTER INSERT ON test_constraint_index_table FROM foreign_table NOT DEFERRABLE INITIALLY IMMEDIATE FOR EACH ROW EXECUTE FUNCTION "RI_FKey_check_ins"();

CREATE CONSTRAINT TRIGGER "RI_ConstraintTrigger_a_25242" AFTER UPDATE ON foreign_parent_table FROM foreign_sub_table NOT DEFERRABLE INITIALLY IMMEDIATE FOR EACH ROW EXECUTE FUNCTION "RI_FKey_noaction_upd"();

CREATE CONSTRAINT TRIGGER "RI_ConstraintTrigger_a_25241" AFTER DELETE ON foreign_parent_table FROM foreign_sub_table NOT DEFERRABLE INITIALLY IMMEDIATE FOR EACH ROW EXECUTE FUNCTION "RI_FKey_noaction_del"();

CREATE CONSTRAINT TRIGGER "RI_ConstraintTrigger_c_25244" AFTER UPDATE ON foreign_sub_table FROM foreign_parent_table NOT DEFERRABLE INITIALLY IMMEDIATE FOR EACH ROW EXECUTE FUNCTION "RI_FKey_check_upd"();

CREATE CONSTRAINT TRIGGER "RI_ConstraintTrigger_c_25243" AFTER INSERT ON foreign_sub_table FROM foreign_parent_table NOT DEFERRABLE INITIALLY IMMEDIATE FOR EACH ROW EXECUTE FUNCTION "RI_FKey_check_ins"();

CREATE TRIGGER trgafterinsertorupdate AFTER INSERT OR UPDATE ON xmltypestest FOR EACH ROW EXECUTE FUNCTION log_xml_changes();

CREATE CONSTRAINT TRIGGER "RI_ConstraintTrigger_a_26093" AFTER UPDATE ON departments FROM employees NOT DEFERRABLE INITIALLY IMMEDIATE FOR EACH ROW EXECUTE FUNCTION "RI_FKey_noaction_upd"();

CREATE CONSTRAINT TRIGGER "RI_ConstraintTrigger_a_26092" AFTER DELETE ON departments FROM employees NOT DEFERRABLE INITIALLY IMMEDIATE FOR EACH ROW EXECUTE FUNCTION "RI_FKey_noaction_del"();

CREATE CONSTRAINT TRIGGER "RI_ConstraintTrigger_c_26095" AFTER UPDATE ON employees FROM departments NOT DEFERRABLE INITIALLY IMMEDIATE FOR EACH ROW EXECUTE FUNCTION "RI_FKey_check_upd"();

CREATE CONSTRAINT TRIGGER "RI_ConstraintTrigger_c_26094" AFTER INSERT ON employees FROM departments NOT DEFERRABLE INITIALLY IMMEDIATE FOR EACH ROW EXECUTE FUNCTION "RI_FKey_check_ins"();

CREATE TRIGGER insert_employee_names INSTEAD OF INSERT ON employee_names FOR EACH ROW EXECUTE FUNCTION employee_names_insert_trigger();

