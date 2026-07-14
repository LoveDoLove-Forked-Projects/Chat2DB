/*
 Navicat Premium Data Transfer

 Source Server         : 11
 Source Server Type    : SQL Server
 Source Server Version : 16004105 (16.00.4105)
 Source Host           : 111.230.113.55:1433
 Source Catalog        : test
 Source Schema         : dbo

 Target Server Type    : SQL Server
 Target Server Version : 16004105 (16.00.4105)
 File Encoding         : 65001

 Date: 18/07/2024 14:57:26
*/


-- ----------------------------
-- Table structure for ChildTable
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[ChildTable]') AND type IN ('U'))
	DROP TABLE [dbo].[ChildTable]
GO

CREATE TABLE [dbo].[ChildTable] (
  [ChildID] int  NOT NULL,
  [ColumnA] int  NULL,
  [ColumnB] int  NULL
)
GO

ALTER TABLE [dbo].[ChildTable] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of ChildTable
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for ChildTable2
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[ChildTable2]') AND type IN ('U'))
	DROP TABLE [dbo].[ChildTable2]
GO

CREATE TABLE [dbo].[ChildTable2] (
  [ChildID] int  NOT NULL,
  [ColumnA] int  NULL,
  [ColumnB] int  NULL
)
GO

ALTER TABLE [dbo].[ChildTable2] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of ChildTable2
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for Department
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[Department]') AND type IN ('U'))
	DROP TABLE [dbo].[Department]
GO

CREATE TABLE [dbo].[Department] (
  [DeptID] int  NOT NULL,
  [DeptName] varchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL,
  [ManagerID] int  NULL,
  [ParentDeptID] int  NULL,
  [ValidFrom] datetime2(7)  NOT NULL,
  [ValidTo] datetime2(7)  NOT NULL
)
GO

ALTER TABLE [dbo].[Department] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of Department
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for Employee
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[Employee]') AND type IN ('U'))
	DROP TABLE [dbo].[Employee]
GO

CREATE TABLE [dbo].[Employee] (
  [EmployeeID] int  NOT NULL,
  [LastName] nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [FirstName] nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [BirthDate] date  NULL,
  [Gender] char(1) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [HireDate] date  NULL,
  [Salary] decimal(10,2)  NULL
)
GO

ALTER TABLE [dbo].[Employee] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'Unique identifier for each employee',
'SCHEMA', N'dbo',
'TABLE', N'Employee',
'COLUMN', N'EmployeeID'
GO

EXEC sp_addextendedproperty
'MS_Description', N'Last name of the employee',
'SCHEMA', N'dbo',
'TABLE', N'Employee',
'COLUMN', N'LastName'
GO

EXEC sp_addextendedproperty
'MS_Description', N'First name of the employee',
'SCHEMA', N'dbo',
'TABLE', N'Employee',
'COLUMN', N'FirstName'
GO

EXEC sp_addextendedproperty
'MS_Description', N'Birth date of the employee',
'SCHEMA', N'dbo',
'TABLE', N'Employee',
'COLUMN', N'BirthDate'
GO

EXEC sp_addextendedproperty
'MS_Description', N'Gender of the employee (M/F)',
'SCHEMA', N'dbo',
'TABLE', N'Employee',
'COLUMN', N'Gender'
GO

EXEC sp_addextendedproperty
'MS_Description', N'Hire date of the employee',
'SCHEMA', N'dbo',
'TABLE', N'Employee',
'COLUMN', N'HireDate'
GO

EXEC sp_addextendedproperty
'MS_Description', N'Salary of the employee',
'SCHEMA', N'dbo',
'TABLE', N'Employee',
'COLUMN', N'Salary'
GO

EXEC sp_addextendedproperty
'MS_Description', N'Employee information table',
'SCHEMA', N'dbo',
'TABLE', N'Employee'
GO


-- ----------------------------
-- Records of Employee
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for Employee2
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[Employee2]') AND type IN ('U'))
	DROP TABLE [dbo].[Employee2]
GO

CREATE TABLE [dbo].[Employee2] (
  [EmployeeID] int  NOT NULL,
  [FullName] nvarchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [Age] int  NULL,
  [Department] nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [Salary] decimal(10,2)  NULL
)
GO

ALTER TABLE [dbo].[Employee2] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of Employee2
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for Employees
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[Employees]') AND type IN ('U'))
	DROP TABLE [dbo].[Employees]
GO

CREATE TABLE [dbo].[Employees] (
  [EmployeeID] int  NOT NULL,
  [FirstName] nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL,
  [LastName] nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL,
  [BaseSalary] decimal(10,2)  NOT NULL,
  [Bonus] decimal(10,2)  NULL,
  [TotalCompensation] AS ([BaseSalary]+[Bonus])
)
GO

ALTER TABLE [dbo].[Employees] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of Employees
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for Employees2
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[Employees2]') AND type IN ('U'))
	DROP TABLE [dbo].[Employees2]
GO

