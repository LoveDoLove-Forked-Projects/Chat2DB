-- ----------------------------
-- Chat2DB export data , export time: 2024-07-24 17:09:36
-- ----------------------------
DROP TABLE IF EXISTS [test_data_types2];
go
CREATE TABLE [test_data_types2]
(
	[column_1] DATE    NULL ,
	[column_2] DATETIME    NOT NULL ,
	[column_3] DATETIME2(6)    NULL ,
	[column_4] DATETIME2    NULL ,
	[column_5] DATETIMEOFFSET(5)    NULL ,
	[column_6] DATETIMEOFFSET    NULL ,
	[column_7] SMALLDATETIME    NULL ,
	[column_8] TIME    NULL ,
	[column_9] TIME(5)    NULL ,
	[column_10] BIT    NULL ,
	[column_11] DECIMAL(7,2)    NULL ,
	[column_12] NUMERIC(7,4)    NULL ,
	[column_13] FLOAT    NULL ,
	[column_14] REAL    NULL ,
	[column_15] INT    NULL ,
	[column_16] BIGINT    NULL ,
	[column_17] SMALLINT    NULL ,
	[column_18] TINYINT    NULL ,
	[column_19] MONEY    NULL ,
	[column_20] SMALLMONEY    NULL ,
	[column_21] BINARY    NULL ,
	[column_22] BINARY    NULL ,
	[column_23] VARBINARY    NULL ,
	[column_24] VARBINARY(3442)    NULL ,
	[column_25] VARBINARY(MAX)    NULL ,
	[column_26] CHAR    NULL ,
	[column_27] CHAR(345)    NULL ,
	[column_28] VARCHAR    NULL ,
	[column_29] VARCHAR(234)    NULL ,
	[column_30] VARCHAR(MAX)    NULL ,
	[column_31] NCHAR    NULL ,
	[column_32] NCHAR(234)    NULL ,
	[column_33] NVARCHAR    NULL ,
	[column_34] NVARCHAR(4000)    NULL ,
	[column_35] NVARCHAR(MAX)    NULL ,
	[column_36] XML    NULL ,
	[column_37] TIME(1)    NULL ,
	[column_38] NVARCHAR    NULL ,
	[column_39] VARCHAR    NULL 
)

go
exec sp_addextendedproperty 'MS_Description',N'test_data_types2','SCHEMA',N'dbo','TABLE',N'test_data_types2' 
go
exec sp_addextendedproperty 'MS_Description',N'column_1','SCHEMA',N'dbo','TABLE',N'test_data_types2','COLUMN',N'column_1' 
go
exec sp_addextendedproperty 'MS_Description',N'column2','SCHEMA',N'dbo','TABLE',N'test_data_types2','COLUMN',N'column_2' 
go

CREATE UNIQUE NONCLUSTERED INDEX [test_data_types2_column_15_uindex]
 ON [dbo].[test_data_types2] ([column_15] ASC)
go
CREATE NONCLUSTERED INDEX [test_data_types2_column_1_index]
 ON [dbo].[test_data_types2] ([column_1] ASC)
go	exec sp_addextendedproperty 'MS_Description',N'这是对test_data_types2_column_1_index索引的注释','SCHEMA',N'dbo','TABLE',N'test_data_types2','INDEX',N'test_data_types2_column_1_index' 
go

CREATE UNIQUE NONCLUSTERED INDEX [test_data_types2_column_16_uindex]
 ON [dbo].[test_data_types2] ([column_16] ASC)
go
go 