CREATE TABLE [dbo].[Employees2] (
  [EmployeeID] int  NOT NULL,
  [FirstName] nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL,
  [LastName] nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL,
  [BaseSalary] decimal(10,2)  NOT NULL,
  [Bonus] decimal(10,2)  NULL,
  [TotalCompensation] AS ([BaseSalary]+[Bonus])
)
GO

ALTER TABLE [dbo].[Employees2] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of Employees2
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for Employees2024622
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[Employees2024622]') AND type IN ('U'))
	DROP TABLE [dbo].[Employees2024622]
GO

CREATE TABLE [dbo].[Employees2024622] (
  [EmployeeID] int  NOT NULL,
  [EmployeeName] nvarchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL
)
GO

ALTER TABLE [dbo].[Employees2024622] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of Employees2024622
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for MSSQL_TemporalHistoryFor_859150106
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[MSSQL_TemporalHistoryFor_859150106]') AND type IN ('U'))
	DROP TABLE [dbo].[MSSQL_TemporalHistoryFor_859150106]
GO

CREATE TABLE [dbo].[MSSQL_TemporalHistoryFor_859150106] (
  [DeptID] int  NOT NULL,
  [DeptName] varchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL,
  [ManagerID] int  NULL,
  [ParentDeptID] int  NULL,
  [ValidFrom] datetime2(7)  NOT NULL,
  [ValidTo] datetime2(7)  NOT NULL
)
GO

ALTER TABLE [dbo].[MSSQL_TemporalHistoryFor_859150106] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of MSSQL_TemporalHistoryFor_859150106
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for MyPartitionedTable
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[MyPartitionedTable]') AND type IN ('U'))
	DROP TABLE [dbo].[MyPartitionedTable]
GO

CREATE TABLE [dbo].[MyPartitionedTable] (
  [ID] int  NOT NULL,
  [Name] nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL,
  [OrderDate] datetime  NOT NULL
)
GO

ALTER TABLE [dbo].[MyPartitionedTable] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of MyPartitionedTable
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for MyPartitionedTable2
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[MyPartitionedTable2]') AND type IN ('U'))
	DROP TABLE [dbo].[MyPartitionedTable2]
GO

CREATE TABLE [dbo].[MyPartitionedTable2] (
  [ID] int  NOT NULL,
  [Name] nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS  NOT NULL,
  [OrderDate] datetime  NOT NULL
)
GO

ALTER TABLE [dbo].[MyPartitionedTable2] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of MyPartitionedTable2
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for Orders
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[Orders]') AND type IN ('U'))
	DROP TABLE [dbo].[Orders]
GO

CREATE TABLE [dbo].[Orders] (
  [OrderID] int  NOT NULL,
  [ProductID] int  NOT NULL,
  [CustomerID] int  NULL,
  [OrderDate] date  NULL,
  [Quantity] int  NULL,
  [OrderReference] nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL
)
GO

ALTER TABLE [dbo].[Orders] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of Orders
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for ParentTable
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[ParentTable]') AND type IN ('U'))
	DROP TABLE [dbo].[ParentTable]
GO

CREATE TABLE [dbo].[ParentTable] (
  [ColumnA] int  NOT NULL,
  [ColumnB] int  NOT NULL
)
GO

ALTER TABLE [dbo].[ParentTable] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of ParentTable
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for Sales
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[Sales]') AND type IN ('U'))
	DROP TABLE [dbo].[Sales]
GO

CREATE TABLE [dbo].[Sales] (
  [SaleID] int  NOT NULL,
  [ProductID] int  NULL,
  [Quantity] int  NULL,
  [UnitPrice] decimal(10,2)  NULL,
  [TotalAmount] AS ([Quantity]*[UnitPrice]) PERSISTED
)
GO

ALTER TABLE [dbo].[Sales] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of Sales
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for Sales_with_options
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[Sales_with_options]') AND type IN ('U'))
	DROP TABLE [dbo].[Sales_with_options]
GO

CREATE TABLE [dbo].[Sales_with_options] (
  [SaleID] int  IDENTITY(1,1) NOT NULL,
  [ProductID] int  NOT NULL,
  [CustomerID] int  NOT NULL,
  [SaleDate] datetime DEFAULT getdate() NULL,
  [Amount] decimal(18,2)  NULL,
  [Discount] decimal(5,2) DEFAULT 0 NULL,
  [Comment] nvarchar(1000) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL
)
GO

ALTER TABLE [dbo].[Sales_with_options] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of Sales_with_options
-- ----------------------------
BEGIN TRANSACTION
GO

SET IDENTITY_INSERT [dbo].[Sales_with_options] ON
GO

SET IDENTITY_INSERT [dbo].[Sales_with_options] OFF
GO

COMMIT
GO


-- ----------------------------
-- Table structure for Sales_with_options2
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[Sales_with_options2]') AND type IN ('U'))
	DROP TABLE [dbo].[Sales_with_options2]
GO