DROP TABLE IF EXISTS [table_name'];
go
CREATE TABLE [table_name']
(
	[column_1] INT    NULL 
)

go

go 

DROP TABLE IF EXISTS [ab];
go
CREATE TABLE [ab]
(
	[id] INT    NULL 
)

go

go 

DROP TABLE IF EXISTS ['ab'];
go
CREATE TABLE ['ab']
(
	[id] INT    NULL 
)

go

go 

DROP TABLE IF EXISTS [ab'];
go
CREATE TABLE [ab']
(
	[id] INT    NULL 
)

go

go 

DROP TABLE IF EXISTS ['table_name'];
go
CREATE TABLE ['table_name']
(
	[column_1] INT    NULL 
)

go

go 

DROP TABLE IF EXISTS [DateTimeTypesTest];
go
CREATE TABLE [DateTimeTypesTest]
(
	[Id] INT identity,
	[DateType] DATE    NULL ,
	[DateTimeType] DATETIME    NULL ,
	[SmallDateTimeType] SMALLDATETIME    NULL ,
	[DateTime2Type] DATETIME2    NULL ,
	[DateTimeOffsetType] DATETIMEOFFSET    NULL ,
	[TimeType] TIME    NULL ,
constraint PK__DateTime__3214EC074F125927
primary key  clustered (Id asc)
)

go

INSERT INTO [test].[dbo].[DateTimeTypesTest] ([Id],[DateType],[DateTimeType],[SmallDateTimeType],[DateTime2Type],[DateTimeOffsetType],[TimeType])  VALUES ('1','2024-07-21','2024-07-21 12:34:56','2024-07-21 12:34:00','2024-07-21 12:34:56','2024-07-21 12:34:56.7891234 +02:00','12:34:56');
INSERT INTO [test].[dbo].[DateTimeTypesTest] ([Id],[DateType],[DateTimeType],[SmallDateTimeType],[DateTime2Type],[DateTimeOffsetType],[TimeType])  VALUES ('2','2023-01-01','2023-01-01 08:00:00','2023-01-01 08:00:00','2023-01-01 08:00:00','2023-01-01 08:00:00.1 -05:00','08:00:00');
INSERT INTO [test].[dbo].[DateTimeTypesTest] ([Id],[DateType],[DateTimeType],[SmallDateTimeType],[DateTime2Type],[DateTimeOffsetType],[TimeType])  VALUES ('3','2022-12-31','2022-12-31 23:59:59','2022-12-31 23:59:00','2022-12-31 23:59:59','2022-12-31 23:59:59.99 +00:00','23:59:59');
INSERT INTO [test].[dbo].[DateTimeTypesTest] ([Id],[DateType],[DateTimeType],[SmallDateTimeType],[DateTime2Type],[DateTimeOffsetType],[TimeType])  VALUES ('4','2020-02-29','2020-02-29 00:00:00','2020-02-29 00:00:00','2020-02-29 00:00:00','2020-02-29 00:00:00 +09:00','00:00:00');
INSERT INTO [test].[dbo].[DateTimeTypesTest] ([Id],[DateType],[DateTimeType],[SmallDateTimeType],[DateTime2Type],[DateTimeOffsetType],[TimeType])  VALUES ('5','1900-01-01','1900-01-01 12:00:00','1900-01-01 12:00:00','1900-01-01 12:00:00','1900-01-01 12:00:00.1234567 -02:00','12:00:00');
INSERT INTO [test].[dbo].[DateTimeTypesTest] ([Id],[DateType],[DateTimeType],[SmallDateTimeType],[DateTime2Type],[DateTimeOffsetType],[TimeType])  VALUES ('6','2024-07-21','2024-07-21 12:34:56','2024-07-21 12:34:00','2024-07-21 12:34:56','2024-07-21 12:34:56.7891234 +02:00','12:34:56');
INSERT INTO [test].[dbo].[DateTimeTypesTest] ([Id],[DateType],[DateTimeType],[SmallDateTimeType],[DateTime2Type],[DateTimeOffsetType],[TimeType])  VALUES ('7','2023-01-01','2023-01-01 08:00:00','2023-01-01 08:00:00','2023-01-01 08:00:00','2023-01-01 08:00:00.1 -05:00','08:00:00');
INSERT INTO [test].[dbo].[DateTimeTypesTest] ([Id],[DateType],[DateTimeType],[SmallDateTimeType],[DateTime2Type],[DateTimeOffsetType],[TimeType])  VALUES ('8','2022-12-31','2022-12-31 23:59:59','2022-12-31 23:59:00','2022-12-31 23:59:59','2022-12-31 23:59:59.99 +00:00','23:59:59');
INSERT INTO [test].[dbo].[DateTimeTypesTest] ([Id],[DateType],[DateTimeType],[SmallDateTimeType],[DateTime2Type],[DateTimeOffsetType],[TimeType])  VALUES ('9','2020-02-29','2020-02-29 00:00:00','2020-02-29 00:00:00','2020-02-29 00:00:00','2020-02-29 00:00:00 +09:00','00:00:00');
INSERT INTO [test].[dbo].[DateTimeTypesTest] ([Id],[DateType],[DateTimeType],[SmallDateTimeType],[DateTime2Type],[DateTimeOffsetType],[TimeType])  VALUES ('10','1900-01-01','1900-01-01 12:00:00','1900-01-01 12:00:00','1900-01-01 12:00:00','1900-01-01 12:00:00.1234567 -02:00','12:00:00');
go 

DROP TABLE IF EXISTS [NumericTypesTest];
go
CREATE TABLE [NumericTypesTest]
(
	[Id] INT identity,
	[BitType] BIT    NULL ,
	[TinyIntType] TINYINT    NULL ,
	[SmallIntType] SMALLINT    NULL ,
	[IntType] INT    NULL ,
	[BigIntType] BIGINT    NULL ,
	[DecimalTypeDefault] DECIMAL(18,0)    NULL ,
	[DecimalTypeHigh] DECIMAL(38,18)    NULL ,
	[DecimalTypeLow] DECIMAL(5,2)    NULL ,
	[NumericTypeDefault] NUMERIC(18,0)    NULL ,
	[NumericTypeHigh] NUMERIC(38,18)    NULL ,
	[NumericTypeLow] NUMERIC(5,2)    NULL ,
	[SmallMoneyType] SMALLMONEY    NULL ,
	[MoneyType] MONEY    NULL ,
	[FloatTypeDefault] FLOAT    NULL ,
	[FloatTypeLow] REAL    NULL ,
	[RealType] REAL    NULL ,
constraint PK__NumericT__3214EC07141D3A67
primary key  clustered (Id asc)
)

go

INSERT INTO [test].[dbo].[NumericTypesTest] ([Id],[BitType],[TinyIntType],[SmallIntType],[IntType],[BigIntType],[DecimalTypeDefault],[DecimalTypeHigh],[DecimalTypeLow],[NumericTypeDefault],[NumericTypeHigh],[NumericTypeLow],[SmallMoneyType],[MoneyType],[FloatTypeDefault],[FloatTypeLow],[RealType])  VALUES ('1','false','0','0','0','0','0','0.000000000000000000','0.00','0','0.000000000000000000','0.00','0.0000','0.0000','0.0','0.0','0.0');
INSERT INTO [test].[dbo].[NumericTypesTest] ([Id],[BitType],[TinyIntType],[SmallIntType],[IntType],[BigIntType],[DecimalTypeDefault],[DecimalTypeHigh],[DecimalTypeLow],[NumericTypeDefault],[NumericTypeHigh],[NumericTypeLow],[SmallMoneyType],[MoneyType],[FloatTypeDefault],[FloatTypeLow],[RealType])  VALUES ('2','true','255','32767','2147483647','9223372036854775807','100000','999999999999999999.999999999999999999','123.45','100000','999999999999999999.999999999999999999','123.45','214748.3647','922337203685477.5807','179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000','340282346638528860000000000000000000000','340282346638528860000000000000000000000');
INSERT INTO [test].[dbo].[NumericTypesTest] ([Id],[BitType],[TinyIntType],[SmallIntType],[IntType],[BigIntType],[DecimalTypeDefault],[DecimalTypeHigh],[DecimalTypeLow],[NumericTypeDefault],[NumericTypeHigh],[NumericTypeLow],[SmallMoneyType],[MoneyType],[FloatTypeDefault],[FloatTypeLow],[RealType])  VALUES ('3','false','0','-32768','-2147483648','-9223372036854775808','-100000','-999999999999999999.999999999999999999','-999.99','-100000','-999999999999999999.999999999999999999','-999.99','-214748.3648','-922337203685477.5808','-179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000','-340282346638528860000000000000000000000','-340282346638528860000000000000000000000');
INSERT INTO [test].[dbo].[NumericTypesTest] ([Id],[BitType],[TinyIntType],[SmallIntType],[IntType],[BigIntType],[DecimalTypeDefault],[DecimalTypeHigh],[DecimalTypeLow],[NumericTypeDefault],[NumericTypeHigh],[NumericTypeLow],[SmallMoneyType],[MoneyType],[FloatTypeDefault],[FloatTypeLow],[RealType])  VALUES ('4','true','1','1','1','1','12346','123456789012345678.123456789012345678','12.34','12346','123456789012345678.123456789012345678','12.34','214748.3647','922337203685477.5807','12300000000','123.0','123.0');
INSERT INTO [test].[dbo].[NumericTypesTest] ([Id],[BitType],[TinyIntType],[SmallIntType],[IntType],[BigIntType],[DecimalTypeDefault],[DecimalTypeHigh],[DecimalTypeLow],[NumericTypeDefault],[NumericTypeHigh],[NumericTypeLow],[SmallMoneyType],[MoneyType],[FloatTypeDefault],[FloatTypeLow],[RealType])  VALUES ('5','true','0','0','0','0','0','0.000000000000000000','0.00','0','0.000000000000000000','0.00','0.0000','0.0000','0.0','0.0','0.0');
INSERT INTO [test].[dbo].[NumericTypesTest] ([Id],[BitType],[TinyIntType],[SmallIntType],[IntType],[BigIntType],[DecimalTypeDefault],[DecimalTypeHigh],[DecimalTypeLow],[NumericTypeDefault],[NumericTypeHigh],[NumericTypeLow],[SmallMoneyType],[MoneyType],[FloatTypeDefault],[FloatTypeLow],[RealType])  VALUES ('6','true','255','32767','2147483647','9223372036854775807','100000','999999999999999999.999999999999999999','123.45','100000','999999999999999999.999999999999999999','123.45','214748.3647','922337203685477.5807','179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000','340282346638528860000000000000000000000','340282346638528860000000000000000000000');
INSERT INTO [test].[dbo].[NumericTypesTest] ([Id],[BitType],[TinyIntType],[SmallIntType],[IntType],[BigIntType],[DecimalTypeDefault],[DecimalTypeHigh],[DecimalTypeLow],[NumericTypeDefault],[NumericTypeHigh],[NumericTypeLow],[SmallMoneyType],[MoneyType],[FloatTypeDefault],[FloatTypeLow],[RealType])  VALUES ('7','true','0','-32768','-2147483648','-9223372036854775808','-100000','-999999999999999999.999999999999999999','-999.99','-100000','-999999999999999999.999999999999999999','-999.99','-214748.3648','-922337203685477.5808','-179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000','-340282346638528860000000000000000000000','-340282346638528860000000000000000000000');
INSERT INTO [test].[dbo].[NumericTypesTest] ([Id],[BitType],[TinyIntType],[SmallIntType],[IntType],[BigIntType],[DecimalTypeDefault],[DecimalTypeHigh],[DecimalTypeLow],[NumericTypeDefault],[NumericTypeHigh],[NumericTypeLow],[SmallMoneyType],[MoneyType],[FloatTypeDefault],[FloatTypeLow],[RealType])  VALUES ('8','true','1','1','1','1','12346','123456789012345678.123456789012345678','12.34','12346','123456789012345678.123456789012345678','12.34','214748.3647','922337203685477.5807','12300000000','123.0','123.0');
INSERT INTO [test].[dbo].[NumericTypesTest] ([Id],[BitType],[TinyIntType],[SmallIntType],[IntType],[BigIntType],[DecimalTypeDefault],[DecimalTypeHigh],[DecimalTypeLow],[NumericTypeDefault],[NumericTypeHigh],[NumericTypeLow],[SmallMoneyType],[MoneyType],[FloatTypeDefault],[FloatTypeLow],[RealType])  VALUES ('9','false','0','0','0','0','0','0.000000000000000000','0.00','0','0.000000000000000000','0.00','0.0000','0.0000','0.0','0.0','0.0');
INSERT INTO [test].[dbo].[NumericTypesTest] ([Id],[BitType],[TinyIntType],[SmallIntType],[IntType],[BigIntType],[DecimalTypeDefault],[DecimalTypeHigh],[DecimalTypeLow],[NumericTypeDefault],[NumericTypeHigh],[NumericTypeLow],[SmallMoneyType],[MoneyType],[FloatTypeDefault],[FloatTypeLow],[RealType])  VALUES ('10','true','255','32767','2147483647','9223372036854775807','100000','999999999999999999.999999999999999999','123.45','100000','999999999999999999.999999999999999999','123.45','214748.3647','922337203685477.5807','179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000','340282346638528860000000000000000000000','340282346638528860000000000000000000000');
INSERT INTO [test].[dbo].[NumericTypesTest] ([Id],[BitType],[TinyIntType],[SmallIntType],[IntType],[BigIntType],[DecimalTypeDefault],[DecimalTypeHigh],[DecimalTypeLow],[NumericTypeDefault],[NumericTypeHigh],[NumericTypeLow],[SmallMoneyType],[MoneyType],[FloatTypeDefault],[FloatTypeLow],[RealType])  VALUES ('11','false','0','-32768','-2147483648','-9223372036854775808','-100000','-999999999999999999.999999999999999999','-999.99','-100000','-999999999999999999.999999999999999999','-999.99','-214748.3648','-922337203685477.5808','-179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000','-340282346638528860000000000000000000000','-340282346638528860000000000000000000000');
INSERT INTO [test].[dbo].[NumericTypesTest] ([Id],[BitType],[TinyIntType],[SmallIntType],[IntType],[BigIntType],[DecimalTypeDefault],[DecimalTypeHigh],[DecimalTypeLow],[NumericTypeDefault],[NumericTypeHigh],[NumericTypeLow],[SmallMoneyType],[MoneyType],[FloatTypeDefault],[FloatTypeLow],[RealType])  VALUES ('12','true','1','1','1','1','12346','123456789012345678.123456789012345678','12.34','12346','123456789012345678.123456789012345678','12.34','214748.3647','922337203685477.5807','12300000000','123.0','123.0');
INSERT INTO [test].[dbo].[NumericTypesTest] ([Id],[BitType],[TinyIntType],[SmallIntType],[IntType],[BigIntType],[DecimalTypeDefault],[DecimalTypeHigh],[DecimalTypeLow],[NumericTypeDefault],[NumericTypeHigh],[NumericTypeLow],[SmallMoneyType],[MoneyType],[FloatTypeDefault],[FloatTypeLow],[RealType])  VALUES ('13','true','0','0','0','0','0','0.000000000000000000','0.00','0','0.000000000000000000','0.00','0.0000','0.0000','0.0','0.0','0.0');
INSERT INTO [test].[dbo].[NumericTypesTest] ([Id],[BitType],[TinyIntType],[SmallIntType],[IntType],[BigIntType],[DecimalTypeDefault],[DecimalTypeHigh],[DecimalTypeLow],[NumericTypeDefault],[NumericTypeHigh],[NumericTypeLow],[SmallMoneyType],[MoneyType],[FloatTypeDefault],[FloatTypeLow],[RealType])  VALUES ('14','true','255','32767','2147483647','9223372036854775807','100000','999999999999999999.999999999999999999','123.45','100000','999999999999999999.999999999999999999','123.45','214748.3647','922337203685477.5807','179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000','340282346638528860000000000000000000000','340282346638528860000000000000000000000');
INSERT INTO [test].[dbo].[NumericTypesTest] ([Id],[BitType],[TinyIntType],[SmallIntType],[IntType],[BigIntType],[DecimalTypeDefault],[DecimalTypeHigh],[DecimalTypeLow],[NumericTypeDefault],[NumericTypeHigh],[NumericTypeLow],[SmallMoneyType],[MoneyType],[FloatTypeDefault],[FloatTypeLow],[RealType])  VALUES ('15','true','0','-32768','-2147483648','-9223372036854775808','-100000','-999999999999999999.999999999999999999','-999.99','-100000','-999999999999999999.999999999999999999','-999.99','-214748.3648','-922337203685477.5808','-179769313486231570000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000','-340282346638528860000000000000000000000','-340282346638528860000000000000000000000');
INSERT INTO [test].[dbo].[NumericTypesTest] ([Id],[BitType],[TinyIntType],[SmallIntType],[IntType],[BigIntType],[DecimalTypeDefault],[DecimalTypeHigh],[DecimalTypeLow],[NumericTypeDefault],[NumericTypeHigh],[NumericTypeLow],[SmallMoneyType],[MoneyType],[FloatTypeDefault],[FloatTypeLow],[RealType])  VALUES ('16','true','1','1','1','1','12346','123456789012345678.123456789012345678','12.34','12346','123456789012345678.123456789012345678','12.34','214748.3647','922337203685477.5807','12300000000','123.0','123.0');
go 

DROP TABLE IF EXISTS [StringAndBinaryTypesTest];
go
CREATE TABLE [StringAndBinaryTypesTest]
(
	[Id] INT identity,
	[CharType] CHAR(10)    NULL ,
	[VarCharType] VARCHAR(50)    NULL ,
	[TextType] TEXT    NULL ,
	[NCharType] NCHAR(10)    NULL ,
	[NVarCharType] NVARCHAR(50)    NULL ,
	[NTextType] NTEXT    NULL ,
	[BinaryType] BINARY    NULL ,
	[VarBinaryType] VARBINARY(50)    NULL ,
	[ImageType] IMAGE    NULL ,
	[binary_test] BINARY    NULL ,
constraint PK__StringAn__3214EC0795ECA325
primary key  clustered (Id asc)
)

go

INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('8',N'abc       ',N'short string',N'short text',N'短文本       ',N'短的字符串',N'短文本',0x12345600000000000000,0x123456,0x123456,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('9',N'abcdefghij',N'this is a much longer string that fits',N'this is a very long text that goes beyond normal limits',N'長字符       ',N'這是一個非常長的字符串，超過了NChar限制',N'这是一段非常长的文本',0x1234567890ABCDEF0000,0x1234567890ABCDEF,0x1234567890ABCDEF,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('10',N'!@#$%^&*()',N'special chars !@#$%^&*()',N'text with special chars !@#$%^&*()',N'特殊字符！＠＃￥％ ',N'含有特殊字符的字符串！＠＃￥％……＆＊（）',N'含有特殊字符的文本！＠＃￥％……＆＊（）',0x0102030405060708090A,0x0102030405060708090A,0x0102030405060708090A,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('11','          ','','','          ','','',0x00000000000000000000,0x00,0x00,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('12',N'large bin ',N'large binary data',N'this is a text with binary data',N'二进制数据     ',N'二进制数据的字符串',N'包含二进制数据的文本',0x1234567890ABCDEF1234,0x1234567890ABCDEF1234567890ABCDEF,0x1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('13',N'abc       ',N'short string',N'short text',N'短文本       ',N'短的字符串',N'短文本',0x68656C6C6F2C20776F72,0x68656C6C6F2C20776F726C64,0x68656C6C6F2C20776F726C64,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('14',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0x01);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('15',N'abc       ',N'short string',N'short text',N'短文本       ',N'短的字符串',N'短文本',0x12345600000000000000,0x123456,0x123456,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('16',N'abcdefghij',N'this is a much longer string that fits',N'this is a very long text that goes beyond normal limits',N'長字符       ',N'這是一個非常長的字符串，超過了NChar限制',N'这是一段非常长的文本',0x1234567890ABCDEF0000,0x1234567890ABCDEF,0x1234567890ABCDEF,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('17',N'!@#$%^&*()',N'special chars !@#$%^&*()',N'text with special chars !@#$%^&*()',N'特殊字符！＠＃￥％ ',N'含有特殊字符的字符串！＠＃￥％……＆＊（）',N'含有特殊字符的文本！＠＃￥％……＆＊（）',0x0102030405060708090A,0x0102030405060708090A,0x0102030405060708090A,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('18','          ','','','          ','','',0x00000000000000000000,0x00,0x00,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('19',N'large bin ',N'large binary data',N'this is a text with binary data',N'二进制数据     ',N'二进制数据的字符串',N'包含二进制数据的文本',0x1234567890ABCDEF1234,0x1234567890ABCDEF1234567890ABCDEF,0x1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('20',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0x01);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('21',N'asda      ',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('22',N'abc       ',N'short string',N'short text',N'短文本       ',N'短的字符串',N'短文本',0x12345600000000000000,0x123456,0x123456,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('23',N'abcdefghij',N'this is a much longer string that fits',N'this is a very long text that goes beyond normal limits',N'長字符       ',N'這是一個非常長的字符串，超過了NChar限制',N'这是一段非常长的文本',0x1234567890ABCDEF0000,0x1234567890ABCDEF,0x1234567890ABCDEF,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('24',N'!@#$%^&*()',N'special chars !@#$%^&*()',N'text with special chars !@#$%^&*()',N'特殊字符！＠＃￥％ ',N'含有特殊字符的字符串！＠＃￥％……＆＊（）',N'含有特殊字符的文本！＠＃￥％……＆＊（）',0x0102030405060708090A,0x0102030405060708090A,0x0102030405060708090A,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('25','          ','','','          ','','',0x00000000000000000000,0x00,0x00,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('26',N'large bin ',N'large binary data',N'this is a text with binary data',N'二进制数据     ',N'二进制数据的字符串',N'包含二进制数据的文本',0x1234567890ABCDEF1234,0x1234567890ABCDEF1234567890ABCDEF,0x1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('27',N'abc       ',N'short string',N'short text',N'短文本       ',N'短的字符串',N'短文本',0x68656C6C6F2C20776F72,0x68656C6C6F2C20776F726C64,0x68656C6C6F2C20776F726C64,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('28',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0x01);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('29',N'abc       ',N'short string',N'short text',N'短文本       ',N'短的字符串',N'短文本',0x12345600000000000000,0x123456,0x123456,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('30',N'abcdefghij',N'this is a much longer string that fits',N'this is a very long text that goes beyond normal limits',N'長字符       ',N'這是一個非常長的字符串，超過了NChar限制',N'这是一段非常长的文本',0x1234567890ABCDEF0000,0x1234567890ABCDEF,0x1234567890ABCDEF,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('31',N'!@#$%^&*()',N'special chars !@#$%^&*()',N'text with special chars !@#$%^&*()',N'特殊字符！＠＃￥％ ',N'含有特殊字符的字符串！＠＃￥％……＆＊（）',N'含有特殊字符的文本！＠＃￥％……＆＊（）',0x0102030405060708090A,0x0102030405060708090A,0x0102030405060708090A,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('32','          ','','','          ','','',0x00000000000000000000,0x00,0x00,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('33',N'large bin ',N'large binary data',N'this is a text with binary data',N'二进制数据     ',N'二进制数据的字符串',N'包含二进制数据的文本',0x1234567890ABCDEF1234,0x1234567890ABCDEF1234567890ABCDEF,0x1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('34',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0x01);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('35',N'asda      ',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('36',N'abc       ',N'short string',N'short text',N'短文本       ',N'短的字符串',N'短文本',0x12345600000000000000,0x123456,0x123456,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('37',N'abcdefghij',N'this is a much longer string that fits',N'this is a very long text that goes beyond normal limits',N'長字符       ',N'這是一個非常長的字符串，超過了NChar限制',N'这是一段非常长的文本',0x1234567890ABCDEF0000,0x1234567890ABCDEF,0x1234567890ABCDEF,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('38',N'!@#$%^&*()',N'special chars !@#$%^&*()',N'text with special chars !@#$%^&*()',N'特殊字符！＠＃￥％ ',N'含有特殊字符的字符串！＠＃￥％……＆＊（）',N'含有特殊字符的文本！＠＃￥％……＆＊（）',0x0102030405060708090A,0x0102030405060708090A,0x0102030405060708090A,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('39','          ','','','          ','','',0x00000000000000000000,0x00,0x00,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('40',N'large bin ',N'large binary data',N'this is a text with binary data',N'二进制数据     ',N'二进制数据的字符串',N'包含二进制数据的文本',0x1234567890ABCDEF1234,0x1234567890ABCDEF1234567890ABCDEF,0x1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('41',N'abc       ',N'short string',N'short text',N'短文本       ',N'短的字符串',N'短文本',0x68656C6C6F2C20776F72,0x68656C6C6F2C20776F726C64,0x68656C6C6F2C20776F726C64,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('42',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0x01);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('43',N'abc       ',N'short string',N'short text',N'短文本       ',N'短的字符串',N'短文本',0x12345600000000000000,0x123456,0x123456,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('44',N'abcdefghij',N'this is a much longer string that fits',N'this is a very long text that goes beyond normal limits',N'長字符       ',N'這是一個非常長的字符串，超過了NChar限制',N'这是一段非常长的文本',0x1234567890ABCDEF0000,0x1234567890ABCDEF,0x1234567890ABCDEF,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('45',N'!@#$%^&*()',N'special chars !@#$%^&*()',N'text with special chars !@#$%^&*()',N'特殊字符！＠＃￥％ ',N'含有特殊字符的字符串！＠＃￥％……＆＊（）',N'含有特殊字符的文本！＠＃￥％……＆＊（）',0x0102030405060708090A,0x0102030405060708090A,0x0102030405060708090A,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('46','          ','','','          ','','',0x00000000000000000000,0x00,0x00,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('47',N'large bin ',N'large binary data',N'this is a text with binary data',N'二进制数据     ',N'二进制数据的字符串',N'包含二进制数据的文本',0x1234567890ABCDEF1234,0x1234567890ABCDEF1234567890ABCDEF,0x1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('48',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0x01);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('49',N'asda      ',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('50',N'abc       ',N'short string',N'short text',N'短文本       ',N'短的字符串',N'短文本',0x12345600000000000000,0x123456,0x123456,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('51',N'abcdefghij',N'this is a much longer string that fits',N'this is a very long text that goes beyond normal limits',N'長字符       ',N'這是一個非常長的字符串，超過了NChar限制',N'这是一段非常长的文本',0x1234567890ABCDEF0000,0x1234567890ABCDEF,0x1234567890ABCDEF,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('52',N'!@#$%^&*()',N'special chars !@#$%^&*()',N'text with special chars !@#$%^&*()',N'特殊字符！＠＃￥％ ',N'含有特殊字符的字符串！＠＃￥％……＆＊（）',N'含有特殊字符的文本！＠＃￥％……＆＊（）',0x0102030405060708090A,0x0102030405060708090A,0x0102030405060708090A,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('53','          ','','','          ','','',0x00000000000000000000,0x00,0x00,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('54',N'large bin ',N'large binary data',N'this is a text with binary data',N'二进制数据     ',N'二进制数据的字符串',N'包含二进制数据的文本',0x1234567890ABCDEF1234,0x1234567890ABCDEF1234567890ABCDEF,0x1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('55',N'abc       ',N'short string',N'short text',N'短文本       ',N'短的字符串',N'短文本',0x68656C6C6F2C20776F72,0x68656C6C6F2C20776F726C64,0x68656C6C6F2C20776F726C64,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('56',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0x01);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('57',N'abc       ',N'short string',N'short text',N'短文本       ',N'短的字符串',N'短文本',0x12345600000000000000,0x123456,0x123456,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('58',N'abcdefghij',N'this is a much longer string that fits',N'this is a very long text that goes beyond normal limits',N'長字符       ',N'這是一個非常長的字符串，超過了NChar限制',N'这是一段非常长的文本',0x1234567890ABCDEF0000,0x1234567890ABCDEF,0x1234567890ABCDEF,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('59',N'!@#$%^&*()',N'special chars !@#$%^&*()',N'text with special chars !@#$%^&*()',N'特殊字符！＠＃￥％ ',N'含有特殊字符的字符串！＠＃￥％……＆＊（）',N'含有特殊字符的文本！＠＃￥％……＆＊（）',0x0102030405060708090A,0x0102030405060708090A,0x0102030405060708090A,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('60','          ','','','          ','','',0x00000000000000000000,0x00,0x00,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('61',N'large bin ',N'large binary data',N'this is a text with binary data',N'二进制数据     ',N'二进制数据的字符串',N'包含二进制数据的文本',0x1234567890ABCDEF1234,0x1234567890ABCDEF1234567890ABCDEF,0x1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF1234567890ABCDEF,NULL);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('62',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0x01);
INSERT INTO [test].[dbo].[StringAndBinaryTypesTest] ([Id],[CharType],[VarCharType],[TextType],[NCharType],[NVarCharType],[NTextType],[BinaryType],[VarBinaryType],[ImageType],[binary_test])  VALUES ('63',N'asda      ',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
go 

DROP TABLE IF EXISTS [Employees];
go
CREATE TABLE [Employees]
(
	[EmployeeID] INT    NOT NULL ,
	[FirstName] NVARCHAR(50)    NOT NULL ,
	[LastName] NVARCHAR(50)    NOT NULL ,
	[BaseSalary] DECIMAL(10,2)    NOT NULL ,
	[Bonus] DECIMAL(10,2)    NULL ,
	[TotalCompensation] AS ([BaseSalary]+[Bonus]),
constraint PK__Employee__7AD04FF1D4D59C25
primary key  clustered (EmployeeID asc)
)

go

go 

DROP TABLE IF EXISTS [GeographyTypesTest];
go
CREATE TABLE [GeographyTypesTest]
(
	[Id] INT identity,
	[GeoPoint] GEOGRAPHY    NULL ,
	[GeoPolygon] GEOGRAPHY    NULL ,
	[GeoLineString] GEOGRAPHY    NULL ,
constraint PK__Geograph__3214EC0717605EA5
primary key  clustered (Id asc)
)

go

INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('1',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('2',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('3',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('4',0xAD100000010C17D9CEF75353444075931804561659C0,0xAD1000000104050000000000000000002E400000000000804140000000000080464000000000008046400000000000804640000000000000394000000000000039400000000000002E400000000000002E40000000000080414001000000020000000001000000FFFFFFFF0000000003,0xAD10000001148716D9CEF7534440D7A3703D0A1759C08716D9CEF7534440CBA145B6F31559C0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('5',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('6',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('7',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('8',0xAD100000010C17D9CEF75353444075931804561659C0,0xAD1000000104050000000000000000002E400000000000804140000000000080464000000000008046400000000000804640000000000000394000000000000039400000000000002E400000000000002E40000000000080414001000000020000000001000000FFFFFFFF0000000003,0xAD10000001148716D9CEF7534440D7A3703D0A1759C08716D9CEF7534440CBA145B6F31559C0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('9',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('10',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('11',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('12',0xAD100000010C17D9CEF75353444075931804561659C0,0xAD1000000104050000000000000000002E400000000000804140000000000080464000000000008046400000000000804640000000000000394000000000000039400000000000002E400000000000002E40000000000080414001000000020000000001000000FFFFFFFF0000000003,0xAD10000001148716D9CEF7534440D7A3703D0A1759C08716D9CEF7534440CBA145B6F31559C0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('13',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('14',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('15',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('16',0xAD100000010C17D9CEF75353444075931804561659C0,0xAD1000000104050000000000000000002E400000000000804140000000000080464000000000008046400000000000804640000000000000394000000000000039400000000000002E400000000000002E40000000000080414001000000020000000001000000FFFFFFFF0000000003,0xAD10000001148716D9CEF7534440D7A3703D0A1759C08716D9CEF7534440CBA145B6F31559C0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('17',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('18',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('19',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('20',0xAD100000010C17D9CEF75353444075931804561659C0,0xAD1000000104050000000000000000002E400000000000804140000000000080464000000000008046400000000000804640000000000000394000000000000039400000000000002E400000000000002E40000000000080414001000000020000000001000000FFFFFFFF0000000003,0xAD10000001148716D9CEF7534440D7A3703D0A1759C08716D9CEF7534440CBA145B6F31559C0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('21',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('22',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('23',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('24',0xAD100000010C17D9CEF75353444075931804561659C0,0xAD1000000104050000000000000000002E400000000000804140000000000080464000000000008046400000000000804640000000000000394000000000000039400000000000002E400000000000002E40000000000080414001000000020000000001000000FFFFFFFF0000000003,0xAD10000001148716D9CEF7534440D7A3703D0A1759C08716D9CEF7534440CBA145B6F31559C0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('25',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('26',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('27',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('28',0xAD100000010C17D9CEF75353444075931804561659C0,0xAD1000000104050000000000000000002E400000000000804140000000000080464000000000008046400000000000804640000000000000394000000000000039400000000000002E400000000000002E40000000000080414001000000020000000001000000FFFFFFFF0000000003,0xAD10000001148716D9CEF7534440D7A3703D0A1759C08716D9CEF7534440CBA145B6F31559C0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('29',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('30',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('31',0xE6100000010C17D9CEF753D347407593180456965EC0,0xE610000001040500000000000000000024400000000000003E4000000000000044400000000000004440000000000000444000000000000034400000000000003440000000000000244000000000000024400000000000003E4001000000020000000001000000FFFFFFFF0000000003,0xE610000001148716D9CEF7D34740D7A3703D0A975EC08716D9CEF7D34740CBA145B6F3955EC0);
INSERT INTO [test].[dbo].[GeographyTypesTest] ([Id],[GeoPoint],[GeoPolygon],[GeoLineString])  VALUES ('32',0xAD100000010C17D9CEF75353444075931804561659C0,0xAD1000000104050000000000000000002E400000000000804140000000000080464000000000008046400000000000804640000000000000394000000000000039400000000000002E400000000000002E40000000000080414001000000020000000001000000FFFFFFFF0000000003,0xAD10000001148716D9CEF7534440D7A3703D0A1759C08716D9CEF7534440CBA145B6F31559C0);
go 

DROP TABLE IF EXISTS [XmlTypesTest];
go
CREATE TABLE [XmlTypesTest]
(
	[Id] INT identity,
	[XmlColumn] XML    NULL ,
constraint PK__XmlTypes__3214EC07E47E53AE
primary key  clustered (Id asc)
)

go

INSERT INTO [test].[dbo].[XmlTypesTest] ([Id],[XmlColumn])  VALUES ('1',N'<root><element>Value 1</element></root>');
INSERT INTO [test].[dbo].[XmlTypesTest] ([Id],[XmlColumn])  VALUES ('2',N'<root><element>Value 2</element><element>Value 3</element></root>');
INSERT INTO [test].[dbo].[XmlTypesTest] ([Id],[XmlColumn])  VALUES ('3',N'<root><element attribute="attrValue">Value 4</element></root>');
INSERT INTO [test].[dbo].[XmlTypesTest] ([Id],[XmlColumn])  VALUES ('4',N'<root><element>Value 1</element></root>');
INSERT INTO [test].[dbo].[XmlTypesTest] ([Id],[XmlColumn])  VALUES ('5',N'<root><element>Value 2</element><element>Value 3</element></root>');
INSERT INTO [test].[dbo].[XmlTypesTest] ([Id],[XmlColumn])  VALUES ('6',N'<root><element attribute="attrValue">Value 4</element></root>');
go 

DROP TABLE IF EXISTS [EmployeeAudit];
go
CREATE TABLE [EmployeeAudit]
(
	[AuditID] INT identity,
	[EmployeeID] INT    NULL ,
	[ChangeType] NVARCHAR(50)    NULL ,
	[ChangeDate] DATETIME  DEFAULT (getdate())  NULL ,
	[ChangedBy] NVARCHAR(50)    NULL ,
	[OldValue] NVARCHAR(1000)    NULL ,
	[NewValue] NVARCHAR(1000)    NULL ,
constraint PK__Employee__A17F23B8FDB9045E
primary key  clustered (AuditID asc)
)

go

go 

DROP TABLE IF EXISTS [Employee];
go
CREATE TABLE [Employee]
(
	[EmployeeID] INT    NOT NULL ,
	[LastName] NVARCHAR(50)    NULL ,
	[FirstName] NVARCHAR(50)    NULL ,
	[BirthDate] DATE    NULL ,
	[Gender] CHAR    NULL ,
	[HireDate] DATE    NULL ,
	[Salary] DECIMAL(10,2)    NULL ,
constraint PK__Employee__7AD04FF15300481F
primary key  clustered (EmployeeID asc)
)

go
exec sp_addextendedproperty 'MS_Description',N'Employee information table','SCHEMA',N'dbo','TABLE',N'Employee' 
go
exec sp_addextendedproperty 'MS_Description',N'Unique identifier for each employee','SCHEMA',N'dbo','TABLE',N'Employee','COLUMN',N'EmployeeID' 
go
exec sp_addextendedproperty 'MS_Description',N'Last name of the employee','SCHEMA',N'dbo','TABLE',N'Employee','COLUMN',N'LastName' 
go
exec sp_addextendedproperty 'MS_Description',N'First name of the employee','SCHEMA',N'dbo','TABLE',N'Employee','COLUMN',N'FirstName' 
go
exec sp_addextendedproperty 'MS_Description',N'Birth date of the employee','SCHEMA',N'dbo','TABLE',N'Employee','COLUMN',N'BirthDate' 
go
exec sp_addextendedproperty 'MS_Description',N'Gender of the employee (M/F)','SCHEMA',N'dbo','TABLE',N'Employee','COLUMN',N'Gender' 
go
exec sp_addextendedproperty 'MS_Description',N'Hire date of the employee','SCHEMA',N'dbo','TABLE',N'Employee','COLUMN',N'HireDate' 
go
exec sp_addextendedproperty 'MS_Description',N'Salary of the employee','SCHEMA',N'dbo','TABLE',N'Employee','COLUMN',N'Salary' 
go

go 

DROP TABLE IF EXISTS [test_increment];
go
CREATE TABLE [test_increment]
(
	[column_1] INT identity (2,3)
)

go

go 

DROP TABLE IF EXISTS [test_table];
go
CREATE TABLE [test_table]
(
	[column1] INT    NOT NULL ,
	[column2] INT    NOT NULL ,
	[column_3] INT    NULL ,
constraint PK__test_tab__26B6AD3FBA0B3B45
primary key  clustered (column1 asc , column2 asc),
constraint check_test_constraint
check ([column1]>(18) AND [test_table].[column2]<(99))
)

go
	exec sp_addextendedproperty 'MS_Description',N'主键的注释','SCHEMA',N'dbo','TABLE',N'test_table','CONSTRAINT',N'PK__test_tab__26B6AD3FBA0B3B45' 
go

go 

DROP TABLE IF EXISTS [Sales];
go
CREATE TABLE [Sales]
(
	[SaleID] INT    NOT NULL ,
	[ProductID] INT    NULL ,
	[Quantity] INT    NULL ,
	[UnitPrice] DECIMAL(10,2)    NULL ,
	[TotalAmount] AS ([Quantity]*[UnitPrice]) PERSISTED,
constraint PK__Sales__1EE3C41F222D2090
primary key  clustered (SaleID asc)
)

go

go 

DROP TABLE IF EXISTS [AuditLog];
go
CREATE TABLE [AuditLog]
(
	[AuditID] INT identity,
	[TableName] NVARCHAR(128)    NULL ,
	[Operation] NVARCHAR(10)    NULL ,
	[InsertedDateTime] DATETIME    NULL ,
	[NewData] NVARCHAR(MAX)    NULL ,
constraint PK__AuditLog__A17F23B80670A4E7
primary key  clustered (AuditID asc)
)

go

go 

DROP TABLE IF EXISTS [ExampleTable];
go
CREATE TABLE [ExampleTable]
(
	[ID] INT    NOT NULL ,
	[Name] NVARCHAR(50)    NULL ,
	[Value] INT    NULL ,
constraint PK__ExampleT__3214EC276151C515
primary key  clustered (ID asc)
)

go

go 

DROP TABLE IF EXISTS [test_increment2];
go
CREATE TABLE [test_increment2]
(
	[column1] INT identity (-1,2),
	[column2] INT    NULL 
)

go

go 

DROP TABLE IF EXISTS [Employees2];
go
CREATE TABLE [Employees2]
(
	[EmployeeID] INT    NOT NULL ,
	[FirstName] NVARCHAR(50)    NOT NULL ,
	[LastName] NVARCHAR(50)    NOT NULL ,
	[BaseSalary] DECIMAL(10,2)    NOT NULL ,
	[Bonus] DECIMAL(10,2)    NULL ,
	[TotalCompensation] AS ([BaseSalary]+[Bonus])
)

go

go 

DROP TABLE IF EXISTS [test_foreign_constraint];
go
CREATE TABLE [test_foreign_constraint]
(
	[column_1] INT    NULL ,
	[column_2] INT    NULL 
)

go

go 

DROP TABLE IF EXISTS [Orders];
go
CREATE TABLE [Orders]
(
	[OrderID] INT    NOT NULL ,
	[ProductID] INT    NOT NULL ,
	[CustomerID] INT    NULL ,
	[OrderDate] DATE    NULL ,
	[Quantity] INT    NULL ,
	[OrderReference] NVARCHAR(50)    NULL ,
constraint PK_Orders
primary key  clustered (OrderID asc , ProductID asc),
constraint UQ_Orders_Customer_OrderDate
unique  nonclustered (CustomerID asc , OrderDate asc)
)

go

CREATE UNIQUE NONCLUSTERED INDEX [IX_Unique_OrderReference]
 ON [dbo].[Orders] ([OrderReference] ASC)
go
go 

DROP TABLE IF EXISTS [test_data_types3];
go
CREATE TABLE [test_data_types3]
(
	[column_1] DATE    NULL ,
	[column_2] DATETIME    NOT NULL ,
	[column_3] DATETIME2(6)    NULL ,
	[column_4] DATETIME2    NULL ,
	[column_5] DATETIMEOFFSET(5)    NULL ,
	[column_6] DATETIMEOFFSET    NULL ,
	[column_7] SMALLDATETIME    NULL ,
	[column_8] TIME    NULL ,
	[column_9] TIME(5)    NULL ,
	[column_10] BIT    NULL ,
	[column_11] DECIMAL(7,2)    NULL ,
	[column_12] NUMERIC(7,4)    NULL ,
	[column_13] FLOAT    NULL ,
	[column_14] REAL    NULL ,
	[column_15] INT    NULL ,
	[column_16] BIGINT    NULL ,
	[column_17] SMALLINT    NULL ,
	[column_18] TINYINT    NULL ,
	[column_19] MONEY    NULL ,
	[column_20] SMALLMONEY    NULL ,
	[column_21] BINARY    NULL ,
	[column_22] BINARY    NULL ,
	[column_23] VARBINARY    NULL ,
	[column_24] VARBINARY(3442)    NULL ,
	[column_25] VARBINARY(MAX)    NULL ,
	[column_26] CHAR    NULL ,
	[column_27] CHAR(345)    NULL ,
	[column_28] VARCHAR    NULL ,
	[column_29] VARCHAR(234)    NULL ,
	[column_30] VARCHAR(MAX)    NULL ,
	[column_31] NCHAR    NULL ,
	[column_32] NCHAR(234)    NULL ,
	[column_33] NVARCHAR    NULL ,
	[column_34] NVARCHAR(4000)    NULL ,
	[column_35] NVARCHAR(MAX)    NULL ,
	[column_36] XML    NULL ,
	[column_37] TIME(1)    NULL ,
	[column_38] NVARCHAR    NULL ,
	[column_39] VARCHAR    NULL 
)

go

go 

DROP TABLE IF EXISTS [Department];
go
CREATE TABLE [Department]
(
	[DeptID] INT    NOT NULL ,
	[DeptName] VARCHAR(50)    NOT NULL ,
	[ManagerID] INT    NULL ,
	[ParentDeptID] INT    NULL ,
	[ValidFrom] DATETIME2    NOT NULL ,
	[ValidTo] DATETIME2    NOT NULL ,
constraint PK__Departme__0148818E6E0F4147
primary key  clustered (DeptID asc)
)

go

go 

DROP TABLE IF EXISTS [MSSQL_TemporalHistoryFor_859150106];
go
CREATE TABLE [MSSQL_TemporalHistoryFor_859150106]
(
	[DeptID] INT    NOT NULL ,
	[DeptName] VARCHAR(50)    NOT NULL ,
	[ManagerID] INT    NULL ,
	[ParentDeptID] INT    NULL ,
	[ValidFrom] DATETIME2    NOT NULL ,
	[ValidTo] DATETIME2    NOT NULL 
)

go

CREATE CLUSTERED INDEX [ix_MSSQL_TemporalHistoryFor_859150106]
 ON [dbo].[MSSQL_TemporalHistoryFor_859150106] ([ValidFrom] ASC,[ValidTo] ASC)
go
go 

DROP TABLE IF EXISTS [Employee2];
go
CREATE TABLE [Employee2]
(
	[EmployeeID] INT    NOT NULL ,
	[FullName] NVARCHAR(100)    NULL ,
	[Age] INT    NULL ,
	[Department] NVARCHAR(50)    NULL ,
	[Salary] DECIMAL(10,2)    NULL ,
constraint PK__Employee__7AD04FF1FC6EABE9
primary key  clustered (EmployeeID asc),
constraint CHK_Age
check ([Age]>=(18) AND [Age]<=(100))
)

go

go 

DROP TABLE IF EXISTS [Customers];
go
CREATE TABLE [Customers]
(
	[CustomerID] INT    NOT NULL ,
	[CustomerName] NVARCHAR(100)    NOT NULL ,
	[ContactName] NVARCHAR(100)    NULL ,
	[Country] NVARCHAR(50)    NULL ,
constraint PK__Customer__A4AE64B8662A522E
primary key  clustered (CustomerID asc)
)

go

go 

DROP TABLE IF EXISTS [test_sparse_table];
go
CREATE TABLE [test_sparse_table]
(
	[column1] INT SPARSE   NULL ,
	[column2] INT    NULL 
)

go

go 

DROP TABLE IF EXISTS [Customers2];
go
CREATE TABLE [Customers2]
(
	[CustomerID] INT    NOT NULL ,
	[CustomerName] NVARCHAR(100)    NOT NULL ,
	[ContactName] NVARCHAR(100)    NULL ,
	[Country] NVARCHAR(50)    NULL ,
constraint PK__Customer__A4AE64B8C6B6A0CF
primary key  clustered (CustomerID asc)
)

go

go 

DROP TABLE IF EXISTS [Employees2024622];
go
CREATE TABLE [Employees2024622]
(
	[EmployeeID] INT    NOT NULL ,
	[EmployeeName] NVARCHAR(100)    NULL ,
constraint PK__Employee__7AD04FF1B1014980
primary key  clustered (EmployeeID asc)
)

go

go 

DROP TABLE IF EXISTS [ParentTable];
go
CREATE TABLE [ParentTable]
(
	[ColumnA] INT    NOT NULL ,
	[ColumnB] INT    NOT NULL ,
constraint PK__ParentTa__F7B5C2EFCB87BC8B
primary key  clustered (ColumnA asc , ColumnB asc)
)

go

go 

DROP TABLE IF EXISTS [Sales_with_options];
go
CREATE TABLE [Sales_with_options]
(
	[SaleID] INT identity,
	[ProductID] INT    NOT NULL ,
	[CustomerID] INT    NOT NULL ,
	[SaleDate] DATETIME  DEFAULT (getdate())  NULL ,
	[Amount] DECIMAL(18,2)    NULL ,
	[Discount] DECIMAL(5,2)  DEFAULT ((0))  NULL ,
	[Comment] NVARCHAR(1000)    NULL ,
constraint PK__Sales_wi__1EE3C41FBD64239A
primary key  clustered (SaleID asc),
constraint CK__Sales_wit__Amoun__72E607DB
check ([Amount]>(0))
)

go

CREATE NONCLUSTERED INDEX [IDX_Sales_ProductID]
 ON [dbo].[Sales_with_options] ([ProductID] ASC)
go
CREATE NONCLUSTERED INDEX [IDX_Sales_CustomerID]
 ON [dbo].[Sales_with_options] ([CustomerID] ASC)
go
go 

DROP TABLE IF EXISTS [Sales_with_options2];
go
CREATE TABLE [Sales_with_options2]
(
	[SaleID] INT identity,
	[ProductID] INT    NOT NULL ,
	[CustomerID] INT    NOT NULL ,
	[SaleDate] DATETIME  DEFAULT (getdate())  NULL ,
	[Amount] DECIMAL(18,2)    NULL ,
	[Discount] DECIMAL(5,2)  DEFAULT ((0))  NULL ,
	[Comment] NVARCHAR(1000)    NULL ,
constraint PK__Sales_wi__1EE3C41FF1865117
primary key  clustered (SaleID asc),
constraint CK__Sales_wit__Amoun__77AABCF8
check ([Amount]>(0))
)

go

go 

DROP TABLE IF EXISTS [test_data_types];
go
CREATE TABLE [test_data_types]
(
	[column_1] DATE    NULL ,
	[column_2] DATETIME    NOT NULL ,
	[column_3] DATETIME2(6)    NULL ,
	[column_4] DATETIME2    NULL ,
	[column_5] DATETIMEOFFSET(5)    NULL ,
	[column_6] DATETIMEOFFSET    NULL ,
	[column_7] SMALLDATETIME    NULL ,
	[column_8] TIME    NULL ,
	[column_9] TIME(5)    NULL ,
	[column_10] BIT    NULL ,
	[column_11] DECIMAL(7,2)    NULL ,
	[column_12] NUMERIC(7,4)    NULL ,
	[column_13] FLOAT    NULL ,
	[column_14] REAL    NULL ,
	[column_15] INT  DEFAULT ((5))  NULL ,
	[column_16] BIGINT    NULL ,
	[column_17] SMALLINT    NULL ,
	[column_18] TINYINT    NULL ,
	[column_19] MONEY    NULL ,
	[column_20] SMALLMONEY    NULL ,
	[column_21] BINARY    NULL ,
	[column_22] BINARY    NULL ,
	[column_23] VARBINARY    NULL ,
	[column_24] VARBINARY(3442)    NULL ,
	[column_25] VARBINARY(MAX)    NULL ,
	[column_26] CHAR    NULL ,
	[column_27] CHAR(345)    NULL ,
	[column_28] VARCHAR    NULL ,
	[column_29] VARCHAR(234)    NULL ,
	[column_30] VARCHAR(MAX)    NULL ,
	[column_31] NCHAR    NULL ,
	[column_32] NCHAR(234)    NULL ,
	[column_33] NVARCHAR    NULL ,
	[column_34] NVARCHAR(4000)    NULL ,
	[column_35] NVARCHAR(MAX)    NULL ,
	[column_36] XML    NULL ,
	[column_37] TIME(1)    NULL ,
	[column_38] NVARCHAR    NULL ,
	[column_39] VARCHAR    NULL ,
	[column_40] INT    NULL 
)

go
exec sp_addextendedproperty 'MS_Description',N'test_data_types','SCHEMA',N'dbo','TABLE',N'test_data_types' 
go
exec sp_addextendedproperty 'MS_Description',N'''''column1''','SCHEMA',N'dbo','TABLE',N'test_data_types','COLUMN',N'column_1' 
go
exec sp_addextendedproperty 'MS_Description',N'column2','SCHEMA',N'dbo','TABLE',N'test_data_types','COLUMN',N'column_2' 
go

CREATE UNIQUE NONCLUSTERED INDEX [test_data_types_column_15_uindex]
 ON [dbo].[test_data_types] ([column_15] ASC)
go
CREATE UNIQUE NONCLUSTERED INDEX [test_data_types_column_16_uindex]
 ON [dbo].[test_data_types] ([column_16] ASC)
go
CREATE NONCLUSTERED INDEX [test_data_types_column_1_index]
 ON [dbo].[test_data_types] ([column_1] ASC)
go	exec sp_addextendedproperty 'MS_Description',N'这是对test_data_types_column_1_index索引的注释','SCHEMA',N'dbo','TABLE',N'test_data_types','INDEX',N'test_data_types_column_1_index' 
go

go 

DROP TABLE IF EXISTS [MyPartitionedTable];
go
CREATE TABLE [MyPartitionedTable]
(
	[ID] INT    NOT NULL ,
	[Name] NVARCHAR(50)    NOT NULL ,
	[OrderDate] DATETIME    NOT NULL ,
constraint PK_MyPartitionedTable
primary key  clustered (ID asc , OrderDate asc)
)
 on [MyPartitionScheme] ([OrderDate]) 
go

go 

DROP TABLE IF EXISTS [MyPartitionedTable2];
go
CREATE TABLE [MyPartitionedTable2]
(
	[ID] INT    NOT NULL ,
	[Name] NVARCHAR(50)    NOT NULL ,
	[OrderDate] DATETIME    NOT NULL ,
constraint PK_MyPartitionedTable2
primary key  clustered (ID asc , OrderDate asc)
)
 on [MyPartitionScheme] ([OrderDate]) 
go

go 

DROP TABLE IF EXISTS [MyPartitionedTable3];
go
CREATE TABLE [MyPartitionedTable3]
(
	[ID] INT    NOT NULL ,
	[Name] NVARCHAR(50)    NOT NULL ,
	[OrderDate] DATETIME    NOT NULL ,
constraint PK_MyPartitionedTable3
primary key  clustered (ID asc , OrderDate asc)
)
 on [MyPartitionScheme] ([OrderDate]) 
go

go 

DROP TABLE IF EXISTS [MyPartitionedTable4];
go
CREATE TABLE [MyPartitionedTable4]
(
	[ID] INT    NOT NULL ,
	[Name] NVARCHAR(50)    NOT NULL ,
	[OrderDate] DATETIME    NOT NULL ,
constraint PK_MyPartitionedTable4
primary key  clustered (ID asc , OrderDate asc)
)
 on [MyPartitionScheme] ([OrderDate]) 
go

go 

DROP TABLE IF EXISTS [ChildTable];
go
CREATE TABLE [ChildTable]
(
	[ChildID] INT    NOT NULL ,
	[ColumnA] INT    NULL ,
	[ColumnB] INT    NULL ,
constraint PK__ChildTab__BEFA0736F40CA1CD
primary key  clustered (ChildID asc),
constraint FK__ChildTable__6774552F
foreign key (ColumnA , ColumnB)
references dbo.ParentTable (ColumnA , ColumnB)
)

go

go 

DROP TABLE IF EXISTS [ChildTable2];
go
CREATE TABLE [ChildTable2]
(
	[ChildID] INT    NOT NULL ,
	[ColumnA] INT    NULL ,
	[ColumnB] INT    NULL ,
constraint PK__ChildTab__BEFA0736B6007458
primary key  clustered (ChildID asc),
constraint FK__ChildTable2__6A50C1DA
foreign key (ColumnA , ColumnB)
references dbo.ParentTable (ColumnA , ColumnB)
)

go

go 

DROP TABLE IF EXISTS [Orders2];
go
CREATE TABLE [Orders2]
(
	[OrderID] INT    NOT NULL ,
	[OrderDate] DATE    NOT NULL ,
	[CustomerID] INT    NULL ,
	[Amount] DECIMAL(10,2)    NULL ,
constraint PK__Orders2__C3905BAF7398094E
primary key  clustered (OrderID asc),
constraint FK_Orders2_Customers2
foreign key (CustomerID)
references dbo.Customers2 (CustomerID)
)

go

go 

DROP TABLE IF EXISTS [table_unique_constraint_];
go
CREATE TABLE [table_unique_constraint_]
(
	[column_1] INT    NULL ,
	[column_2] INT    NULL ,
	[column_3] INT    NOT NULL ,
	[column_4] INT    NOT NULL ,
	[column_5] INT    NULL ,
	[column_6] INT    NULL ,
	[column_7] INT    NULL ,
	[column_8] INT    NULL ,
constraint unique_test_constraint
unique  clustered (column_1 asc , column_2 asc),
constraint foreign_table_constraint
foreign key (column_7 , column_8)
references dbo.test_table (column1 , column2) on update cascade on delete no action
)

go

CREATE UNIQUE NONCLUSTERED INDEX [table_unique_constraint__column_8_uindex]
 ON [dbo].[table_unique_constraint_] ([column_8] ASC)
go
CREATE UNIQUE NONCLUSTERED INDEX [table_unique_constraint__column_7_uindex]
 ON [dbo].[table_unique_constraint_] ([column_7] ASC)
go
go 

DROP TABLE IF EXISTS [test_constraint];
go
CREATE TABLE [test_constraint]
(
	[id] INT    NULL ,
	[age] INT    NULL ,
	[unique_column] INT    NOT NULL ,
	[column_4] INT    NULL ,
	[column_5] INT    NOT NULL ,
	[column_6] INT    NULL ,
	[column_7] INT    NULL ,
	[column_8] INT    NULL ,
	[column_9] VARCHAR(255)    NULL ,
constraint test_constraint_pk
primary key  nonclustered (column_5 asc),
constraint un_column7
unique  clustered (column_7 asc),
constraint un_column6
unique  nonclustered (column_6 asc),
constraint un_con
unique  nonclustered (unique_column asc),
constraint age_check
check ([age]>=(18)),
constraint FK_id
foreign key (id)
references dbo.test_data_types (column_15) on update cascade on delete cascade
)

go
exec sp_addextendedproperty 'MS_Description',N'''??''','SCHEMA',N'dbo','TABLE',N'test_constraint','COLUMN',N'id' 
go
	exec sp_addextendedproperty 'MS_Description',N'这是一个唯一约束的描述，比如它是为了保证数据的唯一业务规则','SCHEMA',N'dbo','TABLE',N'test_constraint','CONSTRAINT',N'un_con' 
go
	exec sp_addextendedproperty 'MS_Description',N'年龄的检查','SCHEMA',N'dbo','TABLE',N'test_constraint','CONSTRAINT',N'age_check' 
go
	exec sp_addextendedproperty 'MS_Description',N'外键的注释','SCHEMA',N'dbo','TABLE',N'test_constraint','CONSTRAINT',N'FK_id' 
go

CREATE UNIQUE NONCLUSTERED INDEX [test_constraint_column_4_uindex]
 ON [dbo].[test_constraint] ([column_4] ASC)
go
CREATE UNIQUE NONCLUSTERED INDEX [test_constraint_column_7_uindex]
 ON [dbo].[test_constraint] ([column_7] ASC)
go
go 

DROP VIEW IF EXISTS [PurchaseOrderDetailView];
go
CREATE VIEW PurchaseOrderDetailView AS
SELECT PurchaseOrderID,
       LineNumber,
       ProductID,
       UnitPrice,
       OrderQty,
       ReceivedQty,
       RejectedQty,
       DueDate
FROM dbo.PurchaseOrderDetail;
go

IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[GetXmlElementValue]') AND type IN ('FN', 'FS', 'FT', 'IF', 'TF'))
DROP FUNCTION [GetXmlElementValue]
GO
CREATE FUNCTION GetXmlElementValue(
        @XmlData XML,
        @ElementName NVARCHAR(50)
    )
        RETURNS NVARCHAR(MAX)
    AS
    BEGIN
        DECLARE @ElementValue NVARCHAR(MAX);
        SET @ElementValue = @XmlData.value('(/root/element/text())[1]', 'NVARCHAR(MAX)');
        RETURN @ElementValue;
    END
go

IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[ufn_GetCreateTableScript]') AND type IN ('FN', 'FS', 'FT', 'IF', 'TF'))
DROP FUNCTION [ufn_GetCreateTableScript]
GO
CREATE FUNCTION dbo.ufn_GetCreateTableScript( @schema_name NVARCHAR(128), @table_name NVARCHAR(128)) RETURNS NVARCHAR(MAX) AS BEGIN DECLARE @CreateTableScript NVARCHAR(MAX); DECLARE @IndexScripts NVARCHAR(MAX) = ''; DECLARE @ColumnDescriptions NVARCHAR(MAX) = N''; SELECT @CreateTableScript = CONCAT( 'CREATE TABLE [', s.name, '].[' , t.name, '] (', STUFF( ( SELECT ', [' + c.name + '] ' + tp.name + CASE WHEN tp.name IN ('varchar', 'nvarchar', 'char', 'nchar') THEN '(' + IIF(c.max_length = -1, 'MAX', CAST(c.max_length AS NVARCHAR(10))) + ')' WHEN tp.name IN ('decimal', 'numeric') THEN '(' + CAST(c.precision AS NVARCHAR(10)) + ', ' + CAST(c.scale AS NVARCHAR(10)) + ')' ELSE '' END + ' ' + CASE WHEN c.is_nullable = 1 THEN 'NULL' ELSE 'NOT NULL' END FROM sys.columns c JOIN sys.types tp ON c.user_type_id = tp.user_type_id WHERE c.object_id = t.object_id FOR XML PATH(''), TYPE ).value('/', 'nvarchar(max)'), 1, 1, ''), ');' ) FROM sys.tables t JOIN sys.schemas s ON t.schema_id = s.schema_id WHERE t.name = @table_name AND s.name = @schema_name; SELECT @IndexScripts = @IndexScripts + 'CREATE ' + CASE WHEN i.is_unique = 1 THEN 'UNIQUE ' ELSE '' END + i.type_desc + ' INDEX [' + i.name + '] ON [' + s.name + '].[' + t.name + '] (' + STUFF( ( SELECT ', [' + c.name + ']' + CASE WHEN ic.is_descending_key = 1 THEN ' DESC' ELSE ' ASC' END FROM sys.index_columns ic JOIN sys.columns c ON ic.object_id = c.object_id AND ic.column_id = c.column_id WHERE ic.object_id = i.object_id AND ic.index_id = i.index_id ORDER BY ic.key_ordinal FOR XML PATH('') ), 1, 1, '') + ')' + CASE WHEN i.has_filter = 1 THEN ' WHERE ' + i.filter_definition ELSE '' END + ';' + CHAR(13) + CHAR(10) FROM sys.indexes i JOIN sys.tables t ON i.object_id = t.object_id JOIN sys.schemas s ON t.schema_id = s.schema_id WHERE i.type > 0 AND t.name = @table_name AND s.name = @schema_name; SELECT @ColumnDescriptions += 'EXEC sp_addextendedproperty @name=N''MS_Description'', @value=N''' + CAST(p.value AS NVARCHAR(MAX)) + ''', @level0type=N''SCHEMA'', @level0name=N''' + @schema_name + ''', @level1type=N''TABLE'', @level1name=N''' + @table_name + ''', @level2type=N''COLUMN'', @level2name=N''' + c.name + ''';' + CHAR(13) + CHAR(10) FROM sys.extended_properties p JOIN sys.columns c ON p.major_id = c.object_id AND p.minor_id = c.column_id JOIN sys.tables t ON c.object_id = t.object_id JOIN sys.schemas s ON t.schema_id = s.schema_id WHERE p.class = 1 AND t.name = @table_name AND s.name = @schema_name; SET @CreateTableScript = @CreateTableScript + CHAR(13) + CHAR(10) + @IndexScripts + CHAR(13) + CHAR(10)+ @ColumnDescriptions+ CHAR(10); RETURN @CreateTableScript; END
go

IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[HelloWorld]') AND type IN ('P', 'PC', 'RF', 'X'))
DROP PROCEDURE [HelloWorld]
GO
CREATE   PROCEDURE HelloWorld
AS
BEGIN
    PRINT 'Hello_World!'
END
go

IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[InsertXmlRecord]') AND type IN ('P', 'PC', 'RF', 'X'))
DROP PROCEDURE [InsertXmlRecord]
GO
CREATE PROCEDURE InsertXmlRecord @XmlData XML
AS
BEGIN
    INSERT INTO XmlTypesTest (XmlColumn)
    VALUES (@XmlData);
END;

    CREATE TABLE XmlTypesTestChanges
    (
        ChangeId   INT PRIMARY KEY IDENTITY (1,1),
        Id         INT,
        ChangeType NVARCHAR(10),
        XmlColumn  XML,
        ChangeDate DATETIME
    );
go

IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[sp_GenerateTableDDLScript]') AND type IN ('P', 'PC', 'RF', 'X'))
DROP PROCEDURE [sp_GenerateTableDDLScript]
GO
/*
---------------------------------------------------------------------------
sp_GenerateTableDDLScript version 1.0 by Eitan Blumin
https://github.com/EitanBlumin/sp_GenerateTableDDLScript
---------------------------------------------------------------------------
Copyright 2019 by Eitan Blumin at https://www.eitanblumin.com all rights reserved
---------------------------------------------------------------------------
Purpose:
--------------------------------------
	This procedure can be used to generate a CREATE TABLE script for a given table.
	You may create this procedure in the master database, and use the following
	command to turn it into a system stored procedure, usable anywhere in the instance:

	EXECUTE sp_MS_marksystemobject 'sp_GenerateTableDDLScript'

---------------------------------------------------------------------------
License:
--------------------------------------
@TableName SYSNAME,					-- The name of the source table. This parameter is mandatory.
									-- If the table's schema is not default (dbo), then please specify the
									-- schema name as well as part of the parameter.
@NewTableName SYSNAME = NULL,		-- The name of the new (target) table. You may also include database and schema as part of the name.
									-- If not specified, same name as source table will be used.
@Result NVARCHAR(MAX) OUTPUT,		-- Output textual parameter that will contain the result TSQL command for creating the table.
@IncludeDefaults BIT = 1,			-- Set whether to include default constraints
@IncludeCheckConstraints BIT = 1,	-- Set whether to include check constraints
@IncludeForeignKeys BIT = 1,		-- Set whether to include foreign key constraints
@IncludeIndexes BIT = 1,			-- Set whether to include indexes
@IncludePrimaryKey BIT = 1,			-- Set whether to include primary key constraints
@IncludeIdentity BIT = 1,			-- Set whether to include identity property
@IncludeUniqueIndexes BIT = 1,		-- Set whether to include unique index constraints
@IncludeComputedColumns BIT = 1,	-- Set whether to include computed columns (if not, they will also be automatically ignored by constraints and indexes)
@UseSystemDataTypes BIT = 0,		-- Set whether to use system data type names instead of user data type names
@ConstraintsNameAppend SYSNAME = '',-- This is an optional text string to append to constraint names,
									-- in order to avoid the duplicate object name exception.
									-- This is useful when creating the new table within the same database.
@Verbose BIT = 0					-- Optional parameter. If set to 1, will display informative messages, and will output a table representing the table fields

---------------------------------------------------------------------------
Example Usages:
--------------------------------------
	-- Example use case 1: Creating a table in an archive database, without foreign keys and identity property:
	DECLARE @CMD NVARCHAR(MAX)
	EXEC sp_GenerateTableDDLScript 'Sales.OrderDetails', 'ArchiveDB.Sales.OrderDetails', @CMD OUTPUT, @IncludeForeignKeys = 0, @IncludeIdentity = 0
	SELECT @CMD

	-- Example use case 2: Duplicating a table within the same database;
	DECLARE @CMD NVARCHAR(MAX)
	EXEC sp_GenerateTableDDLScript 'Sales.OrderDetails', 'Sales.OrderDetails_New', @CMD OUTPUT, @ConstraintsNameAppend = '_New'
	SELECT @CMD

	-- Example use case 3: Duplicating a table as a temporary table, without computed columns:
	DECLARE @CMD NVARCHAR(MAX)
	EXEC sp_GenerateTableDDLScript 'Sales.OrderDetails', '#temp_OrderDetails', @CMD OUTPUT, @ConstraintsNameAppend = '_Temp', @IncludeComputedColumns = 0
	SELECT @CMD

---------------------------------------------------------------------------
Remarks:
--------------------------------------
- The source table must exist, otherwise an exception will be raised.
- The script does not check whether the target table already exists,
  it falls on you to make sure that it doesn't before running the result script.
- The script does not check whether constraint names already exist,
  it falls on you to use the @ConstraintsNameAppend parameter to generate unique names.
- The script (at the moment) does NOT support the following:
	- Column Sets
	- Collations different from Database Default
	- Filestream columns
	- Sparse columns
	- Not for replication property
	- XML document collections
	- Rule objects
	- Non-default Filegroups
	- In-Memory tables

---------------------------------------------------------------------------
Acknowledgements:
--------------------------------------
	The script is mainly based off of the sp_ScriptTable stored procedure
	originally published by Tim Chapman in this URL:
	https://www.techrepublic.com/blog/the-enterprise-cloud/script-table-definitions-using-tsql/

---------------------------------------------------------------------------
Version History:
--------------------------------------
2019-04-17: First publication
2019-07-09: Fixed bug returning -1 character length for MAX length columns
2019-07-09: Added optional @Verbose parameter
---------------------------------------------------------------------------
*/
CREATE PROCEDURE [dbo].[sp_GenerateTableDDLScript]
(
@TableName NVARCHAR(500),
@NewTableName SYSNAME = NULL,
@Result NVARCHAR(MAX) OUTPUT,
@IncludeDefaults BIT = 1,
@IncludeCheckConstraints BIT = 1,
@IncludeForeignKeys BIT = 1,
@IncludeIndexes BIT = 1,
@IncludePrimaryKey BIT = 1,
@IncludeIdentity BIT = 1,
@IncludeUniqueIndexes BIT = 1,
@IncludeComputedColumns BIT = 1,
@UseSystemDataTypes BIT = 0,
@ConstraintsNameAppend SYSNAME = '',
@Verbose BIT = 0
)
AS
BEGIN
SET NOCOUNT ON;
DECLARE @MainDefinition TABLE
(
FieldValue NVARCHAR(4000)
)

DECLARE @TableObjId INT
DECLARE @ClusteredPK BIT
DECLARE @TableSchema NVARCHAR(255)
DECLARE @RCount INT

SELECT @TableName = name, @TableObjId = id, @TableSchema = OBJECT_SCHEMA_NAME(id) FROM sysobjects WHERE id = OBJECT_ID(@TableName);

IF @TableObjId IS NULL
BEGIN
	RAISERROR(N'Table %s not found within current database!', 16, 1, @TableName);
	RETURN -1;
END

SET @NewTableName = ISNULL(@NewTableName, QUOTENAME(DB_NAME(DB_ID())) + '.' + QUOTENAME(@TableSchema) + '.' + @TableName);

DECLARE @ShowFields TABLE
(
FieldID INT IDENTITY(1,1),
DatabaseName SYSNAME,
TableOwner SYSNAME,
TableName SYSNAME,
FieldName SYSNAME,
ColumnPosition INT,
ColumnDefaultValue NVARCHAR(1000),
ColumnDefaultName SYSNAME NULL,
IsNullable BIT,
DataType SYSNAME,
MaxLength INT,
NumericPrecision INT,
NumericScale INT,
DomainName SYSNAME NULL,
FieldListingName NVARCHAR(300),
FieldDefinition NVARCHAR(4000),
IdentityColumn BIT,
IdentitySeed INT,
IdentityIncrement INT,
IsCharColumn BIT
)

DECLARE @HoldingArea TABLE
(
FldID SMALLINT IDENTITY(1,1),
Flds VARCHAR(4000),
FldValue CHAR(1) DEFAULT(0)
)

DECLARE @PKObjectID TABLE
(
ObjectID INT
)

DECLARE @Uniques TABLE
(
ObjectID INT
)

DECLARE @HoldingAreaValues TABLE
(
FldID SMALLINT IDENTITY(1,1),
Flds VARCHAR(4000),
FldValue CHAR(1) DEFAULT(0)
)

DECLARE @Definition TABLE
(
DefinitionID SMALLINT IDENTITY(1,1),
FieldValue NVARCHAR(4000)
)

INSERT INTO @ShowFields
(
DatabaseName,
TableOwner,
TableName,
FieldName,
ColumnPosition,
ColumnDefaultValue,
ColumnDefaultName,
IsNullable,
DataType,
MaxLength,
NumericPrecision,
NumericScale,
DomainName,
FieldListingName,
FieldDefinition,
IdentityColumn,
IdentitySeed,
IdentityIncrement,
IsCharColumn
)
SELECT
DB_NAME(),
TABLE_SCHEMA,
TABLE_NAME,
COLUMN_NAME,
CAST(ORDINAL_POSITION AS INT),
COLUMN_DEFAULT,
dobj.name AS ColumnDefaultName,
CASE WHEN c.IS_NULLABLE = 'YES' THEN 1 ELSE 0 END,
DATA_TYPE,
CAST(CHARACTER_MAXIMUM_LENGTH AS INT),
CAST(NUMERIC_PRECISION AS INT),
CAST(NUMERIC_SCALE AS INT),
DOMAIN_NAME,
QUOTENAME(COLUMN_NAME) + ',',
comp.definition + CASE WHEN comp.is_persisted = 1 THEN ' PERSISTED' ELSE '' END AS FieldDefinition,
CASE WHEN ic.object_id IS NULL THEN 0 ELSE 1 END AS IdentityColumn,
CAST(ISNULL(ic.seed_value,0) AS INT) AS IdentitySeed,
CAST(ISNULL(ic.increment_value,0) AS INT) AS IdentityIncrement,
CASE WHEN st.collation_name IS NOT NULL THEN 1 ELSE 0 END AS IsCharColumn
FROM
INFORMATION_SCHEMA.COLUMNS c
JOIN sys.columns sc ON c.TABLE_NAME = OBJECT_NAME(sc.object_id) AND c.COLUMN_NAME = sc.Name
LEFT JOIN sys.identity_columns ic ON c.TABLE_NAME = OBJECT_NAME(ic.object_id) AND c.COLUMN_NAME = ic.Name
JOIN sys.types st ON COALESCE(c.DOMAIN_NAME,c.DATA_TYPE) = st.name
LEFT OUTER JOIN sys.objects dobj ON dobj.object_id = sc.default_object_id AND dobj.type = 'D'
LEFT OUTER JOIN [sys].[computed_columns] comp ON comp.object_id = sc.object_id AND sc.column_id = comp.column_id
WHERE sc.object_id = @TableObjId
AND (comp.definition IS NULL OR @IncludeComputedColumns = 1)
ORDER BY
c.TABLE_NAME, c.ORDINAL_POSITION

SET @RCount = @@ROWCOUNT
IF @Verbose = 1 RAISERROR(N'Found %d fields',0,1,@RCount) WITH NOWAIT;
IF @Verbose = 1 SELECT * FROM @ShowFields;

INSERT INTO @HoldingArea (Flds) VALUES('(')

INSERT INTO @Definition(FieldValue)
VALUES('CREATE TABLE ' + @NewTableName)

INSERT INTO @Definition(FieldValue)
VALUES('(')

INSERT INTO @Definition(FieldValue)
SELECT
CHAR(10) + QUOTENAME(FieldName) + ' ' +
CASE
WHEN FieldDefinition IS NOT NULL THEN 'AS ' + FieldDefinition
WHEN DomainName IS NOT NULL AND @UseSystemDataTypes = 0 THEN QUOTENAME(DomainName) + CASE WHEN IsNullable = 1 THEN ' NULL ' ELSE ' NOT NULL ' END
ELSE QUOTENAME(UPPER(DataType)) +
CASE WHEN IsCharColumn = 1 THEN '(' + ISNULL(NULLIF(CAST(MaxLength AS VARCHAR(10)),'-1'),'MAX') + ')' ELSE '' END +
CASE WHEN @IncludeIdentity = 1 AND IdentityColumn = 1 THEN ' IDENTITY(' + CAST(IdentitySeed AS VARCHAR(5))+ ',' + CAST(IdentityIncrement AS VARCHAR(5)) + ')' ELSE '' END +
CASE WHEN IsNullable = 1 THEN ' NULL ' ELSE ' NOT NULL ' END +
CASE WHEN ColumnDefaultName IS NOT NULL AND @IncludeDefaults = 1 THEN ' CONSTRAINT ' + QUOTENAME(ColumnDefaultName + @ConstraintsNameAppend) + ' DEFAULT' + UPPER(ColumnDefaultValue) ELSE '' END
END +
CASE WHEN FieldID = (SELECT MAX(FieldID) FROM @ShowFields) THEN '' ELSE ',' END
FROM @ShowFields

INSERT INTO @Definition(FieldValue)
SELECT
', CONSTRAINT ' + QUOTENAME(name + @ConstraintsNameAppend) + ' FOREIGN KEY (' + ParentColumns + ') REFERENCES ' + ReferencedObject + '(' + ReferencedColumns + ')'
FROM
(
SELECT
ReferencedObject = QUOTENAME(OBJECT_SCHEMA_NAME(fk.referenced_object_id)) + '.' + QUOTENAME(OBJECT_NAME(fk.referenced_object_id)),
ParentObject = QUOTENAME(OBJECT_SCHEMA_NAME(parent_object_id)) + '.' + QUOTENAME(OBJECT_NAME(parent_object_id)),
fk.name,
REVERSE(SUBSTRING(REVERSE((
SELECT QUOTENAME(cp.name) + ','
FROM
sys.foreign_key_columns fkc
JOIN sys.columns cp ON fkc.parent_object_id = cp.object_id AND fkc.parent_column_id = cp.column_id
WHERE fkc.constraint_object_id = fk.object_id
AND cp.name IN (SELECT FieldName FROM @ShowFields)
FOR XML PATH('')
)), 2, 8000)) ParentColumns,
REVERSE(SUBSTRING(REVERSE((
SELECT cr.name + ','
FROM
sys.foreign_key_columns fkc
JOIN sys.columns cr ON fkc.referenced_object_id = cr.object_id AND fkc.referenced_column_id = cr.column_id
WHERE fkc.constraint_object_id = fk.object_id
AND cr.name IN (SELECT FieldName FROM @ShowFields)
FOR XML PATH('')
)), 2, 8000)) ReferencedColumns
FROM sys.foreign_keys fk
WHERE fk.parent_object_id = @TableObjId
AND @IncludeForeignKeys = 1
) a

SET @RCount = @@ROWCOUNT
IF @Verbose = 1 RAISERROR(N'Found %d foreign keys',0,1,@RCount) WITH NOWAIT;

INSERT INTO @Definition(FieldValue)
SELECT CHAR(10) + ', CONSTRAINT ' + QUOTENAME(name + @ConstraintsNameAppend) + ' CHECK ' + definition FROM sys.check_constraints
WHERE parent_object_id = @TableObjId
AND @IncludeCheckConstraints = 1

SET @RCount = @@ROWCOUNT
IF @Verbose = 1 RAISERROR(N'Found %d check constraints',0,1,@RCount) WITH NOWAIT;

INSERT INTO @PKObjectID(ObjectID)
SELECT DISTINCT
PKObject = cco.object_id
FROM
sys.key_constraints cco
JOIN sys.index_columns cc ON cco.parent_object_id = cc.object_id AND cco.unique_index_id = cc.index_id
JOIN sys.indexes i ON cc.object_id = i.object_id AND cc.index_id = i.index_id
WHERE
parent_object_id = @TableObjId AND
i.type = 1 AND
is_primary_key = 1
AND @IncludePrimaryKey = 1

SET @RCount = @@ROWCOUNT
IF @Verbose = 1 RAISERROR(N'Found %d primary key',0,1,@RCount) WITH NOWAIT;

INSERT INTO @Uniques(ObjectID)
SELECT DISTINCT
PKObject = cco.object_id
FROM
sys.key_constraints cco
JOIN sys.index_columns cc ON cco.parent_object_id = cc.object_id AND cco.unique_index_id = cc.index_id
JOIN sys.indexes i ON cc.object_id = i.object_id AND cc.index_id = i.index_id
WHERE
parent_object_id = @TableObjId AND
i.type = 2 AND
is_primary_key = 0 AND
is_unique_constraint = 1
AND @IncludeUniqueIndexes = 1

SET @RCount = @@ROWCOUNT
IF @Verbose = 1 RAISERROR(N'Found %d unique indexes',0,1,@RCount) WITH NOWAIT;

SET @ClusteredPK = CASE WHEN @RCount > 0 THEN 1 ELSE 0 END

INSERT INTO @Definition(FieldValue)
SELECT CHAR(10) + ', CONSTRAINT ' + QUOTENAME(name + @ConstraintsNameAppend) + CASE type WHEN 'PK' THEN ' PRIMARY KEY ' + CASE WHEN pk.ObjectID IS NULL THEN ' NONCLUSTERED ' ELSE ' CLUSTERED ' END
WHEN 'UQ' THEN ' UNIQUE ' END + CASE WHEN u.ObjectID IS NOT NULL THEN ' NONCLUSTERED ' ELSE '' END + '(' +
REVERSE(SUBSTRING(REVERSE((
SELECT
QUOTENAME(c.name) + CASE WHEN cc.is_descending_key = 1 THEN ' DESC' ELSE ' ASC' END + ','
FROM
sys.key_constraints ccok
INNER JOIN sys.index_columns cc ON ccok.parent_object_id = cc.object_id AND cco.unique_index_id = cc.index_id
INNER JOIN sys.columns c ON cc.object_id = c.object_id AND cc.column_id = c.column_id AND c.name IN (SELECT FieldName FROM @ShowFields)
INNER JOIN sys.indexes i ON cc.object_id = i.object_id AND cc.index_id = i.index_id
WHERE
i.object_id = ccok.parent_object_id AND
ccok.object_id = cco.object_id
FOR XML PATH('')
)), 2, 8000)) + ')'
FROM
sys.key_constraints cco
LEFT JOIN @PKObjectID pk ON cco.object_id = pk.ObjectID
LEFT JOIN @Uniques u ON cco.object_id = u.objectID
WHERE
cco.parent_object_id = @TableObjId
AND (@IncludePrimaryKey = 1 OR @IncludeUniqueIndexes = 1)

IF @IncludeIndexes = 1
BEGIN
INSERT INTO @Definition(FieldValue)
SELECT
CHAR(10) + ', INDEX ' + QUOTENAME([name]) COLLATE SQL_Latin1_General_CP1_CI_AS + ' ' + type_desc + ' (' +
REVERSE(SUBSTRING(REVERSE((
SELECT QUOTENAME(name) + CASE WHEN sc.is_descending_key = 1 THEN ' DESC' ELSE ' ASC' END + ','
FROM
sys.index_columns sc
JOIN sys.columns c ON sc.object_id = c.object_id AND sc.column_id = c.column_id
WHERE
sc.object_id = @TableObjId AND
sc.object_id = i.object_id AND
sc.index_id = i.index_id AND
c.name IN (SELECT FieldName FROM @ShowFields)
ORDER BY index_column_id ASC
FOR XML PATH('')
)), 2, 8000)) + ')'
FROM sys.indexes i
WHERE
object_id = @TableObjId
AND CASE WHEN @ClusteredPK = 1 AND is_primary_key = 1 AND type = 1 THEN 0 ELSE 1 END = 1
AND is_unique_constraint = 0
AND is_primary_key = 0

SET @RCount = @@ROWCOUNT
IF @Verbose = 1 RAISERROR(N'Found %d indexes',0,1,@RCount) WITH NOWAIT;
END

INSERT INTO @Definition(FieldValue)
VALUES(CHAR(10) + ')')

INSERT INTO @MainDefinition(FieldValue)
SELECT FieldValue FROM @Definition
ORDER BY DefinitionID ASC

SET @RCount = @@ROWCOUNT
IF @Verbose = 1 RAISERROR(N'Collected %d rows for main definition',0,1,@RCount) WITH NOWAIT;

SET @Result = N'';

SELECT @Result = @Result + CHAR(13) + FieldValue FROM @MainDefinition WHERE FieldValue IS NOT NULL;

END
go

IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[sp_GetDDL]') AND type IN ('P', 'PC', 'RF', 'X'))
DROP PROCEDURE [sp_GetDDL]
GO
--#################################################################################################
-- Real World DBA Toolkit Version 2019-08-01 Lowell Izaguirre lowell@stormrage.com
--#################################################################################################
-- USAGE: exec sp_GetDDL GMACT
--   or   exec sp_GetDDL 'bob.example'
--   or   exec sp_GetDDL '[schemaname].[tablename]'
--   or   exec sp_GetDDL #temp
--#################################################################################################
-- copyright 2004-2018 by Lowell Izaguirre scripts*at*stormrage.com all rights reserved.
--developer utility function added by Lowell, used in SQL Server Management Studio
-- http://www.stormrage.com/SQLStuff/sp_GetDDL_Latest.txt
--Purpose: Script Any Table, Temp Table or Object(Procedure Function Synonym View Table Trigger)
--#################################################################################################
-- see the thread here for lots of details: http://www.sqlservercentral.com/Forums/Topic751783-566-7.aspx
-- You can use this however you like...this script is not rocket science, but it took a bit of work to create.
-- the only thing that I ask
-- is that if you adapt my procedure or make it better, to simply send me a copy of it,
-- so I can learn from the things you've enhanced.The feedback you give will be what makes
-- it worthwhile to me, and will be fed back to the SQL community.
-- add this to your toolbox of helpful scripts.
--#################################################################################################
--
-- V300  uses String concatination and sys.tables instead of a cursor
-- V301  enhanced 07/31/2009 to include extended properties definitions
-- V302  fixes an issue where the schema is created , ie 'bob', but no user named 'bob' owns the schema, so the table is not found
-- V303  fixes an issue where all rules are appearing, instead of jsut the rule related to a column
-- V304  testing whether vbCrLf is better than just CHAR(13), some formatting cleanup with GO statements
--       also fixed an issue with the conversion from syscolumns to sys.columns, max-length is only field we need, not [precision]
-- V305  user feedback helped me find that the type_name function should call user_type_id instead of system_type_id
--       also fixed issue where identity definition missing from numeric/decimal definition
-- V306  fixes the computed columns definition that got broken/removed somehow in V300
--       also formatting when decimal is not an identity
-- V307  fixes bug identified by David Griffiths-491597 from SSC where the  @TABLE_ID
--       is reselected, but without it's schema  , potentially selecting the wrong table
--       also fixed is the missing size definition for varbinary, also found by David Griffith
-- V308  abtracted all SQLs to use Table Alaises
--       added logic to script a temp table.
--       added warning about possibly not being marked as system object.
-- V309  added logic based on feedback from Vincent Wylenzek @SSC to return the definition from sys.sql_modules for
--       any object like procedure/view/function/trigger, and not just a table.
--       note previously, if you pointed sp_GetDDL at a view, it returned the view definition as a table...
--       now it will return the view definition instead.
-- V309a returns multi row recordset, one line per record
-- V310a fixed the commented out code related to collation identified by moadh.bs @SSC
--       changed the DEFAULT definitions to not include the default name.
-- V310b Added PERSISTED to calculated columns where applicable
-- V310b fixed COLLATE statement for temp tables
-- V310c fixed NVARCHAR size misreported as doubled.
-- V311  fixed issue where indexes did not identify if the column was ASC or DESC found by nikus @ SSC
-- V311a fixed issue where indexes did not identify if the index was CLUSTERED or NONCLUSTERED found by nikus @ SSC 02/22/2013
-- V312  got rid of all upper casing, and allowing all scripts to generate the exact object names in cases of case sensitive databases.
--       now using the case sensitive name of the table passed: so of you did 'exec sp_GetDDL invoicedocs , it might return the script for InvoiceDocs, as that is how it is spelled in sys.objects.
--       added if exists(drop table/procedure/function) statement to the scripting automatically.
--       toggled the commented out code to list any default constraints by name, hopefully to be more accurate..
--       formatting of index statements to be multi line for better readability
--V314   03/30/2015
--       did i mention this scripts out temp tables too? sp_GetDDL #tmp
--       scripts any object:table,#temptable procedure, function, view or trigger
--       added ability to script synonyms
--       moved logic for REAL datatype to fix error when scripting real columns
--       added OmaCoders suggestion to script column extended properties as well.
--       added matt_slack suggestion to script schemaname as part of index portion of script.
--       minor script cleanup to use QUOTENAME insead of concatenating square brackets.
--       changed compatibility to 2008 and above only, now filtered idnexes with WHERE statmeents script correctly
--       foreign key tables and columns  in script now quotenamed to account for spaces in names; previously an error for Applciation ID instead of [Application ID]
--V315   Fixes Aliases and column names that prevented Case Sensitive collations from working.
--       Adds code if the procedure scripted is a system object
--       index scripts featuring filtered indexes is now included
--       index scripts now include filegroup name and compression settings
--       foreign key casecade delete/update settings now included as identified by Alberto aserio@SSC)
--       Fixes related to scripting extended events  as identified by Alberto aserio@SSC)
--V316   Fixes Identified 07/27/2016 by mlm( m.martinelli@SSC)
--       Added logic  resolving error when custom data type are defined using name greather than 16 char.
--       Added handling for data types: binary, datetime2, datetimeoffset, time
--       Added Set Based logic for Handling Fixed FOREIGN KEYS handling when one foreign key is define on more then one field
--       Added SPARSE column property
--V317   Fixes Identified 03/30/2017 by Lowell
--       Scripting of Foreign key column(s) are now quotenamed
--       Scripting column store indexes was broken, now fixed for column store indexes
--V318   Fixes Identified 02/14/2018 by Lowell
--       Scripting of with collation added/required for scripting SharePoint/ReportServer , or databases with non standard collations
--       Scripting enhanced to definitively handle case sensitive collations as well.
--V319   Adding logic for Temporal Tables, to grab their auto nistory tables
--       first attempt for partitioned tables, to get the columns correctly on the partition scheme
--V320   TIM C: Fixed a bug with generating FKs when the table being generated is in a different schema than the FK refernce table
-- DROP PROCEDURE [dbo].[sp_GetDDL]
--#############################################################################
--if you are going to put this in MASTER, and want it to be able to query
--each database's sys.indexes, you MUST mark it as a system procedure:
--EXECUTE sp_ms_marksystemobject 'sp_GetDDL'
--#############################################################################
CREATE PROCEDURE [dbo].[sp_GetDDL]
  @TBL                VARCHAR(255)
AS
BEGIN
  SET NOCOUNT ON;
  DECLARE     @TBLNAME                VARCHAR(200),
              @SCHEMANAME             VARCHAR(255),
              @STRINGLEN              INT,
              @TABLE_ID               INT,
              @FINALSQL               VARCHAR(MAX),
              @CONSTRAINTSQLS         VARCHAR(MAX),
              @CHECKCONSTSQLS         VARCHAR(MAX),
              @RULESCONSTSQLS         VARCHAR(MAX),
              @FKSQLS                 VARCHAR(MAX),
              @TRIGGERSTATEMENT       VARCHAR(MAX),
              @EXTENDEDPROPERTIES     VARCHAR(MAX),
              @INDEXSQLS              VARCHAR(MAX),
              @MARKSYSTEMOBJECT       VARCHAR(MAX),
              @vbCrLf                 CHAR(2),
              @ISSYSTEMOBJECT         INT,
              @PROCNAME               VARCHAR(256),
              @input                  VARCHAR(MAX),
              @ObjectTypeFound        VARCHAR(255),
              @ObjectDataTypeLen      INT,
              --V3.20 additions
              @WithStatement          VARCHAR(MAX),
              @FileGroupStatement     VARCHAR(MAX),
              @PartitioningStatement  VARCHAR(MAX),
              @TemporalStatement      VARCHAR(MAX);
--##############################################################################
-- INITIALIZE
--##############################################################################
  SET @input = '';
  --new code: determine whether this proc is marked as a system proc with sp_ms_marksystemobject,
  --which flips the is_ms_shipped bit in sys.objects
    SELECT @ISSYSTEMOBJECT = ISNULL([is_ms_shipped],0),@PROCNAME = ISNULL([name],'sp_GetDDL') FROM [sys].[objects] WHERE [object_id] = @@PROCID;
  IF @ISSYSTEMOBJECT IS NULL
    SELECT @ISSYSTEMOBJECT = ISNULL([is_ms_shipped],0),@PROCNAME = ISNULL([name],'sp_GetDDL') FROM [master].[sys].[objects] WHERE [object_id] = @@PROCID;
  IF @ISSYSTEMOBJECT IS NULL
    SET @ISSYSTEMOBJECT = 0;
  IF @PROCNAME IS NULL
    SET @PROCNAME = 'sp_GetDDL';
  --SET @TBL =  '[DBO].[WHATEVER1]'
  --does the tablename contain a schema?
  SET @vbCrLf =  CHAR(10);
  SELECT @SCHEMANAME = ISNULL(PARSENAME(@TBL,2),'dbo') ,
         @TBLNAME    = PARSENAME(@TBL,1);
  SELECT
    @TBLNAME    = [objz].[name],
    @TABLE_ID   = [objz].[object_id]
  FROM [sys].[objects] AS [objz]
  WHERE [objz].[type]          IN ('S','U')
    AND [objz].[name]          <>  'dtproperties'
    AND [objz].[name]           =  @TBLNAME
    AND [objz].[schema_id] =  SCHEMA_ID(@SCHEMANAME) ;
 SELECT @ObjectDataTypeLen = MAX(LEN([name])) FROM [sys].[types];
--##############################################################################
-- Check If TEMP TableName is Valid
--##############################################################################
  IF LEFT(@TBLNAME,1) = '#'  COLLATE SQL_Latin1_General_CP1_CI_AS
    BEGIN
      PRINT '--TEMP TABLE  ' + QUOTENAME(@TBLNAME) + '  FOUND';
      IF OBJECT_ID('tempdb..' + QUOTENAME(@TBLNAME)) IS NOT NULL
        BEGIN
          PRINT '--GOIN TO TEMP PROCESSING';
          GOTO TEMPPROCESS;
        END;
    END;
  ELSE
    BEGIN
      PRINT '--Non-Temp Table, ' + QUOTENAME(@TBLNAME) + ' continue Processing';
    END;
--##############################################################################
-- Check If TableName is Valid
--##############################################################################
  IF ISNULL(@TABLE_ID,0) = 0
    BEGIN
      --V309 code: see if it is an object and not a table.
      SELECT
        @TBLNAME    = [objz].[name],
        @TABLE_ID   = [objz].[object_id],
        @ObjectTypeFound = [objz].[type_desc]
      FROM [sys].[objects] AS [objz]
      --WHERE [type_desc]     IN('SQL_STORED_PROCEDURE','VIEW','SQL_TRIGGER','AGGREGATE_FUNCTION','SQL_INLINE_TABLE_VALUED_FUNCTION','SQL_TABLE_VALUED_FUNCTION','SQL_SCALAR_FUNCTION','SYNONYMN')
      WHERE [objz].[type]          IN ('P','V','TR','AF','IF','FN','TF','SN')
        AND [objz].[name]          <>  'dtproperties'
        AND [objz].[name]           =  @TBLNAME
        AND [objz].[schema_id] =  SCHEMA_ID(@SCHEMANAME) ;
      IF ISNULL(@TABLE_ID,0) <> 0
        BEGIN
          --adding a drop statement.
          --adding a sp_ms_marksystemobject if needed
          SELECT @MARKSYSTEMOBJECT = CASE
                                       WHEN [objz].[is_ms_shipped] = 1
                                       THEN '
GO
--#################################################################################################
--Mark as a system object
EXECUTE sp_ms_marksystemobject  ''' + QUOTENAME(@SCHEMANAME) +'.' + QUOTENAME(@TBLNAME) + '''
--#################################################################################################
'
                                       ELSE '
GO
'
                                     END
          FROM [sys].[objects] AS [objz]
          WHERE [objz].[object_id] = @TABLE_ID;
          --adding a drop statement.
          IF @ObjectTypeFound = 'SYNONYM'  COLLATE SQL_Latin1_General_CP1_CI_AS
            BEGIN
               SELECT @FINALSQL =
                'IF EXISTS(SELECT * FROM sys.synonyms WHERE name = '''
                                + [name]
                                + ''''
                                + ' AND base_object_name <> ''' + [base_object_name] + ''')'
                                + @vbCrLf
                                + '  DROP SYNONYM ' + QUOTENAME([name]) + ''
                                + @vbCrLf
                                +'GO'
                                + @vbCrLf
                                +'IF NOT EXISTS(SELECT * FROM sys.synonyms WHERE name = '''
                                + [name]
                                + ''')'
                                + @vbCrLf
                                + 'CREATE SYNONYM ' + QUOTENAME([name]) + ' FOR ' + [base_object_name] +';'
                                FROM [sys].[synonyms]
                                WHERE  [name]   =  @TBLNAME
                                AND [schema_id] =  SCHEMA_ID(@SCHEMANAME);
            END;
          ELSE
            BEGIN
          SELECT @FINALSQL =
          'IF OBJECT_ID(''' + QUOTENAME(@SCHEMANAME) + '.' + QUOTENAME(@TBLNAME) + ''') IS NOT NULL ' + @vbCrLf
          + 'DROP ' + CASE
                        WHEN [objz].[type] IN ('P')
                        THEN ' PROCEDURE '
                        WHEN [objz].[type] IN ('V')
                        THEN ' VIEW      '
                        WHEN [objz].[type] IN ('TR')
                        THEN ' TRIGGER   '
                        ELSE ' FUNCTION  '
                      END
                      + QUOTENAME(@SCHEMANAME) + '.' + QUOTENAME(@TBLNAME) + ' ' + @vbCrLf + 'GO' + @vbCrLf
          + [def].[definition] + @MARKSYSTEMOBJECT
          FROM [sys].[objects] AS [objz]
            INNER JOIN [sys].[sql_modules] AS [def]
              ON [objz].[object_id] = [def].[object_id]
          WHERE [objz].[type]          IN ('P','V','TR','AF','IF','FN','TF')
            AND [objz].[name]          <>  'dtproperties'
            AND [objz].[name]           =  @TBLNAME
            AND [objz].[schema_id] =  SCHEMA_ID(@SCHEMANAME) ;
            END;
          SET @input = @FINALSQL;

        SELECT @input AS [Item];
         RETURN;
        END;
      ELSE
        BEGIN
        SET @FINALSQL = 'Object ' + QUOTENAME(@SCHEMANAME) + '.' + QUOTENAME(@TBLNAME) + ' does not exist in Database ' + QUOTENAME(DB_NAME())   + ' '
                      + CASE
                          WHEN @ISSYSTEMOBJECT = 0 THEN @vbCrLf + ' (also note that ' + @PROCNAME + ' is not marked as a system proc and cross db access to sys.tables will fail.)'
                          ELSE ''
                        END;
      IF LEFT(@TBLNAME,1) = '#'
        SET @FINALSQL = @FINALSQL + ' OR in The tempdb database.';
      SELECT @FINALSQL AS [Item];
      RETURN 0;
        END;

    END;
--##############################################################################
-- Valid Table, Continue Processing
--##############################################################################
--Is this a SYSTEM versioned TABLE?
SELECT @FINALSQL =
     CASE
       WHEN [tabz].[history_table_id] IS NULL
       THEN ''
       ELSE 'ALTER TABLE ' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[object_id]) ) + '.' + QUOTENAME(OBJECT_NAME([tabz].[object_id])) + ' SET (SYSTEM_VERSIONING = OFF);' + @vbCrLf
            +  'IF OBJECT_ID(''' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[history_table_id]) ) + '.' + QUOTENAME(OBJECT_NAME([tabz].[history_table_id])) + ''') IS NOT NULL ' + @vbCrLf
              + 'DROP TABLE ' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[history_table_id])) + '.' + QUOTENAME(OBJECT_NAME([tabz].[history_table_id])) + ' ' + @vbCrLf + 'GO' + @vbCrLf
       END
    + 'IF OBJECT_ID(''' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[object_id]) ) + '.' + QUOTENAME(OBJECT_NAME([tabz].[object_id])) + ''') IS NOT NULL ' + @vbCrLf
              + 'DROP TABLE ' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[object_id])) + '.' + QUOTENAME(OBJECT_NAME([tabz].[object_id])) + ' ' + @vbCrLf + 'GO' + @vbCrLf
              + 'CREATE TABLE ' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[object_id])) + '.' + QUOTENAME(OBJECT_NAME([tabz].[object_id])) + ' ( '
    FROM [sys].[tables] [tabz] WHERE [tabz].[object_id] = @TABLE_ID
    PRINT @FINALSQL
  --removed invalid code here which potentially selected wrong table--thanks David Grifiths @SSC!
  SELECT
    @STRINGLEN = MAX(LEN([colz].[name])) + 1
  FROM [sys].[objects] AS [objz]
    INNER JOIN [sys].[columns] AS [colz]
      ON  [objz].[object_id] = [colz].[object_id]
      AND [objz].[object_id] = @TABLE_ID;
--##############################################################################
--Get the columns, their definitions and defaults.
--##############################################################################
  SELECT
    @FINALSQL = @FINALSQL
    + CASE
        WHEN [colz].[is_computed] = 1
        THEN @vbCrLf
             + QUOTENAME([colz].[name])
             + ' '
             + SPACE(@STRINGLEN - LEN([colz].[name]))
             + 'AS ' + ISNULL([CALC].[definition],'')
             + CASE
                 WHEN [CALC].[is_persisted] = 1
                 THEN ' PERSISTED'
                 ELSE ''
               END
        ELSE @vbCrLf
             + QUOTENAME([colz].[name])
             + ' '
             + SPACE(@STRINGLEN - LEN([colz].[name]))
             + UPPER(TYPE_NAME([colz].[user_type_id]))
             + CASE
-- data types with precision and scale  IE DECIMAL(18,3), NUMERIC(10,2)
               WHEN TYPE_NAME([colz].[user_type_id]) IN ('decimal','numeric')
               THEN '('
                    + CONVERT(VARCHAR,[colz].[precision])
                    + ','
                    + CONVERT(VARCHAR,[colz].[scale])
                    + ') '
                    + SPACE(6 - LEN(CONVERT(VARCHAR,[colz].[precision])
                    + ','
                    + CONVERT(VARCHAR,[colz].[scale])))
                    + SPACE(7)
                    + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                    + CASE
                        WHEN COLUMNPROPERTY ( @TABLE_ID , [colz].[name] , 'IsIdentity' ) = 0
                        THEN ''
                        ELSE ' IDENTITY('
                               + CONVERT(VARCHAR,ISNULL(IDENT_SEED(@TBLNAME),1) )
                               + ','
                               + CONVERT(VARCHAR,ISNULL(IDENT_INCR(@TBLNAME),1) )
                               + ')'
                        END
                    + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                    + CASE
                        WHEN [colz].[is_nullable] = 0
                        THEN ' NOT NULL'
                        ELSE '     NULL'
                      END
-- data types with scale  IE datetime2(7),TIME(7)
               WHEN TYPE_NAME([colz].[user_type_id]) IN ('datetime2','datetimeoffset','time')
               THEN CASE
                      WHEN [colz].[scale] < 7 THEN
                      '('
                      + CONVERT(VARCHAR,[colz].[scale])
                      + ') '
                    ELSE
                      '    '
                    END
                    + SPACE(4)
                    + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                    + '        '
                    + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                    + CASE [colz].[generated_always_type]
                        WHEN 0 THEN ''
                        WHEN 1 THEN ' GENERATED ALWAYS AS ROW START'
                        WHEN 2 THEN ' GENERATED ALWAYS AS ROW END'
                        ELSE ''
                      END
                    + CASE WHEN [colz].[is_hidden] = 1 THEN ' HIDDEN' ELSE '' END
                    + CASE
                        WHEN [colz].[is_nullable] = 0
                        THEN ' NOT NULL'
                        ELSE '     NULL'
                      END
--data types with no/precision/scale,IE  FLOAT
               WHEN  TYPE_NAME([colz].[user_type_id]) IN ('float') --,'real')
               THEN
               --addition: if 53, no need to specifically say (53), otherwise display it
                    CASE
                      WHEN [colz].[precision] = 53
                      THEN SPACE(11 - LEN(CONVERT(VARCHAR,[colz].[precision])))
                           + SPACE(7)
                           + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                           + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                           + CASE
                               WHEN [colz].[is_nullable] = 0
                               THEN ' NOT NULL'
                               ELSE '     NULL'
                             END
                      ELSE '('
                           + CONVERT(VARCHAR,[colz].[precision])
                           + ') '
                           + SPACE(6 - LEN(CONVERT(VARCHAR,[colz].[precision])))
                           + SPACE(7) + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                           + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                           + CASE
                               WHEN [colz].[is_nullable] = 0
                               THEN ' NOT NULL'
                               ELSE '     NULL'
                             END
                      END
--data type with max_length		ie CHAR (44), VARCHAR(40), BINARY(5000),
--##############################################################################
-- COLLATE STATEMENTS
-- personally i do not like collation statements,
-- but included here to make it easy on those who do
--##############################################################################
               WHEN  TYPE_NAME([colz].[user_type_id]) IN ('char','varchar','binary','varbinary')
               THEN CASE
                      WHEN  [colz].[max_length] = -1
                      THEN  '(max)'
                            + SPACE(6 - LEN(CONVERT(VARCHAR,[colz].[max_length])))
                            + SPACE(7) + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                            ----collate to comment out when not desired
                            --+ CASE
                            --    WHEN COLS.collation_name IS NULL
                            --    THEN ''
                            --    ELSE ' COLLATE ' + COLS.collation_name
                            --  END
                            + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                            + CASE
                                WHEN [colz].[is_nullable] = 0
                                THEN ' NOT NULL'
                                ELSE '     NULL'
                              END
                      ELSE '('
                           + CONVERT(VARCHAR,[colz].[max_length])
                           + ') '
                           + SPACE(6 - LEN(CONVERT(VARCHAR,[colz].[max_length])))
                           + SPACE(7) + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                           ----collate to comment out when not desired
                           --+ CASE
                           --     WHEN COLS.collation_name IS NULL
                           --     THEN ''
                           --     ELSE ' COLLATE ' + COLS.collation_name
                           --   END
                           + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                           + CASE
                               WHEN [colz].[is_nullable] = 0
                               THEN ' NOT NULL'
                               ELSE '     NULL'
                             END
                    END
--data type with max_length ( BUT DOUBLED) ie NCHAR(33), NVARCHAR(40)
               WHEN TYPE_NAME([colz].[user_type_id]) IN ('nchar','nvarchar')
               THEN CASE
                      WHEN  [colz].[max_length] = -1
                      THEN '(max)'
                           + SPACE(5 - LEN(CONVERT(VARCHAR,([colz].[max_length] / 2))))
                           + SPACE(7)
                           + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                           ----collate to comment out when not desired
                           --+ CASE
                           --     WHEN COLS.collation_name IS NULL
                           --     THEN ''
                           --     ELSE ' COLLATE ' + COLS.collation_name
                           --   END
                           + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                           + CASE
                               WHEN [colz].[is_nullable] = 0
                               THEN  ' NOT NULL'
                               ELSE '     NULL'
                             END
                      ELSE '('
                           + CONVERT(VARCHAR,([colz].[max_length] / 2))
                           + ') '
                           + SPACE(6 - LEN(CONVERT(VARCHAR,([colz].[max_length] / 2))))
                           + SPACE(7)
                           + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                           ----collate to comment out when not desired
                           --+ CASE
                           --     WHEN COLS.collation_name IS NULL
                           --     THEN ''
                           --     ELSE ' COLLATE ' + COLS.collation_name
                           --   END
                           + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                           + CASE
                               WHEN [colz].[is_nullable] = 0
                               THEN ' NOT NULL'
                               ELSE '     NULL'
                             END
                    END
               WHEN TYPE_NAME([colz].[user_type_id]) IN ('datetime','money','text','image','real')
               THEN SPACE(18 - LEN(TYPE_NAME([colz].[user_type_id])))
                    + '              '
                    + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                    + CASE
                        WHEN [colz].[is_nullable] = 0
                        THEN ' NOT NULL'
                        ELSE '     NULL'
                      END
--  other data type 	IE INT, DATETIME, MONEY, CUSTOM DATA TYPE,...
               ELSE SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                            + CASE
                                WHEN COLUMNPROPERTY ( @TABLE_ID , [colz].[name] , 'IsIdentity' ) = 0
                                THEN '              '
                                ELSE ' IDENTITY('
                                     + CONVERT(VARCHAR,ISNULL(IDENT_SEED(@TBLNAME),1) )
                                     + ','
                                     + CONVERT(VARCHAR,ISNULL(IDENT_INCR(@TBLNAME),1) )
                                     + ')'
                              END
                            + SPACE(2)
                            + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                            + CASE
                                WHEN [colz].[is_nullable] = 0
                                THEN ' NOT NULL'
                                ELSE '     NULL'
                              END
               END
             + CASE
                 WHEN [colz].[default_object_id] = 0
                 THEN ''
                 --ELSE ' DEFAULT '  + ISNULL(def.[definition] ,'')
                 --optional section in case NAMED default constraints are needed:
                 ELSE '  CONSTRAINT ' + QUOTENAME([DEF].[name]) + ' DEFAULT ' + ISNULL([DEF].[definition] ,'')
                        --i thought it needed to be handled differently! NOT!
               END  --CASE cdefault
      END --iscomputed
    + ','
    FROM [sys].[columns] AS [colz]
      LEFT OUTER JOIN  [sys].[default_constraints]  AS [DEF]
        ON [colz].[default_object_id] = [DEF].[object_id]
      LEFT OUTER JOIN [sys].[computed_columns] AS [CALC]
         ON  [colz].[object_id] = [CALC].[object_id]
         AND [colz].[column_id] = [CALC].[column_id]
    WHERE [colz].[object_id]=@TABLE_ID
    ORDER BY [colz].[column_id];
--##############################################################################
--used for formatting the rest of the constraints:
--##############################################################################
  SELECT
    @STRINGLEN = MAX(LEN([objz].[name])) + 1
  FROM [sys].[objects] AS [objz];
--##############################################################################
--PK/Unique Constraints and Indexes, using the 2005/08 INCLUDE syntax
--##############################################################################
  DECLARE @Results  TABLE (
                    [SCHEMA_ID]             INT,
                    [SCHEMA_NAME]           VARCHAR(255),
                    [OBJECT_ID]             INT,
                    [OBJECT_NAME]           VARCHAR(255),
                    [index_id]              INT,
                    [index_name]            VARCHAR(255),
                    [ROWS]                  BIGINT,
                    [SizeMB]                DECIMAL(19,3),
                    [IndexDepth]            INT,
                    [TYPE]                  INT,
                    [type_desc]             VARCHAR(30),
                    [fill_factor]           INT,
                    [is_unique]             INT,
                    [is_primary_key]        INT ,
                    [is_unique_constraint]  INT,
                    [index_columns_key]     VARCHAR(MAX),
                    [index_columns_include] VARCHAR(MAX),
                    [has_filter] BIT ,
                    [filter_definition] VARCHAR(MAX),
                    [currentFilegroupName]  VARCHAR(128),
                    [CurrentCompression]    VARCHAR(128));
  INSERT INTO @Results
    SELECT
      [SCH].[schema_id], [SCH].[name] AS [SCHEMA_NAME],
      [objz].[object_id], [objz].[name] AS [OBJECT_NAME],
      [IDX].[index_id], ISNULL([IDX].[name], '---') AS [index_name],
      [partitions].[ROWS], [partitions].[SizeMB], INDEXPROPERTY([objz].[object_id], [IDX].[name], 'IndexDepth') AS [IndexDepth],
      [IDX].[type], [IDX].[type_desc], [IDX].[fill_factor],
      [IDX].[is_unique], [IDX].[is_primary_key], [IDX].[is_unique_constraint],
      ISNULL([Index_Columns].[index_columns_key], '---') AS [index_columns_key],
      ISNULL([Index_Columns].[index_columns_include], '---') AS [index_columns_include],
      [IDX].[has_filter],
      [IDX].[filter_definition],
      [filz].[name],
      ISNULL([p].[data_compression_desc],'')
    FROM [sys].[objects] AS [objz]
      INNER JOIN [sys].[schemas] AS [SCH] ON [objz].[schema_id]=[SCH].[schema_id]
      INNER JOIN [sys].[indexes] AS [IDX] ON [objz].[object_id]=[IDX].[object_id]
      INNER JOIN [sys].[filegroups] AS [filz] ON [IDX].[data_space_id] = [filz].[data_space_id]
      INNER JOIN [sys].[partitions] AS [p]     ON  [IDX].[object_id] =  [p].[object_id]  AND [IDX].[index_id] = [p].[index_id]
      INNER JOIN (
                  SELECT
                    [statz].[object_id], [statz].[index_id], SUM([statz].[row_count]) AS [ROWS],
                    CONVERT(NUMERIC(19,3), CONVERT(NUMERIC(19,3), SUM([statz].[in_row_reserved_page_count]+[statz].[lob_reserved_page_count]+[statz].[row_overflow_reserved_page_count]))/CONVERT(NUMERIC(19,3), 128)) AS [SizeMB]
                  FROM [sys].[dm_db_partition_stats] AS [statz]
                  GROUP BY [statz].[object_id], [statz].[index_id]
                 ) AS [partitions]
        ON  [IDX].[object_id]=[partitions].[object_id]
        AND [IDX].[index_id]=[partitions].[index_id]
    CROSS APPLY (
                 SELECT
                   LEFT([Index_Columns].[index_columns_key], LEN([Index_Columns].[index_columns_key])-1) AS [index_columns_key],
                  LEFT([Index_Columns].[index_columns_include], LEN([Index_Columns].[index_columns_include])-1) AS [index_columns_include]
                 FROM
                      (
                       SELECT
                              (
                              SELECT QUOTENAME([colz].[name]) + CASE WHEN [IXCOLS].[is_descending_key] = 0 THEN ' asc' ELSE ' desc' END + ',' + ' '
                               FROM [sys].[index_columns] AS [IXCOLS]
                                 INNER JOIN [sys].[columns] AS [colz]
                                   ON  [IXCOLS].[column_id]   = [colz].[column_id]
                                   AND [IXCOLS].[object_id] = [colz].[object_id]
                               WHERE [IXCOLS].[is_included_column] = 0
                                 AND [IDX].[object_id] = [IXCOLS].[object_id]
                                 AND [IDX].[index_id] = [IXCOLS].[index_id]
                               ORDER BY [IXCOLS].[key_ordinal]
                               FOR XML PATH('')
                              ) AS [index_columns_key],
                             (
                             SELECT QUOTENAME([colz].[name]) + ',' + ' '
                              FROM [sys].[index_columns] AS [IXCOLS]
                                INNER JOIN [sys].[columns] AS [colz]
                                  ON  [IXCOLS].[column_id]   = [colz].[column_id]
                                  AND [IXCOLS].[object_id] = [colz].[object_id]
                              WHERE [IXCOLS].[is_included_column] = 1
                                AND [IDX].[object_id] = [IXCOLS].[object_id]
                                AND [IDX].[index_id] = [IXCOLS].[index_id]
                              ORDER BY [IXCOLS].[index_column_id]
                              FOR XML PATH('')
                             ) AS [index_columns_include]
                      ) AS [Index_Columns]
                ) AS [Index_Columns]
    WHERE [SCH].[name]  LIKE CASE
                                     WHEN @SCHEMANAME = ''   COLLATE SQL_Latin1_General_CP1_CI_AS
                                     THEN [SCH].[name]
                                     ELSE @SCHEMANAME
                                   END
    AND [objz].[name] LIKE CASE
                                  WHEN @TBLNAME = ''   COLLATE SQL_Latin1_General_CP1_CI_AS
                                  THEN [objz].[name]
                                  ELSE @TBLNAME
                                END
    ORDER BY
      [SCH].[name],
      [objz].[name],
      [IDX].[name];
--@Results table has both PK,s Uniques and indexes in thme...pull them out for adding to funal results:
  SET @CONSTRAINTSQLS = '';
  SET @INDEXSQLS      = '';
  SET @TemporalStatement = '';
  SET @WithStatement = '';
--##############################################################################
  -- Temporal tables
--##############################################################################
  SELECT @TemporalStatement =  ISNULL(@vbCrLf + 'PERIOD FOR SYSTEM_TIME ('
  + MAX(CASE WHEN [colz].[generated_always_type] = 1 THEN [colz].[name] ELSE '' END)
  +','
 + MAX(CASE WHEN [colz].[generated_always_type] = 2 THEN [colz].[name] ELSE '' END)
  +'),','') ,
  @WithStatement = ISNULL(' SYSTEM_VERSIONING = ON (HISTORY_TABLE=' + QUOTENAME(OBJECT_SCHEMA_NAME([objz].[history_table_id])) + '.' + QUOTENAME(OBJECT_NAME([objz].[history_table_id])) + '),' ,'')
  FROM [sys].[tables] [objz]
  INNER JOIN [sys].[columns] [colz]
  ON [objz].[object_id] = [colz].[object_id]
  WHERE [colz].[object_id] = @TABLE_ID
  AND [colz].[generated_always_type] > 0
  GROUP BY [colz].[object_id],[objz].[history_table_id]
--##############################################################################
-- memory optimized
--##############################################################################
SELECT @WithStatement  = @WithStatement + ISNULL('MEMORY_OPTIMIZED=ON, DURABILITY=' + [objz].[durability_desc] + ',','')
FROM [sys].[tables] [objz]
WHERE [objz].[is_memory_optimized] =1
AND [objz].[object_id] = @TABLE_ID
--##############################################################################
--constraints
--column store indexes are different: the "include" columns for normal indexes as scripted above are the columnstores indexed columns
--add a CASE for that situation.
--##############################################################################
  SELECT @CONSTRAINTSQLS = @CONSTRAINTSQLS
         + CASE
             WHEN [is_primary_key] = 1 OR [is_unique] = 1
             THEN @vbCrLf
                  + 'CONSTRAINT   '  COLLATE SQL_Latin1_General_CP1_CI_AS + QUOTENAME([index_name]) + ' '
                  + CASE
                      WHEN [is_primary_key] = 1
                      THEN ' PRIMARY KEY '
                      ELSE CASE
                             WHEN [is_unique] = 1
                             THEN ' UNIQUE      '
                             ELSE ''
                           END
                    END
                  + [type_desc]
                  + CASE
                      WHEN [type_desc]='NONCLUSTERED'
                      THEN ''
                      ELSE '   '
                    END
                  + ' (' + [index_columns_key] + ')'
                  + CASE
                      WHEN [index_columns_include] <> '---'
                      THEN ' INCLUDE (' + [index_columns_include] + ')'
                      ELSE ''
                    END
                  + CASE
                      WHEN [has_filter] = 1
                      THEN ' ' + [filter_definition]
                      ELSE ' '
                    END
                  + CASE WHEN [fill_factor] <> 0 OR [CurrentCompression] <> 'NONE'
                  THEN ' WITH (' + CASE
                                    WHEN [fill_factor] <> 0
                                    THEN 'FILLFACTOR = ' + CONVERT(VARCHAR(30),[fill_factor])
                                    ELSE ''
                                  END
                                + CASE
                                    WHEN [fill_factor] <> 0  AND [CurrentCompression] <> 'NONE' THEN ',DATA_COMPRESSION = ' + [CurrentCompression] + ' '
                                    WHEN [fill_factor] <> 0  AND [CurrentCompression]  = 'NONE' THEN ''
                                    WHEN [fill_factor]  = 0  AND [CurrentCompression] <> 'NONE' THEN 'DATA_COMPRESSION = ' + [CurrentCompression] + ' '
                                    ELSE ''
                                  END
                                  + ')'
                  ELSE ''
                  END

             ELSE ''
           END + ','
  FROM @Results
  WHERE [type_desc] != 'HEAP'
    AND [is_primary_key] = 1
    OR  [is_unique] = 1
  ORDER BY
    [is_primary_key] DESC,
    [is_unique] DESC;
    --
--##############################################################################
--indexes
--##############################################################################
  SELECT @INDEXSQLS = @INDEXSQLS
         + CASE
             WHEN [is_primary_key] = 0 OR [is_unique] = 0
             THEN @vbCrLf
                  + 'CREATE '  COLLATE SQL_Latin1_General_CP1_CI_AS + [type_desc] + ' INDEX '  COLLATE SQL_Latin1_General_CP1_CI_AS + QUOTENAME([index_name]) + ' '
                  + @vbCrLf
                  + '   ON '   COLLATE SQL_Latin1_General_CP1_CI_AS
                  + QUOTENAME([SCHEMA_NAME]) + '.' + QUOTENAME([OBJECT_NAME])
                  + CASE
                        WHEN [CurrentCompression] = 'COLUMNSTORE'  COLLATE SQL_Latin1_General_CP1_CI_AS
                        THEN ' (' + [index_columns_include] + ')'
                        ELSE ' (' + [index_columns_key] + ')'
                    END
                  + CASE
                      WHEN [CurrentCompression] = 'COLUMNSTORE'  COLLATE SQL_Latin1_General_CP1_CI_AS
                      THEN ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                      ELSE
                        CASE
                     WHEN [index_columns_include] <> '---'
                     THEN @vbCrLf + '   INCLUDE ('  COLLATE SQL_Latin1_General_CP1_CI_AS + [index_columns_include] + ')'   COLLATE SQL_Latin1_General_CP1_CI_AS
                     ELSE ''   COLLATE SQL_Latin1_General_CP1_CI_AS
                   END
                    END
                  --2008 filtered indexes syntax
                  + CASE
                      WHEN [has_filter] = 1
                      THEN @vbCrLf + '   WHERE '  COLLATE SQL_Latin1_General_CP1_CI_AS + [filter_definition]
                      ELSE ''
                    END
                  + CASE WHEN [fill_factor] <> 0 OR [CurrentCompression] <> 'NONE'  COLLATE SQL_Latin1_General_CP1_CI_AS
                  THEN ' WITH ('  COLLATE SQL_Latin1_General_CP1_CI_AS + CASE
                                    WHEN [fill_factor] <> 0
                                    THEN 'FILLFACTOR = '  COLLATE SQL_Latin1_General_CP1_CI_AS + CONVERT(VARCHAR(30),[fill_factor])
                                    ELSE ''
                                  END
                                + CASE
                                    WHEN [fill_factor] <> 0  AND [CurrentCompression] <> 'NONE' THEN ',DATA_COMPRESSION = ' + [CurrentCompression]+' '
                                    WHEN [fill_factor] <> 0  AND [CurrentCompression]  = 'NONE' THEN ''
                                    WHEN [fill_factor]  = 0  AND [CurrentCompression] <> 'NONE' THEN 'DATA_COMPRESSION = ' + [CurrentCompression]+' '
                                    ELSE ''
                                  END
                                  + ')'
                  ELSE ''
                  END
           END
  FROM @Results
  WHERE [type_desc] != 'HEAP'
    AND [is_primary_key] = 0
    AND [is_unique] = 0
  ORDER BY
    [is_primary_key] DESC,
    [is_unique] DESC;
  IF @INDEXSQLS <> ''  COLLATE SQL_Latin1_General_CP1_CI_AS
    SET @INDEXSQLS = @vbCrLf + 'GO'  COLLATE SQL_Latin1_General_CP1_CI_AS + @vbCrLf + @INDEXSQLS;
--##############################################################################
--CHECK Constraints
--##############################################################################
  SET @CHECKCONSTSQLS = ''  COLLATE SQL_Latin1_General_CP1_CI_AS;
  SELECT
    @CHECKCONSTSQLS = @CHECKCONSTSQLS
    + @vbCrLf
    + ISNULL('CONSTRAINT   ' + QUOTENAME([objz].[name]) + ' '
    + SPACE(@STRINGLEN - LEN([objz].[name]))
    + ' CHECK ' + ISNULL([CHECKS].[definition],'')
    + ',','')
  FROM [sys].[objects] AS [objz]
    INNER JOIN [sys].[check_constraints] AS [CHECKS] ON [objz].[object_id] = [CHECKS].[object_id]
  WHERE [objz].[type] = 'C'
    AND [objz].[parent_object_id] = @TABLE_ID;
--##############################################################################
--FOREIGN KEYS
--##############################################################################
  SET @FKSQLS = '' ;
    SELECT
    @FKSQLS=@FKSQLS
    + @vbCrLf + [MyAlias].[Command] FROM
(
SELECT
  DISTINCT
  --FK must be added AFTER the PK/unique constraints are added back.
  850 AS [ExecutionOrder],
  'CONSTRAINT '
  + QUOTENAME([conz].[name])
  + ' FOREIGN KEY ('
  + [ChildCollection].[ChildColumns]
  + ') REFERENCES '
  + QUOTENAME(OBJECT_SCHEMA_NAME([conz].[referenced_object_id])) -- TIM C: Changed so that if the FK object is in another schema it will report the correct schema for the FK object
  + '.'
  + QUOTENAME(OBJECT_NAME([conz].[referenced_object_id]))
  + ' (' + [ParentCollection].[ParentColumns]
  + ') '
  +  CASE [conz].[update_referential_action]
                                        WHEN 0 THEN '' --' ON UPDATE NO ACTION '
                                        WHEN 1 THEN ' ON UPDATE CASCADE '
                                        WHEN 2 THEN ' ON UPDATE SET NULL '
                                        ELSE ' ON UPDATE SET DEFAULT '
                                    END
                  + CASE [conz].[delete_referential_action]
                                        WHEN 0 THEN '' --' ON DELETE NO ACTION '
                                        WHEN 1 THEN ' ON DELETE CASCADE '
                                        WHEN 2 THEN ' ON DELETE SET NULL '
                                        ELSE ' ON DELETE SET DEFAULT '
                                    END
                  + CASE [conz].[is_not_for_replication]
                        WHEN 1 THEN ' NOT FOR REPLICATION '
                        ELSE ''
                    END
  + ',' AS [Command]
FROM   [sys].[foreign_keys] AS [conz]
       INNER JOIN [sys].[foreign_key_columns] AS [colz]
         ON [conz].[object_id] = [colz].[constraint_object_id]

       INNER JOIN (--gets my child tables column names
SELECT
 [conz].[name],
 --technically, FK's can contain up to 16 columns, but real life is often a single column. coding here is for all columns
 [ChildColumns] = STUFF((SELECT
                         ',' + QUOTENAME([REFZ].[name])
                       FROM   [sys].[foreign_key_columns] AS [fkcolz]
                              INNER JOIN [sys].[columns] AS [REFZ]
                                ON [fkcolz].[parent_object_id] = [REFZ].[object_id]
                                   AND [fkcolz].[parent_column_id] = [REFZ].[column_id]
                       WHERE [fkcolz].[parent_object_id] = [conz].[parent_object_id]
                           AND [fkcolz].[constraint_object_id] = [conz].[object_id]
                         ORDER  BY
                        [fkcolz].[constraint_column_id]
                      FOR XML PATH(''), TYPE).[value]('.','varchar(max)'),1,1,'')
FROM   [sys].[foreign_keys] AS [conz]
      INNER JOIN [sys].[foreign_key_columns] AS [colz]
        ON [conz].[object_id] = [colz].[constraint_object_id]
        WHERE [conz].[parent_object_id]= @TABLE_ID
GROUP  BY
[conz].[name],
[conz].[parent_object_id],--- without GROUP BY multiple rows are returned
 [conz].[object_id]
    ) AS [ChildCollection]
         ON [conz].[name] = [ChildCollection].[name]
       INNER JOIN (--gets the parent tables column names for the FK reference
                  SELECT
                     [conz].[name],
                     [ParentColumns] = STUFF((SELECT
                                              ',' + [REFZ].[name]
                                            FROM   [sys].[foreign_key_columns] AS [fkcolz]
                                                   INNER JOIN [sys].[columns] AS [REFZ]
                                                     ON [fkcolz].[referenced_object_id] = [REFZ].[object_id]
                                                        AND [fkcolz].[referenced_column_id] = [REFZ].[column_id]
                                            WHERE  [fkcolz].[referenced_object_id] = [conz].[referenced_object_id]
                                              AND [fkcolz].[constraint_object_id] = [conz].[object_id]
                                            ORDER BY [fkcolz].[constraint_column_id]
                                            FOR XML PATH(''), TYPE).[value]('.','varchar(max)'),1,1,'')
                   FROM   [sys].[foreign_keys] AS [conz]
                          INNER JOIN [sys].[foreign_key_columns] AS [colz]
                            ON [conz].[object_id] = [colz].[constraint_object_id]
                           -- AND colz.parent_column_id
                   GROUP  BY
                    [conz].[name],
                    [conz].[referenced_object_id],--- without GROUP BY multiple rows are returned
                    [conz].[object_id]
                  ) AS [ParentCollection]
         ON [conz].[name] = [ParentCollection].[name]
)AS [MyAlias];
--##############################################################################
--RULES
--##############################################################################
  SET @RULESCONSTSQLS = '';
  SELECT
    @RULESCONSTSQLS = @RULESCONSTSQLS
    + ISNULL(
             @vbCrLf
             + 'if not exists(SELECT [name] FROM sys.objects WHERE TYPE=''R'' AND schema_id = ' COLLATE SQL_Latin1_General_CP1_CI_AS + CONVERT(VARCHAR(30),[objz].[schema_id]) + ' AND [name] = '''  COLLATE SQL_Latin1_General_CP1_CI_AS + QUOTENAME(OBJECT_NAME([colz].[rule_object_id])) + ''')'  COLLATE SQL_Latin1_General_CP1_CI_AS + @vbCrLf
             + [MODS].[definition]  + @vbCrLf + 'GO' COLLATE SQL_Latin1_General_CP1_CI_AS +  @vbCrLf
             + 'EXEC sp_binderule  ' + QUOTENAME([objz].[name]) + ', ''' + QUOTENAME(OBJECT_NAME([colz].[object_id])) + '.' + QUOTENAME([colz].[name]) + ''''  COLLATE SQL_Latin1_General_CP1_CI_AS + @vbCrLf + 'GO'  COLLATE SQL_Latin1_General_CP1_CI_AS ,'')
  FROM [sys].[columns] [colz]
    INNER JOIN [sys].[objects] [objz]
      ON [objz].[object_id] = [colz].[object_id]
    INNER JOIN [sys].[sql_modules] AS [MODS]
      ON [colz].[rule_object_id] = [MODS].[object_id]
  WHERE [colz].[rule_object_id] <> 0
    AND [colz].[object_id] = @TABLE_ID;
--##############################################################################
--TRIGGERS
--##############################################################################
  SET @TRIGGERSTATEMENT = '';
  SELECT
    @TRIGGERSTATEMENT = @TRIGGERSTATEMENT +  @vbCrLf + [MODS].[definition] + @vbCrLf + 'GO'
  FROM [sys].[sql_modules] AS [MODS]
  WHERE [MODS].[object_id] IN(SELECT
                         [objz].[object_id]
                       FROM [sys].[objects] AS [objz]
                       WHERE [objz].[type] = 'TR'
                       AND [objz].[parent_object_id] = @TABLE_ID);
  IF @TRIGGERSTATEMENT <> ''  COLLATE SQL_Latin1_General_CP1_CI_AS
    SET @TRIGGERSTATEMENT = @vbCrLf + 'GO'  COLLATE SQL_Latin1_General_CP1_CI_AS + @vbCrLf + @TRIGGERSTATEMENT;
--##############################################################################
--NEW SECTION QUERY ALL EXTENDED PROPERTIES
--##############################################################################
  SET @EXTENDEDPROPERTIES = '';
  SELECT  @EXTENDEDPROPERTIES =
          @EXTENDEDPROPERTIES + @vbCrLf +
         'EXEC sys.sp_addextendedproperty
          @name = N'''  COLLATE SQL_Latin1_General_CP1_CI_AS + [name] + ''', @value = N'''  COLLATE SQL_Latin1_General_CP1_CI_AS + REPLACE(CONVERT(VARCHAR(MAX),[value]),'''','''''') + ''',
          @level0type = N''SCHEMA'', @level0name = '  COLLATE SQL_Latin1_General_CP1_CI_AS + QUOTENAME(@SCHEMANAME) + ',
          @level1type = N''TABLE'', @level1name = '  COLLATE SQL_Latin1_General_CP1_CI_AS + QUOTENAME(@TBLNAME) + ';'
 --SELECT objtype, objname, name, value
  FROM [sys].[fn_listextendedproperty] (NULL, 'schema', @SCHEMANAME, 'table', @TBLNAME, NULL, NULL);
  --OMacoder suggestion for column extended properties http://www.sqlservercentral.com/Forums/FindPost1651606.aspx
   ;WITH [obj] AS (
	SELECT [split].[a].[value]('.', 'VARCHAR(20)') AS [name]
	FROM (
		SELECT CAST ('<M>' + REPLACE('column,constraint,index,trigger,parameter', ',', '</M><M>') + '</M>' AS XML) AS [data]
		) AS [A]
		CROSS APPLY [data].[nodes] ('/M') AS [split]([a])
	)
  SELECT
  @EXTENDEDPROPERTIES =
		 @EXTENDEDPROPERTIES + @vbCrLf + @vbCrLf +
         'EXEC sys.sp_addextendedproperty
         @name = N''' COLLATE SQL_Latin1_General_CP1_CI_AS
         + [lep].[name]
         + ''', @value = N''' COLLATE SQL_Latin1_General_CP1_CI_AS
         + REPLACE(CONVERT(VARCHAR(MAX),[lep].[value]),'''','''''') + ''',
         @level0type = N''SCHEMA'', @level0name = ' COLLATE SQL_Latin1_General_CP1_CI_AS
         + QUOTENAME(@SCHEMANAME)
         + ',
         @level1type = N''TABLE'', @level1name = ' COLLATE SQL_Latin1_General_CP1_CI_AS
         + QUOTENAME(@TBLNAME)
         + ',
         @level2type = N''' COLLATE SQL_Latin1_General_CP1_CI_AS
         + UPPER([obj].[name])
         + ''', @level2name = ' COLLATE SQL_Latin1_General_CP1_CI_AS
         + QUOTENAME([lep].[objname]) + ';' COLLATE SQL_Latin1_General_CP1_CI_AS
  --SELECT objtype, objname, name, value
  FROM [obj]
	CROSS APPLY [sys].[fn_listextendedproperty] (NULL, 'schema', @SCHEMANAME, 'table', @TBLNAME, [obj].[name], NULL) AS [lep];

  IF @EXTENDEDPROPERTIES <> '' COLLATE SQL_Latin1_General_CP1_CI_AS
    SET @EXTENDEDPROPERTIES = @vbCrLf + 'GO' COLLATE SQL_Latin1_General_CP1_CI_AS + @vbCrLf + @EXTENDEDPROPERTIES;
--##############################################################################
--FINAL CLEANUP AND PRESENTATION
--##############################################################################
--at this point, there is a trailing comma, or it blank
--WITH statment has a trailing comma

IF @WithStatement > ''
  SET @WithStatement='WITH (' + SUBSTRING(@WithStatement,1,LEN(@WithStatement) -1)  + ')'
  SELECT
    @FINALSQL = @FINALSQL
                + @TemporalStatement
                + @CONSTRAINTSQLS
                + @CHECKCONSTSQLS
                + @FKSQLS;
--note that this trims the trailing comma from the end of the statements
  SET @FINALSQL = SUBSTRING(@FINALSQL,1,LEN(@FINALSQL) -1) ;
  SET @FINALSQL = @FINALSQL + ')' COLLATE SQL_Latin1_General_CP1_CI_AS +  @vbCrLf + @WithStatement COLLATE SQL_Latin1_General_CP1_CI_AS +  @vbCrLf ;
  SET @input = @vbCrLf
       + @FINALSQL
       + @INDEXSQLS
       + @RULESCONSTSQLS
       + @TRIGGERSTATEMENT
       + @EXTENDEDPROPERTIES;
  SELECT @input AS [Item];
  RETURN 0;
--##############################################################################
-- END Normal Table Processing
--##############################################################################

--simple, primitive version to get the results of a TEMP table from the TEMP db.
--##############################################################################
-- NEW Temp Table Logic
--##############################################################################
TEMPPROCESS:
  SELECT @TABLE_ID = OBJECT_ID('tempdb..' COLLATE SQL_Latin1_General_CP1_CI_AS + @TBLNAME);
--##############################################################################
-- Valid temp Table, Continue Processing
--##############################################################################
SELECT @FINALSQL =
     CASE
       WHEN [tabz].[history_table_id] IS NULL
       THEN ''
       ELSE 'ALTER TABLE ' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[object_id]) ) + '.' + QUOTENAME(OBJECT_NAME([tabz].[object_id])) + ' SET (SYSTEM_VERSIONING = OFF);' + @vbCrLf
            +  'IF OBJECT_ID(''' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[history_table_id]) ) + '.' + QUOTENAME(OBJECT_NAME([tabz].[history_table_id])) + ''') IS NOT NULL ' + @vbCrLf
              + 'DROP TABLE ' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[history_table_id])) + '.' + QUOTENAME(OBJECT_NAME([tabz].[history_table_id])) + ' ' + @vbCrLf + 'GO' + @vbCrLf
       END
    + 'IF OBJECT_ID(''' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[object_id]) ) + '.' + QUOTENAME(OBJECT_NAME([tabz].[object_id])) + ''') IS NOT NULL ' + @vbCrLf
              + 'DROP TABLE ' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[object_id])) + '.' + QUOTENAME(OBJECT_NAME([tabz].[object_id])) + ' ' + @vbCrLf + 'GO' + @vbCrLf
              + 'CREATE TABLE ' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[object_id])) + '.' + QUOTENAME(OBJECT_NAME([tabz].[object_id])) + ' ( '
FROM [sys].[tables] [tabz] WHERE [tabz].[object_id] = OBJECT_ID(@TABLE_ID)
  --removed invalid code here which potentially selected wrong table--thansk David Grifiths @SSC!
SELECT
    @STRINGLEN = MAX(LEN([colz].[name])) + 1
  FROM [tempdb].[sys].[objects] AS [objz]
    INNER JOIN [tempdb].[sys].[columns] AS [colz]
      ON  [objz].[object_id] = [colz].[object_id]
      AND [objz].[object_id] = @TABLE_ID;
--##############################################################################
--Get the hash index definitions for memory optimized tables, if any.
--##############################################################################

--##############################################################################
--Get the columns, their definitions and defaults.
--##############################################################################
  SELECT
    @FINALSQL = @FINALSQL
    + CASE
        WHEN [colz].[is_computed] = 1
        THEN @vbCrLf
             + QUOTENAME([colz].[name])
             + ' '
             + SPACE(@STRINGLEN - LEN([colz].[name]))
             + 'AS ' + ISNULL([CALC].[definition],'')
              + CASE
                 WHEN [CALC].[is_persisted] = 1
                 THEN ' PERSISTED'
                 ELSE ''
               END
        ELSE @vbCrLf
             + QUOTENAME([colz].[name])
             + ' '
             + SPACE(@STRINGLEN - LEN([colz].[name]))
             + UPPER(TYPE_NAME([colz].[user_type_id]))
             + CASE
-- data types with precision and scale  IE DECIMAL(18,3), NUMERIC(10,2)
               WHEN TYPE_NAME([colz].[user_type_id]) IN ('decimal','numeric')
               THEN '('
                    + CONVERT(VARCHAR,[colz].[precision])
                    + ','
                    + CONVERT(VARCHAR,[colz].[scale])
                    + ') '
                    + SPACE(6 - LEN(CONVERT(VARCHAR,[colz].[precision])
                    + ','
                    + CONVERT(VARCHAR,[colz].[scale])))
                    + SPACE(7)
                    + SPACE(16 - LEN(TYPE_NAME([colz].[user_type_id])))
                    + CASE
                        WHEN [colz].[is_identity] = 1
                        THEN ' IDENTITY(1,1)'
                        ELSE ''
                        ----WHEN COLUMNPROPERTY ( @TABLE_ID , COLS.[name] , 'IsIdentity' ) = 1
                        ----THEN ' IDENTITY('
                        ----       + CONVERT(VARCHAR,ISNULL(IDENT_SEED('tempdb..' + @TBLNAME),1) )
                        ----       + ','
                        ----       + CONVERT(VARCHAR,ISNULL(IDENT_INCR('tempdb..' + @TBLNAME),1) )
                        ----       + ')'
                        ----ELSE ''
                        END
                    + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                    + CASE
                        WHEN [colz].[is_nullable] = 0
                        THEN ' NOT NULL'
                        ELSE '     NULL'
                      END
-- data types with scale  IE datetime2(7),TIME(7)
               WHEN TYPE_NAME([colz].[user_type_id]) IN ('datetime2','datetimeoffset','time')
               THEN CASE
                      WHEN [colz].[scale] < 7 THEN
                      '('
                      + CONVERT(VARCHAR,[colz].[scale])
                      + ') '
                    ELSE
                      '    '
                    END
                    + SPACE(4)
                    + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                    + '        '
                    + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                    + CASE [colz].[generated_always_type]
                        WHEN 0 THEN ''
                        WHEN 1 THEN ' GENERATED ALWAYS AS ROW START'
                        WHEN 2 THEN ' GENERATED ALWAYS AS ROW END'
                        ELSE ''
                      END
                    + CASE WHEN [colz].[is_hidden] = 1 THEN ' HIDDEN' ELSE '' END
                    + CASE
                        WHEN [colz].[is_nullable] = 0
                        THEN ' NOT NULL'
                        ELSE '     NULL'
                      END
--data types with no/precision/scale,IE  FLOAT
               WHEN  TYPE_NAME([colz].[user_type_id]) IN ('float') --,'real')
               THEN
               --addition: if 53, no need to specifically say (53), otherwise display it
                    CASE
                      WHEN [colz].[precision] = 53
                      THEN SPACE(11 - LEN(CONVERT(VARCHAR,[colz].[precision])))
                           + SPACE(7)
                           + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                           + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                           + CASE
                               WHEN [colz].[is_nullable] = 0
                               THEN ' NOT NULL'
                               ELSE '     NULL'
                             END
                      ELSE '('
                           + CONVERT(VARCHAR,[colz].[precision])
                           + ') '
                           + SPACE(6 - LEN(CONVERT(VARCHAR,[colz].[precision])))
                           + SPACE(7) + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                           + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                           + CASE
                               WHEN [colz].[is_nullable] = 0
                               THEN ' NOT NULL'
                               ELSE '     NULL'
                             END
                      END
--ie VARCHAR(40)
--##############################################################################
-- COLLATE STATEMENTS in tempdb!
-- personally i do not like collation statements,
-- but included here to make it easy on those who do
--##############################################################################
               WHEN  TYPE_NAME([colz].[user_type_id]) IN ('char','varchar','binary','varbinary')
               THEN CASE
                      WHEN  [colz].[max_length] = -1
                      THEN  '(max)'
                            + SPACE(6 - LEN(CONVERT(VARCHAR,[colz].[max_length])))
                            + SPACE(7) + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                            ----collate to comment out when not desired
                            --+ CASE
                            --    WHEN COLS.collation_name IS NULL
                            --    THEN ''
                            --    ELSE ' COLLATE ' + COLS.collation_name
                            --  END
                            + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                            + CASE
                                WHEN [colz].[is_nullable] = 0
                                THEN ' NOT NULL'
                                ELSE '     NULL'
                              END
                      ELSE '('
                           + CONVERT(VARCHAR,[colz].[max_length])
                           + ') '
                           + SPACE(6 - LEN(CONVERT(VARCHAR,[colz].[max_length])))
                           + SPACE(7) + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                           ----collate to comment out when not desired
                           --+ CASE
                           --     WHEN COLS.collation_name IS NULL
                           --     THEN ''
                           --     ELSE ' COLLATE ' + COLS.collation_name
                           --   END
                           + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                           + CASE
                               WHEN [colz].[is_nullable] = 0
                               THEN ' NOT NULL'
                               ELSE '     NULL'
                             END
                    END
--data type with max_length ( BUT DOUBLED) ie NCHAR(33), NVARCHAR(40)
               WHEN TYPE_NAME([colz].[user_type_id]) IN ('nchar','nvarchar')
               THEN CASE
                      WHEN  [colz].[max_length] = -1
                      THEN '(max)'
                           + SPACE(5 - LEN(CONVERT(VARCHAR,([colz].[max_length] / 2))))
                           + SPACE(7)
                           + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                           -- --collate to comment out when not desired
                           --+ CASE
                           --     WHEN COLS.collation_name IS NULL
                           --     THEN ''
                           --     ELSE ' COLLATE ' + COLS.collation_name
                           --   END
                           + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                           + CASE
                               WHEN [colz].[is_nullable] = 0
                               THEN  ' NOT NULL'
                               ELSE '     NULL'
                             END
                      ELSE '('
                           + CONVERT(VARCHAR,([colz].[max_length] / 2))
                           + ') '
                           + SPACE(6 - LEN(CONVERT(VARCHAR,([colz].[max_length] / 2))))
                           + SPACE(7)
                           + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                           -- --collate to comment out when not desired
                           --+ CASE
                           --     WHEN COLS.collation_name IS NULL
                           --     THEN ''
                           --     ELSE ' COLLATE ' + COLS.collation_name
                           --   END
                           + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                           + CASE
                               WHEN [colz].[is_nullable] = 0
                               THEN ' NOT NULL'
                               ELSE '     NULL'
                             END
                    END
--  other data type 	IE INT, DATETIME, MONEY, CUSTOM DATA TYPE,...
               WHEN TYPE_NAME([colz].[user_type_id]) IN ('datetime','money','text','image','real')
               THEN SPACE(18 - LEN(TYPE_NAME([colz].[user_type_id])))
                    + '              '
                    + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                    + CASE
                        WHEN [colz].[is_nullable] = 0
                        THEN ' NOT NULL'
                        ELSE '     NULL'
                      END
--IE INT
               ELSE SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                            + CASE
                                WHEN [colz].[is_identity] = 1
                                THEN ' IDENTITY(1,1)'
                                ELSE '              '
                                ----WHEN COLUMNPROPERTY ( @TABLE_ID , COLS.[name] , 'IsIdentity' ) = 1
                                ----THEN ' IDENTITY('
                                ----     + CONVERT(VARCHAR,ISNULL(IDENT_SEED('tempdb..' + @TBLNAME),1) )
                                ----     + ','
                                ----     + CONVERT(VARCHAR,ISNULL(IDENT_INCR('tempdb..' + @TBLNAME),1) )
                                ----     + ')'
                                ----ELSE '              '
                              END
                            + SPACE(2)
                            + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                            + CASE
                                WHEN [colz].[is_nullable] = 0
                                THEN ' NOT NULL'
                                ELSE '     NULL'
                              END
               END
             + CASE
                 WHEN [colz].[default_object_id] = 0
                 THEN ''
                 ELSE ' DEFAULT '  + ISNULL([DEF].[definition] ,'')
                 --optional section in case NAMED default cosntraints are needed:
                 --ELSE ' CONSTRAINT [' + DEF.name + '] DEFAULT '+ REPLACE(REPLACE(ISNULL(DEF.[definition] ,''),'((','('),'))',')')
                        --i thought it needed to be handled differently! NOT!
               END  --CASE cdefault
      END --iscomputed
    + ','
    FROM [tempdb].[sys].[columns] AS [colz]
      LEFT OUTER JOIN  [tempdb].[sys].[default_constraints]  AS [DEF]
        ON [colz].[default_object_id] = [DEF].[object_id]
      LEFT OUTER JOIN [tempdb].[sys].[computed_columns] AS [CALC]
         ON  [colz].[object_id] = [CALC].[object_id]
         AND [colz].[column_id] = [CALC].[column_id]
    WHERE [colz].[object_id]=@TABLE_ID
    ORDER BY [colz].[column_id];
--##############################################################################
--used for formatting the rest of the constraints:
--##############################################################################
  SELECT
    @STRINGLEN = MAX(LEN([objz].[name])) + 1
  FROM [tempdb].[sys].[objects] AS [objz];
--##############################################################################
--PK/Unique Constraints and Indexes, using the 2005/08 INCLUDE syntax
--##############################################################################
  DECLARE @Results2  TABLE (
                    [SCHEMA_ID]             INT,
                    [SCHEMA_NAME]           VARCHAR(255),
                    [OBJECT_ID]             INT,
                    [OBJECT_NAME]           VARCHAR(255),
                    [index_id]              INT,
                    [index_name]            VARCHAR(255),
                    [ROWS]                  BIGINT,
                    [SizeMB]                DECIMAL(19,3),
                    [IndexDepth]            INT,
                    [TYPE]                  INT,
                    [type_desc]             VARCHAR(30),
                    [fill_factor]           INT,
                    [is_unique]             INT,
                    [is_primary_key]        INT ,
                    [is_unique_constraint]  INT,
                    [index_columns_key]     VARCHAR(MAX),
                    [index_columns_include] VARCHAR(MAX),
                    [has_filter] BIT ,
                    [filter_definition] VARCHAR(MAX),
                    [currentFilegroupName]  VARCHAR(128),
                    [CurrentCompression]    VARCHAR(128));
  INSERT INTO @Results2
    SELECT
      [SCH].[schema_id], [SCH].[name] AS [SCHEMA_NAME],
      [objz].[object_id], [objz].[name] AS [OBJECT_NAME],
      [IDX].[index_id], ISNULL([IDX].[name], '---') AS [index_name],
      [partitions].[ROWS], [partitions].[SizeMB], INDEXPROPERTY([objz].[object_id], [IDX].[name], 'IndexDepth') AS [IndexDepth],
      [IDX].[type], [IDX].[type_desc], [IDX].[fill_factor],
      [IDX].[is_unique], [IDX].[is_primary_key], [IDX].[is_unique_constraint],
      ISNULL([Index_Columns].[index_columns_key], '---') AS [index_columns_key],
      ISNULL([Index_Columns].[index_columns_include], '---') AS [index_columns_include],
      [IDX].[has_filter],
      [IDX].[filter_definition],
      [filz].[name],
      ISNULL([p].[data_compression_desc],'')
    FROM [tempdb].[sys].[objects] AS [objz]
      INNER JOIN [tempdb].[sys].[schemas] AS [SCH] ON [objz].[schema_id]=[SCH].[schema_id]
      INNER JOIN [tempdb].[sys].[indexes] AS [IDX] ON [objz].[object_id]=[IDX].[object_id]
      INNER JOIN [sys].[filegroups] AS [filz] ON [IDX].[data_space_id] = [filz].[data_space_id]
      INNER JOIN [sys].[partitions] AS [p]     ON  [IDX].[object_id] =  [p].[object_id]  AND [IDX].[index_id] = [p].[index_id]
      INNER JOIN (
                  SELECT
                    [statz].[object_id], [statz].[index_id], SUM([statz].[row_count]) AS [ROWS],
                    CONVERT(NUMERIC(19,3), CONVERT(NUMERIC(19,3), SUM([statz].[in_row_reserved_page_count]+[statz].[lob_reserved_page_count]+[statz].[row_overflow_reserved_page_count]))/CONVERT(NUMERIC(19,3), 128)) AS [SizeMB]
                  FROM [tempdb].[sys].[dm_db_partition_stats] AS [statz]
                  GROUP BY [statz].[object_id], [statz].[index_id]
                 ) AS [partitions]
        ON  [IDX].[object_id]=[partitions].[object_id]
        AND [IDX].[index_id]=[partitions].[index_id]
    CROSS APPLY (
                 SELECT
                   LEFT([Index_Columns].[index_columns_key], LEN([Index_Columns].[index_columns_key])-1) AS [index_columns_key],
                  LEFT([Index_Columns].[index_columns_include], LEN([Index_Columns].[index_columns_include])-1) AS [index_columns_include]
                 FROM
                      (
                       SELECT
                              (
                              SELECT QUOTENAME([colz].[name]) + CASE WHEN [IXCOLS].[is_descending_key] = 0 THEN ' asc' ELSE ' desc' END + ',' + ' '
                               FROM [tempdb].[sys].[index_columns] AS [IXCOLS]
                                 INNER JOIN [tempdb].[sys].[columns] AS [colz]
                                   ON  [IXCOLS].[column_id]   = [colz].[column_id]
                                   AND [IXCOLS].[object_id] = [colz].[object_id]
                               WHERE [IXCOLS].[is_included_column] = 0
                                 AND [IDX].[object_id] = [IXCOLS].[object_id]
                                 AND [IDX].[index_id] = [IXCOLS].[index_id]
                               ORDER BY [IXCOLS].[key_ordinal]
                               FOR XML PATH('')
                              ) AS [index_columns_key],
                             (
                             SELECT QUOTENAME([colz].[name]) + ',' + ' '
                              FROM [tempdb].[sys].[index_columns] AS [IXCOLS]
                                INNER JOIN [tempdb].[sys].[columns] AS [colz]
                                  ON  [IXCOLS].[column_id]   = [colz].[column_id]
                                  AND [IXCOLS].[object_id] = [colz].[object_id]
                              WHERE [IXCOLS].[is_included_column] = 1
                                AND [IDX].[object_id] = [IXCOLS].[object_id]
                                AND [IDX].[index_id] = [IXCOLS].[index_id]
                              ORDER BY [IXCOLS].[index_column_id]
                              FOR XML PATH('')
                             ) AS [index_columns_include]
                      ) AS [Index_Columns]
                ) AS [Index_Columns]
    WHERE [SCH].[name]  LIKE CASE
                                     WHEN @SCHEMANAME = '' COLLATE SQL_Latin1_General_CP1_CI_AS
                                     THEN [SCH].[name]
                                     ELSE @SCHEMANAME
                                   END
    AND [objz].[name] LIKE CASE
                                  WHEN @TBLNAME = ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                                  THEN [objz].[name]
                                  ELSE @TBLNAME
                                END
    ORDER BY
      [SCH].[name],
      [objz].[name],
      [IDX].[name];
--@Results2 table has both PK,s Uniques and indexes in thme...pull them out for adding to funal results:
  SET @CONSTRAINTSQLS = '' COLLATE SQL_Latin1_General_CP1_CI_AS;
  SET @INDEXSQLS      = '' COLLATE SQL_Latin1_General_CP1_CI_AS;
--##############################################################################
--constraints
--##############################################################################
  SELECT @CONSTRAINTSQLS = @CONSTRAINTSQLS
         + CASE
             WHEN [is_primary_key] = 1 OR [is_unique] = 1
             THEN @vbCrLf
                  + 'CONSTRAINT   '  COLLATE SQL_Latin1_General_CP1_CI_AS + QUOTENAME([index_name]) + ' '
                  + SPACE(@STRINGLEN - LEN([index_name]))
                  + CASE
                      WHEN [is_primary_key] = 1
                      THEN ' PRIMARY KEY '  COLLATE SQL_Latin1_General_CP1_CI_AS
                      ELSE CASE
                             WHEN [is_unique] = 1
                             THEN ' UNIQUE      '     COLLATE SQL_Latin1_General_CP1_CI_AS
                             ELSE ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                           END
                    END
                  + [type_desc]
                  + CASE
                      WHEN [type_desc]='NONCLUSTERED'
                      THEN ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                      ELSE '   '
                    END
                  + ' (' + [index_columns_key] + ')'
                  + CASE
                      WHEN [index_columns_include] <> '---'
                      THEN ' INCLUDE (' + [index_columns_include] + ')'
                      ELSE ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                    END
                  + CASE
                      WHEN [has_filter] = 1
                      THEN ' ' + [filter_definition]
                      ELSE ' '
                    END
                  + CASE WHEN [fill_factor] <> 0 OR [CurrentCompression] <> 'NONE'
                  THEN ' WITH (' + CASE
                                    WHEN [fill_factor] <> 0
                                    THEN 'FILLFACTOR = ' + CONVERT(VARCHAR(30),[fill_factor])
                                    ELSE ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                                  END
                                + CASE
                                    WHEN [fill_factor] <> 0  AND [CurrentCompression] <> 'NONE' THEN ',DATA_COMPRESSION = ' + [CurrentCompression] + ' '
                                    WHEN [fill_factor] <> 0  AND [CurrentCompression]  = 'NONE' THEN ''
                                    WHEN [fill_factor]  = 0  AND [CurrentCompression] <> 'NONE' THEN 'DATA_COMPRESSION = ' + [CurrentCompression] + ' '
                                    ELSE ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                                  END
                                  + ')'
                  ELSE ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                  END
             ELSE '' COLLATE SQL_Latin1_General_CP1_CI_AS
           END + ','
  FROM @Results2
  WHERE [type_desc] != 'HEAP'
    AND [is_primary_key] = 1
    OR  [is_unique] = 1
  ORDER BY
    [is_primary_key] DESC,
    [is_unique] DESC;
--##############################################################################
--indexes
--##############################################################################
  SELECT @INDEXSQLS = @INDEXSQLS
         + CASE
             WHEN [is_primary_key] = 0 OR [is_unique] = 0
             THEN @vbCrLf
                  + 'CREATE '  COLLATE SQL_Latin1_General_CP1_CI_AS + [type_desc] + ' INDEX '  COLLATE SQL_Latin1_General_CP1_CI_AS + QUOTENAME([index_name]) + ' ' COLLATE SQL_Latin1_General_CP1_CI_AS
                  + @vbCrLf
                  + '   ON '  COLLATE SQL_Latin1_General_CP1_CI_AS
                  + QUOTENAME([SCHEMA_NAME]) + '.' + QUOTENAME([OBJECT_NAME])
                  + CASE
                        WHEN [CurrentCompression] = 'COLUMNSTORE'  COLLATE SQL_Latin1_General_CP1_CI_AS
                        THEN ' ('  COLLATE SQL_Latin1_General_CP1_CI_AS+ [index_columns_include] + ')'  COLLATE SQL_Latin1_General_CP1_CI_AS
                        ELSE ' ('  COLLATE SQL_Latin1_General_CP1_CI_AS+ [index_columns_key] + ')' COLLATE SQL_Latin1_General_CP1_CI_AS
                    END
                  + CASE
                      WHEN [CurrentCompression] = 'COLUMNSTORE'  COLLATE SQL_Latin1_General_CP1_CI_AS
                      THEN ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                      ELSE
                        CASE
                     WHEN [index_columns_include] <> '---'
                     THEN @vbCrLf + '   INCLUDE ('  COLLATE SQL_Latin1_General_CP1_CI_AS + [index_columns_include] + ')'  COLLATE SQL_Latin1_General_CP1_CI_AS
                     ELSE ''   COLLATE SQL_Latin1_General_CP1_CI_AS
                   END
                    END
                  --2008 filtered indexes syntax
                  + CASE
                      WHEN [has_filter] = 1
                      THEN @vbCrLf + '   WHERE '  COLLATE SQL_Latin1_General_CP1_CI_AS + [filter_definition]
                      ELSE ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                    END
                  + CASE WHEN [fill_factor] <> 0 OR [CurrentCompression] <> 'NONE'  COLLATE SQL_Latin1_General_CP1_CI_AS
                  THEN ' WITH ('  COLLATE SQL_Latin1_General_CP1_CI_AS + CASE
                                    WHEN [fill_factor] <> 0
                                    THEN 'FILLFACTOR = '  COLLATE SQL_Latin1_General_CP1_CI_AS + CONVERT(VARCHAR(30),[fill_factor])
                                    ELSE ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                                  END
                                + CASE
                                    WHEN [fill_factor] <> 0  AND [CurrentCompression] <> 'NONE'  COLLATE SQL_Latin1_General_CP1_CI_AS THEN ',DATA_COMPRESSION = ' COLLATE SQL_Latin1_General_CP1_CI_AS + [CurrentCompression] + ' '
                                    WHEN [fill_factor] <> 0  AND [CurrentCompression]  = 'NONE'  COLLATE SQL_Latin1_General_CP1_CI_AS THEN ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                                    WHEN [fill_factor]  = 0  AND [CurrentCompression] <> 'NONE'  COLLATE SQL_Latin1_General_CP1_CI_AS THEN 'DATA_COMPRESSION = '  COLLATE SQL_Latin1_General_CP1_CI_AS+ [CurrentCompression] + ' '
                                    ELSE ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                                  END
                                  + ')' COLLATE SQL_Latin1_General_CP1_CI_AS
                  ELSE ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                  END
           END
  FROM @Results2
  WHERE [type_desc] != 'HEAP'
    AND [is_primary_key] = 0
    AND [is_unique] = 0
  ORDER BY
    [is_primary_key] DESC,
    [is_unique] DESC;
  IF @INDEXSQLS <> '' COLLATE SQL_Latin1_General_CP1_CI_AS
    SET @INDEXSQLS = @vbCrLf + 'GO'  COLLATE SQL_Latin1_General_CP1_CI_AS+ @vbCrLf + @INDEXSQLS;
--##############################################################################
--CHECK Constraints
--##############################################################################
  SET @CHECKCONSTSQLS = '';
  SELECT
    @CHECKCONSTSQLS = @CHECKCONSTSQLS
    + @vbCrLf
    + ISNULL('CONSTRAINT   ' + QUOTENAME([objz].[name]) + ' '
    + SPACE(@STRINGLEN - LEN([objz].[name]))
    + ' CHECK ' + ISNULL([CHECKS].[definition],'')
    + ',','')
  FROM [tempdb].[sys].[objects] AS [objz]
    INNER JOIN [tempdb].[sys].[check_constraints] AS [CHECKS] ON [objz].[object_id] = [CHECKS].[object_id]
  WHERE [objz].[type] = 'C'
    AND [objz].[parent_object_id] = @TABLE_ID;
--##############################################################################
--FOREIGN KEYS
--##############################################################################
  SET @FKSQLS = '' ;
    SELECT
    @FKSQLS=@FKSQLS
    + @vbCrLf + [MyAlias].[Command] FROM
(
SELECT
  DISTINCT
  --FK must be added AFTER the PK/unique constraints are added back.
  850 AS [ExecutionOrder],
  'CONSTRAINT '
  + QUOTENAME([conz].[name])
  + ' FOREIGN KEY ('
  + [ChildCollection].[ChildColumns]
  + ') REFERENCES '
  + QUOTENAME(SCHEMA_NAME([conz].[schema_id]))
  + '.'
  + QUOTENAME(OBJECT_NAME([conz].[referenced_object_id]))
  + ' (' + [ParentCollection].[ParentColumns]
  + ') '
   +  CASE [conz].[update_referential_action]
                                        WHEN 0 THEN '' --' ON UPDATE NO ACTION '
                                        WHEN 1 THEN ' ON UPDATE CASCADE '
                                        WHEN 2 THEN ' ON UPDATE SET NULL '
                                        ELSE ' ON UPDATE SET DEFAULT '
                                    END
                  + CASE [conz].[delete_referential_action]
                                        WHEN 0 THEN '' --' ON DELETE NO ACTION '
                                        WHEN 1 THEN ' ON DELETE CASCADE '
                                        WHEN 2 THEN ' ON DELETE SET NULL '
                                        ELSE ' ON DELETE SET DEFAULT '
                                    END
                  + CASE [conz].[is_not_for_replication]
                        WHEN 1 THEN ' NOT FOR REPLICATION '
                        ELSE ''
                    END
  + ',' AS [Command]
FROM   [sys].[foreign_keys] AS [conz]
       INNER JOIN [sys].[foreign_key_columns] AS [colz]
         ON [conz].[object_id] = [colz].[constraint_object_id]

       INNER JOIN (--gets my child tables column names
SELECT
 [conz].[name],
 --technically, FK's can contain up to 16 columns, but real life is often a single column. coding here is for all columns
 [ChildColumns] = STUFF((SELECT
                         ',' + QUOTENAME([REFZ].[name])
                       FROM   [sys].[foreign_key_columns] AS [fkcolz]
                              INNER JOIN [sys].[columns] AS [REFZ]
                                ON [fkcolz].[parent_object_id] = [REFZ].[object_id]
                                   AND [fkcolz].[parent_column_id] = [REFZ].[column_id]
                       WHERE [fkcolz].[parent_object_id] = [conz].[parent_object_id]
                           AND [fkcolz].[constraint_object_id] = [conz].[object_id]
                         ORDER  BY
                        [fkcolz].[constraint_column_id]
                       FOR XML PATH(''), TYPE).[value]('.','varchar(max)'),1,1,'')
FROM   [sys].[foreign_keys] AS [conz]
      INNER JOIN [sys].[foreign_key_columns] AS [colz]
        ON [conz].[object_id] = [colz].[constraint_object_id]
 WHERE [conz].[parent_object_id]= @TABLE_ID
GROUP  BY
[conz].[name],
[conz].[parent_object_id],--- without GROUP BY multiple rows are returned
 [conz].[object_id]
    ) AS [ChildCollection]
         ON [conz].[name] = [ChildCollection].[name]
       INNER JOIN (--gets the parent tables column names for the FK reference
                  SELECT
                     [conz].[name],
                     [ParentColumns] = STUFF((SELECT
                                              ',' + [REFZ].[name]
                                            FROM   [sys].[foreign_key_columns] AS [fkcolz]
                                                   INNER JOIN [sys].[columns] AS [REFZ]
                                                     ON [fkcolz].[referenced_object_id] = [REFZ].[object_id]
                                                        AND [fkcolz].[referenced_column_id] = [REFZ].[column_id]
                                            WHERE  [fkcolz].[referenced_object_id] = [conz].[referenced_object_id]
                                              AND [fkcolz].[constraint_object_id] = [conz].[object_id]
                                            ORDER BY [fkcolz].[constraint_column_id]
                                            FOR XML PATH(''), TYPE).[value]('.','varchar(max)'),1,1,'')
                   FROM   [sys].[foreign_keys] AS [conz]
                          INNER JOIN [sys].[foreign_key_columns] AS [colz]
                            ON [conz].[object_id] = [colz].[constraint_object_id]
                           -- AND colz.parent_column_id
                   GROUP  BY
                    [conz].[name],
                    [conz].[referenced_object_id],--- without GROUP BY multiple rows are returned
                    [conz].[object_id]
                  ) AS [ParentCollection]
         ON [conz].[name] = [ParentCollection].[name]
)AS [MyAlias];
--##############################################################################
--RULES
--##############################################################################
  SET @RULESCONSTSQLS = ''  COLLATE SQL_Latin1_General_CP1_CI_AS;
  SELECT
    @RULESCONSTSQLS = @RULESCONSTSQLS
    + ISNULL(
             @vbCrLf
             + 'if not exists(SELECT [name] FROM tempdb.sys.objects WHERE TYPE=''R'' AND schema_id = '  COLLATE SQL_Latin1_General_CP1_CI_AS
             + CONVERT(VARCHAR(30),[objz].[schema_id])
             + ' AND [name] = '''  COLLATE SQL_Latin1_General_CP1_CI_AS
             + QUOTENAME(OBJECT_NAME([colz].[rule_object_id]))
             + ''')'  COLLATE SQL_Latin1_General_CP1_CI_AS
             + @vbCrLf
             + [MODS].[definition]  + @vbCrLf
             + 'GO'  COLLATE SQL_Latin1_General_CP1_CI_AS +  @vbCrLf
             + 'EXEC sp_binderule  '  COLLATE SQL_Latin1_General_CP1_CI_AS
             + QUOTENAME([objz].[name])
             + ', '''  COLLATE SQL_Latin1_General_CP1_CI_AS
             + QUOTENAME(OBJECT_NAME([colz].[object_id]))
             + '.'  COLLATE SQL_Latin1_General_CP1_CI_AS + QUOTENAME([colz].[name])
             + ''''  COLLATE SQL_Latin1_General_CP1_CI_AS
             + @vbCrLf
             + 'GO' ,''  COLLATE SQL_Latin1_General_CP1_CI_AS)
  FROM [tempdb].[sys].[columns] [colz]
    INNER JOIN [tempdb].[sys].[objects] [objz]
      ON [objz].[object_id] = [colz].[object_id]
    INNER JOIN [tempdb].[sys].[sql_modules] AS [MODS]
      ON [colz].[rule_object_id] = [MODS].[object_id]
  WHERE [colz].[rule_object_id] <> 0
    AND [colz].[object_id] = @TABLE_ID;
--##############################################################################
--TRIGGERS
--##############################################################################
  SET @TRIGGERSTATEMENT = '';
  SELECT
    @TRIGGERSTATEMENT = @TRIGGERSTATEMENT +  @vbCrLf + [MODS].[definition] + @vbCrLf + 'GO'
  FROM [tempdb].[sys].[sql_modules] AS [MODS]
  WHERE [MODS].[object_id] IN(SELECT
                         [objz].[object_id]
                       FROM [tempdb].[sys].[objects] AS [objz]
                       WHERE [objz].[type] = 'TR'
                       AND [objz].[parent_object_id] = @TABLE_ID);
  IF @TRIGGERSTATEMENT <> ''  COLLATE SQL_Latin1_General_CP1_CI_AS
    SET @TRIGGERSTATEMENT = @vbCrLf + 'GO'  COLLATE SQL_Latin1_General_CP1_CI_AS + @vbCrLf + @TRIGGERSTATEMENT;
--##############################################################################
--NEW SECTION QUERY ALL EXTENDED PROPERTIES
--##############################################################################
  SET @EXTENDEDPROPERTIES = ''  COLLATE SQL_Latin1_General_CP1_CI_AS;
  SELECT  @EXTENDEDPROPERTIES =
          @EXTENDEDPROPERTIES + @vbCrLf +
         'EXEC tempdb.sys.sp_addextendedproperty
          @name = N'''  COLLATE SQL_Latin1_General_CP1_CI_AS
          + [name]
          + ''', @value = N'''  COLLATE SQL_Latin1_General_CP1_CI_AS
          + REPLACE(CONVERT(VARCHAR(MAX),[value]),'''','''''') + ''',
          @level0type = N''SCHEMA'', @level0name = '  COLLATE SQL_Latin1_General_CP1_CI_AS
          + QUOTENAME(@SCHEMANAME + ',
          @level1type = N''TABLE'', @level1name = ['  COLLATE SQL_Latin1_General_CP1_CI_AS
          + @TBLNAME)
          + '];' COLLATE SQL_Latin1_General_CP1_CI_AS
 --SELECT objtype, objname, name, value
  FROM [sys].[fn_listextendedproperty] (NULL, 'schema', @SCHEMANAME, 'table', @TBLNAME, NULL, NULL);
  --OMacoder suggestion for column extended properties http://www.sqlservercentral.com/Forums/FindPost1651606.aspx
  SELECT @EXTENDEDPROPERTIES =
         @EXTENDEDPROPERTIES + @vbCrLf +
         'EXEC sys.sp_addextendedproperty
         @name = N'''  COLLATE SQL_Latin1_General_CP1_CI_AS
         + [name]
         + ''', @value = N'''  COLLATE SQL_Latin1_General_CP1_CI_AS
         + REPLACE(CONVERT(VARCHAR(MAX),[value]),'''','''''')
         + ''',
         @level0type = N''SCHEMA'', @level0name = '  COLLATE SQL_Latin1_General_CP1_CI_AS
         + QUOTENAME(@SCHEMANAME) + ',
         @level1type = N''TABLE'', @level1name = '  COLLATE SQL_Latin1_General_CP1_CI_AS
         + QUOTENAME(@TBLNAME) + ',
         @level2type = N''COLUMN'', @level2name = '  COLLATE SQL_Latin1_General_CP1_CI_AS
         + QUOTENAME([objname]) + ';' COLLATE SQL_Latin1_General_CP1_CI_AS
  --SELECT objtype, objname, name, value
  FROM [sys].[fn_listextendedproperty] (NULL, 'schema', @SCHEMANAME, 'table', @TBLNAME, 'column', NULL);
  IF @EXTENDEDPROPERTIES <> '' COLLATE SQL_Latin1_General_CP1_CI_AS
    SET @EXTENDEDPROPERTIES = @vbCrLf + 'GO' COLLATE SQL_Latin1_General_CP1_CI_AS + @vbCrLf + @EXTENDEDPROPERTIES;
--##############################################################################
--FINAL CLEANUP AND PRESENTATION
--##############################################################################
--at this point, there is a trailing comma, or it blank
  SELECT
    @FINALSQL = @FINALSQL
                + @CONSTRAINTSQLS
                + @CHECKCONSTSQLS
                + @FKSQLS;
--note that this trims the trailing comma from the end of the statements
  SET @FINALSQL = SUBSTRING(@FINALSQL,1,LEN(@FINALSQL) -1) ;
  SET @FINALSQL = @FINALSQL + ')'  COLLATE SQL_Latin1_General_CP1_CI_AS + @vbCrLf ;
  SET @input = @vbCrLf
       + @FINALSQL
       + @INDEXSQLS
       + @RULESCONSTSQLS
       + @TRIGGERSTATEMENT
       + @EXTENDEDPROPERTIES;
  SELECT @input AS [Item];

  RETURN 0;
END;
go

IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[sp_GetDDLa]') AND type IN ('P', 'PC', 'RF', 'X'))
DROP PROCEDURE [sp_GetDDLa]
GO
--#################################################################################################
-- Real World DBA Toolkit version 5.08 Lowell Izaguirre lowell@stormrage.com
--#################################################################################################
-- USAGE: exec sp_GetDDLa GMACT
--   or   exec sp_GetDDLa 'bob.example'
--   or   exec sp_GetDDLa '[schemaname].[tablename]'
--   or   exec sp_GetDDLa #temp
--#################################################################################################
-- copyright 2004-2018 by Lowell Izaguirre scripts*at*stormrage.com all rights reserved.
--developer utility function added by Lowell, used in SQL Server Management Studio 
-- http://www.stormrage.com/SQLStuff/sp_GetDDL_Latest.txt
--Purpose: Script Any Table, Temp Table or Object(Procedure Function Synonym View Table Trigger)
--#################################################################################################
-- see the thread here for lots of details: http://www.sqlservercentral.com/Forums/Topic751783-566-7.aspx
-- You can use this however you like...this script is not rocket science, but it took a bit of work to create.
-- the only thing that I ask
-- is that if you adapt my procedure or make it better, to simply send me a copy of it,
-- so I can learn from the things you've enhanced.The feedback you give will be what makes
-- it worthwhile to me, and will be fed back to the SQL community.
-- add this to your toolbox of helpful scripts.
--#################################################################################################
--
-- V300  uses String concatination and sys.tables instead of a cursor
-- V301  enhanced 07/31/2009 to include extended properties definitions
-- V302  fixes an issue where the schema is created , ie 'bob', but no user named 'bob' owns the schema, so the table is not found
-- V303  fixes an issue where all rules are appearing, instead of jsut the rule related to a column
-- V304  testing whether vbCrLf is better than just CHAR(13), some formatting cleanup with GO statements
--       also fixed an issue with the conversion from syscolumns to sys.columns, max-length is only field we need, not [precision]
-- V305  user feedback helped me find that the type_name function should call user_type_id instead of system_type_id
--       also fixed issue where identity definition missing from numeric/decimal definition
-- V306  fixes the computed columns definition that got broken/removed somehow in V300
--       also formatting when decimal is not an identity
-- V307  fixes bug identified by David Griffiths-491597 from SSC where the  @TABLE_ID
--       is reselected, but without it's schema  , potentially selecting the wrong table
--       also fixed is the missing size definition for varbinary, also found by David Griffith
-- V308  abtracted all SQLs to use Table Alaises
--       added logic to script a temp table.
--       added warning about possibly not being marked as system object.
-- V309  added logic based on feedback from Vincent Wylenzek @SSC to return the definition from sys.sql_modules for
--       any object like procedure/view/function/trigger, and not just a table. 
--       note previously, if you pointed sp_GetDDLa at a view, it returned the view definition as a table...
--       now it will return the view definition instead.
-- V309a returns multi row recordset, one line per record 
-- V310a fixed the commented out code related to collation identified by moadh.bs @SSC
--       changed the DEFAULT definitions to not include the default name.
-- V310b Added PERSISTED to calculated columns where applicable
-- V310b fixed COLLATE statement for temp tables
-- V310c fixed NVARCHAR size misreported as doubled.
-- V311  fixed issue where indexes did not identify if the column was ASC or DESC found by nikus @ SSC
-- V311a fixed issue where indexes did not identify if the index was CLUSTERED or NONCLUSTERED found by nikus @ SSC 02/22/2013
-- V312  got rid of all upper casing, and allowing all scripts to generate the exact object names in cases of case sensitive databases.
--       now using the case sensitive name of the table passed: so of you did 'exec sp_getDDLA invoicedocs , it might return the script for InvoiceDocs, as that is how it is spelled in sys.objects.
--       added if exists(drop table/procedure/function) statement to the scripting automatically.
--       toggled the commented out code to list any default constraints by name, hopefully to be more accurate..
--       formatting of index statements to be multi line for better readability
--V314   03/30/2015
--       did i mention this scripts out temp tables too? sp_getDDLa #tmp
--       scripts any object:table,#temptable procedure, function, view or trigger
--       added ability to script synonyms
--       moved logic for REAL datatype to fix error when scripting real columns
--       added OmaCoders suggestion to script column extended properties as well.
--       added matt_slack suggestion to script schemaname as part of index portion of script.
--       minor script cleanup to use QUOTENAME insead of concatenating square brackets.
--       changed compatibility to 2008 and above only, now filtered idnexes with WHERE statmeents script correctly
--       foreign key tables and columns  in script now quotenamed to account for spaces in names; previously an error for Applciation ID instead of [Application ID]
--V315   Fixes Aliases and column names that prevented Case Sensitive collations from working.
--       Adds code if the procedure scripted is a system object
--       index scripts featuring filtered indexes is now included
--       index scripts now include filegroup name and compression settings
--       foreign key casecade delete/update settings now included as identified by Alberto aserio@SSC)
--       Fixes related to scripting extended events  as identified by Alberto aserio@SSC)
--V316   Fixes Identified 07/27/2016 by mlm( m.martinelli@SSC)
--       Added logic  resolving error when custom data type are defined using name greather than 16 char.
--       Added handling for data types: binary, datetime2, datetimeoffset, time
--       Added Set Based logic for Handling Fixed FOREIGN KEYS handling when one foreign key is define on more then one field
--       Added SPARSE column property
--V317   Fixes Identified 03/30/2017 by Lowell
--       Scripting of Foreign key column(s) are now quotenamed
--       Scripting column store indexes was broken, now fixed for column store indexes
--V318   Fixes Identified 02/14/2018 by Lowell
--       Scripting of with collation added/required for scripting SharePoint/ReportServer , or databases with non standard collations
--       Scripting enhanced to definitively handle case sensitive collations as well.
--V319   Adding logic for Temporal Tables, to grab their auto nistory tables
--       first attempt for partitioned tables, to get the columns correctly on the partition scheme
--V320   TIM C: Fixed a bug with generating FKs when the table being generated is in a different schema than the FK refernce table
-- DROP PROCEDURE [dbo].[sp_GetDDLa]
-- DROP PROCEDURE [dbo].[sp_GetDDLa]
--#############################################################################
--if you are going to put this in MASTER, and want it to be able to query
--each database's sys.indexes, you MUST mark it as a system procedure:
--EXECUTE sp_ms_marksystemobject 'sp_GetDDLa'
--#############################################################################
CREATE PROCEDURE [dbo].[sp_GetDDLa]
  @TBL                VARCHAR(255)
AS
BEGIN
  SET NOCOUNT ON;
  DECLARE     @TBLNAME                VARCHAR(200),
              @SCHEMANAME             VARCHAR(255),
              @STRINGLEN              INT,
              @TABLE_ID               INT,
              @FINALSQL               VARCHAR(MAX),
              @CONSTRAINTSQLS         VARCHAR(MAX),
              @CHECKCONSTSQLS         VARCHAR(MAX),
              @RULESCONSTSQLS         VARCHAR(MAX),
              @FKSQLS                 VARCHAR(MAX),
              @TRIGGERSTATEMENT       VARCHAR(MAX),
              @EXTENDEDPROPERTIES     VARCHAR(MAX),
              @INDEXSQLS              VARCHAR(MAX),
              @MARKSYSTEMOBJECT       VARCHAR(MAX),
              @vbCrLf                 CHAR(2),
              @ISSYSTEMOBJECT         INT,
              @PROCNAME               VARCHAR(256),
              @input                  VARCHAR(MAX),
              @ObjectTypeFound        VARCHAR(255),
              @ObjectDataTypeLen      INT,
              --V3.20 additions
              @WithStatement          VARCHAR(MAX),
              @FileGroupStatement     VARCHAR(MAX),
              @PartitioningStatement  VARCHAR(MAX),
              @TemporalStatement      VARCHAR(MAX);
--##############################################################################
-- INITIALIZE
--##############################################################################
  SET @input = '';
  --new code: determine whether this proc is marked as a system proc with sp_ms_marksystemobject,
  --which flips the is_ms_shipped bit in sys.objects
    SELECT @ISSYSTEMOBJECT = ISNULL([is_ms_shipped],0),@PROCNAME = ISNULL([name],'sp_GetDDLa') FROM [sys].[objects] WHERE [object_id] = @@PROCID;
  IF @ISSYSTEMOBJECT IS NULL 
    SELECT @ISSYSTEMOBJECT = ISNULL([is_ms_shipped],0),@PROCNAME = ISNULL([name],'sp_GetDDLa') FROM [master].[sys].[objects] WHERE [object_id] = @@PROCID;
  IF @ISSYSTEMOBJECT IS NULL 
    SET @ISSYSTEMOBJECT = 0;  
  IF @PROCNAME IS NULL
    SET @PROCNAME = 'sp_GetDDLa';
  --SET @TBL =  '[DBO].[WHATEVER1]'
  --does the tablename contain a schema?
  SET @vbCrLf =  CHAR(10);
  SELECT @SCHEMANAME = ISNULL(PARSENAME(@TBL,2),'dbo') ,
         @TBLNAME    = PARSENAME(@TBL,1);
  SELECT
    @TBLNAME    = [objz].[name],
    @TABLE_ID   = [objz].[object_id]
  FROM [sys].[objects] AS [objz]
  WHERE [objz].[type]          IN ('S','U')
    AND [objz].[name]          <>  'dtproperties'
    AND [objz].[name]           =  @TBLNAME
    AND [objz].[schema_id] =  SCHEMA_ID(@SCHEMANAME) ;
 SELECT @ObjectDataTypeLen = MAX(LEN([name])) FROM [sys].[types];
--##############################################################################
-- Check If TEMP TableName is Valid
--##############################################################################
  IF LEFT(@TBLNAME,1) = '#'  COLLATE SQL_Latin1_General_CP1_CI_AS
    BEGIN
      PRINT '--TEMP TABLE  ' + QUOTENAME(@TBLNAME) + '  FOUND';
      IF OBJECT_ID('tempdb..' + QUOTENAME(@TBLNAME)) IS NOT NULL
        BEGIN
          PRINT '--GOIN TO TEMP PROCESSING';
          GOTO TEMPPROCESS;
        END;
    END;
  ELSE
    BEGIN
      PRINT '--Non-Temp Table, ' + QUOTENAME(@TBLNAME) + ' continue Processing';
    END;
--##############################################################################
-- Check If TableName is Valid
--##############################################################################
  IF ISNULL(@TABLE_ID,0) = 0
    BEGIN
      --V309 code: see if it is an object and not a table.
      SELECT
        @TBLNAME    = [objz].[name],
        @TABLE_ID   = [objz].[object_id],
        @ObjectTypeFound = [objz].[type_desc]
      FROM [sys].[objects] AS [objz]
      --WHERE [type_desc]     IN('SQL_STORED_PROCEDURE','VIEW','SQL_TRIGGER','AGGREGATE_FUNCTION','SQL_INLINE_TABLE_VALUED_FUNCTION','SQL_TABLE_VALUED_FUNCTION','SQL_SCALAR_FUNCTION','SYNONYMN')
      WHERE [objz].[type]          IN ('P','V','TR','AF','IF','FN','TF','SN')
        AND [objz].[name]          <>  'dtproperties'
        AND [objz].[name]           =  @TBLNAME
        AND [objz].[schema_id] =  SCHEMA_ID(@SCHEMANAME) ;
      IF ISNULL(@TABLE_ID,0) <> 0  
        BEGIN
          --adding a drop statement.
          --adding a sp_ms_marksystemobject if needed
          SELECT @MARKSYSTEMOBJECT = CASE 
                                       WHEN [objz].[is_ms_shipped] = 1 
                                       THEN '
GO
--#################################################################################################
--Mark as a system object
EXECUTE sp_ms_marksystemobject  ''' + QUOTENAME(@SCHEMANAME) +'.' + QUOTENAME(@TBLNAME) + '''
--#################################################################################################
' 
                                       ELSE '
GO
' 
                                     END 
          FROM [sys].[objects] AS [objz] 
          WHERE [objz].[object_id] = @TABLE_ID;
          --adding a drop statement.
          IF @ObjectTypeFound = 'SYNONYM'  COLLATE SQL_Latin1_General_CP1_CI_AS
            BEGIN
               SELECT @FINALSQL = 
                'IF EXISTS(SELECT * FROM sys.synonyms WHERE name = ''' 
                                + [name] 
                                + ''''
                                + ' AND base_object_name <> ''' + [base_object_name] + ''')'
                                + @vbCrLf
                                + '  DROP SYNONYM ' + QUOTENAME([name]) + ''
                                + @vbCrLf
                                +'GO'
                                + @vbCrLf
                                +'IF NOT EXISTS(SELECT * FROM sys.synonyms WHERE name = ''' 
                                + [name] 
                                + ''')'
                                + @vbCrLf
                                + 'CREATE SYNONYM ' + QUOTENAME([name]) + ' FOR ' + [base_object_name] +';'
                                FROM [sys].[synonyms]
                                WHERE  [name]   =  @TBLNAME
                                AND [schema_id] =  SCHEMA_ID(@SCHEMANAME);
            END;
          ELSE
            BEGIN
          SELECT @FINALSQL = 
          'IF OBJECT_ID(''' + QUOTENAME(@SCHEMANAME) + '.' + QUOTENAME(@TBLNAME) + ''') IS NOT NULL ' + @vbCrLf
          + 'DROP ' + CASE 
                        WHEN [objz].[type] IN ('P')
                        THEN ' PROCEDURE '
                        WHEN [objz].[type] IN ('V')
                        THEN ' VIEW      '
                        WHEN [objz].[type] IN ('TR')
                        THEN ' TRIGGER   '
                        ELSE ' FUNCTION  '
                      END 
                      + QUOTENAME(@SCHEMANAME) + '.' + QUOTENAME(@TBLNAME) + ' ' + @vbCrLf + 'GO' + @vbCrLf
          + [def].[definition] + @MARKSYSTEMOBJECT
          FROM [sys].[objects] AS [objz] 
            INNER JOIN [sys].[sql_modules] AS [def]
              ON [objz].[object_id] = [def].[object_id]
          WHERE [objz].[type]          IN ('P','V','TR','AF','IF','FN','TF')
            AND [objz].[name]          <>  'dtproperties'
            AND [objz].[name]           =  @TBLNAME
            AND [objz].[schema_id] =  SCHEMA_ID(@SCHEMANAME) ;
            END;
          SET @input = @FINALSQL;  
            --ten years worth of days from todays date:
         ;WITH [E01]([N]) AS (SELECT 1 UNION ALL SELECT 1 UNION ALL
                          SELECT 1 UNION ALL SELECT 1 UNION ALL
                          SELECT 1 UNION ALL SELECT 1 UNION ALL
                          SELECT 1 UNION ALL SELECT 1 UNION ALL
                          SELECT 1 UNION ALL SELECT 1), --         10 or 10E01 rows
               [E02]([N]) AS (SELECT 1 FROM [E01] AS [a], [E01] AS [b]),  --        100 or 10E02 rows
               [E04]([N]) AS (SELECT 1 FROM [E02] AS [a], [E02] AS [b]),  --     10,000 or 10E04 rows
               [E08]([N]) AS (SELECT 1 FROM [E04] AS [a], [E04] AS [b]),  --100,000,000 or 10E08 rows
               --E16(N) AS (SELECT 1 FROM E08 a, E08 b),  --10E16 or more rows than you'll EVER need,
               [Tally]([N]) AS (SELECT ROW_NUMBER() OVER (ORDER BY [E08].[N]) FROM [E08]),
             [ItemSplit](
                       [ItemOrder],
                       [Item]
                      ) AS (
                            SELECT [Tally].[N],
                              SUBSTRING(@vbCrLf + @input + @vbCrLf,[Tally].[N] + DATALENGTH(@vbCrLf),CHARINDEX(@vbCrLf,@vbCrLf + @input + @vbCrLf,[Tally].[N] + DATALENGTH(@vbCrLf)) - [Tally].[N] - DATALENGTH(@vbCrLf))
                            FROM [Tally]
                            WHERE [Tally].[N] < DATALENGTH(@vbCrLf + @input)
                            --WHERE N < DATALENGTH(@vbCrLf + @input) -- REMOVED added @vbCrLf
                              AND SUBSTRING(@vbCrLf + @input + @vbCrLf,[Tally].[N],DATALENGTH(@vbCrLf)) = @vbCrLf --Notice how we find the delimiter
                           )
        SELECT
          --row_number() over (order by ItemOrder) as ItemID,
          [ItemSplit].[Item]
        FROM [ItemSplit];
         RETURN 0;
        END;
      ELSE
        BEGIN
        SET @FINALSQL = 'Object ' + QUOTENAME(@SCHEMANAME) + '.' + QUOTENAME(@TBLNAME) + ' does not exist in Database ' + QUOTENAME(DB_NAME())   + ' '  
                      + CASE 
                          WHEN @ISSYSTEMOBJECT = 0 THEN @vbCrLf + ' (also note that ' + @PROCNAME + ' is not marked as a system proc and cross db access to sys.tables will fail.)'
                          ELSE ''
                        END;
      IF LEFT(@TBLNAME,1) = '#' 
        SET @FINALSQL = @FINALSQL + ' OR in The tempdb database.';
      SELECT @FINALSQL AS [Item];
      RETURN 0;
        END;  
      
    END;
--##############################################################################
-- Valid Table, Continue Processing
--##############################################################################
--Is this a SYSTEM versioned TABLE?
SELECT @FINALSQL = 
     CASE 
       WHEN [tabz].[history_table_id] IS NULL 
       THEN '' 
       ELSE 'ALTER TABLE ' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[object_id]) ) + '.' + QUOTENAME(OBJECT_NAME([tabz].[object_id])) + ' SET (SYSTEM_VERSIONING = OFF);' + @vbCrLf
            +  'IF OBJECT_ID(''' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[history_table_id]) ) + '.' + QUOTENAME(OBJECT_NAME([tabz].[history_table_id])) + ''') IS NOT NULL ' + @vbCrLf
              + 'DROP TABLE ' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[history_table_id])) + '.' + QUOTENAME(OBJECT_NAME([tabz].[history_table_id])) + ' ' + @vbCrLf + 'GO' + @vbCrLf
       END
    + 'IF OBJECT_ID(''' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[object_id]) ) + '.' + QUOTENAME(OBJECT_NAME([tabz].[object_id])) + ''') IS NOT NULL ' + @vbCrLf
              + 'DROP TABLE ' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[object_id])) + '.' + QUOTENAME(OBJECT_NAME([tabz].[object_id])) + ' ' + @vbCrLf + 'GO' + @vbCrLf
              + 'CREATE TABLE ' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[object_id])) + '.' + QUOTENAME(OBJECT_NAME([tabz].[object_id])) + ' ( '
    FROM [sys].[tables] [tabz] WHERE [tabz].[object_id] = @TABLE_ID
    PRINT @FINALSQL
  --removed invalid code here which potentially selected wrong table--thanks David Grifiths @SSC!
  SELECT
    @STRINGLEN = MAX(LEN([colz].[name])) + 1
  FROM [sys].[objects] AS [objz]
    INNER JOIN [sys].[columns] AS [colz]
      ON  [objz].[object_id] = [colz].[object_id]
      AND [objz].[object_id] = @TABLE_ID;
--##############################################################################
--Get the columns, their definitions and defaults.
--##############################################################################
  SELECT
    @FINALSQL = @FINALSQL
    + CASE
        WHEN [colz].[is_computed] = 1
        THEN @vbCrLf
             + QUOTENAME([colz].[name])
             + ' '
             + SPACE(@STRINGLEN - LEN([colz].[name]))
             + 'AS ' + ISNULL([CALC].[definition],'')
             + CASE 
                 WHEN [CALC].[is_persisted] = 1 
                 THEN ' PERSISTED'
                 ELSE ''
               END
        ELSE @vbCrLf
             + QUOTENAME([colz].[name])
             + ' '
             + SPACE(@STRINGLEN - LEN([colz].[name]))
             + UPPER(TYPE_NAME([colz].[user_type_id]))
             + CASE
-- data types with precision and scale  IE DECIMAL(18,3), NUMERIC(10,2)
               WHEN TYPE_NAME([colz].[user_type_id]) IN ('decimal','numeric')
               THEN '('
                    + CONVERT(VARCHAR,[colz].[precision])
                    + ','
                    + CONVERT(VARCHAR,[colz].[scale])
                    + ') '
                    + SPACE(6 - LEN(CONVERT(VARCHAR,[colz].[precision])
                    + ','
                    + CONVERT(VARCHAR,[colz].[scale])))
                    + SPACE(7)
                    + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                    + CASE
                        WHEN COLUMNPROPERTY ( @TABLE_ID , [colz].[name] , 'IsIdentity' ) = 0
                        THEN ''
                        ELSE ' IDENTITY('
                               + CONVERT(VARCHAR,ISNULL(IDENT_SEED(@TBLNAME),1) )
                               + ','
                               + CONVERT(VARCHAR,ISNULL(IDENT_INCR(@TBLNAME),1) )
                               + ')'
                        END
                    + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                    + CASE
                        WHEN [colz].[is_nullable] = 0
                        THEN ' NOT NULL'
                        ELSE '     NULL'
                      END
-- data types with scale  IE datetime2(7),TIME(7)
               WHEN TYPE_NAME([colz].[user_type_id]) IN ('datetime2','datetimeoffset','time')
               THEN CASE 
                      WHEN [colz].[scale] < 7 THEN
                      '('
                      + CONVERT(VARCHAR,[colz].[scale])
                      + ') '
                    ELSE 
                      '    '
                    END
                    + SPACE(4)
                    + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                    + '        '
                    + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                    + CASE [colz].[generated_always_type]
                        WHEN 0 THEN ''
                        WHEN 1 THEN ' GENERATED ALWAYS AS ROW START'
                        WHEN 2 THEN ' GENERATED ALWAYS AS ROW END'
                        ELSE ''
                      END 
                    + CASE WHEN [colz].[is_hidden] = 1 THEN ' HIDDEN' ELSE '' END
                    + CASE
                        WHEN [colz].[is_nullable] = 0
                        THEN ' NOT NULL'
                        ELSE '     NULL'
                      END
--data types with no/precision/scale,IE  FLOAT
               WHEN  TYPE_NAME([colz].[user_type_id]) IN ('float') --,'real')
               THEN
               --addition: if 53, no need to specifically say (53), otherwise display it
                    CASE
                      WHEN [colz].[precision] = 53
                      THEN SPACE(11 - LEN(CONVERT(VARCHAR,[colz].[precision])))
                           + SPACE(7)
                           + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                           + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                           + CASE
                               WHEN [colz].[is_nullable] = 0
                               THEN ' NOT NULL'
                               ELSE '     NULL'
                             END
                      ELSE '('
                           + CONVERT(VARCHAR,[colz].[precision])
                           + ') '
                           + SPACE(6 - LEN(CONVERT(VARCHAR,[colz].[precision])))
                           + SPACE(7) + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                           + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                           + CASE
                               WHEN [colz].[is_nullable] = 0
                               THEN ' NOT NULL'
                               ELSE '     NULL'
                             END
                      END
--data type with max_length		ie CHAR (44), VARCHAR(40), BINARY(5000),
--##############################################################################
-- COLLATE STATEMENTS
-- personally i do not like collation statements,
-- but included here to make it easy on those who do
--##############################################################################
               WHEN  TYPE_NAME([colz].[user_type_id]) IN ('char','varchar','binary','varbinary')
               THEN CASE
                      WHEN  [colz].[max_length] = -1
                      THEN  '(max)'
                            + SPACE(6 - LEN(CONVERT(VARCHAR,[colz].[max_length])))
                            + SPACE(7) + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                            ----collate to comment out when not desired
                            --+ CASE
                            --    WHEN COLS.collation_name IS NULL
                            --    THEN ''
                            --    ELSE ' COLLATE ' + COLS.collation_name
                            --  END
                            + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                            + CASE
                                WHEN [colz].[is_nullable] = 0
                                THEN ' NOT NULL'
                                ELSE '     NULL'
                              END
                      ELSE '('
                           + CONVERT(VARCHAR,[colz].[max_length])
                           + ') '
                           + SPACE(6 - LEN(CONVERT(VARCHAR,[colz].[max_length])))
                           + SPACE(7) + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                           ----collate to comment out when not desired
                           --+ CASE
                           --     WHEN COLS.collation_name IS NULL
                           --     THEN ''
                           --     ELSE ' COLLATE ' + COLS.collation_name
                           --   END
                           + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                           + CASE
                               WHEN [colz].[is_nullable] = 0
                               THEN ' NOT NULL'
                               ELSE '     NULL'
                             END
                    END
--data type with max_length ( BUT DOUBLED) ie NCHAR(33), NVARCHAR(40)
               WHEN TYPE_NAME([colz].[user_type_id]) IN ('nchar','nvarchar')
               THEN CASE
                      WHEN  [colz].[max_length] = -1
                      THEN '(max)'
                           + SPACE(5 - LEN(CONVERT(VARCHAR,([colz].[max_length] / 2))))
                           + SPACE(7)
                           + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                           ----collate to comment out when not desired
                           --+ CASE
                           --     WHEN COLS.collation_name IS NULL
                           --     THEN ''
                           --     ELSE ' COLLATE ' + COLS.collation_name
                           --   END
                           + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                           + CASE
                               WHEN [colz].[is_nullable] = 0
                               THEN  ' NOT NULL'
                               ELSE '     NULL'
                             END
                      ELSE '('
                           + CONVERT(VARCHAR,([colz].[max_length] / 2))
                           + ') '
                           + SPACE(6 - LEN(CONVERT(VARCHAR,([colz].[max_length] / 2))))
                           + SPACE(7)
                           + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                           ----collate to comment out when not desired
                           --+ CASE
                           --     WHEN COLS.collation_name IS NULL
                           --     THEN ''
                           --     ELSE ' COLLATE ' + COLS.collation_name
                           --   END
                           + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                           + CASE
                               WHEN [colz].[is_nullable] = 0
                               THEN ' NOT NULL'
                               ELSE '     NULL'
                             END
                    END
               WHEN TYPE_NAME([colz].[user_type_id]) IN ('datetime','money','text','image','real')
               THEN SPACE(18 - LEN(TYPE_NAME([colz].[user_type_id])))
                    + '              '
                    + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                    + CASE
                        WHEN [colz].[is_nullable] = 0
                        THEN ' NOT NULL'
                        ELSE '     NULL'
                      END
--  other data type 	IE INT, DATETIME, MONEY, CUSTOM DATA TYPE,...
               ELSE SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                            + CASE
                                WHEN COLUMNPROPERTY ( @TABLE_ID , [colz].[name] , 'IsIdentity' ) = 0
                                THEN '              '
                                ELSE ' IDENTITY('
                                     + CONVERT(VARCHAR,ISNULL(IDENT_SEED(@TBLNAME),1) )
                                     + ','
                                     + CONVERT(VARCHAR,ISNULL(IDENT_INCR(@TBLNAME),1) )
                                     + ')'
                              END
                            + SPACE(2)
                            + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                            + CASE
                                WHEN [colz].[is_nullable] = 0
                                THEN ' NOT NULL'
                                ELSE '     NULL'
                              END
               END
             + CASE
                 WHEN [colz].[default_object_id] = 0
                 THEN ''
                 --ELSE ' DEFAULT '  + ISNULL(def.[definition] ,'')
                 --optional section in case NAMED default constraints are needed:
                 ELSE '  CONSTRAINT ' + QUOTENAME([DEF].[name]) + ' DEFAULT ' + ISNULL([DEF].[definition] ,'')
                        --i thought it needed to be handled differently! NOT!
               END  --CASE cdefault
      END --iscomputed
    + ','
    FROM [sys].[columns] AS [colz]
      LEFT OUTER JOIN  [sys].[default_constraints]  AS [DEF]
        ON [colz].[default_object_id] = [DEF].[object_id]
      LEFT OUTER JOIN [sys].[computed_columns] AS [CALC]
         ON  [colz].[object_id] = [CALC].[object_id]
         AND [colz].[column_id] = [CALC].[column_id]
    WHERE [colz].[object_id]=@TABLE_ID
    ORDER BY [colz].[column_id];
--##############################################################################
--used for formatting the rest of the constraints:
--##############################################################################
  SELECT
    @STRINGLEN = MAX(LEN([objz].[name])) + 1
  FROM [sys].[objects] AS [objz];
--##############################################################################
--PK/Unique Constraints and Indexes, using the 2005/08 INCLUDE syntax
--##############################################################################
  DECLARE @Results  TABLE (
                    [SCHEMA_ID]             INT,
                    [SCHEMA_NAME]           VARCHAR(255),
                    [OBJECT_ID]             INT,
                    [OBJECT_NAME]           VARCHAR(255),
                    [index_id]              INT,
                    [index_name]            VARCHAR(255),
                    [ROWS]                  BIGINT,
                    [SizeMB]                DECIMAL(19,3),
                    [IndexDepth]            INT,
                    [TYPE]                  INT,
                    [type_desc]             VARCHAR(30),
                    [fill_factor]           INT,
                    [is_unique]             INT,
                    [is_primary_key]        INT ,
                    [is_unique_constraint]  INT,
                    [index_columns_key]     VARCHAR(MAX),
                    [index_columns_include] VARCHAR(MAX),
                    [has_filter] BIT ,
                    [filter_definition] VARCHAR(MAX),
                    [currentFilegroupName]  VARCHAR(128),
                    [CurrentCompression]    VARCHAR(128));
  INSERT INTO @Results
    SELECT
      [SCH].[schema_id], [SCH].[name] AS [SCHEMA_NAME],
      [objz].[object_id], [objz].[name] AS [OBJECT_NAME],
      [IDX].[index_id], ISNULL([IDX].[name], '---') AS [index_name],
      [partitions].[ROWS], [partitions].[SizeMB], INDEXPROPERTY([objz].[object_id], [IDX].[name], 'IndexDepth') AS [IndexDepth],
      [IDX].[type], [IDX].[type_desc], [IDX].[fill_factor],
      [IDX].[is_unique], [IDX].[is_primary_key], [IDX].[is_unique_constraint],
      ISNULL([Index_Columns].[index_columns_key], '---') AS [index_columns_key],
      ISNULL([Index_Columns].[index_columns_include], '---') AS [index_columns_include],
      [IDX].[has_filter],
      [IDX].[filter_definition],
      [filz].[name],
      ISNULL([p].[data_compression_desc],'')
    FROM [sys].[objects] AS [objz]
      INNER JOIN [sys].[schemas] AS [SCH] ON [objz].[schema_id]=[SCH].[schema_id]
      INNER JOIN [sys].[indexes] AS [IDX] ON [objz].[object_id]=[IDX].[object_id]
      INNER JOIN [sys].[filegroups] AS [filz] ON [IDX].[data_space_id] = [filz].[data_space_id]
      INNER JOIN [sys].[partitions] AS [p]     ON  [IDX].[object_id] =  [p].[object_id]  AND [IDX].[index_id] = [p].[index_id]
      INNER JOIN (
                  SELECT
                    [statz].[object_id], [statz].[index_id], SUM([statz].[row_count]) AS [ROWS],
                    CONVERT(NUMERIC(19,3), CONVERT(NUMERIC(19,3), SUM([statz].[in_row_reserved_page_count]+[statz].[lob_reserved_page_count]+[statz].[row_overflow_reserved_page_count]))/CONVERT(NUMERIC(19,3), 128)) AS [SizeMB]
                  FROM [sys].[dm_db_partition_stats] AS [statz]
                  GROUP BY [statz].[object_id], [statz].[index_id]
                 ) AS [partitions] 
        ON  [IDX].[object_id]=[partitions].[object_id] 
        AND [IDX].[index_id]=[partitions].[index_id]
    CROSS APPLY (
                 SELECT
                   LEFT([Index_Columns].[index_columns_key], LEN([Index_Columns].[index_columns_key])-1) AS [index_columns_key],
                  LEFT([Index_Columns].[index_columns_include], LEN([Index_Columns].[index_columns_include])-1) AS [index_columns_include]
                 FROM
                      (
                       SELECT
                              (
                              SELECT QUOTENAME([colz].[name]) + CASE WHEN [IXCOLS].[is_descending_key] = 0 THEN ' asc' ELSE ' desc' END + ',' + ' '
                               FROM [sys].[index_columns] AS [IXCOLS]
                                 INNER JOIN [sys].[columns] AS [colz]
                                   ON  [IXCOLS].[column_id]   = [colz].[column_id]
                                   AND [IXCOLS].[object_id] = [colz].[object_id]
                               WHERE [IXCOLS].[is_included_column] = 0
                                 AND [IDX].[object_id] = [IXCOLS].[object_id] 
                                 AND [IDX].[index_id] = [IXCOLS].[index_id]
                               ORDER BY [IXCOLS].[key_ordinal]
                               FOR XML PATH('')
                              ) AS [index_columns_key],
                             (
                             SELECT QUOTENAME([colz].[name]) + ',' + ' '
                              FROM [sys].[index_columns] AS [IXCOLS]
                                INNER JOIN [sys].[columns] AS [colz]
                                  ON  [IXCOLS].[column_id]   = [colz].[column_id]
                                  AND [IXCOLS].[object_id] = [colz].[object_id]
                              WHERE [IXCOLS].[is_included_column] = 1
                                AND [IDX].[object_id] = [IXCOLS].[object_id] 
                                AND [IDX].[index_id] = [IXCOLS].[index_id]
                              ORDER BY [IXCOLS].[index_column_id]
                              FOR XML PATH('')
                             ) AS [index_columns_include]
                      ) AS [Index_Columns]
                ) AS [Index_Columns]
    WHERE [SCH].[name]  LIKE CASE 
                                     WHEN @SCHEMANAME = ''   COLLATE SQL_Latin1_General_CP1_CI_AS
                                     THEN [SCH].[name] 
                                     ELSE @SCHEMANAME 
                                   END
    AND [objz].[name] LIKE CASE 
                                  WHEN @TBLNAME = ''   COLLATE SQL_Latin1_General_CP1_CI_AS 
                                  THEN [objz].[name] 
                                  ELSE @TBLNAME 
                                END
    ORDER BY 
      [SCH].[name], 
      [objz].[name], 
      [IDX].[name];
--@Results table has both PK,s Uniques and indexes in thme...pull them out for adding to funal results:
  SET @CONSTRAINTSQLS = '';
  SET @INDEXSQLS      = '';
  SET @TemporalStatement = '';
  SET @WithStatement = '';
  --##############################################################################
  -- Temporal tables
--##############################################################################
  SELECT @TemporalStatement =  ISNULL(@vbCrLf + 'PERIOD FOR SYSTEM_TIME ('
  + MAX(CASE WHEN [colz].[generated_always_type] = 1 THEN [colz].[name] ELSE '' END)
  +','
 + MAX(CASE WHEN [colz].[generated_always_type] = 2 THEN [colz].[name] ELSE '' END)
  +'),','') ,
  @WithStatement = ISNULL(' SYSTEM_VERSIONING = ON (HISTORY_TABLE=' + QUOTENAME(OBJECT_SCHEMA_NAME([objz].[history_table_id])) + '.' + QUOTENAME(OBJECT_NAME([objz].[history_table_id])) + '),' ,'')
  FROM [sys].[tables] [objz]
  INNER JOIN [sys].[columns] [colz] 
  ON [objz].[object_id] = [colz].[object_id]
  WHERE [colz].[object_id] = @TABLE_ID 
  AND [colz].[generated_always_type] > 0
  GROUP BY [colz].[object_id],[objz].[history_table_id]
--##############################################################################
-- memory optimized
--##############################################################################
SELECT @WithStatement  = @WithStatement + ISNULL('MEMORY_OPTIMIZED=ON, DURABILITY=' + [objz].[durability_desc] + ',','') 
FROM [sys].[tables] [objz]
WHERE [objz].[is_memory_optimized] =1
AND [objz].[object_id] = @TABLE_ID 
--##############################################################################
--constraints
--column store indexes are different: the "include" columns for normal indexes as scripted above are the columnstores indexed columns
--add a CASE for that situation.
--##############################################################################
  SELECT @CONSTRAINTSQLS = @CONSTRAINTSQLS 
         + CASE
             WHEN [is_primary_key] = 1 OR [is_unique] = 1
             THEN @vbCrLf
                  + 'CONSTRAINT   '  COLLATE SQL_Latin1_General_CP1_CI_AS + QUOTENAME([index_name]) + ' '
                  + CASE  
                      WHEN [is_primary_key] = 1 
                      THEN ' PRIMARY KEY ' 
                      ELSE CASE  
                             WHEN [is_unique] = 1     
                             THEN ' UNIQUE      '      
                             ELSE '' 
                           END 
                    END
                  + [type_desc] 
                  + CASE 
                      WHEN [type_desc]='NONCLUSTERED' 
                      THEN '' 
                      ELSE '   ' 
                    END
                  + ' (' + [index_columns_key] + ')'
                  + CASE 
                      WHEN [index_columns_include] <> '---' 
                      THEN ' INCLUDE (' + [index_columns_include] + ')' 
                      ELSE '' 
                    END
                  + CASE
                      WHEN [has_filter] = 1 
                      THEN ' ' + [filter_definition]
                      ELSE ' '
                    END
                  + CASE WHEN [fill_factor] <> 0 OR [CurrentCompression] <> 'NONE'
                  THEN ' WITH (' + CASE
                                    WHEN [fill_factor] <> 0 
                                    THEN 'FILLFACTOR = ' + CONVERT(VARCHAR(30),[fill_factor]) 
                                    ELSE '' 
                                  END
                                + CASE
                                    WHEN [fill_factor] <> 0  AND [CurrentCompression] <> 'NONE' THEN ',DATA_COMPRESSION = ' + [CurrentCompression] + ' '
                                    WHEN [fill_factor] <> 0  AND [CurrentCompression]  = 'NONE' THEN ''
                                    WHEN [fill_factor]  = 0  AND [CurrentCompression] <> 'NONE' THEN 'DATA_COMPRESSION = ' + [CurrentCompression] + ' '
                                    ELSE '' 
                                  END
                                  + ')'
                  ELSE '' 
                  END 
                      
             ELSE ''
           END + ','
  FROM @Results
  WHERE [type_desc] != 'HEAP'
    AND [is_primary_key] = 1 
    OR  [is_unique] = 1
  ORDER BY 
    [is_primary_key] DESC,
    [is_unique] DESC;
    --
--##############################################################################
--indexes
--##############################################################################
  SELECT @INDEXSQLS = @INDEXSQLS 
         + CASE
             WHEN [is_primary_key] = 0 OR [is_unique] = 0
             THEN @vbCrLf
                  + 'CREATE '  COLLATE SQL_Latin1_General_CP1_CI_AS + [type_desc] + ' INDEX '  COLLATE SQL_Latin1_General_CP1_CI_AS + QUOTENAME([index_name]) + ' '
                  + @vbCrLf
                  + '   ON '   COLLATE SQL_Latin1_General_CP1_CI_AS
                  + QUOTENAME([SCHEMA_NAME]) + '.' + QUOTENAME([OBJECT_NAME])
                  + CASE 
                        WHEN [CurrentCompression] = 'COLUMNSTORE'  COLLATE SQL_Latin1_General_CP1_CI_AS
                        THEN ' (' + [index_columns_include] + ')' 
                        ELSE ' (' + [index_columns_key] + ')'
                    END
                  + CASE 
                      WHEN [CurrentCompression] = 'COLUMNSTORE'  COLLATE SQL_Latin1_General_CP1_CI_AS
                      THEN ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                      ELSE
                        CASE
                     WHEN [index_columns_include] <> '---' 
                     THEN @vbCrLf + '   INCLUDE ('  COLLATE SQL_Latin1_General_CP1_CI_AS + [index_columns_include] + ')'   COLLATE SQL_Latin1_General_CP1_CI_AS
                     ELSE ''   COLLATE SQL_Latin1_General_CP1_CI_AS
                   END
                    END
                  --2008 filtered indexes syntax
                  + CASE 
                      WHEN [has_filter] = 1 
                      THEN @vbCrLf + '   WHERE '  COLLATE SQL_Latin1_General_CP1_CI_AS + [filter_definition]
                      ELSE ''
                    END
                  + CASE WHEN [fill_factor] <> 0 OR [CurrentCompression] <> 'NONE'  COLLATE SQL_Latin1_General_CP1_CI_AS
                  THEN ' WITH ('  COLLATE SQL_Latin1_General_CP1_CI_AS + CASE
                                    WHEN [fill_factor] <> 0 
                                    THEN 'FILLFACTOR = '  COLLATE SQL_Latin1_General_CP1_CI_AS + CONVERT(VARCHAR(30),[fill_factor]) 
                                    ELSE '' 
                                  END
                                + CASE
                                    WHEN [fill_factor] <> 0  AND [CurrentCompression] <> 'NONE' THEN ',DATA_COMPRESSION = ' + [CurrentCompression]+' '
                                    WHEN [fill_factor] <> 0  AND [CurrentCompression]  = 'NONE' THEN ''
                                    WHEN [fill_factor]  = 0  AND [CurrentCompression] <> 'NONE' THEN 'DATA_COMPRESSION = ' + [CurrentCompression]+' '
                                    ELSE '' 
                                  END
                                  + ')'
                  ELSE '' 
                  END 
           END
  FROM @Results
  WHERE [type_desc] != 'HEAP'
    AND [is_primary_key] = 0 
    AND [is_unique] = 0
  ORDER BY 
    [is_primary_key] DESC,
    [is_unique] DESC;
  IF @INDEXSQLS <> ''  COLLATE SQL_Latin1_General_CP1_CI_AS
    SET @INDEXSQLS = @vbCrLf + 'GO'  COLLATE SQL_Latin1_General_CP1_CI_AS + @vbCrLf + @INDEXSQLS;
--##############################################################################
--CHECK Constraints
--##############################################################################
  SET @CHECKCONSTSQLS = ''  COLLATE SQL_Latin1_General_CP1_CI_AS;
  SELECT
    @CHECKCONSTSQLS = @CHECKCONSTSQLS
    + @vbCrLf
    + ISNULL('CONSTRAINT   ' + QUOTENAME([objz].[name]) + ' '
    + SPACE(@STRINGLEN - LEN([objz].[name]))
    + ' CHECK ' + ISNULL([CHECKS].[definition],'')
    + ',','')
  FROM [sys].[objects] AS [objz]
    INNER JOIN [sys].[check_constraints] AS [CHECKS] ON [objz].[object_id] = [CHECKS].[object_id]
  WHERE [objz].[type] = 'C'
    AND [objz].[parent_object_id] = @TABLE_ID;
--##############################################################################
--FOREIGN KEYS
--##############################################################################
  SET @FKSQLS = '' ;
    SELECT
    @FKSQLS=@FKSQLS
    + @vbCrLf + [MyAlias].[Command] FROM
(
SELECT
  DISTINCT
  --FK must be added AFTER the PK/unique constraints are added back.
  850 AS [ExecutionOrder],
  'CONSTRAINT ' 
  + QUOTENAME([conz].[name]) 
  + ' FOREIGN KEY (' 
  + [ChildCollection].[ChildColumns] 
  + ') REFERENCES ' 
  + QUOTENAME(OBJECT_SCHEMA_NAME([conz].[referenced_object_id])) -- TIM C: Changed so that if the FK object is in another schema it will report the correct schema for the FK object
  + '.' 
  + QUOTENAME(OBJECT_NAME([conz].[referenced_object_id])) 
  + ' (' + [ParentCollection].[ParentColumns] 
  + ') ' 
  +  CASE [conz].[update_referential_action]
                                        WHEN 0 THEN '' --' ON UPDATE NO ACTION '
                                        WHEN 1 THEN ' ON UPDATE CASCADE '
                                        WHEN 2 THEN ' ON UPDATE SET NULL '
                                        ELSE ' ON UPDATE SET DEFAULT '
                                    END
                  + CASE [conz].[delete_referential_action]
                                        WHEN 0 THEN '' --' ON DELETE NO ACTION '
                                        WHEN 1 THEN ' ON DELETE CASCADE '
                                        WHEN 2 THEN ' ON DELETE SET NULL '
                                        ELSE ' ON DELETE SET DEFAULT '
                                    END
                  + CASE [conz].[is_not_for_replication]
                        WHEN 1 THEN ' NOT FOR REPLICATION '
                        ELSE ''
                    END
  + ',' AS [Command]
FROM   [sys].[foreign_keys] AS [conz]
       INNER JOIN [sys].[foreign_key_columns] AS [colz]
         ON [conz].[object_id] = [colz].[constraint_object_id]
      
       INNER JOIN (--gets my child tables column names   
SELECT
 [conz].[name],
 --technically, FK's can contain up to 16 columns, but real life is often a single column. coding here is for all columns
 [ChildColumns] = STUFF((SELECT 
                         ',' + QUOTENAME([REFZ].[name])
                       FROM   [sys].[foreign_key_columns] AS [fkcolz]
                              INNER JOIN [sys].[columns] AS [REFZ]
                                ON [fkcolz].[parent_object_id] = [REFZ].[object_id]
                                   AND [fkcolz].[parent_column_id] = [REFZ].[column_id]
                       WHERE [fkcolz].[parent_object_id] = [conz].[parent_object_id]
                           AND [fkcolz].[constraint_object_id] = [conz].[object_id]
                         ORDER  BY
                        [fkcolz].[constraint_column_id]
                      FOR XML PATH(''), TYPE).[value]('.','varchar(max)'),1,1,'')
FROM   [sys].[foreign_keys] AS [conz]
      INNER JOIN [sys].[foreign_key_columns] AS [colz]
        ON [conz].[object_id] = [colz].[constraint_object_id]
        WHERE [conz].[parent_object_id]= @TABLE_ID
GROUP  BY
[conz].[name],
[conz].[parent_object_id],--- without GROUP BY multiple rows are returned
 [conz].[object_id]
    ) AS [ChildCollection]
         ON [conz].[name] = [ChildCollection].[name]
       INNER JOIN (--gets the parent tables column names for the FK reference
                  SELECT
                     [conz].[name],
                     [ParentColumns] = STUFF((SELECT
                                              ',' + [REFZ].[name]
                                            FROM   [sys].[foreign_key_columns] AS [fkcolz]
                                                   INNER JOIN [sys].[columns] AS [REFZ]
                                                     ON [fkcolz].[referenced_object_id] = [REFZ].[object_id]
                                                        AND [fkcolz].[referenced_column_id] = [REFZ].[column_id]
                                            WHERE  [fkcolz].[referenced_object_id] = [conz].[referenced_object_id]
                                              AND [fkcolz].[constraint_object_id] = [conz].[object_id]
                                            ORDER BY [fkcolz].[constraint_column_id]
                                            FOR XML PATH(''), TYPE).[value]('.','varchar(max)'),1,1,'')
                   FROM   [sys].[foreign_keys] AS [conz]
                          INNER JOIN [sys].[foreign_key_columns] AS [colz]
                            ON [conz].[object_id] = [colz].[constraint_object_id]
                           -- AND colz.parent_column_id 
                   GROUP  BY
                    [conz].[name],
                    [conz].[referenced_object_id],--- without GROUP BY multiple rows are returned
                    [conz].[object_id]
                  ) AS [ParentCollection]
         ON [conz].[name] = [ParentCollection].[name]
)AS [MyAlias];
--##############################################################################
--RULES
--##############################################################################
  SET @RULESCONSTSQLS = '';
  SELECT
    @RULESCONSTSQLS = @RULESCONSTSQLS
    + ISNULL(
             @vbCrLf
             + 'if not exists(SELECT [name] FROM sys.objects WHERE TYPE=''R'' AND schema_id = ' COLLATE SQL_Latin1_General_CP1_CI_AS + CONVERT(VARCHAR(30),[objz].[schema_id]) + ' AND [name] = '''  COLLATE SQL_Latin1_General_CP1_CI_AS + QUOTENAME(OBJECT_NAME([colz].[rule_object_id])) + ''')'  COLLATE SQL_Latin1_General_CP1_CI_AS + @vbCrLf
             + [MODS].[definition]  + @vbCrLf + 'GO' COLLATE SQL_Latin1_General_CP1_CI_AS +  @vbCrLf
             + 'EXEC sp_binderule  ' + QUOTENAME([objz].[name]) + ', ''' + QUOTENAME(OBJECT_NAME([colz].[object_id])) + '.' + QUOTENAME([colz].[name]) + ''''  COLLATE SQL_Latin1_General_CP1_CI_AS + @vbCrLf + 'GO'  COLLATE SQL_Latin1_General_CP1_CI_AS ,'')
  FROM [sys].[columns] [colz] 
    INNER JOIN [sys].[objects] [objz]
      ON [objz].[object_id] = [colz].[object_id]
    INNER JOIN [sys].[sql_modules] AS [MODS]
      ON [colz].[rule_object_id] = [MODS].[object_id]
  WHERE [colz].[rule_object_id] <> 0
    AND [colz].[object_id] = @TABLE_ID;
--##############################################################################
--TRIGGERS
--##############################################################################
  SET @TRIGGERSTATEMENT = '';
  SELECT
    @TRIGGERSTATEMENT = @TRIGGERSTATEMENT +  @vbCrLf + [MODS].[definition] + @vbCrLf + 'GO'
  FROM [sys].[sql_modules] AS [MODS]
  WHERE [MODS].[object_id] IN(SELECT
                         [objz].[object_id]
                       FROM [sys].[objects] AS [objz]
                       WHERE [objz].[type] = 'TR'
                       AND [objz].[parent_object_id] = @TABLE_ID);
  IF @TRIGGERSTATEMENT <> ''  COLLATE SQL_Latin1_General_CP1_CI_AS
    SET @TRIGGERSTATEMENT = @vbCrLf + 'GO'  COLLATE SQL_Latin1_General_CP1_CI_AS + @vbCrLf + @TRIGGERSTATEMENT;
--##############################################################################
--NEW SECTION QUERY ALL EXTENDED PROPERTIES
--##############################################################################
  SET @EXTENDEDPROPERTIES = '';
  SELECT  @EXTENDEDPROPERTIES =
          @EXTENDEDPROPERTIES + @vbCrLf +
         'EXEC sys.sp_addextendedproperty
          @name = N'''  COLLATE SQL_Latin1_General_CP1_CI_AS + [name] + ''', @value = N'''  COLLATE SQL_Latin1_General_CP1_CI_AS + REPLACE(CONVERT(VARCHAR(MAX),[value]),'''','''''') + ''',
          @level0type = N''SCHEMA'', @level0name = '  COLLATE SQL_Latin1_General_CP1_CI_AS + QUOTENAME(@SCHEMANAME) + ',
          @level1type = N''TABLE'', @level1name = '  COLLATE SQL_Latin1_General_CP1_CI_AS + QUOTENAME(@TBLNAME) + ';'
 --SELECT objtype, objname, name, value
  FROM [sys].[fn_listextendedproperty] (NULL, 'schema', @SCHEMANAME, 'table', @TBLNAME, NULL, NULL);
  --OMacoder suggestion for column extended properties http://www.sqlservercentral.com/Forums/FindPost1651606.aspx
   ;WITH [obj] AS (
	SELECT [split].[a].[value]('.', 'VARCHAR(20)') AS [name]
	FROM ( 
		SELECT CAST ('<M>' + REPLACE('column,constraint,index,trigger,parameter', ',', '</M><M>') + '</M>' AS XML) AS [data] 
		) AS [A] 
		CROSS APPLY [data].[nodes] ('/M') AS [split]([a])
	)
  SELECT 
  @EXTENDEDPROPERTIES =
		 @EXTENDEDPROPERTIES + @vbCrLf + @vbCrLf +
         'EXEC sys.sp_addextendedproperty
         @name = N''' COLLATE SQL_Latin1_General_CP1_CI_AS
         + [lep].[name] 
         + ''', @value = N''' COLLATE SQL_Latin1_General_CP1_CI_AS
         + REPLACE(CONVERT(VARCHAR(MAX),[lep].[value]),'''','''''') + ''',
         @level0type = N''SCHEMA'', @level0name = ' COLLATE SQL_Latin1_General_CP1_CI_AS
         + QUOTENAME(@SCHEMANAME) 
         + ',
         @level1type = N''TABLE'', @level1name = ' COLLATE SQL_Latin1_General_CP1_CI_AS
         + QUOTENAME(@TBLNAME) 
         + ',
         @level2type = N''' COLLATE SQL_Latin1_General_CP1_CI_AS
         + UPPER([obj].[name])  
         + ''', @level2name = ' COLLATE SQL_Latin1_General_CP1_CI_AS
         + QUOTENAME([lep].[objname]) + ';' COLLATE SQL_Latin1_General_CP1_CI_AS
  --SELECT objtype, objname, name, value
  FROM [obj] 
	CROSS APPLY [sys].[fn_listextendedproperty] (NULL, 'schema', @SCHEMANAME, 'table', @TBLNAME, [obj].[name], NULL) AS [lep];  
  
  IF @EXTENDEDPROPERTIES <> '' COLLATE SQL_Latin1_General_CP1_CI_AS
    SET @EXTENDEDPROPERTIES = @vbCrLf + 'GO' COLLATE SQL_Latin1_General_CP1_CI_AS + @vbCrLf + @EXTENDEDPROPERTIES;
--##############################################################################
--FINAL CLEANUP AND PRESENTATION
--##############################################################################
--at this point, there is a trailing comma, or it blank
--WITH statment has a trailing comma

IF @WithStatement > '' 
  SET @WithStatement='WITH (' + SUBSTRING(@WithStatement,1,LEN(@WithStatement) -1)  + ')'
  SELECT
    @FINALSQL = @FINALSQL
                + @TemporalStatement
                + @CONSTRAINTSQLS
                + @CHECKCONSTSQLS
                + @FKSQLS;
--note that this trims the trailing comma from the end of the statements
  SET @FINALSQL = SUBSTRING(@FINALSQL,1,LEN(@FINALSQL) -1) ;
  SET @FINALSQL = @FINALSQL + ')' COLLATE SQL_Latin1_General_CP1_CI_AS +  @vbCrLf + @WithStatement COLLATE SQL_Latin1_General_CP1_CI_AS +  @vbCrLf ;

  SET @input = @vbCrLf
       + @FINALSQL
       + @INDEXSQLS
       + @RULESCONSTSQLS
       + @TRIGGERSTATEMENT
       + @EXTENDEDPROPERTIES
  --ten years worth of days from todays date:
   ;WITH [E01]([N]) AS (SELECT 1 UNION ALL SELECT 1 UNION ALL
                    SELECT 1 UNION ALL SELECT 1 UNION ALL
                    SELECT 1 UNION ALL SELECT 1 UNION ALL
                    SELECT 1 UNION ALL SELECT 1 UNION ALL
                    SELECT 1 UNION ALL SELECT 1), --         10 or 10E01 rows
         [E02]([N]) AS (SELECT 1 FROM [E01] AS [a], [E01] AS [b]),  --        100 or 10E02 rows
         [E04]([N]) AS (SELECT 1 FROM [E02] AS [a], [E02] AS [b]),  --     10,000 or 10E04 rows
         [E08]([N]) AS (SELECT 1 FROM [E04] AS [a], [E04] AS [b]),  --100,000,000 or 10E08 rows
         --E16(N) AS (SELECT 1 FROM E08 a, E08 b),  --10E16 or more rows than you'll EVER need,
         [Tally]([N]) AS (SELECT ROW_NUMBER() OVER (ORDER BY [E08].[N]) FROM [E08]),
       [ItemSplit](
                 [ItemOrder],
                 [Item]
                ) AS (
                      SELECT [Tally].[N],
                        SUBSTRING(@vbCrLf + @input + @vbCrLf,[Tally].[N] + DATALENGTH(@vbCrLf),CHARINDEX(@vbCrLf,@vbCrLf + @input + @vbCrLf,[Tally].[N] + DATALENGTH(@vbCrLf)) - [Tally].[N] - DATALENGTH(@vbCrLf))
                      FROM [Tally]
                      WHERE [Tally].[N] < DATALENGTH(@vbCrLf + @input)
                      --WHERE N < DATALENGTH(@vbCrLf + @input) -- REMOVED added @vbCrLf
                        AND SUBSTRING(@vbCrLf + @input + @vbCrLf,[Tally].[N],DATALENGTH(@vbCrLf)) = @vbCrLf --Notice how we find the delimiter
                     )
  SELECT
    --row_number() over (order by ItemOrder) as ItemID,
    [ItemSplit].[Item]
  FROM [ItemSplit];
  RETURN;     
--##############################################################################
-- END Normal Table Processing
--############################################################################## 
    
--simple, primitive version to get the results of a TEMP table from the TEMP db.  
--##############################################################################
-- NEW Temp Table Logic
--##############################################################################     
TEMPPROCESS:
  SELECT @TABLE_ID = OBJECT_ID('tempdb..' COLLATE SQL_Latin1_General_CP1_CI_AS + @TBLNAME);
--##############################################################################
-- Valid temp Table, Continue Processing
--##############################################################################
SELECT @FINALSQL = 
     CASE 
       WHEN [tabz].[history_table_id] IS NULL 
       THEN '' 
       ELSE 'ALTER TABLE ' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[object_id]) ) + '.' + QUOTENAME(OBJECT_NAME([tabz].[object_id])) + ' SET (SYSTEM_VERSIONING = OFF);' + @vbCrLf
            +  'IF OBJECT_ID(''' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[history_table_id]) ) + '.' + QUOTENAME(OBJECT_NAME([tabz].[history_table_id])) + ''') IS NOT NULL ' + @vbCrLf
              + 'DROP TABLE ' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[history_table_id])) + '.' + QUOTENAME(OBJECT_NAME([tabz].[history_table_id])) + ' ' + @vbCrLf + 'GO' + @vbCrLf
       END
    + 'IF OBJECT_ID(''' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[object_id]) ) + '.' + QUOTENAME(OBJECT_NAME([tabz].[object_id])) + ''') IS NOT NULL ' + @vbCrLf
              + 'DROP TABLE ' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[object_id])) + '.' + QUOTENAME(OBJECT_NAME([tabz].[object_id])) + ' ' + @vbCrLf + 'GO' + @vbCrLf
              + 'CREATE TABLE ' + QUOTENAME(OBJECT_SCHEMA_NAME([tabz].[object_id])) + '.' + QUOTENAME(OBJECT_NAME([tabz].[object_id])) + ' ( '
FROM [sys].[tables] [tabz] WHERE [tabz].[object_id] = OBJECT_ID(@TABLE_ID)
  --removed invalid code here which potentially selected wrong table--thansk David Grifiths @SSC!
  SELECT
    @STRINGLEN = MAX(LEN([colz].[name])) + 1
  FROM [tempdb].[sys].[objects] AS [objz]
    INNER JOIN [tempdb].[sys].[columns] AS [colz]
      ON  [objz].[object_id] = [colz].[object_id]
      AND [objz].[object_id] = @TABLE_ID;
--##############################################################################
--Get the hash index definitions for memory optimized tables, if any.
--##############################################################################

--##############################################################################
--Get the columns, their definitions and defaults.
--##############################################################################
  SELECT
    @FINALSQL = @FINALSQL
    + CASE
        WHEN [colz].[is_computed] = 1
        THEN @vbCrLf
             + QUOTENAME([colz].[name])
             + ' '
             + SPACE(@STRINGLEN - LEN([colz].[name]))
             + 'AS ' + ISNULL([CALC].[definition],'')
              + CASE 
                 WHEN [CALC].[is_persisted] = 1 
                 THEN ' PERSISTED'
                 ELSE ''
               END
        ELSE @vbCrLf
             + QUOTENAME([colz].[name])
             + ' '
             + SPACE(@STRINGLEN - LEN([colz].[name]))
             + UPPER(TYPE_NAME([colz].[user_type_id]))
             + CASE
-- data types with precision and scale  IE DECIMAL(18,3), NUMERIC(10,2)
               WHEN TYPE_NAME([colz].[user_type_id]) IN ('decimal','numeric')
               THEN '('
                    + CONVERT(VARCHAR,[colz].[precision])
                    + ','
                    + CONVERT(VARCHAR,[colz].[scale])
                    + ') '
                    + SPACE(6 - LEN(CONVERT(VARCHAR,[colz].[precision])
                    + ','
                    + CONVERT(VARCHAR,[colz].[scale])))
                    + SPACE(7)
                    + SPACE(16 - LEN(TYPE_NAME([colz].[user_type_id])))
                    + CASE
                        WHEN [colz].[is_identity] = 1
                        THEN ' IDENTITY(1,1)'
                        ELSE ''
                        ----WHEN COLUMNPROPERTY ( @TABLE_ID , COLS.[name] , 'IsIdentity' ) = 1
                        ----THEN ' IDENTITY('
                        ----       + CONVERT(VARCHAR,ISNULL(IDENT_SEED('tempdb..' + @TBLNAME),1) )
                        ----       + ','
                        ----       + CONVERT(VARCHAR,ISNULL(IDENT_INCR('tempdb..' + @TBLNAME),1) )
                        ----       + ')'
                        ----ELSE ''
                        END
                    + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                    + CASE
                        WHEN [colz].[is_nullable] = 0
                        THEN ' NOT NULL'
                        ELSE '     NULL'
                      END
-- data types with scale  IE datetime2(7),TIME(7)
               WHEN TYPE_NAME([colz].[user_type_id]) IN ('datetime2','datetimeoffset','time')
               THEN CASE 
                      WHEN [colz].[scale] < 7 THEN
                      '('
                      + CONVERT(VARCHAR,[colz].[scale])
                      + ') '
                    ELSE 
                      '    '
                    END
                    + SPACE(4)
                    + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                    + '        '
                    + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                    + CASE [colz].[generated_always_type]
                        WHEN 0 THEN ''
                        WHEN 1 THEN ' GENERATED ALWAYS AS ROW START'
                        WHEN 2 THEN ' GENERATED ALWAYS AS ROW END'
                        ELSE ''
                      END 
                    + CASE WHEN [colz].[is_hidden] = 1 THEN ' HIDDEN' ELSE '' END
                    + CASE
                        WHEN [colz].[is_nullable] = 0
                        THEN ' NOT NULL'
                        ELSE '     NULL'
                      END
--data types with no/precision/scale,IE  FLOAT
               WHEN  TYPE_NAME([colz].[user_type_id]) IN ('float') --,'real')
               THEN
               --addition: if 53, no need to specifically say (53), otherwise display it
                    CASE
                      WHEN [colz].[precision] = 53
                      THEN SPACE(11 - LEN(CONVERT(VARCHAR,[colz].[precision])))
                           + SPACE(7)
                           + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                           + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                           + CASE
                               WHEN [colz].[is_nullable] = 0
                               THEN ' NOT NULL'
                               ELSE '     NULL'
                             END
                      ELSE '('
                           + CONVERT(VARCHAR,[colz].[precision])
                           + ') '
                           + SPACE(6 - LEN(CONVERT(VARCHAR,[colz].[precision])))
                           + SPACE(7) + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                           + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                           + CASE
                               WHEN [colz].[is_nullable] = 0
                               THEN ' NOT NULL'
                               ELSE '     NULL'
                             END
                      END
--ie VARCHAR(40)
--##############################################################################
-- COLLATE STATEMENTS in tempdb!
-- personally i do not like collation statements,
-- but included here to make it easy on those who do
--##############################################################################
               WHEN  TYPE_NAME([colz].[user_type_id]) IN ('char','varchar','binary','varbinary')
               THEN CASE
                      WHEN  [colz].[max_length] = -1
                      THEN  '(max)'
                            + SPACE(6 - LEN(CONVERT(VARCHAR,[colz].[max_length])))
                            + SPACE(7) + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                            ----collate to comment out when not desired
                            --+ CASE
                            --    WHEN COLS.collation_name IS NULL
                            --    THEN ''
                            --    ELSE ' COLLATE ' + COLS.collation_name
                            --  END
                            + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                            + CASE
                                WHEN [colz].[is_nullable] = 0
                                THEN ' NOT NULL'
                                ELSE '     NULL'
                              END
                      ELSE '('
                           + CONVERT(VARCHAR,[colz].[max_length])
                           + ') '
                           + SPACE(6 - LEN(CONVERT(VARCHAR,[colz].[max_length])))
                           + SPACE(7) + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                           ----collate to comment out when not desired
                           --+ CASE
                           --     WHEN COLS.collation_name IS NULL
                           --     THEN ''
                           --     ELSE ' COLLATE ' + COLS.collation_name
                           --   END
                           + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                           + CASE
                               WHEN [colz].[is_nullable] = 0
                               THEN ' NOT NULL'
                               ELSE '     NULL'
                             END
                    END
--data type with max_length ( BUT DOUBLED) ie NCHAR(33), NVARCHAR(40)
               WHEN TYPE_NAME([colz].[user_type_id]) IN ('nchar','nvarchar')
               THEN CASE
                      WHEN  [colz].[max_length] = -1
                      THEN '(max)'
                           + SPACE(5 - LEN(CONVERT(VARCHAR,([colz].[max_length] / 2))))
                           + SPACE(7)
                           + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                           -- --collate to comment out when not desired
                           --+ CASE
                           --     WHEN COLS.collation_name IS NULL
                           --     THEN ''
                           --     ELSE ' COLLATE ' + COLS.collation_name
                           --   END
                           + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                           + CASE
                               WHEN [colz].[is_nullable] = 0
                               THEN  ' NOT NULL'
                               ELSE '     NULL'
                             END
                      ELSE '('
                           + CONVERT(VARCHAR,([colz].[max_length] / 2))
                           + ') '
                           + SPACE(6 - LEN(CONVERT(VARCHAR,([colz].[max_length] / 2))))
                           + SPACE(7)
                           + SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                           -- --collate to comment out when not desired
                           --+ CASE
                           --     WHEN COLS.collation_name IS NULL
                           --     THEN ''
                           --     ELSE ' COLLATE ' + COLS.collation_name
                           --   END
                           + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                           + CASE
                               WHEN [colz].[is_nullable] = 0
                               THEN ' NOT NULL'
                               ELSE '     NULL'
                             END
                    END
--  other data type 	IE INT, DATETIME, MONEY, CUSTOM DATA TYPE,...
               WHEN TYPE_NAME([colz].[user_type_id]) IN ('datetime','money','text','image','real')
               THEN SPACE(18 - LEN(TYPE_NAME([colz].[user_type_id])))
                    + '              '
                    + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                    + CASE
                        WHEN [colz].[is_nullable] = 0
                        THEN ' NOT NULL'
                        ELSE '     NULL'
                      END
--IE INT
               ELSE SPACE(@ObjectDataTypeLen - LEN(TYPE_NAME([colz].[user_type_id])))
                            + CASE
                                WHEN [colz].[is_identity] = 1
                                THEN ' IDENTITY(1,1)'
                                ELSE '              '
                                ----WHEN COLUMNPROPERTY ( @TABLE_ID , COLS.[name] , 'IsIdentity' ) = 1
                                ----THEN ' IDENTITY('
                                ----     + CONVERT(VARCHAR,ISNULL(IDENT_SEED('tempdb..' + @TBLNAME),1) )
                                ----     + ','
                                ----     + CONVERT(VARCHAR,ISNULL(IDENT_INCR('tempdb..' + @TBLNAME),1) )
                                ----     + ')'
                                ----ELSE '              '
                              END
                            + SPACE(2)
                            + CASE  WHEN [colz].[is_sparse] = 1 THEN ' sparse' ELSE '       ' END
                            + CASE
                                WHEN [colz].[is_nullable] = 0
                                THEN ' NOT NULL'
                                ELSE '     NULL'
                              END
               END
             + CASE
                 WHEN [colz].[default_object_id] = 0
                 THEN ''
                 ELSE ' DEFAULT '  + ISNULL([DEF].[definition] ,'')
                 --optional section in case NAMED default cosntraints are needed:
                 --ELSE ' CONSTRAINT [' + DEF.name + '] DEFAULT '+ REPLACE(REPLACE(ISNULL(DEF.[definition] ,''),'((','('),'))',')')
                        --i thought it needed to be handled differently! NOT!
               END  --CASE cdefault
      END --iscomputed
    + ','
    FROM [tempdb].[sys].[columns] AS [colz]
      LEFT OUTER JOIN  [tempdb].[sys].[default_constraints]  AS [DEF]
        ON [colz].[default_object_id] = [DEF].[object_id]
      LEFT OUTER JOIN [tempdb].[sys].[computed_columns] AS [CALC]
         ON  [colz].[object_id] = [CALC].[object_id]
         AND [colz].[column_id] = [CALC].[column_id]
    WHERE [colz].[object_id]=@TABLE_ID
    ORDER BY [colz].[column_id];
--##############################################################################
--used for formatting the rest of the constraints:
--##############################################################################
  SELECT
    @STRINGLEN = MAX(LEN([objz].[name])) + 1
  FROM [tempdb].[sys].[objects] AS [objz];
--##############################################################################
--PK/Unique Constraints and Indexes, using the 2005/08 INCLUDE syntax
--##############################################################################
  DECLARE @Results2  TABLE (
                    [SCHEMA_ID]             INT,
                    [SCHEMA_NAME]           VARCHAR(255),
                    [OBJECT_ID]             INT,
                    [OBJECT_NAME]           VARCHAR(255),
                    [index_id]              INT,
                    [index_name]            VARCHAR(255),
                    [ROWS]                  BIGINT,
                    [SizeMB]                DECIMAL(19,3),
                    [IndexDepth]            INT,
                    [TYPE]                  INT,
                    [type_desc]             VARCHAR(30),
                    [fill_factor]           INT,
                    [is_unique]             INT,
                    [is_primary_key]        INT ,
                    [is_unique_constraint]  INT,
                    [index_columns_key]     VARCHAR(MAX),
                    [index_columns_include] VARCHAR(MAX),
                    [has_filter] BIT ,
                    [filter_definition] VARCHAR(MAX),
                    [currentFilegroupName]  VARCHAR(128),
                    [CurrentCompression]    VARCHAR(128));
  INSERT INTO @Results2
    SELECT
      [SCH].[schema_id], [SCH].[name] AS [SCHEMA_NAME],
      [objz].[object_id], [objz].[name] AS [OBJECT_NAME],
      [IDX].[index_id], ISNULL([IDX].[name], '---') AS [index_name],
      [partitions].[ROWS], [partitions].[SizeMB], INDEXPROPERTY([objz].[object_id], [IDX].[name], 'IndexDepth') AS [IndexDepth],
      [IDX].[type], [IDX].[type_desc], [IDX].[fill_factor],
      [IDX].[is_unique], [IDX].[is_primary_key], [IDX].[is_unique_constraint],
      ISNULL([Index_Columns].[index_columns_key], '---') AS [index_columns_key],
      ISNULL([Index_Columns].[index_columns_include], '---') AS [index_columns_include],
      [IDX].[has_filter],
      [IDX].[filter_definition],
      [filz].[name],
      ISNULL([p].[data_compression_desc],'')
    FROM [tempdb].[sys].[objects] AS [objz]
      INNER JOIN [tempdb].[sys].[schemas] AS [SCH] ON [objz].[schema_id]=[SCH].[schema_id]
      INNER JOIN [tempdb].[sys].[indexes] AS [IDX] ON [objz].[object_id]=[IDX].[object_id]
      INNER JOIN [sys].[filegroups] AS [filz] ON [IDX].[data_space_id] = [filz].[data_space_id]
      INNER JOIN [sys].[partitions] AS [p]     ON  [IDX].[object_id] =  [p].[object_id]  AND [IDX].[index_id] = [p].[index_id]
      INNER JOIN (
                  SELECT
                    [statz].[object_id], [statz].[index_id], SUM([statz].[row_count]) AS [ROWS],
                    CONVERT(NUMERIC(19,3), CONVERT(NUMERIC(19,3), SUM([statz].[in_row_reserved_page_count]+[statz].[lob_reserved_page_count]+[statz].[row_overflow_reserved_page_count]))/CONVERT(NUMERIC(19,3), 128)) AS [SizeMB]
                  FROM [tempdb].[sys].[dm_db_partition_stats] AS [statz]
                  GROUP BY [statz].[object_id], [statz].[index_id]
                 ) AS [partitions] 
        ON  [IDX].[object_id]=[partitions].[object_id] 
        AND [IDX].[index_id]=[partitions].[index_id]
    CROSS APPLY (
                 SELECT
                   LEFT([Index_Columns].[index_columns_key], LEN([Index_Columns].[index_columns_key])-1) AS [index_columns_key],
                  LEFT([Index_Columns].[index_columns_include], LEN([Index_Columns].[index_columns_include])-1) AS [index_columns_include]
                 FROM
                      (
                       SELECT
                              (
                              SELECT QUOTENAME([colz].[name]) + CASE WHEN [IXCOLS].[is_descending_key] = 0 THEN ' asc' ELSE ' desc' END + ',' + ' '
                               FROM [tempdb].[sys].[index_columns] AS [IXCOLS]
                                 INNER JOIN [tempdb].[sys].[columns] AS [colz]
                                   ON  [IXCOLS].[column_id]   = [colz].[column_id]
                                   AND [IXCOLS].[object_id] = [colz].[object_id]
                               WHERE [IXCOLS].[is_included_column] = 0
                                 AND [IDX].[object_id] = [IXCOLS].[object_id] 
                                 AND [IDX].[index_id] = [IXCOLS].[index_id]
                               ORDER BY [IXCOLS].[key_ordinal]
                               FOR XML PATH('')
                              ) AS [index_columns_key],
                             (
                             SELECT QUOTENAME([colz].[name]) + ',' + ' '
                              FROM [tempdb].[sys].[index_columns] AS [IXCOLS]
                                INNER JOIN [tempdb].[sys].[columns] AS [colz]
                                  ON  [IXCOLS].[column_id]   = [colz].[column_id]
                                  AND [IXCOLS].[object_id] = [colz].[object_id]
                              WHERE [IXCOLS].[is_included_column] = 1
                                AND [IDX].[object_id] = [IXCOLS].[object_id] 
                                AND [IDX].[index_id] = [IXCOLS].[index_id]
                              ORDER BY [IXCOLS].[index_column_id]
                              FOR XML PATH('')
                             ) AS [index_columns_include]
                      ) AS [Index_Columns]
                ) AS [Index_Columns]
    WHERE [SCH].[name]  LIKE CASE 
                                     WHEN @SCHEMANAME = '' COLLATE SQL_Latin1_General_CP1_CI_AS
                                     THEN [SCH].[name] 
                                     ELSE @SCHEMANAME 
                                   END
    AND [objz].[name] LIKE CASE 
                                  WHEN @TBLNAME = ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                                  THEN [objz].[name] 
                                  ELSE @TBLNAME 
                                END
    ORDER BY 
      [SCH].[name], 
      [objz].[name], 
      [IDX].[name];
--@Results2 table has both PK,s Uniques and indexes in thme...pull them out for adding to funal results:
  SET @CONSTRAINTSQLS = '' COLLATE SQL_Latin1_General_CP1_CI_AS;
  SET @INDEXSQLS      = '' COLLATE SQL_Latin1_General_CP1_CI_AS;
--##############################################################################
--constraints
--##############################################################################
  SELECT @CONSTRAINTSQLS = @CONSTRAINTSQLS 
         + CASE
             WHEN [is_primary_key] = 1 OR [is_unique] = 1
             THEN @vbCrLf
                  + 'CONSTRAINT   '  COLLATE SQL_Latin1_General_CP1_CI_AS + QUOTENAME([index_name]) + ' '
                  + SPACE(@STRINGLEN - LEN([index_name]))
                  + CASE  
                      WHEN [is_primary_key] = 1 
                      THEN ' PRIMARY KEY '  COLLATE SQL_Latin1_General_CP1_CI_AS
                      ELSE CASE  
                             WHEN [is_unique] = 1     
                             THEN ' UNIQUE      '     COLLATE SQL_Latin1_General_CP1_CI_AS  
                             ELSE ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                           END 
                    END
                  + [type_desc] 
                  + CASE 
                      WHEN [type_desc]='NONCLUSTERED' 
                      THEN ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                      ELSE '   ' 
                    END
                  + ' (' + [index_columns_key] + ')'
                  + CASE 
                      WHEN [index_columns_include] <> '---' 
                      THEN ' INCLUDE (' + [index_columns_include] + ')' 
                      ELSE ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                    END
                  + CASE
                      WHEN [has_filter] = 1 
                      THEN ' ' + [filter_definition]
                      ELSE ' '
                    END
                  + CASE WHEN [fill_factor] <> 0 OR [CurrentCompression] <> 'NONE'
                  THEN ' WITH (' + CASE
                                    WHEN [fill_factor] <> 0 
                                    THEN 'FILLFACTOR = ' + CONVERT(VARCHAR(30),[fill_factor]) 
                                    ELSE ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                                  END
                                + CASE
                                    WHEN [fill_factor] <> 0  AND [CurrentCompression] <> 'NONE' THEN ',DATA_COMPRESSION = ' + [CurrentCompression] + ' '
                                    WHEN [fill_factor] <> 0  AND [CurrentCompression]  = 'NONE' THEN ''
                                    WHEN [fill_factor]  = 0  AND [CurrentCompression] <> 'NONE' THEN 'DATA_COMPRESSION = ' + [CurrentCompression] + ' '
                                    ELSE ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                                  END
                                  + ')'
                  ELSE ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                  END 
             ELSE '' COLLATE SQL_Latin1_General_CP1_CI_AS
           END + ','
  FROM @Results2
  WHERE [type_desc] != 'HEAP'
    AND [is_primary_key] = 1 
    OR  [is_unique] = 1
  ORDER BY 
    [is_primary_key] DESC,
    [is_unique] DESC;
--##############################################################################
--indexes
--##############################################################################
  SELECT @INDEXSQLS = @INDEXSQLS 
         + CASE
             WHEN [is_primary_key] = 0 OR [is_unique] = 0
             THEN @vbCrLf
                  + 'CREATE '  COLLATE SQL_Latin1_General_CP1_CI_AS + [type_desc] + ' INDEX '  COLLATE SQL_Latin1_General_CP1_CI_AS + QUOTENAME([index_name]) + ' ' COLLATE SQL_Latin1_General_CP1_CI_AS
                  + @vbCrLf
                  + '   ON '  COLLATE SQL_Latin1_General_CP1_CI_AS
                  + QUOTENAME([SCHEMA_NAME]) + '.' + QUOTENAME([OBJECT_NAME])
                  + CASE 
                        WHEN [CurrentCompression] = 'COLUMNSTORE'  COLLATE SQL_Latin1_General_CP1_CI_AS
                        THEN ' ('  COLLATE SQL_Latin1_General_CP1_CI_AS+ [index_columns_include] + ')'  COLLATE SQL_Latin1_General_CP1_CI_AS
                        ELSE ' ('  COLLATE SQL_Latin1_General_CP1_CI_AS+ [index_columns_key] + ')' COLLATE SQL_Latin1_General_CP1_CI_AS
                    END
                  + CASE 
                      WHEN [CurrentCompression] = 'COLUMNSTORE'  COLLATE SQL_Latin1_General_CP1_CI_AS
                      THEN ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                      ELSE
                        CASE
                     WHEN [index_columns_include] <> '---' 
                     THEN @vbCrLf + '   INCLUDE ('  COLLATE SQL_Latin1_General_CP1_CI_AS + [index_columns_include] + ')'  COLLATE SQL_Latin1_General_CP1_CI_AS 
                     ELSE ''   COLLATE SQL_Latin1_General_CP1_CI_AS
                   END
                    END
                  --2008 filtered indexes syntax
                  + CASE 
                      WHEN [has_filter] = 1 
                      THEN @vbCrLf + '   WHERE '  COLLATE SQL_Latin1_General_CP1_CI_AS + [filter_definition]
                      ELSE ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                    END
                  + CASE WHEN [fill_factor] <> 0 OR [CurrentCompression] <> 'NONE'  COLLATE SQL_Latin1_General_CP1_CI_AS
                  THEN ' WITH ('  COLLATE SQL_Latin1_General_CP1_CI_AS + CASE
                                    WHEN [fill_factor] <> 0 
                                    THEN 'FILLFACTOR = '  COLLATE SQL_Latin1_General_CP1_CI_AS + CONVERT(VARCHAR(30),[fill_factor]) 
                                    ELSE ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                                  END
                                + CASE
                                    WHEN [fill_factor] <> 0  AND [CurrentCompression] <> 'NONE'  COLLATE SQL_Latin1_General_CP1_CI_AS THEN ',DATA_COMPRESSION = ' COLLATE SQL_Latin1_General_CP1_CI_AS + [CurrentCompression] + ' '
                                    WHEN [fill_factor] <> 0  AND [CurrentCompression]  = 'NONE'  COLLATE SQL_Latin1_General_CP1_CI_AS THEN ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                                    WHEN [fill_factor]  = 0  AND [CurrentCompression] <> 'NONE'  COLLATE SQL_Latin1_General_CP1_CI_AS THEN 'DATA_COMPRESSION = '  COLLATE SQL_Latin1_General_CP1_CI_AS+ [CurrentCompression] + ' '
                                    ELSE ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                                  END
                                  + ')' COLLATE SQL_Latin1_General_CP1_CI_AS
                  ELSE ''  COLLATE SQL_Latin1_General_CP1_CI_AS
                  END 
           END
  FROM @Results2
  WHERE [type_desc] != 'HEAP'
    AND [is_primary_key] = 0 
    AND [is_unique] = 0
  ORDER BY 
    [is_primary_key] DESC,
    [is_unique] DESC;
  IF @INDEXSQLS <> '' COLLATE SQL_Latin1_General_CP1_CI_AS
    SET @INDEXSQLS = @vbCrLf + 'GO'  COLLATE SQL_Latin1_General_CP1_CI_AS+ @vbCrLf + @INDEXSQLS;
--##############################################################################
--CHECK Constraints
--##############################################################################
  SET @CHECKCONSTSQLS = '';
  SELECT
    @CHECKCONSTSQLS = @CHECKCONSTSQLS
    + @vbCrLf
    + ISNULL('CONSTRAINT   ' + QUOTENAME([objz].[name]) + ' '
    + SPACE(@STRINGLEN - LEN([objz].[name]))
    + ' CHECK ' + ISNULL([CHECKS].[definition],'')
    + ',','')
  FROM [tempdb].[sys].[objects] AS [objz]
    INNER JOIN [tempdb].[sys].[check_constraints] AS [CHECKS] ON [objz].[object_id] = [CHECKS].[object_id]
  WHERE [objz].[type] = 'C'
    AND [objz].[parent_object_id] = @TABLE_ID;
--##############################################################################
--FOREIGN KEYS
--##############################################################################
  SET @FKSQLS = '' ;
    SELECT
    @FKSQLS=@FKSQLS
    + @vbCrLf + [MyAlias].[Command] FROM
(
SELECT
  DISTINCT
  --FK must be added AFTER the PK/unique constraints are added back.
  850 AS [ExecutionOrder],
  'CONSTRAINT ' 
  + QUOTENAME([conz].[name]) 
  + ' FOREIGN KEY (' 
  + [ChildCollection].[ChildColumns] 
  + ') REFERENCES ' 
  + QUOTENAME(SCHEMA_NAME([conz].[schema_id])) 
  + '.' 
  + QUOTENAME(OBJECT_NAME([conz].[referenced_object_id])) 
  + ' (' + [ParentCollection].[ParentColumns] 
  + ') ' 
   +  CASE [conz].[update_referential_action]
                                        WHEN 0 THEN '' --' ON UPDATE NO ACTION '
                                        WHEN 1 THEN ' ON UPDATE CASCADE '
                                        WHEN 2 THEN ' ON UPDATE SET NULL '
                                        ELSE ' ON UPDATE SET DEFAULT '
                                    END
                  + CASE [conz].[delete_referential_action]
                                        WHEN 0 THEN '' --' ON DELETE NO ACTION '
                                        WHEN 1 THEN ' ON DELETE CASCADE '
                                        WHEN 2 THEN ' ON DELETE SET NULL '
                                        ELSE ' ON DELETE SET DEFAULT '
                                    END
                  + CASE [conz].[is_not_for_replication]
                        WHEN 1 THEN ' NOT FOR REPLICATION '
                        ELSE ''
                    END
  + ',' AS [Command]
FROM   [sys].[foreign_keys] AS [conz]
       INNER JOIN [sys].[foreign_key_columns] AS [colz]
         ON [conz].[object_id] = [colz].[constraint_object_id]
      
       INNER JOIN (--gets my child tables column names   
SELECT
 [conz].[name],
 --technically, FK's can contain up to 16 columns, but real life is often a single column. coding here is for all columns
 [ChildColumns] = STUFF((SELECT 
                         ',' + QUOTENAME([REFZ].[name])
                       FROM   [sys].[foreign_key_columns] AS [fkcolz]
                              INNER JOIN [sys].[columns] AS [REFZ]
                                ON [fkcolz].[parent_object_id] = [REFZ].[object_id]
                                   AND [fkcolz].[parent_column_id] = [REFZ].[column_id]
                       WHERE [fkcolz].[parent_object_id] = [conz].[parent_object_id]
                           AND [fkcolz].[constraint_object_id] = [conz].[object_id]
                         ORDER  BY
                        [fkcolz].[constraint_column_id]
                       FOR XML PATH(''), TYPE).[value]('.','varchar(max)'),1,1,'')
FROM   [sys].[foreign_keys] AS [conz]
      INNER JOIN [sys].[foreign_key_columns] AS [colz]
        ON [conz].[object_id] = [colz].[constraint_object_id]
 WHERE [conz].[parent_object_id]= @TABLE_ID
GROUP  BY
[conz].[name],
[conz].[parent_object_id],--- without GROUP BY multiple rows are returned
 [conz].[object_id]
    ) AS [ChildCollection]
         ON [conz].[name] = [ChildCollection].[name]
       INNER JOIN (--gets the parent tables column names for the FK reference
                  SELECT
                     [conz].[name],
                     [ParentColumns] = STUFF((SELECT
                                              ',' + [REFZ].[name]
                                            FROM   [sys].[foreign_key_columns] AS [fkcolz]
                                                   INNER JOIN [sys].[columns] AS [REFZ]
                                                     ON [fkcolz].[referenced_object_id] = [REFZ].[object_id]
                                                        AND [fkcolz].[referenced_column_id] = [REFZ].[column_id]
                                            WHERE  [fkcolz].[referenced_object_id] = [conz].[referenced_object_id]
                                              AND [fkcolz].[constraint_object_id] = [conz].[object_id]
                                            ORDER BY [fkcolz].[constraint_column_id]
                                            FOR XML PATH(''), TYPE).[value]('.','varchar(max)'),1,1,'')
                   FROM   [sys].[foreign_keys] AS [conz]
                          INNER JOIN [sys].[foreign_key_columns] AS [colz]
                            ON [conz].[object_id] = [colz].[constraint_object_id]
                           -- AND colz.parent_column_id 
                   GROUP  BY
                    [conz].[name],
                    [conz].[referenced_object_id],--- without GROUP BY multiple rows are returned
                    [conz].[object_id]
                  ) AS [ParentCollection]
         ON [conz].[name] = [ParentCollection].[name]
)AS [MyAlias];
--##############################################################################
--RULES
--##############################################################################
  SET @RULESCONSTSQLS = ''  COLLATE SQL_Latin1_General_CP1_CI_AS;
  SELECT
    @RULESCONSTSQLS = @RULESCONSTSQLS
    + ISNULL(
             @vbCrLf
             + 'if not exists(SELECT [name] FROM tempdb.sys.objects WHERE TYPE=''R'' AND schema_id = '  COLLATE SQL_Latin1_General_CP1_CI_AS 
             + CONVERT(VARCHAR(30),[objz].[schema_id]) 
             + ' AND [name] = '''  COLLATE SQL_Latin1_General_CP1_CI_AS
             + QUOTENAME(OBJECT_NAME([colz].[rule_object_id])) 
             + ''')'  COLLATE SQL_Latin1_General_CP1_CI_AS
             + @vbCrLf
             + [MODS].[definition]  + @vbCrLf 
             + 'GO'  COLLATE SQL_Latin1_General_CP1_CI_AS +  @vbCrLf
             + 'EXEC sp_binderule  '  COLLATE SQL_Latin1_General_CP1_CI_AS
             + QUOTENAME([objz].[name]) 
             + ', '''  COLLATE SQL_Latin1_General_CP1_CI_AS 
             + QUOTENAME(OBJECT_NAME([colz].[object_id])) 
             + '.'  COLLATE SQL_Latin1_General_CP1_CI_AS + QUOTENAME([colz].[name]) 
             + ''''  COLLATE SQL_Latin1_General_CP1_CI_AS
             + @vbCrLf 
             + 'GO' ,''  COLLATE SQL_Latin1_General_CP1_CI_AS)
  FROM [tempdb].[sys].[columns] [colz] 
    INNER JOIN [tempdb].[sys].[objects] [objz]
      ON [objz].[object_id] = [colz].[object_id]
    INNER JOIN [tempdb].[sys].[sql_modules] AS [MODS]
      ON [colz].[rule_object_id] = [MODS].[object_id]
  WHERE [colz].[rule_object_id] <> 0
    AND [colz].[object_id] = @TABLE_ID;
--##############################################################################
--TRIGGERS
--##############################################################################
  SET @TRIGGERSTATEMENT = '';
  SELECT
    @TRIGGERSTATEMENT = @TRIGGERSTATEMENT +  @vbCrLf + [MODS].[definition] + @vbCrLf + 'GO'
  FROM [tempdb].[sys].[sql_modules] AS [MODS]
  WHERE [MODS].[object_id] IN(SELECT
                         [objz].[object_id]
                       FROM [tempdb].[sys].[objects] AS [objz]
                       WHERE [objz].[type] = 'TR'
                       AND [objz].[parent_object_id] = @TABLE_ID);
  IF @TRIGGERSTATEMENT <> ''  COLLATE SQL_Latin1_General_CP1_CI_AS
    SET @TRIGGERSTATEMENT = @vbCrLf + 'GO'  COLLATE SQL_Latin1_General_CP1_CI_AS + @vbCrLf + @TRIGGERSTATEMENT;
--##############################################################################
--NEW SECTION QUERY ALL EXTENDED PROPERTIES
--##############################################################################
  SET @EXTENDEDPROPERTIES = ''  COLLATE SQL_Latin1_General_CP1_CI_AS;
  SELECT  @EXTENDEDPROPERTIES =
          @EXTENDEDPROPERTIES + @vbCrLf +
         'EXEC tempdb.sys.sp_addextendedproperty
          @name = N'''  COLLATE SQL_Latin1_General_CP1_CI_AS
          + [name] 
          + ''', @value = N'''  COLLATE SQL_Latin1_General_CP1_CI_AS
          + REPLACE(CONVERT(VARCHAR(MAX),[value]),'''','''''') + ''',
          @level0type = N''SCHEMA'', @level0name = '  COLLATE SQL_Latin1_General_CP1_CI_AS
          + QUOTENAME(@SCHEMANAME + ',
          @level1type = N''TABLE'', @level1name = ['  COLLATE SQL_Latin1_General_CP1_CI_AS
          + @TBLNAME) 
          + '];' COLLATE SQL_Latin1_General_CP1_CI_AS
 --SELECT objtype, objname, name, value
  FROM [sys].[fn_listextendedproperty] (NULL, 'schema', @SCHEMANAME, 'table', @TBLNAME, NULL, NULL);
  --OMacoder suggestion for column extended properties http://www.sqlservercentral.com/Forums/FindPost1651606.aspx
  SELECT @EXTENDEDPROPERTIES =
         @EXTENDEDPROPERTIES + @vbCrLf +
         'EXEC sys.sp_addextendedproperty
         @name = N'''  COLLATE SQL_Latin1_General_CP1_CI_AS
         + [name] 
         + ''', @value = N'''  COLLATE SQL_Latin1_General_CP1_CI_AS
         + REPLACE(CONVERT(VARCHAR(MAX),[value]),'''','''''') 
         + ''',
         @level0type = N''SCHEMA'', @level0name = '  COLLATE SQL_Latin1_General_CP1_CI_AS
         + QUOTENAME(@SCHEMANAME) + ',
         @level1type = N''TABLE'', @level1name = '  COLLATE SQL_Latin1_General_CP1_CI_AS
         + QUOTENAME(@TBLNAME) + ',
         @level2type = N''COLUMN'', @level2name = '  COLLATE SQL_Latin1_General_CP1_CI_AS
         + QUOTENAME([objname]) + ';' COLLATE SQL_Latin1_General_CP1_CI_AS
  --SELECT objtype, objname, name, value
  FROM [sys].[fn_listextendedproperty] (NULL, 'schema', @SCHEMANAME, 'table', @TBLNAME, 'column', NULL);
  IF @EXTENDEDPROPERTIES <> '' COLLATE SQL_Latin1_General_CP1_CI_AS
    SET @EXTENDEDPROPERTIES = @vbCrLf + 'GO' COLLATE SQL_Latin1_General_CP1_CI_AS + @vbCrLf + @EXTENDEDPROPERTIES;
--##############################################################################
--FINAL CLEANUP AND PRESENTATION
--##############################################################################
--at this point, there is a trailing comma, or it blank
  SELECT
    @FINALSQL = @FINALSQL
                + @CONSTRAINTSQLS
                + @CHECKCONSTSQLS
                + @FKSQLS;
--note that this trims the trailing comma from the end of the statements
  SET @FINALSQL = SUBSTRING(@FINALSQL,1,LEN(@FINALSQL) -1) ;
  SET @FINALSQL = @FINALSQL + ')'  COLLATE SQL_Latin1_General_CP1_CI_AS + @vbCrLf ;
  SET @input = @vbCrLf
       + @FINALSQL
       + @INDEXSQLS
       + @RULESCONSTSQLS
       + @TRIGGERSTATEMENT
       + @EXTENDEDPROPERTIES
--ten years worth of days from todays date:
   ;WITH [E01]([N]) AS (SELECT 1 UNION ALL SELECT 1 UNION ALL
                    SELECT 1 UNION ALL SELECT 1 UNION ALL
                    SELECT 1 UNION ALL SELECT 1 UNION ALL
                    SELECT 1 UNION ALL SELECT 1 UNION ALL
                    SELECT 1 UNION ALL SELECT 1), --         10 or 10E01 rows
         [E02]([N]) AS (SELECT 1 FROM [E01] AS [a], [E01] AS [b]),  --        100 or 10E02 rows
         [E04]([N]) AS (SELECT 1 FROM [E02] AS [a], [E02] AS [b]),  --     10,000 or 10E04 rows
         [E08]([N]) AS (SELECT 1 FROM [E04] AS [a], [E04] AS [b]),  --100,000,000 or 10E08 rows
         --E16(N) AS (SELECT 1 FROM E08 a, E08 b),  --10E16 or more rows than you'll EVER need,
         [Tally]([N]) AS (SELECT ROW_NUMBER() OVER (ORDER BY [E08].[N]) FROM [E08]),
       [ItemSplit](
                 [ItemOrder],
                 [Item]
                ) AS (
                      SELECT [Tally].[N],
                        SUBSTRING(@vbCrLf + @input + @vbCrLf,[Tally].[N] + DATALENGTH(@vbCrLf),CHARINDEX(@vbCrLf,@vbCrLf + @input + @vbCrLf,[Tally].[N] + DATALENGTH(@vbCrLf)) - [Tally].[N] - DATALENGTH(@vbCrLf))
                      FROM [Tally]
                      WHERE [Tally].[N] < DATALENGTH(@vbCrLf + @input)
                      --WHERE N < DATALENGTH(@vbCrLf + @input) -- REMOVED added @vbCrLf
                        AND SUBSTRING(@vbCrLf + @input + @vbCrLf,[Tally].[N],DATALENGTH(@vbCrLf)) = @vbCrLf --Notice how we find the delimiter
                     )
  SELECT
    --row_number() over (order by ItemOrder) as ItemID,
    [ItemSplit].[Item]
  FROM [ItemSplit];
         
  RETURN;     
END;
go

CREATE TRIGGER trgAfterInsert
ON ExampleTable
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    -- Record newly inserted data in AuditLog
    INSERT INTO AuditLog (TableName, Operation, InsertedDateTime, NewData)
    SELECT 
        'ExampleTable' AS TableName,
        'INSERT' AS Operation,
        GETDATE() AS InsertedDateTime,
        (SELECT * FROM INSERTED FOR JSON AUTO) AS NewData;
END;

go

CREATE TRIGGER trgAfterInsertOnEmployees
ON Employees
AFTER INSERT
AS
BEGIN
    DECLARE @EmployeeID INT,
            @FirstName NVARCHAR(100),
            @LastName NVARCHAR(100),
            @ChangedBy NVARCHAR(50);

    -- Read the inserted values
    SELECT @EmployeeID = i.EmployeeID,
           @FirstName = i.FirstName,
           @LastName = i.LastName,
           @ChangedBy = SYSTEM_USER
    FROM INSERTED i;

    -- Insert a record into EmployeeAudit
    INSERT INTO EmployeeAudit (EmployeeID, ChangeType, ChangedBy, OldValue, NewValue)
    VALUES (@EmployeeID, 'INSERT', @ChangedBy, '', CONCAT(@FirstName, ' ', @LastName));
END;
go

CREATE TRIGGER trgAfterInsertOrUpdate
    ON XmlTypesTest
    AFTER INSERT, UPDATE
    AS
BEGIN
    -- Record insert or update operations in XmlTypesTestChanges
    INSERT INTO XmlTypesTestChanges (Id, ChangeType, XmlColumn, ChangeDate)
    SELECT i.Id,
           CASE
               WHEN EXISTS (SELECT 1 FROM deleted WHERE deleted.Id = i.Id) THEN 'UPDATE'
               ELSE 'INSERT'
               END,
           i.XmlColumn,
           GETDATE()
    FROM inserted i;
END;
go