CREATE TABLE [dbo].[Sales_with_options2] (
  [SaleID] int  IDENTITY(1,1) NOT NULL,
  [ProductID] int  NOT NULL,
  [CustomerID] int  NOT NULL,
  [SaleDate] datetime DEFAULT getdate() NULL,
  [Amount] decimal(18,2)  NULL,
  [Discount] decimal(5,2) DEFAULT 0 NULL,
  [Comment] nvarchar(1000) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL
)
GO

ALTER TABLE [dbo].[Sales_with_options2] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of Sales_with_options2
-- ----------------------------
BEGIN TRANSACTION
GO

SET IDENTITY_INSERT [dbo].[Sales_with_options2] ON
GO

SET IDENTITY_INSERT [dbo].[Sales_with_options2] OFF
GO

COMMIT
GO


-- ----------------------------
-- Table structure for table_name'
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[table_name']') AND type IN ('U'))
	DROP TABLE [dbo].[table_name']
GO

CREATE TABLE [dbo].[table_name'] (
  [column_1] int  NULL
)
GO

ALTER TABLE [dbo].[table_name'] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of table_name'
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for table_unique_constraint_
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[table_unique_constraint_]') AND type IN ('U'))
	DROP TABLE [dbo].[table_unique_constraint_]
GO

CREATE TABLE [dbo].[table_unique_constraint_] (
  [column_1] int  NULL,
  [column_2] int  NULL,
  [column_3] int  NOT NULL,
  [column_4] int  NOT NULL,
  [column_5] int  NULL,
  [column_6] int  NULL,
  [column_7] int  NULL,
  [column_8] int  NULL
)
GO

ALTER TABLE [dbo].[table_unique_constraint_] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of table_unique_constraint_
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for test_constraint
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[test_constraint]') AND type IN ('U'))
	DROP TABLE [dbo].[test_constraint]
GO

CREATE TABLE [dbo].[test_constraint] (
  [id] int  NULL,
  [age] int  NULL,
  [unique_column] int  NOT NULL,
  [column_4] int  NULL,
  [column_5] int  NOT NULL,
  [column_6] int  NULL,
  [column_7] int  NULL,
  [column_8] int  NULL,
  [column_9] varchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL
)
GO

ALTER TABLE [dbo].[test_constraint] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'''??''',
'SCHEMA', N'dbo',
'TABLE', N'test_constraint',
'COLUMN', N'id'
GO


-- ----------------------------
-- Records of test_constraint
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for test_data_types
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[test_data_types]') AND type IN ('U'))
	DROP TABLE [dbo].[test_data_types]
GO

CREATE TABLE [dbo].[test_data_types] (
  [column_1] date  NULL,
  [column_2] datetime  NOT NULL,
  [column_3] datetime2(6)  NULL,
  [column_4] datetime2(7)  NULL,
  [column_5] datetimeoffset(5)  NULL,
  [column_6] datetimeoffset(7)  NULL,
  [column_7] smalldatetime  NULL,
  [column_8] time(7)  NULL,
  [column_9] time(5)  NULL,
  [column_10] bit  NULL,
  [column_11] decimal(7,2)  NULL,
  [column_12] numeric(7,4)  NULL,
  [column_13] float(53)  NULL,
  [column_14] real  NULL,
  [column_15] int DEFAULT 5 NULL,
  [column_16] bigint  NULL,
  [column_17] smallint  NULL,
  [column_18] tinyint  NULL,
  [column_19] money  NULL,
  [column_20] smallmoney  NULL,
  [column_21] binary(567)  NULL,
  [column_22] binary(1)  NULL,
  [column_23] varbinary(1)  NULL,
  [column_24] varbinary(3442)  NULL,
  [column_25] varbinary(max)  NULL,
  [column_26] char(1) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_27] char(345) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_28] varchar(1) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_29] varchar(234) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_30] varchar(max) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_31] nchar(1) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_32] nchar(234) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_33] nvarchar(1) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_34] nvarchar(4000) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_35] nvarchar(max) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_36] xml  NULL,
  [column_37] time(1)  NULL,
  [column_38] nvarchar(1) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_39] varchar(1) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_40] int  NULL
)
GO

ALTER TABLE [dbo].[test_data_types] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'''''column1''',
'SCHEMA', N'dbo',
'TABLE', N'test_data_types',
'COLUMN', N'column_1'
GO

EXEC sp_addextendedproperty
'MS_Description', N'column2',
'SCHEMA', N'dbo',
'TABLE', N'test_data_types',
'COLUMN', N'column_2'
GO

EXEC sp_addextendedproperty
'MS_Description', N'test_data_types',
'SCHEMA', N'dbo',
'TABLE', N'test_data_types'
GO


-- ----------------------------
-- Records of test_data_types
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for test_data_types2
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[test_data_types2]') AND type IN ('U'))
	DROP TABLE [dbo].[test_data_types2]
GO

CREATE TABLE [dbo].[test_data_types2] (
  [column_1] date  NULL,
  [column_2] datetime  NOT NULL,
  [column_3] datetime2(6)  NULL,
  [column_4] datetime2(7)  NULL,
  [column_5] datetimeoffset(5)  NULL,
  [column_6] datetimeoffset(7)  NULL,
  [column_7] smalldatetime  NULL,
  [column_8] time(7)  NULL,
  [column_9] time(5)  NULL,
  [column_10] bit  NULL,
  [column_11] decimal(7,2)  NULL,
  [column_12] numeric(7,4)  NULL,
  [column_13] float(53)  NULL,
  [column_14] real  NULL,
  [column_15] int  NULL,
  [column_16] bigint  NULL,
  [column_17] smallint  NULL,
  [column_18] tinyint  NULL,
  [column_19] money  NULL,
  [column_20] smallmoney  NULL,
  [column_21] binary(567)  NULL,
  [column_22] binary(1)  NULL,
  [column_23] varbinary(1)  NULL,
  [column_24] varbinary(3442)  NULL,
  [column_25] varbinary(max)  NULL,
  [column_26] char(1) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_27] char(345) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_28] varchar(1) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_29] varchar(234) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_30] varchar(max) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_31] nchar(1) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_32] nchar(234) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_33] nvarchar(1) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_34] nvarchar(4000) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_35] nvarchar(max) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_36] xml  NULL,
  [column_37] time(1)  NULL,
  [column_38] nvarchar(1) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_39] varchar(1) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL
)
GO

ALTER TABLE [dbo].[test_data_types2] SET (LOCK_ESCALATION = TABLE)
GO

EXEC sp_addextendedproperty
'MS_Description', N'column_1',
'SCHEMA', N'dbo',
'TABLE', N'test_data_types2',
'COLUMN', N'column_1'
GO

EXEC sp_addextendedproperty
'MS_Description', N'column2',
'SCHEMA', N'dbo',
'TABLE', N'test_data_types2',
'COLUMN', N'column_2'
GO

EXEC sp_addextendedproperty
'MS_Description', N'test_data_types2',
'SCHEMA', N'dbo',
'TABLE', N'test_data_types2'
GO


-- ----------------------------
-- Records of test_data_types2
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for test_data_types3
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[test_data_types3]') AND type IN ('U'))
	DROP TABLE [dbo].[test_data_types3]
GO

CREATE TABLE [dbo].[test_data_types3] (
  [column_1] date  NULL,
  [column_2] datetime  NOT NULL,
  [column_3] datetime2(6)  NULL,
  [column_4] datetime2(7)  NULL,
  [column_5] datetimeoffset(5)  NULL,
  [column_6] datetimeoffset(7)  NULL,
  [column_7] smalldatetime  NULL,
  [column_8] time(7)  NULL,
  [column_9] time(5)  NULL,
  [column_10] bit  NULL,
  [column_11] decimal(7,2)  NULL,
  [column_12] numeric(7,4)  NULL,
  [column_13] float(53)  NULL,
  [column_14] real  NULL,
  [column_15] int  NULL,
  [column_16] bigint  NULL,
  [column_17] smallint  NULL,
  [column_18] tinyint  NULL,
  [column_19] money  NULL,
  [column_20] smallmoney  NULL,
  [column_21] binary(567)  NULL,
  [column_22] binary(1)  NULL,
  [column_23] varbinary(1)  NULL,
  [column_24] varbinary(3442)  NULL,
  [column_25] varbinary(max)  NULL,
  [column_26] char(1) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_27] char(345) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_28] varchar(1) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_29] varchar(234) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_30] varchar(max) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_31] nchar(1) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_32] nchar(234) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_33] nvarchar(1) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_34] nvarchar(4000) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_35] nvarchar(max) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_36] xml  NULL,
  [column_37] time(1)  NULL,
  [column_38] nvarchar(1) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL,
  [column_39] varchar(1) COLLATE SQL_Latin1_General_CP1_CI_AS  NULL
)
GO

ALTER TABLE [dbo].[test_data_types3] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of test_data_types3
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for test_foreign_constraint
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[test_foreign_constraint]') AND type IN ('U'))
	DROP TABLE [dbo].[test_foreign_constraint]
GO

CREATE TABLE [dbo].[test_foreign_constraint] (
  [column_1] int  NULL,
  [column_2] int  NULL
)
GO

ALTER TABLE [dbo].[test_foreign_constraint] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of test_foreign_constraint
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for test_increment
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[test_increment]') AND type IN ('U'))
	DROP TABLE [dbo].[test_increment]
GO

CREATE TABLE [dbo].[test_increment] (
  [column_1] int  IDENTITY(2,3) NOT NULL
)
GO

ALTER TABLE [dbo].[test_increment] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of test_increment
-- ----------------------------
BEGIN TRANSACTION
GO

SET IDENTITY_INSERT [dbo].[test_increment] ON
GO

SET IDENTITY_INSERT [dbo].[test_increment] OFF
GO

COMMIT
GO


-- ----------------------------
-- Table structure for test_increment2
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[test_increment2]') AND type IN ('U'))
	DROP TABLE [dbo].[test_increment2]
GO

CREATE TABLE [dbo].[test_increment2] (
  [column1] int  IDENTITY NOT NULL,
  [column2] int  NULL
)
GO

ALTER TABLE [dbo].[test_increment2] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of test_increment2
-- ----------------------------
BEGIN TRANSACTION
GO

SET IDENTITY_INSERT [dbo].[test_increment2] ON
GO

SET IDENTITY_INSERT [dbo].[test_increment2] OFF
GO

COMMIT
GO


-- ----------------------------
-- Table structure for test_sparse_table
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[test_sparse_table]') AND type IN ('U'))
	DROP TABLE [dbo].[test_sparse_table]
GO

CREATE TABLE [dbo].[test_sparse_table] (
  [column1] int SPARSE  NULL,
  [column2] int  NULL
)
GO

ALTER TABLE [dbo].[test_sparse_table] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of test_sparse_table
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- Table structure for test_table
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[test_table]') AND type IN ('U'))
	DROP TABLE [dbo].[test_table]
GO

CREATE TABLE [dbo].[test_table] (
  [column1] int  NOT NULL,
  [column2] int  NOT NULL,
  [column_3] int  NULL
)
GO

ALTER TABLE [dbo].[test_table] SET (LOCK_ESCALATION = TABLE)
GO


-- ----------------------------
-- Records of test_table
-- ----------------------------
BEGIN TRANSACTION
GO

COMMIT
GO


-- ----------------------------
-- View structure for PurchaseOrderDetailView
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[PurchaseOrderDetailView]') AND type IN ('V'))
	DROP VIEW [dbo].[PurchaseOrderDetailView]
GO

CREATE VIEW [dbo].[PurchaseOrderDetailView] AS SELECT PurchaseOrderID,
       LineNumber,
       ProductID,
       UnitPrice,
       OrderQty,
       ReceivedQty,
       RejectedQty,
       DueDate
FROM dbo.PurchaseOrderDetail
GO


-- ----------------------------
-- procedure structure for sp_GenerateTableDDLScript
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_GenerateTableDDLScript]') AND type IN ('P', 'PC', 'RF', 'X'))
	DROP PROCEDURE[dbo].[sp_GenerateTableDDLScript]
GO

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
GO


-- ----------------------------
-- procedure structure for sp_GetDDLa
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_GetDDLa]') AND type IN ('P', 'PC', 'RF', 'X'))
	DROP PROCEDURE[dbo].[sp_GetDDLa]
GO

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
GO


-- ----------------------------
-- procedure structure for sp_GetDDL
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[sp_GetDDL]') AND type IN ('P', 'PC', 'RF', 'X'))
	DROP PROCEDURE[dbo].[sp_GetDDL]
GO

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
GO


-- ----------------------------
-- function structure for ufn_GetCreateTableScript
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[ufn_GetCreateTableScript]') AND type IN ('FN', 'FS', 'FT', 'IF', 'TF'))
	DROP FUNCTION[dbo].[ufn_GetCreateTableScript]
GO

CREATE FUNCTION [dbo].[ufn_GetCreateTableScript]( @schema_name NVARCHAR(128), @table_name NVARCHAR(128)) RETURNS NVARCHAR(MAX) AS BEGIN DECLARE @CreateTableScript NVARCHAR(MAX); DECLARE @IndexScripts NVARCHAR(MAX) = ''; DECLARE @ColumnDescriptions NVARCHAR(MAX) = N''; SELECT @CreateTableScript = CONCAT( 'CREATE TABLE [', s.name, '].[' , t.name, '] (', STUFF( ( SELECT ', [' + c.name + '] ' + tp.name + CASE WHEN tp.name IN ('varchar', 'nvarchar', 'char', 'nchar') THEN '(' + IIF(c.max_length = -1, 'MAX', CAST(c.max_length AS NVARCHAR(10))) + ')' WHEN tp.name IN ('decimal', 'numeric') THEN '(' + CAST(c.precision AS NVARCHAR(10)) + ', ' + CAST(c.scale AS NVARCHAR(10)) + ')' ELSE '' END + ' ' + CASE WHEN c.is_nullable = 1 THEN 'NULL' ELSE 'NOT NULL' END FROM sys.columns c JOIN sys.types tp ON c.user_type_id = tp.user_type_id WHERE c.object_id = t.object_id FOR XML PATH(''), TYPE ).value('/', 'nvarchar(max)'), 1, 1, ''), ');' ) FROM sys.tables t JOIN sys.schemas s ON t.schema_id = s.schema_id WHERE t.name = @table_name AND s.name = @schema_name; SELECT @IndexScripts = @IndexScripts + 'CREATE ' + CASE WHEN i.is_unique = 1 THEN 'UNIQUE ' ELSE '' END + i.type_desc + ' INDEX [' + i.name + '] ON [' + s.name + '].[' + t.name + '] (' + STUFF( ( SELECT ', [' + c.name + ']' + CASE WHEN ic.is_descending_key = 1 THEN ' DESC' ELSE ' ASC' END FROM sys.index_columns ic JOIN sys.columns c ON ic.object_id = c.object_id AND ic.column_id = c.column_id WHERE ic.object_id = i.object_id AND ic.index_id = i.index_id ORDER BY ic.key_ordinal FOR XML PATH('') ), 1, 1, '') + ')' + CASE WHEN i.has_filter = 1 THEN ' WHERE ' + i.filter_definition ELSE '' END + ';' + CHAR(13) + CHAR(10) FROM sys.indexes i JOIN sys.tables t ON i.object_id = t.object_id JOIN sys.schemas s ON t.schema_id = s.schema_id WHERE i.type > 0 AND t.name = @table_name AND s.name = @schema_name; SELECT @ColumnDescriptions += 'EXEC sp_addextendedproperty @name=N''MS_Description'', @value=N''' + CAST(p.value AS NVARCHAR(MAX)) + ''', @level0type=N''SCHEMA'', @level0name=N''' + @schema_name + ''', @level1type=N''TABLE'', @level1name=N''' + @table_name + ''', @level2type=N''COLUMN'', @level2name=N''' + c.name + ''';' + CHAR(13) + CHAR(10) FROM sys.extended_properties p JOIN sys.columns c ON p.major_id = c.object_id AND p.minor_id = c.column_id JOIN sys.tables t ON c.object_id = t.object_id JOIN sys.schemas s ON t.schema_id = s.schema_id WHERE p.class = 1 AND t.name = @table_name AND s.name = @schema_name; SET @CreateTableScript = @CreateTableScript + CHAR(13) + CHAR(10) + @IndexScripts + CHAR(13) + CHAR(10)+ @ColumnDescriptions+ CHAR(10); RETURN @CreateTableScript; END
GO


-- ----------------------------
-- procedure structure for HelloWorld
-- ----------------------------
IF EXISTS (SELECT * FROM sys.all_objects WHERE object_id = OBJECT_ID(N'[dbo].[HelloWorld]') AND type IN ('P', 'PC', 'RF', 'X'))
	DROP PROCEDURE[dbo].[HelloWorld]
GO

CREATE PROCEDURE [dbo].[HelloWorld]
AS
BEGIN
    PRINT 'Hello_World!'
END
GO


-- ----------------------------
-- Primary Key structure for table ChildTable
-- ----------------------------
ALTER TABLE [dbo].[ChildTable] ADD CONSTRAINT [PK__ChildTab__BEFA0736F40CA1CD] PRIMARY KEY CLUSTERED ([ChildID])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table ChildTable2
-- ----------------------------
ALTER TABLE [dbo].[ChildTable2] ADD CONSTRAINT [PK__ChildTab__BEFA0736B6007458] PRIMARY KEY CLUSTERED ([ChildID])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table Department
-- ----------------------------
ALTER TABLE [dbo].[Department] ADD CONSTRAINT [PK__Departme__0148818E6E0F4147] PRIMARY KEY CLUSTERED ([DeptID])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table Employee
-- ----------------------------
ALTER TABLE [dbo].[Employee] ADD CONSTRAINT [PK__Employee__7AD04FF15300481F] PRIMARY KEY CLUSTERED ([EmployeeID])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Checks structure for table Employee2
-- ----------------------------
ALTER TABLE [dbo].[Employee2] ADD CONSTRAINT [CHK_Age] CHECK ([Age]>=(18) AND [Age]<=(100))
GO


-- ----------------------------
-- Primary Key structure for table Employee2
-- ----------------------------
ALTER TABLE [dbo].[Employee2] ADD CONSTRAINT [PK__Employee__7AD04FF1FC6EABE9] PRIMARY KEY CLUSTERED ([EmployeeID])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table Employees
-- ----------------------------
ALTER TABLE [dbo].[Employees] ADD CONSTRAINT [PK__Employee__7AD04FF1D4D59C25] PRIMARY KEY CLUSTERED ([EmployeeID])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table Employees2024622
-- ----------------------------
ALTER TABLE [dbo].[Employees2024622] ADD CONSTRAINT [PK__Employee__7AD04FF1B1014980] PRIMARY KEY CLUSTERED ([EmployeeID])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Indexes structure for table MSSQL_TemporalHistoryFor_859150106
-- ----------------------------
CREATE CLUSTERED INDEX [ix_MSSQL_TemporalHistoryFor_859150106]
ON [dbo].[MSSQL_TemporalHistoryFor_859150106] (
  [ValidTo] ASC,
  [ValidFrom] ASC
)  
FILESTREAM_ON [NULL]
GO


-- ----------------------------
-- Primary Key structure for table MyPartitionedTable
-- ----------------------------
ALTER TABLE [dbo].[MyPartitionedTable] ADD CONSTRAINT [PK_MyPartitionedTable] PRIMARY KEY CLUSTERED ([ID], [OrderDate])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Primary Key structure for table MyPartitionedTable2
-- ----------------------------
ALTER TABLE [dbo].[MyPartitionedTable2] ADD CONSTRAINT [PK_MyPartitionedTable2] PRIMARY KEY CLUSTERED ([ID], [OrderDate])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)
GO


-- ----------------------------
-- Indexes structure for table Orders
-- ----------------------------
CREATE UNIQUE NONCLUSTERED INDEX [IX_Unique_OrderReference]
ON [dbo].[Orders] (
  [OrderReference] ASC
)
GO


-- ----------------------------
-- Uniques structure for table Orders
-- ----------------------------
ALTER TABLE [dbo].[Orders] ADD CONSTRAINT [UQ_Orders_Customer_OrderDate] UNIQUE NONCLUSTERED ([CustomerID] ASC, [OrderDate] ASC)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table Orders
-- ----------------------------
ALTER TABLE [dbo].[Orders] ADD CONSTRAINT [PK_Orders] PRIMARY KEY CLUSTERED ([OrderID], [ProductID])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table ParentTable
-- ----------------------------
ALTER TABLE [dbo].[ParentTable] ADD CONSTRAINT [PK__ParentTa__F7B5C2EFCB87BC8B] PRIMARY KEY CLUSTERED ([ColumnA], [ColumnB])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Primary Key structure for table Sales
-- ----------------------------
ALTER TABLE [dbo].[Sales] ADD CONSTRAINT [PK__Sales__1EE3C41F222D2090] PRIMARY KEY CLUSTERED ([SaleID])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Auto increment value for Sales_with_options
-- ----------------------------
DBCC CHECKIDENT ('[dbo].[Sales_with_options]', RESEED, 1)
GO


-- ----------------------------
-- Indexes structure for table Sales_with_options
-- ----------------------------
CREATE NONCLUSTERED INDEX [IDX_Sales_ProductID]
ON [dbo].[Sales_with_options] (
  [ProductID] ASC
)
GO

CREATE NONCLUSTERED INDEX [IDX_Sales_CustomerID]
ON [dbo].[Sales_with_options] (
  [CustomerID] ASC
)
GO


-- ----------------------------
-- Checks structure for table Sales_with_options
-- ----------------------------
ALTER TABLE [dbo].[Sales_with_options] ADD CONSTRAINT [CK__Sales_wit__Amoun__72E607DB] CHECK ([Amount]>(0))
GO


-- ----------------------------
-- Primary Key structure for table Sales_with_options
-- ----------------------------
ALTER TABLE [dbo].[Sales_with_options] ADD CONSTRAINT [PK__Sales_wi__1EE3C41FBD64239A] PRIMARY KEY CLUSTERED ([SaleID])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Auto increment value for Sales_with_options2
-- ----------------------------
DBCC CHECKIDENT ('[dbo].[Sales_with_options2]', RESEED, 1)
GO


-- ----------------------------
-- Checks structure for table Sales_with_options2
-- ----------------------------
ALTER TABLE [dbo].[Sales_with_options2] ADD CONSTRAINT [CK__Sales_wit__Amoun__77AABCF8] CHECK ([Amount]>(0))
GO


-- ----------------------------
-- Primary Key structure for table Sales_with_options2
-- ----------------------------
ALTER TABLE [dbo].[Sales_with_options2] ADD CONSTRAINT [PK__Sales_wi__1EE3C41FF1865117] PRIMARY KEY CLUSTERED ([SaleID])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Indexes structure for table table_unique_constraint_
-- ----------------------------
CREATE UNIQUE NONCLUSTERED INDEX [table_unique_constraint__column_7_uindex]
ON [dbo].[table_unique_constraint_] (
  [column_7] ASC
)
GO

CREATE UNIQUE NONCLUSTERED INDEX [table_unique_constraint__column_8_uindex]
ON [dbo].[table_unique_constraint_] (
  [column_8] ASC
)
GO


-- ----------------------------
-- Uniques structure for table table_unique_constraint_
-- ----------------------------
ALTER TABLE [dbo].[table_unique_constraint_] ADD CONSTRAINT [unique_test_constraint] UNIQUE CLUSTERED ([column_1] ASC, [column_2] ASC)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Indexes structure for table test_constraint
-- ----------------------------
CREATE UNIQUE NONCLUSTERED INDEX [test_constraint_column_4_uindex]
ON [dbo].[test_constraint] (
  [column_4] ASC
)
GO

CREATE UNIQUE NONCLUSTERED INDEX [test_constraint_column_7_uindex]
ON [dbo].[test_constraint] (
  [column_7] ASC
)
GO


-- ----------------------------
-- Uniques structure for table test_constraint
-- ----------------------------
ALTER TABLE [dbo].[test_constraint] ADD CONSTRAINT [un_column6] UNIQUE NONCLUSTERED ([column_6] ASC)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO

ALTER TABLE [dbo].[test_constraint] ADD CONSTRAINT [un_column7] UNIQUE CLUSTERED ([column_7] ASC)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO

ALTER TABLE [dbo].[test_constraint] ADD CONSTRAINT [un_con] UNIQUE NONCLUSTERED ([unique_column] ASC)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO

EXEC sp_addextendedproperty
'MS_Description', N'Unique constraint that enforces a business uniqueness rule',
'SCHEMA', N'dbo',
'TABLE', N'test_constraint',
'CONSTRAINT', N'un_con'
GO


-- ----------------------------
-- Checks structure for table test_constraint
-- ----------------------------
ALTER TABLE [dbo].[test_constraint] ADD CONSTRAINT [age_check] CHECK ([age]>=(18))
GO

EXEC sp_addextendedproperty
'MS_Description', N'Age validation',
'SCHEMA', N'dbo',
'TABLE', N'test_constraint',
'CONSTRAINT', N'age_check'
GO


-- ----------------------------
-- Primary Key structure for table test_constraint
-- ----------------------------
ALTER TABLE [dbo].[test_constraint] ADD CONSTRAINT [test_constraint_pk] PRIMARY KEY NONCLUSTERED ([column_5])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Indexes structure for table test_data_types
-- ----------------------------
CREATE NONCLUSTERED INDEX [test_data_types_column_1_index]
ON [dbo].[test_data_types] (
  [column_1] ASC
)
GO

EXEC sp_addextendedproperty
'MS_Description', N'Description for the test_data_types_column_1_index index',
'SCHEMA', N'dbo',
'TABLE', N'test_data_types',
'INDEX', N'test_data_types_column_1_index'
GO

CREATE UNIQUE NONCLUSTERED INDEX [test_data_types_column_15_uindex]
ON [dbo].[test_data_types] (
  [column_15] ASC
)
GO

CREATE UNIQUE NONCLUSTERED INDEX [test_data_types_column_16_uindex]
ON [dbo].[test_data_types] (
  [column_16] ASC
)
GO


-- ----------------------------
-- Indexes structure for table test_data_types2
-- ----------------------------
CREATE UNIQUE NONCLUSTERED INDEX [test_data_types2_column_15_uindex]
ON [dbo].[test_data_types2] (
  [column_15] ASC
)
GO

CREATE UNIQUE NONCLUSTERED INDEX [test_data_types2_column_16_uindex]
ON [dbo].[test_data_types2] (
  [column_16] ASC
)
GO

CREATE NONCLUSTERED INDEX [test_data_types2_column_1_index]
ON [dbo].[test_data_types2] (
  [column_1] ASC
)
GO

EXEC sp_addextendedproperty
'MS_Description', N'Description for the test_data_types2_column_1_index index',
'SCHEMA', N'dbo',
'TABLE', N'test_data_types2',
'INDEX', N'test_data_types2_column_1_index'
GO


-- ----------------------------
-- Auto increment value for test_increment
-- ----------------------------
DBCC CHECKIDENT ('[dbo].[test_increment]', RESEED, 2)
GO


-- ----------------------------
-- Auto increment value for test_increment2
-- ----------------------------

-- ----------------------------
-- Checks structure for table test_table
-- ----------------------------
ALTER TABLE [dbo].[test_table] ADD CONSTRAINT [check_test_constraint] CHECK ([column1]>(18) AND [test_table].[column2]<(99))
GO


-- ----------------------------
-- Primary Key structure for table test_table
-- ----------------------------
ALTER TABLE [dbo].[test_table] ADD CONSTRAINT [PK__test_tab__26B6AD3FBA0B3B45] PRIMARY KEY CLUSTERED ([column1], [column2])
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON)  
ON [PRIMARY]
GO


-- ----------------------------
-- Foreign Keys structure for table ChildTable
-- ----------------------------
ALTER TABLE [dbo].[ChildTable] ADD CONSTRAINT [FK__ChildTable__6774552F] FOREIGN KEY ([ColumnA], [ColumnB]) REFERENCES [dbo].[ParentTable] ([ColumnA], [ColumnB]) ON DELETE NO ACTION ON UPDATE NO ACTION
GO


-- ----------------------------
-- Foreign Keys structure for table ChildTable2
-- ----------------------------
ALTER TABLE [dbo].[ChildTable2] ADD CONSTRAINT [FK__ChildTable2__6A50C1DA] FOREIGN KEY ([ColumnA], [ColumnB]) REFERENCES [dbo].[ParentTable] ([ColumnA], [ColumnB]) ON DELETE NO ACTION ON UPDATE NO ACTION
GO


-- ----------------------------
-- Foreign Keys structure for table table_unique_constraint_
-- ----------------------------
ALTER TABLE [dbo].[table_unique_constraint_] ADD CONSTRAINT [foreign_table_constraint] FOREIGN KEY ([column_7], [column_8]) REFERENCES [dbo].[test_table] ([column1], [column2]) ON DELETE NO ACTION ON UPDATE CASCADE
GO


-- ----------------------------
-- Foreign Keys structure for table test_constraint
-- ----------------------------
ALTER TABLE [dbo].[test_constraint] ADD CONSTRAINT [FK_id] FOREIGN KEY ([id]) REFERENCES [dbo].[test_data_types] ([column_15]) ON DELETE CASCADE ON UPDATE CASCADE
GO

EXEC sp_addextendedproperty
'MS_Description', N'Foreign key description',
'SCHEMA', N'dbo',
'TABLE', N'test_constraint',
'CONSTRAINT', N'FK_id'
GO

