/*
MySQL (Positive Technologies) grammar
The MIT License (MIT).
Copyright (c) 2015-2017, Ivan Kochurkin (kvanttt@gmail.com), Positive Technologies.
Copyright (c) 2017, Ivan Khudyashev (IHudyashov@ptsecurity.com)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

// $antlr-format alignTrailingComments true, columnLimit 150, minEmptyLines 1, maxEmptyLinesToKeep 1, reflowComments false, useTab false
// $antlr-format allowShortRulesOnASingleLine false, allowShortBlocksOnASingleLine true, alignSemicolons hanging, alignColons hanging

parser grammar MySqlParser;

options {
    tokenVocab = MySqlLexer;
}

// Top Level Description

root
    : sqlStatements? (MINUS MINUS)? EOF
    ;

sqlStatements
    : (sqlStatement (MINUS MINUS)? SEMI? | emptyStatement_ | DELIMITER_STATEMENT)* (
        sqlStatement ((MINUS MINUS)? SEMI)?
        | emptyStatement_ | DELIMITER_STATEMENT
    )
    ;

sqlStatement
    : ddlStatement
    | dmlStatement
    | transactionStatement
    | replicationStatement
    | preparedStatement
    | administrationStatement
    | utilityStatement
    ;

emptyStatement_
    : SEMI
    ;

ddlStatement
    : createDatabase
    | createEvent
    | createIndex
    | createLogfileGroup
    | createProcedure
    | createFunction
    | createServer
    | createTable
    | createTablespaceInnodb
    | createTablespaceNdb
    | createTrigger
    | createView
    | createRole
    | alterDatabase
    | alterEvent
    | alterFunction
    | alterInstance
    | alterLogfileGroup
    | alterProcedure
    | alterServer
    | alterTable
    | alterTablespace
    | alterView
    | dropDatabase
    | dropEvent
    | dropIndex
    | dropLogfileGroup
    | dropProcedure
    | dropFunction
    | dropServer
    | dropTable
    | dropTablespace
    | dropTrigger
    | dropView
    | dropRole
    | setRole
    | renameTable
    | truncateTable
    ;

dmlStatement
    : selectStatement
    | insertStatement
    | updateStatement
    | deleteStatement
    | replaceStatement
    | callStatement
    | loadDataStatement
    | loadXmlStatement
    | doStatement
    | handlerStatement
    | valuesStatement
    | tableStatement
    ;

transactionStatement
    : startTransaction
    | beginWork
    | commitWork
    | rollbackWork
    | savepointStatement
    | rollbackStatement
    | releaseStatement
    | lockTables
    | unlockTables
    ;

replicationStatement
    : changeMaster
    | changeReplicationFilter
    | purgeBinaryLogs
    | resetMaster
    | resetSlave
    | startSlave
    | stopSlave
    | startGroupReplication
    | stopGroupReplication
    | xaStartTransaction
    | xaEndTransaction
    | xaPrepareStatement
    | xaCommitWork
    | xaRollbackWork
    | xaRecoverWork
    ;

preparedStatement
    : prepareStatement
    | executeStatement
    | deallocatePrepare
    ;

// remark: NOT INCLUDED IN sqlStatement, but include in body
//  of routine's statements
compoundStatement
    : blockStatement
    | caseStatement
    | ifStatement
    | leaveStatement
    | loopStatement
    | repeatStatement
    | whileStatement
    | iterateStatement
    | returnStatement
    | cursorStatement
    | withStatement dmlStatement
    ;

administrationStatement
    : alterUser
    | createUser
    | dropUser
    | grantStatement
    | grantProxy
    | renameUser
    | revokeStatement
    | revokeProxy
    | analyzeTable
    | checkTable
    | checksumTable
    | optimizeTable
    | repairTable
    | createUdfunction
    | installPlugin
    | uninstallPlugin
    | setStatement
    | showStatement
    | binlogStatement
    | cacheIndexStatement
    | flushStatement
    | killStatement
    | loadIndexIntoCache
    | resetStatement
    | shutdownStatement
    ;

utilityStatement
    : simpleDescribeStatement
    | fullDescribeStatement
    | helpStatement
    | useStatement
    | signalStatement
    | resignalStatement
    | diagnosticsStatement
    ;

// Data Definition Language

//    Create statements

createDatabase
    : CREATE dbFormat = (DATABASE | SCHEMA) ifNotExists? databaseDeclarationName createDatabaseOption*
    ;

createEvent
    : CREATE ownerStatement? EVENT ifNotExists? eventDeclarationName ON SCHEDULE scheduleExpression (
        ON COMPLETION NOT? PRESERVE
    )? enableType? (COMMENT STRING_LITERAL)? DO routineBody
    ;

createIndex
    : CREATE intimeAction = (ONLINE | OFFLINE)? indexCategory = (UNIQUE | FULLTEXT | SPATIAL)? INDEX indexDeclarationName indexType? ON tableReferenceName indexColumnNames
        indexOption* (
        ALGORITHM EQUAL_SYMBOL? algType = (DEFAULT | INPLACE | COPY)
        | LOCK EQUAL_SYMBOL? lockType = (DEFAULT | NONE | SHARED | EXCLUSIVE)
    )*
    ;

createLogfileGroup
    : CREATE LOGFILE GROUP uid ADD UNDOFILE undoFile = STRING_LITERAL (
        INITIAL_SIZE '='? initSize = fileSizeLiteral
    )? (UNDO_BUFFER_SIZE '='? undoSize = fileSizeLiteral)? (
        REDO_BUFFER_SIZE '='? redoSize = fileSizeLiteral
    )? (NODEGROUP '='? uid)? WAIT? (COMMENT '='? comment = STRING_LITERAL)? ENGINE '='? engineName
    ;

createProcedure
    : CREATE ownerStatement? PROCEDURE procedureDeclarationName '(' procedureParameter? (
        ',' procedureParameter
    )* ')' routineOption* routineBody
    ;

createFunction
    : CREATE ownerStatement? AGGREGATE? FUNCTION ifNotExists? functionDeclarationName '(' functionParameter? (
        ',' functionParameter
    )* ')' RETURNS dataType routineOption* (routineBody | returnStatement)
    ;

createRole
    : CREATE ROLE ifNotExists? roleDeclarationName (',' roleDeclarationName)*
    ;

createServer
    : CREATE SERVER uid FOREIGN DATA WRAPPER wrapperName = (MYSQL | STRING_LITERAL) OPTIONS '(' serverOption (
        ',' serverOption
    )* ')'
    ;

createTable
    : CREATE TEMPORARY? TABLE ifNotExists? tableDeclarationName (
        LIKE tableReferenceName
        | '(' LIKE parenthesisTable = tableReferenceName ')'
    ) # copyCreateTable
    | CREATE TEMPORARY? TABLE ifNotExists? tableDeclarationName createDefinitions? (
        tableOption (','? tableOption)*
    )? partitionDefinitions? keyViolate = (IGNORE | REPLACE)? AS? selectStatement # queryCreateTable
    | CREATE TEMPORARY? TABLE ifNotExists? tableDeclarationName createDefinitions (
        tableOption (','? tableOption)*
    )? partitionDefinitions? # columnCreateTable
    ;

createTablespaceInnodb
    : CREATE TABLESPACE tablespaceDeclarationName ADD DATAFILE datafile = STRING_LITERAL (
        FILE_BLOCK_SIZE '=' fileBlockSize = fileSizeLiteral
    )? (ENGINE '='? engineName)?
    ;

createTablespaceNdb
    : CREATE TABLESPACE tablespaceDeclarationName ADD DATAFILE datafile = STRING_LITERAL USE LOGFILE GROUP uid (
        EXTENT_SIZE '='? extentSize = fileSizeLiteral
    )? (INITIAL_SIZE '='? initialSize = fileSizeLiteral)? (
        AUTOEXTEND_SIZE '='? autoextendSize = fileSizeLiteral
    )? (MAX_SIZE '='? maxSize = fileSizeLiteral)? (NODEGROUP '='? uid)? WAIT? (
        COMMENT '='? comment = STRING_LITERAL
    )? ENGINE '='? engineName
    ;

createTrigger
    : CREATE ownerStatement? TRIGGER ifNotExists? thisTrigger = triggerDeclarationName triggerTime = (BEFORE | AFTER) triggerEvent = (
        INSERT
        | UPDATE
        | DELETE
    ) ON tableReferenceName FOR EACH ROW (triggerPlace = (FOLLOWS | PRECEDES) otherTrigger = triggerReferenceName)? routineBody
    ;

withClause
    : WITH RECURSIVE? commonTableExpressions
    ;

commonTableExpressions
    : cteName ('(' cteColumnName (',' cteColumnName)* ')')? AS '(' dmlStatement ')' (
        ',' commonTableExpressions
    )?
    ;

cteName
    : uid
    ;

cteColumnName
    : uid
    ;

createView
    : CREATE orReplace? (ALGORITHM '=' algType = (UNDEFINED | MERGE | TEMPTABLE))? ownerStatement? (
        SQL SECURITY secContext = (DEFINER | INVOKER)
    )? VIEW viewDeclarationName ('(' uidList ')')? AS (
        '(' withClause? selectStatement ')'
        | withClause? selectStatement (WITH checkOption = (CASCADED | LOCAL)? CHECK OPTION)?
    )
    ;

// details

createDatabaseOption
    : DEFAULT? charSet '='? (charsetName | DEFAULT)
    | DEFAULT? COLLATE '='? collationName
    | DEFAULT? ENCRYPTION '='? STRING_LITERAL
    | READ ONLY '='? (DEFAULT | ZERO_DECIMAL | ONE_DECIMAL)
    ;

charSet
    : CHARACTER SET
    | CHARSET
    | CHAR SET
    ;

currentUserExpression
    : CURRENT_USER ('(' ')')?
    ;

ownerStatement
    : DEFINER '=' (userName | currentUserExpression)
    ;

scheduleExpression
    : AT timestampValue intervalExpr* # preciseSchedule
    | EVERY (decimalLiteral | expression) intervalType (
        STARTS startTimestamp = timestampValue (startIntervals += intervalExpr)*
    )? (ENDS endTimestamp = timestampValue (endIntervals += intervalExpr)*)? # intervalSchedule
    ;

timestampValue
    : CURRENT_TIMESTAMP
    | stringLiteral
    | decimalLiteral
    | expression
    ;

intervalExpr
    : '+' INTERVAL (decimalLiteral | expression) intervalType
    ;

intervalType
    : intervalTypeBase
    | YEAR
    | YEAR_MONTH
    | DAY_HOUR
    | DAY_MINUTE
    | DAY_SECOND
    | HOUR_MINUTE
    | HOUR_SECOND
    | MINUTE_SECOND
    | SECOND_MICROSECOND
    | MINUTE_MICROSECOND
    | HOUR_MICROSECOND
    | DAY_MICROSECOND
    ;

enableType
    : ENABLE
    | DISABLE
    | DISABLE ON SLAVE
    ;

indexType
    : USING (BTREE | HASH)
    ;

indexOption
    : KEY_BLOCK_SIZE EQUAL_SYMBOL? fileSizeLiteral
    | indexType
    | WITH PARSER uid
    | COMMENT STRING_LITERAL
    | (VISIBLE | INVISIBLE)
    | ENGINE_ATTRIBUTE EQUAL_SYMBOL? STRING_LITERAL
    | SECONDARY_ENGINE_ATTRIBUTE EQUAL_SYMBOL? STRING_LITERAL
    ;

procedureParameter
    : direction = (IN | OUT | INOUT)? uid dataType
    ;

functionParameter
    : uid dataType
    ;

routineOption
    : COMMENT STRING_LITERAL                                        # routineComment
    | LANGUAGE SQL                                                  # routineLanguage
    | NOT? DETERMINISTIC                                            # routineBehavior
    | ( CONTAINS SQL | NO SQL | READS SQL DATA | MODIFIES SQL DATA) # routineData
    | SQL SECURITY context = (DEFINER | INVOKER)                    # routineSecurity
    ;

serverOption
    : HOST STRING_LITERAL
    | DATABASE STRING_LITERAL
    | USER STRING_LITERAL
    | PASSWORD STRING_LITERAL
    | SOCKET STRING_LITERAL
    | OWNER STRING_LITERAL
    | PORT decimalLiteral
    ;

createDefinitions
    : '(' createDefinition (',' createDefinition)* ')'
    ;

createDefinition
    : columnDeclarationName columnDefinition # columnDeclaration
    | tableConstraint NOT? ENFORCED?  # constraintDeclaration
    | indexColumnDefinition           # indexDeclaration
    ;

columnDefinition
    : dataType columnConstraint* NOT? ENFORCED?
    ;

columnConstraint
    : nullNotnull                                                   # nullColumnConstraint
    | DEFAULT defaultValue                                          # defaultColumnConstraint
    | VISIBLE                                                       # visibilityColumnConstraint
    | INVISIBLE                                                     # invisibilityColumnConstraint
    | (AUTO_INCREMENT | ON UPDATE currentTimestamp)                 # autoIncrementColumnConstraint
    | PRIMARY? KEY                                                  # primaryKeyColumnConstraint
    | UNIQUE KEY?                                                   # uniqueKeyColumnConstraint
    | COMMENT STRING_LITERAL                                        # commentColumnConstraint
    | COLUMN_FORMAT colformat = (FIXED | DYNAMIC | DEFAULT)         # formatColumnConstraint
    | STORAGE storageval = (DISK | MEMORY | DEFAULT)                # storageColumnConstraint
    | referenceDefinition                                           # referenceColumnConstraint
    | COLLATE collationName                                         # collateColumnConstraint
    | (GENERATED ALWAYS)? AS '(' expression ')' (VIRTUAL | STORED)? # generatedColumnConstraint
    | SERIAL DEFAULT VALUE                                          # serialDefaultColumnConstraint
    | (CONSTRAINT name = constraintDeclarationName?)? CHECK '(' expression ')' # checkColumnConstraint
    ;

tableConstraint
    : (CONSTRAINT name = constraintDeclarationName?)? PRIMARY KEY index = indexDeclarationName? indexType? indexColumnNames indexOption*                         # primaryKeyTableConstraint
    | (CONSTRAINT name = constraintDeclarationName?)? UNIQUE indexFormat = (INDEX | KEY)? index = indexDeclarationName? indexType? indexColumnNames indexOption* # uniqueKeyTableConstraint
    | (CONSTRAINT name = constraintDeclarationName?)? FOREIGN KEY index = indexDeclarationName? indexColumnNames referenceDefinition                             # foreignKeyTableConstraint
    | (CONSTRAINT name = constraintDeclarationName?)? CHECK '(' expression ')'                                                                  # checkTableConstraint
    ;

referenceDefinition
    : REFERENCES tableReferenceName indexColumnNames? (MATCH matchType = (FULL | PARTIAL | SIMPLE))? referenceAction?
    ;

referenceAction
    : ON DELETE onDelete = referenceControlType (ON UPDATE onUpdate = referenceControlType)?
    | ON UPDATE onUpdate = referenceControlType (ON DELETE onDelete = referenceControlType)?
    ;

referenceControlType
    : RESTRICT
    | CASCADE
    | SET NULL_LITERAL
    | NO ACTION
    | SET DEFAULT
    ;

indexColumnDefinition
    : indexFormat = (INDEX | KEY) indexDeclarationName? indexType? indexColumnNames indexOption*            # simpleIndexDeclaration
    | (FULLTEXT | SPATIAL) indexFormat = (INDEX | KEY)? indexDeclarationName? indexColumnNames indexOption* # specialIndexDeclaration
    ;

tableOption
    : ENGINE '='? engineName?                                       # tableOptionEngine
    | ENGINE_ATTRIBUTE '='? STRING_LITERAL                          # tableOptionEngineAttribute
    | AUTOEXTEND_SIZE '='? decimalLiteral                           # tableOptionAutoextendSize
    | AUTO_INCREMENT '='? decimalLiteral                            # tableOptionAutoIncrement
    | AVG_ROW_LENGTH '='? decimalLiteral                            # tableOptionAverage
    | DEFAULT? charSet '='? (charsetName | DEFAULT)                 # tableOptionCharset
    | (CHECKSUM | PAGE_CHECKSUM) '='? boolValue = ('0' | '1')       # tableOptionChecksum
    | DEFAULT? COLLATE '='? collationName                           # tableOptionCollate
    | COMMENT '='? STRING_LITERAL                                   # tableOptionComment
    | COMPRESSION '='? (STRING_LITERAL | ID)                        # tableOptionCompression
    | CONNECTION '='? STRING_LITERAL                                # tableOptionConnection
    | (DATA | INDEX) DIRECTORY '='? STRING_LITERAL                  # tableOptionDataDirectory
    | DELAY_KEY_WRITE '='? boolValue = ('0' | '1')                  # tableOptionDelay
    | ENCRYPTION '='? STRING_LITERAL                                # tableOptionEncryption
    | (PAGE_COMPRESSED | STRING_LITERAL) '='? ('0' | '1')           # tableOptionPageCompressed
    | (PAGE_COMPRESSION_LEVEL | STRING_LITERAL) '='? decimalLiteral # tableOptionPageCompressionLevel
    | ENCRYPTION_KEY_ID '='? decimalLiteral                         # tableOptionEncryptionKeyId
    | INDEX DIRECTORY '='? STRING_LITERAL                           # tableOptionIndexDirectory
    | INSERT_METHOD '='? insertMethod = (NO | FIRST | LAST)         # tableOptionInsertMethod
    | KEY_BLOCK_SIZE '='? fileSizeLiteral                           # tableOptionKeyBlockSize
    | MAX_ROWS '='? decimalLiteral                                  # tableOptionMaxRows
    | MIN_ROWS '='? decimalLiteral                                  # tableOptionMinRows
    | PACK_KEYS '='? extBoolValue = ('0' | '1' | DEFAULT)           # tableOptionPackKeys
    | PASSWORD '='? STRING_LITERAL                                  # tableOptionPassword
    | ROW_FORMAT '='? rowFormat = (
        DEFAULT
        | DYNAMIC
        | FIXED
        | COMPRESSED
        | REDUNDANT
        | COMPACT
        | ID
    )                                                             # tableOptionRowFormat
    | START TRANSACTION                                           # tableOptionStartTransaction
    | SECONDARY_ENGINE_ATTRIBUTE '='? STRING_LITERAL              # tableOptionSecondaryEngineAttribute
    | STATS_AUTO_RECALC '='? extBoolValue = (DEFAULT | '0' | '1') # tableOptionRecalculation
    | STATS_PERSISTENT '='? extBoolValue = (DEFAULT | '0' | '1')  # tableOptionPersistent
    | STATS_SAMPLE_PAGES '='? (DEFAULT | decimalLiteral)          # tableOptionSamplePage
    | TABLESPACE uid tablespaceStorage?                           # tableOptionTablespace
    | TABLE_TYPE '=' tableType                                    # tableOptionTableType
    | tablespaceStorage                                           # tableOptionTablespace
    | TRANSACTIONAL '='? ('0' | '1')                              # tableOptionTransactional
    | UNION '='? '(' tables ')'                                   # tableOptionUnion
    ;

tableType
    : MYSQL
    | ODBC
    ;

tablespaceStorage
    : STORAGE (DISK | MEMORY | DEFAULT)
    ;

partitionDefinitions
    : PARTITION BY partitionFunctionDefinition (PARTITIONS count = decimalLiteral)? (
        SUBPARTITION BY subpartitionFunctionDefinition (SUBPARTITIONS subCount = decimalLiteral)?
    )? ('(' partitionDefinition (',' partitionDefinition)* ')')?
    ;

partitionFunctionDefinition
    : LINEAR? HASH '(' expression ')'                                     # partitionFunctionHash
    | LINEAR? KEY (ALGORITHM '=' algType = ('1' | '2'))? '(' uidList? ')' # partitionFunctionKey // Optional uidList for MySQL only
    | RANGE ('(' expression ')' | COLUMNS '(' uidList ')')                # partitionFunctionRange
    | LIST ('(' expression ')' | COLUMNS '(' uidList ')')                 # partitionFunctionList
    ;

subpartitionFunctionDefinition
    : LINEAR? HASH '(' expression ')'                                    # subPartitionFunctionHash
    | LINEAR? KEY (ALGORITHM '=' algType = ('1' | '2'))? '(' uidList ')' # subPartitionFunctionKey
    ;

partitionDefinition
    : PARTITION uid VALUES LESS THAN '(' partitionDefinerAtom (',' partitionDefinerAtom)* ')' partitionOption* (
        '(' subpartitionDefinition (',' subpartitionDefinition)* ')'
    )? # partitionComparison
    | PARTITION uid VALUES LESS THAN partitionDefinerAtom partitionOption* (
        '(' subpartitionDefinition (',' subpartitionDefinition)* ')'
    )? # partitionComparison
    | PARTITION uid VALUES IN '(' partitionDefinerAtom (',' partitionDefinerAtom)* ')' partitionOption* (
        '(' subpartitionDefinition (',' subpartitionDefinition)* ')'
    )? # partitionListAtom
    | PARTITION uid VALUES IN '(' partitionDefinerVector (',' partitionDefinerVector)* ')' partitionOption* (
        '(' subpartitionDefinition (',' subpartitionDefinition)* ')'
    )?                                                                                               # partitionListVector
    | PARTITION uid partitionOption* ('(' subpartitionDefinition (',' subpartitionDefinition)* ')')? # partitionSimple
    ;

partitionDefinerAtom
    : constant
    | expression
    | MAXVALUE
    ;

partitionDefinerVector
    : '(' partitionDefinerAtom (',' partitionDefinerAtom)+ ')'
    ;

subpartitionDefinition
    : SUBPARTITION uid partitionOption*
    ;

partitionOption
    : DEFAULT? STORAGE? ENGINE '='? engineName             # partitionOptionEngine
    | COMMENT '='? comment = STRING_LITERAL                # partitionOptionComment
    | DATA DIRECTORY '='? dataDirectory = STRING_LITERAL   # partitionOptionDataDirectory
    | INDEX DIRECTORY '='? indexDirectory = STRING_LITERAL # partitionOptionIndexDirectory
    | MAX_ROWS '='? maxRows = decimalLiteral               # partitionOptionMaxRows
    | MIN_ROWS '='? minRows = decimalLiteral               # partitionOptionMinRows
    | TABLESPACE '='? tablespace = uid                     # partitionOptionTablespace
    | NODEGROUP '='? nodegroup = uid                       # partitionOptionNodeGroup
    ;

//    Alter statements

alterDatabase
    : ALTER dbFormat = (DATABASE | SCHEMA) databaseReferenceName? createDatabaseOption+      # alterSimpleDatabase
    | ALTER dbFormat = (DATABASE | SCHEMA) databaseReferenceName UPGRADE DATA DIRECTORY NAME # alterUpgradeName
    ;

alterEvent
    : ALTER ownerStatement? EVENT eventReferenceName (ON SCHEDULE scheduleExpression)? (
        ON COMPLETION NOT? PRESERVE
    )? (RENAME TO eventDeclarationName)? enableType? (COMMENT STRING_LITERAL)? (DO routineBody)?
    ;

alterFunction
    : ALTER FUNCTION functionReferenceName routineOption*
    ;

alterInstance
    : ALTER INSTANCE ROTATE INNODB MASTER KEY
    ;

alterLogfileGroup
    : ALTER LOGFILE GROUP uid ADD UNDOFILE STRING_LITERAL (INITIAL_SIZE '='? fileSizeLiteral)? WAIT? ENGINE '='? engineName
    ;

alterProcedure
    : ALTER PROCEDURE procedureReferenceName routineOption*
    ;

alterServer
    : ALTER SERVER uid OPTIONS '(' serverOption (',' serverOption)* ')'
    ;

alterTable
    : ALTER intimeAction = (ONLINE | OFFLINE)? IGNORE? TABLE tableReferenceName waitNowaitClause? (
        alterSpecification (',' alterSpecification)*
    )? partitionDefinitions?
    ;

alterTablespace
    : ALTER TABLESPACE tablespaceReferenceName objectAction = (ADD | DROP) DATAFILE STRING_LITERAL (
        INITIAL_SIZE '=' fileSizeLiteral
    )? WAIT? ENGINE '='? engineName
    ;

alterView
    : ALTER (ALGORITHM '=' algType = (UNDEFINED | MERGE | TEMPTABLE))? ownerStatement? (
        SQL SECURITY secContext = (DEFINER | INVOKER)
    )? VIEW viewReferenceName ('(' uidList ')')? AS selectStatement (
        WITH checkOpt = (CASCADED | LOCAL)? CHECK OPTION
    )?
    ;

// details

alterSpecification
    : tableOption (','? tableOption)*                                                                                             # alterByTableOption
    | ADD COLUMN? columnDeclarationName columnDefinition (FIRST | AFTER columnReferenceName)?                                     # alterByAddColumn
    | ADD COLUMN? '(' columnDeclarationName columnDefinition (',' columnDeclarationName columnDefinition)* ')'                     # alterByAddColumns
    | ADD indexFormat = (INDEX | KEY) indexDeclarationName? indexType? indexColumnNames indexOption*                                               # alterByAddIndex
    | ADD (CONSTRAINT name = constraintDeclarationName?)? PRIMARY KEY index = indexDeclarationName? indexType? indexColumnNames indexOption*                             # alterByAddPrimaryKey
    | ADD (CONSTRAINT name = constraintDeclarationName?)? UNIQUE indexFormat = (INDEX | KEY)? indexName = indexDeclarationName? indexType? indexColumnNames indexOption* # alterByAddUniqueKey
    | ADD keyType = (FULLTEXT | SPATIAL) indexFormat = (INDEX | KEY)? indexDeclarationName? indexColumnNames indexOption*                          # alterByAddSpecialIndex
    | ADD (CONSTRAINT name = constraintDeclarationName?)? FOREIGN KEY indexName = indexDeclarationName? indexColumnNames referenceDefinition                             # alterByAddForeignKey
    | ADD (CONSTRAINT name = constraintDeclarationName?)? CHECK (constraintReferenceName | stringLiteral | '(' expression ')') NOT? ENFORCED?                               # alterByAddCheckTableConstraint
    | ALTER (CONSTRAINT name = constraintReferenceName?)? CHECK (constraintReferenceName | stringLiteral | '(' expression ')') NOT? ENFORCED?                             # alterByAlterCheckTableConstraint
    | ADD (CONSTRAINT name = constraintDeclarationName?)? CHECK '(' expression ')'                                                                      # alterByAddCheckTableConstraint
    | ALGORITHM '='? algType = (DEFAULT | INSTANT | INPLACE | COPY)                                                               # alterBySetAlgorithm
    | ALTER COLUMN? columnReferenceName (SET DEFAULT defaultValue | DROP DEFAULT)                                                 # alterByChangeDefault
    | CHANGE COLUMN? oldColumn = columnReferenceName newColumn = columnDeclarationName columnDefinition (
        FIRST
        | AFTER afterColumn = columnReferenceName
    )?                                                           # alterByChangeColumn
    | RENAME COLUMN oldColumn = columnReferenceName TO newColumn = columnDeclarationName # alterByRenameColumn
    | LOCK '='? lockType = (DEFAULT | NONE | SHARED | EXCLUSIVE) # alterByLock
    | MODIFY COLUMN? columnReferenceName columnDefinition (FIRST | AFTER columnReferenceName)? # alterByModifyColumn
    | DROP COLUMN? columnReferenceName RESTRICT?                 # alterByDropColumn
    | DROP (CONSTRAINT | CHECK) constraintReferenceName          # alterByDropConstraintCheck
    | DROP PRIMARY KEY                                           # alterByDropPrimaryKey
    | DROP indexFormat = (INDEX | KEY) indexReferenceName        # alterByDropIndex
    | RENAME indexFormat = (INDEX | KEY) indexReferenceName TO indexDeclarationName # alterByRenameIndex
    | ALTER COLUMN? columnReferenceName (
        SET DEFAULT ( stringLiteral | '(' expression ')')
        | SET (VISIBLE | INVISIBLE)
        | DROP DEFAULT
    )                                                                           # alterByAlterColumnDefault
    | ALTER INDEX indexReferenceName (VISIBLE | INVISIBLE)                                     # alterByAlterIndexVisibility
    | DROP FOREIGN KEY constraintReferenceName                                                  # alterByDropForeignKey
    | DISABLE KEYS                                                              # alterByDisableKeys
    | ENABLE KEYS                                                               # alterByEnableKeys
    | RENAME renameFormat = (TO | AS)? (uid | fullId)                           # alterByRename
    | ORDER BY uidList                                                          # alterByOrder
    | CONVERT TO (CHARSET | CHARACTER SET) charsetName (COLLATE collationName)? # alterByConvertCharset
    | DEFAULT? CHARACTER SET '=' charsetName (COLLATE '=' collationName)?       # alterByDefaultCharset
    | DISCARD TABLESPACE                                                        # alterByDiscardTablespace
    | IMPORT TABLESPACE                                                         # alterByImportTablespace
    | FORCE                                                                     # alterByForce
    | validationFormat = (WITHOUT | WITH) VALIDATION                            # alterByValidate
    | ADD COLUMN? '(' createDefinition (',' createDefinition)* ')'              # alterByAddDefinitions
    | alterPartitionSpecification                                               # alterPartition
    ;

alterPartitionSpecification
    : ADD PARTITION '(' partitionDefinition (',' partitionDefinition)* ')'                          # alterByAddPartition
    | DROP PARTITION uidList                                                                        # alterByDropPartition
    | DISCARD PARTITION (uidList | ALL) TABLESPACE                                                  # alterByDiscardPartition
    | IMPORT PARTITION (uidList | ALL) TABLESPACE                                                   # alterByImportPartition
    | TRUNCATE PARTITION (uidList | ALL)                                                            # alterByTruncatePartition
    | COALESCE PARTITION decimalLiteral                                                             # alterByCoalescePartition
    | REORGANIZE PARTITION uidList INTO '(' partitionDefinition (',' partitionDefinition)* ')'      # alterByReorganizePartition
    | EXCHANGE PARTITION uid WITH TABLE tableReferenceName (validationFormat = (WITH | WITHOUT) VALIDATION)? # alterByExchangePartition
    | ANALYZE PARTITION (uidList | ALL)                                                             # alterByAnalyzePartition
    | CHECK PARTITION (uidList | ALL)                                                               # alterByCheckPartition
    | OPTIMIZE PARTITION (uidList | ALL)                                                            # alterByOptimizePartition
    | REBUILD PARTITION (uidList | ALL)                                                             # alterByRebuildPartition
    | REPAIR PARTITION (uidList | ALL)                                                              # alterByRepairPartition
    | REMOVE PARTITIONING                                                                           # alterByRemovePartitioning
    | UPGRADE PARTITIONING                                                                          # alterByUpgradePartitioning
    ;

//    Drop statements

dropDatabase
    : DROP dbFormat = (DATABASE | SCHEMA) ifExists? databaseReferenceName
    ;

dropEvent
    : DROP EVENT ifExists? eventReferenceName
    ;

dropIndex
    : DROP INDEX intimeAction = (ONLINE | OFFLINE)? indexReferenceName ON tableReferenceName (
        ALGORITHM '='? algType = (DEFAULT | INPLACE | COPY)
        | LOCK '='? lockType = (DEFAULT | NONE | SHARED | EXCLUSIVE)
    )*
    ;

dropLogfileGroup
    : DROP LOGFILE GROUP uid ENGINE '=' engineName
    ;

dropProcedure
    : DROP PROCEDURE ifExists? procedureReferenceName
    ;

dropFunction
    : DROP FUNCTION ifExists? functionReferenceName
    ;

dropServer
    : DROP SERVER ifExists? uid
    ;

dropTable
    : DROP TEMPORARY? TABLE ifExists? tables dropType = (RESTRICT | CASCADE)?
    ;

dropTablespace
    : DROP TABLESPACE tablespaceReferenceName (ENGINE '='? engineName)?
    ;

dropTrigger
    : DROP TRIGGER ifExists? triggerReferenceName
    ;

dropView
    : DROP VIEW ifExists? viewReferenceName (',' viewReferenceName)* dropType = (RESTRICT | CASCADE)?
    ;

dropRole
    : DROP ROLE ifExists? roleReferenceName (',' roleReferenceName)*
    ;

setRole
    : SET DEFAULT ROLE (NONE | ALL | roleReferenceName (',' roleReferenceName)*) TO userReferenceName (
        ',' userReferenceName
    )*
    | SET ROLE roleOption
    ;

//    Other DDL statements

renameTable
    : RENAME TABLE renameTableClause (',' renameTableClause)*
    ;

renameTableClause
    : tableReferenceName TO tableDeclarationName
    ;

truncateTable
    : TRUNCATE TABLE? tableReferenceName
    ;

// Data Manipulation Language

//    Primary DML Statements

callStatement
    : CALL procedureReferenceName ('(' (constants | expressions)? ')')?
    ;

deleteStatement
    : withStatement? singleDeleteStatement
    | withStatement? multipleDeleteStatement
    ;

doStatement
    : DO expressions
    ;

handlerStatement
    : handlerOpenStatement
    | handlerReadIndexStatement
    | handlerReadStatement
    | handlerCloseStatement
    ;

insertStatement
    : INSERT priority = (LOW_PRIORITY | DELAYED | HIGH_PRIORITY)? IGNORE? INTO? tableReferenceName (
        PARTITION '(' partitions = uidList? ')'
    )? (
        ('(' columns = columnReferenceNameList? ')')? insertStatementValue (AS? uid)?
        | SET setFirst = updatedElement (',' setElements += updatedElement)*
    ) (
        ON DUPLICATE KEY UPDATE duplicatedFirst = updatedElement (
            ',' duplicatedElements += updatedElement
        )*
    )?
    ;

loadDataStatement
    : LOAD DATA priority = (LOW_PRIORITY | CONCURRENT)? LOCAL? INFILE filename = STRING_LITERAL violation = (
        REPLACE
        | IGNORE
    )? INTO TABLE tableReferenceName (PARTITION '(' uidList ')')? (CHARACTER SET charset = charsetName)? (
        fieldsFormat = (FIELDS | COLUMNS) selectFieldsInto+
    )? (LINES selectLinesInto+)? (IGNORE decimalLiteral linesFormat = (LINES | ROWS))? (
        '(' loadDataAssignmentField (',' loadDataAssignmentField)* ')'
    )? (SET updatedElement (',' updatedElement)*)?
    ;

loadXmlStatement
    : LOAD XML priority = (LOW_PRIORITY | CONCURRENT)? LOCAL? INFILE filename = STRING_LITERAL violation = (
        REPLACE
        | IGNORE
    )? INTO TABLE tableReferenceName (CHARACTER SET charset = charsetName)? (
        ROWS IDENTIFIED BY '<' tag = STRING_LITERAL '>'
    )? (IGNORE decimalLiteral linesFormat = (LINES | ROWS))? (
        '(' loadDataAssignmentField (',' loadDataAssignmentField)* ')'
    )? (SET updatedElement (',' updatedElement)*)?
    ;

replaceStatement
    : REPLACE priority = (LOW_PRIORITY | DELAYED)? INTO? tableReferenceName (
        PARTITION '(' partitions = uidList ')'
    )? (
        ('(' columns = columnReferenceNameList ')')? insertStatementValue
        | SET setFirst = updatedElement (',' setElements += updatedElement)*
    )
    ;

selectStatement
    : withStatement? querySpecification lockClause?                              # simpleSelect
    | withStatement? queryExpression lockClause?                                 # parenthesisSelect
    | withStatement? (querySpecificationNointo | queryExpressionNointo) unionStatement+ (
        UNION unionType = (ALL | DISTINCT)? (querySpecification | queryExpression)
    )? orderByClause? limitClause? lockClause?                                   # unionSelect
    | withStatement? queryExpressionNointo unionParenthesis+ (
        UNION unionType = (ALL | DISTINCT)? queryExpression
    )? orderByClause? limitClause? lockClause?                                   # unionParenthesisSelect
    | withStatement? querySpecificationNointo (',' lateralStatement)+            # withLateralStatement
    ;

updateStatement
    : withStatement? singleUpdateStatement
    | withStatement? multipleUpdateStatement
    ;

// https://dev.mysql.com/doc/refman/8.0/en/values.html
valuesStatement
    : VALUES '(' expressionsWithDefaults? ')' (',' '(' expressionsWithDefaults? ')')*
    ;

// details

insertStatementValue
    : selectStatement
    | insertFormat = (VALUES | VALUE) '(' insertValueExpressions? ')' (
        ',' '(' insertValueExpressions? ')'
    )*
    ;

insertValueExpressions
    : insertValueExpression (',' insertValueExpression)*
    ;

insertValueExpression
    : expressionOrDefault
    ;

updatedElement
    : columnReferenceName '=' (expression | DEFAULT)
    ;

loadDataAssignmentField
    : columnReferenceName
    | LOCAL_ID
    ;

assignmentField
    : uid
    | LOCAL_ID
    ;

lockClause
    : FOR UPDATE
    | LOCK IN SHARE MODE
    ;

//    Detailed DML Statements

singleDeleteStatement
    : DELETE priority = LOW_PRIORITY? QUICK? IGNORE? FROM tableReferenceName (AS? aliasDeclarationName)? (
        PARTITION '(' uidList ')'
    )? (WHERE expression)? orderByClause? (LIMIT limitClauseAtom)?
    ;

multipleDeleteStatement
    : DELETE priority = LOW_PRIORITY? QUICK? IGNORE? (
        tableReferenceName ('.' '*')? ( ',' tableReferenceName ('.' '*')?)* FROM tableSources
        | FROM tableReferenceName ('.' '*')? ( ',' tableReferenceName ('.' '*')?)* USING tableSources
    ) (WHERE expression)?
    ;

handlerOpenStatement
    : HANDLER tableReferenceName OPEN (AS? aliasDeclarationName)?
    ;

handlerReadIndexStatement
    : HANDLER tableReferenceName READ index = uid (
        comparisonOperator '(' constants ')'
        | moveOrder = (FIRST | NEXT | PREV | LAST)
    ) (WHERE expression)? (LIMIT limitClauseAtom)?
    ;

handlerReadStatement
    : HANDLER tableReferenceName READ moveOrder = (FIRST | NEXT) (WHERE expression)? (LIMIT limitClauseAtom)?
    ;

handlerCloseStatement
    : HANDLER tableReferenceName CLOSE
    ;

singleUpdateStatement
    : UPDATE priority = LOW_PRIORITY? IGNORE? tableReferenceName (AS? aliasDeclarationName)? SET updatedElement (
        ',' updatedElement
    )* (WHERE expression)? orderByClause? limitClause?
    ;

multipleUpdateStatement
    : UPDATE priority = LOW_PRIORITY? IGNORE? tableSources SET updatedElement (',' updatedElement)* (
        WHERE expression
    )?
    ;

// details

orderByClause
    : ORDER BY orderByExpression (',' orderByExpression)*
    ;

orderByExpression
    : expression order = (ASC | DESC)?
    ;

tableSources
    : tableSource (',' tableSource)*
    ;

tableSource
    : tableSourceItem joinPart*         # tableSourceBase
    | '(' tableSourceItem joinPart* ')' # tableSourceNested
    | jsonTable                         # tableJson
    ;

tableSourceItem
    : tableReferenceName (PARTITION '(' uidList ')')? (AS? alias = aliasDeclarationName)? (indexHint (',' indexHint)*)? # atomTableItem
    | (selectStatement | '(' parenthesisSubquery = selectStatement ')') AS? alias = aliasDeclarationName                # subqueryTableItem
    | '(' tableSources ')'                                                                    # tableSourcesItem
    ;

indexHint
    : indexHintAction = (USE | IGNORE | FORCE) keyFormat = (INDEX | KEY) (FOR indexHintType)? '(' uidList ')'
    ;

indexHintType
    : JOIN
    | ORDER BY
    | GROUP BY
    ;

joinPart
    : (INNER | CROSS)? JOIN LATERAL? tableSourceItem joinSpec*      # innerJoin
    | STRAIGHT_JOIN tableSourceItem (ON expression)*                # straightJoin
    | (LEFT | RIGHT) OUTER? JOIN LATERAL? tableSourceItem joinSpec* # outerJoin
    | NATURAL ((LEFT | RIGHT) OUTER?)? JOIN tableSourceItem         # naturalJoin
    ;

joinSpec
    : (ON expression)
    | USING '(' columnReferenceNameList ')'
    ;

//    Select Statement's Details

queryExpression
    : '(' querySpecification ')'
    | '(' queryExpression ')'
    ;

queryExpressionNointo
    : '(' querySpecificationNointo ')'
    | '(' queryExpressionNointo ')'
    ;

querySpecification
    : SELECT selectSpec* selectElements selectIntoExpression? fromClause groupByClause? havingClause? windowClause? orderByClause? limitClause?
    | SELECT selectSpec* selectElements fromClause groupByClause? havingClause? windowClause? orderByClause? limitClause? selectIntoExpression?
    ;

querySpecificationNointo
    : SELECT selectSpec* selectElements fromClause groupByClause? havingClause? windowClause? orderByClause? limitClause? unionStatement?
    ;

unionParenthesis
    : UNION unionType = (ALL | DISTINCT)? queryExpressionNointo
    ;

unionStatement
    : UNION unionType = (ALL | DISTINCT)? (querySpecificationNointo | queryExpressionNointo)
    ;

lateralStatement
    : LATERAL (
        querySpecificationNointo
        | queryExpressionNointo
        | ('(' (querySpecificationNointo | queryExpressionNointo) ')' (AS? uid)?)
    )
    ;

// JSON

// https://dev.mysql.com/doc/refman/8.0/en/json-table-functions.html
jsonTable
    : JSON_TABLE '(' STRING_LITERAL ',' STRING_LITERAL COLUMNS '(' jsonColumnList ')' ')' (AS? uid)?
    ;

jsonColumnList
    : jsonColumn (',' jsonColumn)*
    ;

jsonColumn
    : fullColumnName (
        FOR ORDINALITY
        | dataType (PATH STRING_LITERAL jsonOnEmpty? jsonOnError? | EXISTS PATH STRING_LITERAL)
    )
    | NESTED PATH? STRING_LITERAL COLUMNS '(' jsonColumnList ')'
    ;

jsonOnEmpty
    : (NULL_LITERAL | ERROR | DEFAULT defaultValue) ON EMPTY
    ;

jsonOnError
    : (NULL_LITERAL | ERROR | DEFAULT defaultValue) ON ERROR
    ;

// details

selectSpec
    : (ALL | DISTINCT | DISTINCTROW)
    | HIGH_PRIORITY
    | STRAIGHT_JOIN
    | SQL_SMALL_RESULT
    | SQL_BIG_RESULT
    | SQL_BUFFER_RESULT
    | (SQL_CACHE | SQL_NO_CACHE)
    | SQL_CALC_FOUND_ROWS
    ;

selectElements
    : (star = '*' | selectElement) (',' selectElement)*
    ;

selectElement
    : fullId '.' '*'                               # selectStarElement
    | columnReferenceName (AS? aliasDeclarationName)?                    # selectColumnElement
    | functionCall (AS? aliasDeclarationName)?                           # selectFunctionElement
    | (LOCAL_ID VAR_ASSIGN)? expression (AS? aliasDeclarationName)?      # selectExpressionElement
    ;

selectIntoExpression
    : INTO assignmentField (',' assignmentField)* # selectIntoVariables
    | INTO DUMPFILE STRING_LITERAL                # selectIntoDumpFile
    | (
        INTO OUTFILE filename = STRING_LITERAL (CHARACTER SET charset = charsetName)? (
            fieldsFormat = (FIELDS | COLUMNS) selectFieldsInto+
        )? (LINES selectLinesInto+)?
    ) # selectIntoTextFile
    ;

selectFieldsInto
    : TERMINATED BY terminationField = STRING_LITERAL
    | OPTIONALLY? ENCLOSED BY enclosion = STRING_LITERAL
    | ESCAPED BY escaping = STRING_LITERAL
    ;

selectLinesInto
    : STARTING BY starting = STRING_LITERAL
    | TERMINATED BY terminationLine = STRING_LITERAL
    ;

fromClause
    : (FROM tableSources)? (WHERE whereExpr = expression)?
    ;

groupByClause
    : GROUP BY groupByItem (',' groupByItem)* (WITH ROLLUP)?
    ;

havingClause
    : HAVING havingExpr = expression
    ;

windowClause
    : WINDOW windowName AS '(' windowSpec ')' (',' windowName AS '(' windowSpec ')')*
    ;

groupByItem
    : expression order = (ASC | DESC)?
    ;

limitClause
    : LIMIT (
        (offset = limitClauseAtom ',')? limit = limitClauseAtom
        | limit = limitClauseAtom OFFSET offset = limitClauseAtom
    )
    ;

limitClauseAtom
    : decimalLiteral
    | mysqlVariable
    | simpleId
    ;

// Transaction's Statements

startTransaction
    : START TRANSACTION (transactionMode (',' transactionMode)*)?
    ;

beginWork
    : BEGIN WORK?
    ;

commitWork
    : COMMIT WORK? (AND nochain = NO? CHAIN)? (norelease = NO? RELEASE)?
    ;

rollbackWork
    : ROLLBACK WORK? (AND nochain = NO? CHAIN)? (norelease = NO? RELEASE)?
    ;

savepointStatement
    : SAVEPOINT uid
    ;

rollbackStatement
    : ROLLBACK WORK? TO SAVEPOINT? uid
    ;

releaseStatement
    : RELEASE SAVEPOINT uid
    ;

lockTables
    : LOCK (TABLE | TABLES) lockTableElement (',' lockTableElement)* waitNowaitClause?
    ;

unlockTables
    : UNLOCK TABLES
    ;

// details

setAutocommitStatement
    : SET AUTOCOMMIT '=' autocommitValue = ('0' | '1')
    ;

setTransactionStatement
    : SET transactionContext = (GLOBAL | SESSION)? TRANSACTION transactionOption (
        ',' transactionOption
    )*
    ;

transactionMode
    : WITH CONSISTENT SNAPSHOT
    | READ WRITE
    | READ ONLY
    ;

lockTableElement
    : tableReferenceName (AS? uid)? lockAction
    ;

lockAction
    : READ LOCAL?
    | LOW_PRIORITY? WRITE
    ;

transactionOption
    : ISOLATION LEVEL transactionLevel
    | READ WRITE
    | READ ONLY
    ;

transactionLevel
    : REPEATABLE READ
    | READ COMMITTED
    | READ UNCOMMITTED
    | SERIALIZABLE
    ;

// Replication's Statements

//    Base Replication

changeMaster
    : CHANGE MASTER TO masterOption (',' masterOption)* channelOption?
    ;

changeReplicationFilter
    : CHANGE REPLICATION FILTER replicationFilter (',' replicationFilter)*
    ;

purgeBinaryLogs
    : PURGE purgeFormat = (BINARY | MASTER) LOGS (
        TO fileName = STRING_LITERAL
        | BEFORE timeValue = STRING_LITERAL
    )
    ;

resetMaster
    : RESET MASTER
    ;

resetSlave
    : RESET SLAVE ALL? channelOption?
    ;

startSlave
    : START SLAVE (threadType (',' threadType)*)? (UNTIL untilOption)? connectionOption* channelOption?
    ;

stopSlave
    : STOP SLAVE (threadType (',' threadType)*)?
    ;

startGroupReplication
    : START GROUP_REPLICATION
    ;

stopGroupReplication
    : STOP GROUP_REPLICATION
    ;

// details

masterOption
    : stringMasterOption '=' STRING_LITERAL           # masterStringOption
    | decimalMasterOption '=' decimalLiteral          # masterDecimalOption
    | boolMasterOption '=' boolVal = ('0' | '1')      # masterBoolOption
    | MASTER_HEARTBEAT_PERIOD '=' REAL_LITERAL        # masterRealOption
    | IGNORE_SERVER_IDS '=' '(' (uid (',' uid)*)? ')' # masterUidListOption
    ;

stringMasterOption
    : MASTER_BIND
    | MASTER_HOST
    | MASTER_USER
    | MASTER_PASSWORD
    | MASTER_LOG_FILE
    | RELAY_LOG_FILE
    | MASTER_SSL_CA
    | MASTER_SSL_CAPATH
    | MASTER_SSL_CERT
    | MASTER_SSL_CRL
    | MASTER_SSL_CRLPATH
    | MASTER_SSL_KEY
    | MASTER_SSL_CIPHER
    | MASTER_TLS_VERSION
    ;

decimalMasterOption
    : MASTER_PORT
    | MASTER_CONNECT_RETRY
    | MASTER_RETRY_COUNT
    | MASTER_DELAY
    | MASTER_LOG_POS
    | RELAY_LOG_POS
    ;

boolMasterOption
    : MASTER_AUTO_POSITION
    | MASTER_SSL
    | MASTER_SSL_VERIFY_SERVER_CERT
    ;

channelOption
    : FOR CHANNEL STRING_LITERAL
    ;

replicationFilter
    : REPLICATE_DO_DB '=' '(' uidList ')'                         # doDbReplication
    | REPLICATE_IGNORE_DB '=' '(' uidList ')'                     # ignoreDbReplication
    | REPLICATE_DO_TABLE '=' '(' tables ')'                       # doTableReplication
    | REPLICATE_IGNORE_TABLE '=' '(' tables ')'                   # ignoreTableReplication
    | REPLICATE_WILD_DO_TABLE '=' '(' simpleStrings ')'           # wildDoTableReplication
    | REPLICATE_WILD_IGNORE_TABLE '=' '(' simpleStrings ')'       # wildIgnoreTableReplication
    | REPLICATE_REWRITE_DB '=' '(' tablePair (',' tablePair)* ')' # rewriteDbReplication
    ;

tablePair
    : '(' firstTable = tableName ',' secondTable = tableName ')'
    ;

threadType
    : IO_THREAD
    | SQL_THREAD
    ;

untilOption
    : gtids = (SQL_BEFORE_GTIDS | SQL_AFTER_GTIDS) '=' gtuidSet                # gtidsUntilOption
    | MASTER_LOG_FILE '=' STRING_LITERAL ',' MASTER_LOG_POS '=' decimalLiteral # masterLogUntilOption
    | RELAY_LOG_FILE '=' STRING_LITERAL ',' RELAY_LOG_POS '=' decimalLiteral   # relayLogUntilOption
    | SQL_AFTER_MTS_GAPS                                                       # sqlGapsUntilOption
    ;

connectionOption
    : USER '=' conOptUser = STRING_LITERAL            # userConnectionOption
    | PASSWORD '=' conOptPassword = STRING_LITERAL    # passwordConnectionOption
    | DEFAULT_AUTH '=' conOptDefAuth = STRING_LITERAL # defaultAuthConnectionOption
    | PLUGIN_DIR '=' conOptPluginDir = STRING_LITERAL # pluginDirConnectionOption
    ;

gtuidSet
    : uuidSet (',' uuidSet)*
    | STRING_LITERAL
    ;

//    XA Transactions

xaStartTransaction
    : XA xaStart = (START | BEGIN) xid xaAction = (JOIN | RESUME)?
    ;

xaEndTransaction
    : XA (END | END_SYMBOLE) xid (SUSPEND (FOR MIGRATE)?)?
    ;

xaPrepareStatement
    : XA PREPARE xid
    ;

xaCommitWork
    : XA COMMIT xid (ONE PHASE)?
    ;

xaRollbackWork
    : XA ROLLBACK xid
    ;

xaRecoverWork
    : XA RECOVER (CONVERT xid)?
    ;

// Prepared Statements

prepareStatement
    : PREPARE uid FROM (query = STRING_LITERAL | variable = LOCAL_ID)
    ;

executeStatement
    : EXECUTE uid (USING userVariables)?
    ;

deallocatePrepare
    : dropFormat = (DEALLOCATE | DROP) PREPARE uid
    ;

// Compound Statements

routineBody
    : blockStatement
    | sqlStatement
    ;

// details

blockStatement
    : (uid ':')? BEGIN (declareVariable SEMI)* (declareCondition SEMI)* (declareCursor SEMI)* (
        declareHandler SEMI
    )* procedureSqlStatement* (END | END_SYMBOLE)
    ;

caseStatement
    : CASE (uid | expression)? caseAlternative+ (ELSE procedureSqlStatement+)? (END | END_SYMBOLE) CASE
    ;

ifStatement
    : IF expression THEN thenStatements += procedureSqlStatement+ elifAlternative* (
        ELSE elseStatements += procedureSqlStatement+
    )? (END | END_SYMBOLE) IF
    ;

iterateStatement
    : ITERATE uid
    ;

leaveStatement
    : LEAVE uid
    ;

loopStatement
    : (uid ':')? LOOP procedureSqlStatement+ (END | END_SYMBOLE) LOOP uid?
    ;

repeatStatement
    : (uid ':')? REPEAT procedureSqlStatement+ UNTIL expression (END | END_SYMBOLE) REPEAT uid?
    ;

returnStatement
    : RETURN expression
    ;

whileStatement
    : (uid ':')? WHILE expression DO procedureSqlStatement+ (END | END_SYMBOLE) WHILE uid?
    ;

cursorStatement
    : CLOSE uid                            # CloseCursor
    | FETCH (NEXT? FROM)? uid INTO uidList # FetchCursor
    | OPEN uid                             # OpenCursor
    ;

// details

declareVariable
    : DECLARE uidList dataType (DEFAULT expression)?
    ;

declareCondition
    : DECLARE uid CONDITION FOR (decimalLiteral | SQLSTATE VALUE? STRING_LITERAL)
    ;

declareCursor
    : DECLARE uid CURSOR FOR selectStatement
    ;

declareHandler
    : DECLARE handlerAction = (CONTINUE | EXIT | UNDO) HANDLER FOR handlerConditionValue (
        ',' handlerConditionValue
    )* routineBody
    ;

handlerConditionValue
    : decimalLiteral                 # handlerConditionCode
    | SQLSTATE VALUE? STRING_LITERAL # handlerConditionState
    | uid                            # handlerConditionName
    | SQLWARNING                     # handlerConditionWarning
    | NOT FOUND                      # handlerConditionNotfound
    | SQLEXCEPTION                   # handlerConditionException
    ;

procedureSqlStatement
    : (compoundStatement | sqlStatement) SEMI
    ;

caseAlternative
    : WHEN (constant | expression) THEN procedureSqlStatement+
    ;

elifAlternative
    : ELSEIF expression THEN procedureSqlStatement+
    ;

// Administration Statements

//    Account management statements

alterUser
    : ALTER USER userSpecification (',' userSpecification)* # alterUserMysqlV56
    | ALTER USER ifExists? userAuthOption (',' userAuthOption)* (
        REQUIRE (tlsNone = NONE | tlsOption (AND? tlsOption)*)
    )? (WITH userResourceOption+)? (userPasswordOption | userLockOption)* (
        COMMENT STRING_LITERAL
        | ATTRIBUTE STRING_LITERAL
    )?                                                              # alterUserMysqlV80
    | ALTER USER ifExists? (userName | uid) DEFAULT ROLE roleOption # alterUserMysqlV80
    ;

createUser
    : CREATE USER userAuthOption (',' userAuthOption)* # createUserMysqlV56
    | CREATE USER ifNotExists? userAuthOption (',' userAuthOption)* (DEFAULT ROLE roleOption)? (
        REQUIRE (tlsNone = NONE | tlsOption (AND? tlsOption)*)
    )? (WITH userResourceOption+)? (userPasswordOption | userLockOption)* (
        COMMENT STRING_LITERAL
        | ATTRIBUTE STRING_LITERAL
    )? # createUserMysqlV80
    ;

dropUser
    : DROP USER ifExists? userReferenceName (',' userReferenceName)*
    ;

grantStatement
    : GRANT privelegeClause (',' privelegeClause)* ON privilegeLevel TO userAuthOption (',' userAuthOption)* (
        REQUIRE (tlsNone = NONE | tlsOption (AND? tlsOption)*)
    )? (WITH (GRANT OPTION | userResourceOption)*)? (AS userName WITH ROLE roleOption)?
    | GRANT roleReferenceName (',' roleReferenceName)* TO userReferenceName (',' userReferenceName)* (
        WITH ADMIN OPTION
    )?
    ;

roleOption
    : DEFAULT
    | NONE
    | ALL (EXCEPT roleReferenceName (',' roleReferenceName)*)?
    | roleReferenceName (',' roleReferenceName)*
    ;

grantProxy
    : GRANT PROXY ON fromFirst = userReferenceName TO toFirst = userReferenceName (',' toOther += userReferenceName)* (
        WITH GRANT OPTION
    )?
    ;

renameUser
    : RENAME USER renameUserClause (',' renameUserClause)*
    ;

revokeStatement
    : REVOKE ifExists? (privelegeClause | uid) (',' privelegeClause | uid)*
      ON
      privilegeLevel
      FROM userReferenceName (',' userReferenceName)* (IGNORE UNKNOWN USER)?          #detailRevoke
    | REVOKE ifExists? ALL PRIVILEGES? ',' GRANT OPTION
      FROM userReferenceName (',' userReferenceName)*  (IGNORE UNKNOWN USER)?         #shortRevoke
    | REVOKE ifExists? roleReferenceName (',' roleReferenceName)*
      FROM userReferenceName (',' userReferenceName)*
      (IGNORE UNKNOWN USER)?                                        #roleRevoke
    ;

revokeProxy
    : REVOKE PROXY ON onUser = userReferenceName FROM fromFirst = userReferenceName (',' fromOther += userReferenceName)*
    ;

setPasswordStatement
    : SET PASSWORD (FOR userName)? '=' (passwordFunctionClause | STRING_LITERAL)
    ;

// details

userSpecification
    : userName userPasswordOption
    ;

userAuthOption
    : userName IDENTIFIED BY PASSWORD hashed = STRING_LITERAL # hashAuthOption
    | userName IDENTIFIED BY RANDOM PASSWORD authOptionClause # randomAuthOption
    | userName IDENTIFIED BY STRING_LITERAL authOptionClause  # stringAuthOption
    | userName IDENTIFIED WITH authenticationRule             # moduleAuthOption
    | userName                                                # simpleAuthOption
    ;

authOptionClause
    : (REPLACE STRING_LITERAL)? (RETAIN CURRENT PASSWORD)?
    ;

authenticationRule
    : authPlugin ((BY | USING | AS) (STRING_LITERAL | RANDOM PASSWORD) authOptionClause)? # module
    | authPlugin USING passwordFunctionClause                                             # passwordModuleOption
    ;

tlsOption
    : SSL
    | X509
    | CIPHER STRING_LITERAL
    | ISSUER STRING_LITERAL
    | SUBJECT STRING_LITERAL
    ;

userResourceOption
    : MAX_QUERIES_PER_HOUR decimalLiteral
    | MAX_UPDATES_PER_HOUR decimalLiteral
    | MAX_CONNECTIONS_PER_HOUR decimalLiteral
    | MAX_USER_CONNECTIONS decimalLiteral
    ;

userPasswordOption
    : PASSWORD EXPIRE (
        expireType = DEFAULT
        | expireType = NEVER
        | expireType = INTERVAL decimalLiteral DAY
    )?
    | PASSWORD HISTORY (DEFAULT | decimalLiteral)
    | PASSWORD REUSE INTERVAL (DEFAULT | decimalLiteral DAY)
    | PASSWORD REQUIRE CURRENT (OPTIONAL | DEFAULT)?
    | FAILED_LOGIN_ATTEMPTS decimalLiteral
    | PASSWORD_LOCK_TIME (decimalLiteral | UNBOUNDED)
    ;

userLockOption
    : ACCOUNT lockType = (LOCK | UNLOCK)
    ;

privelegeClause
    : privilege ('(' uidList ')')?
    ;

privilege
    : ALL PRIVILEGES?
    | ALTER ROUTINE?
    | CREATE (TEMPORARY TABLES | ROUTINE | VIEW | USER | TABLESPACE | ROLE)?
    | DELETE
    | DROP (ROLE)?
    | EVENT
    | EXECUTE
    | FILE
    | GRANT OPTION
    | INDEX
    | INSERT
    | LOCK TABLES
    | PROCESS
    | PROXY
    | REFERENCES
    | RELOAD
    | REPLICATION (CLIENT | SLAVE)
    | SELECT
    | SHOW (VIEW | DATABASES)
    | SHUTDOWN
    | SUPER
    | TRIGGER
    | UPDATE
    | USAGE
    | APPLICATION_PASSWORD_ADMIN
    | AUDIT_ABORT_EXEMPT
    | AUDIT_ADMIN
    | AUTHENTICATION_POLICY_ADMIN
    | BACKUP_ADMIN
    | BINLOG_ADMIN
    | BINLOG_ENCRYPTION_ADMIN
    | CLONE_ADMIN
    | CONNECTION_ADMIN
    | ENCRYPTION_KEY_ADMIN
    | FIREWALL_ADMIN
    | FIREWALL_EXEMPT
    | FIREWALL_USER
    | FLUSH_OPTIMIZER_COSTS
    | FLUSH_STATUS
    | FLUSH_TABLES
    | FLUSH_USER_RESOURCES
    | GROUP_REPLICATION_ADMIN
    | INNODB_REDO_LOG_ARCHIVE
    | INNODB_REDO_LOG_ENABLE
    | NDB_STORED_USER
    | PASSWORDLESS_USER_ADMIN
    | PERSIST_RO_VARIABLES_ADMIN
    | REPLICATION_APPLIER
    | REPLICATION_SLAVE_ADMIN
    | RESOURCE_GROUP_ADMIN
    | RESOURCE_GROUP_USER
    | ROLE_ADMIN
    | SERVICE_CONNECTION_ADMIN
    | SESSION_VARIABLES_ADMIN
    | SET_USER_ID
    | SKIP_QUERY_REWRITE
    | SHOW_ROUTINE
    | SYSTEM_USER
    | SYSTEM_VARIABLES_ADMIN
    | TABLE_ENCRYPTION_ADMIN
    | TP_CONNECTION_ADMIN
    | VERSION_TOKEN_ADMIN
    | XA_RECOVER_ADMIN
    // MySQL on Amazon RDS
    | LOAD FROM S3
    | SELECT INTO S3
    | INVOKE LAMBDA
    ;

privilegeLevel
    : '*'          # currentSchemaPriviLevel
    | '*' '.' '*'  # globalPrivLevel
    | databaseReferenceName '.' '*' # definiteSchemaPrivLevel
    | TABLE? tableReferenceName     # definiteTablePrivLevel
    | FUNCTION functionReferenceName # definiteFunctionPrivLevel
    | PROCEDURE procedureReferenceName # definiteProcedurePrivLevel
    ;

renameUserClause
    : fromFirst = userName TO toFirst = userName
    ;

//    Table maintenance statements

analyzeTable
    : ANALYZE actionOption = (NO_WRITE_TO_BINLOG | LOCAL)? (TABLE | TABLES) tables (
        UPDATE HISTOGRAM ON fullColumnName (',' fullColumnName)* (WITH decimalLiteral BUCKETS)?
    )? (DROP HISTOGRAM ON fullColumnName (',' fullColumnName)*)?
    ;

checkTable
    : CHECK TABLE tables checkTableOption*
    ;

checksumTable
    : CHECKSUM TABLE tables actionOption = (QUICK | EXTENDED)?
    ;

optimizeTable
    : OPTIMIZE actionOption = (NO_WRITE_TO_BINLOG | LOCAL)? (TABLE | TABLES) tables
    ;

repairTable
    : REPAIR actionOption = (NO_WRITE_TO_BINLOG | LOCAL)? TABLE tables QUICK? EXTENDED? USE_FRM?
    ;

// details

checkTableOption
    : FOR UPGRADE
    | QUICK
    | FAST
    | MEDIUM
    | EXTENDED
    | CHANGED
    ;

//    Plugin and udf statements

createUdfunction
    : CREATE AGGREGATE? FUNCTION ifNotExists? uid RETURNS returnType = (
        STRING
        | INTEGER
        | REAL
        | DECIMAL
    ) SONAME STRING_LITERAL
    ;

installPlugin
    : INSTALL PLUGIN uid SONAME STRING_LITERAL
    ;

uninstallPlugin
    : UNINSTALL PLUGIN uid
    ;

//    Set and show statements

setStatement
    : SET variableClause ('=' | ':=') (expression | ON) (
        ',' variableClause ('=' | ':=') (expression | ON)
    )*                                                                         # setVariable
    | SET charSet (charsetName | DEFAULT)                                      # setCharset
    | SET NAMES (charsetName (COLLATE collationName)? | DEFAULT)               # setNames
    | setPasswordStatement                                                     # setPassword
    | setTransactionStatement                                                  # setTransaction
    | setAutocommitStatement                                                   # setAutocommit
    | SET fullId ('=' | ':=') expression (',' fullId ('=' | ':=') expression)* # setNewValueInsideTrigger
    ;

showStatement
    : SHOW logFormat = (BINARY | MASTER) LOGS # showMasterLogs
    | SHOW logFormat = (BINLOG | RELAYLOG) EVENTS (IN filename = STRING_LITERAL)? (
        FROM fromPosition = decimalLiteral
    )? (LIMIT (offset = decimalLiteral ',')? rowCount = decimalLiteral)? # showLogEvents
    | SHOW showCommonEntity showFilter?                                  # showObjectFilter
    | SHOW FULL? columnsFormat = (COLUMNS | FIELDS) tableFormat = (FROM | IN) tableReferenceName (
        schemaFormat = (FROM | IN) databaseReferenceName
    )? showFilter?                                                                             # showColumns
    | SHOW CREATE schemaFormat = (DATABASE | SCHEMA) ifNotExists? databaseReferenceName        # showCreateDb
    | SHOW CREATE EVENT eventReferenceName                                               # showCreateEvent
    | SHOW CREATE FUNCTION functionReferenceName                                         # showCreateFunction
    | SHOW CREATE PROCEDURE procedureReferenceName                                       # showCreateProcedure
    | SHOW CREATE TABLE tableReferenceName                                               # showCreateTable
    | SHOW CREATE TRIGGER triggerReferenceName                                           # showCreateTrigger
    | SHOW CREATE VIEW viewReferenceName                                                 # showCreateView
    | SHOW CREATE USER userReferenceName                                                       # showCreateUser
    | SHOW ENGINE engineName engineOption = (STATUS | MUTEX)                                   # showEngine
    | SHOW showGlobalInfoClause                                                                # showGlobalInfo
    | SHOW errorFormat = (ERRORS | WARNINGS) (
        LIMIT (offset = decimalLiteral ',')? rowCount = decimalLiteral
    )?                                                                    # showErrors
    | SHOW COUNT '(' '*' ')' errorFormat = (ERRORS | WARNINGS)            # showCountErrors
    | SHOW showSchemaEntity (schemaFormat = (FROM | IN) databaseReferenceName)? showFilter? # showSchemaFilter
    | SHOW FUNCTION CODE functionReferenceName                            # showFunctionCode
    | SHOW PROCEDURE CODE procedureReferenceName                          # showProcedureCode
    | SHOW GRANTS (FOR userName)?                                         # showGrants
    | SHOW indexFormat = (INDEX | INDEXES | KEYS) tableFormat = (FROM | IN) tableReferenceName (
        schemaFormat = (FROM | IN) databaseReferenceName
    )? (WHERE expression)?                                           # showIndexes
    | SHOW OPEN TABLES (schemaFormat = (FROM | IN) databaseReferenceName)? showFilter? # showOpenTables
    | SHOW PROFILE showProfileType (',' showProfileType)* (FOR QUERY queryCount = decimalLiteral)? (
        LIMIT (offset = decimalLiteral ',')? rowCount = decimalLiteral
    )                                                 # showProfile
    | SHOW SLAVE STATUS (FOR CHANNEL STRING_LITERAL)? # showSlaveStatus
    ;

// details

variableClause
    : LOCAL_ID
    | GLOBAL_ID
    | ( ('@' '@')? (GLOBAL | SESSION | LOCAL))? uid
    ;

showCommonEntity
    : CHARACTER SET
    | COLLATION
    | DATABASES
    | SCHEMAS
    | FUNCTION STATUS
    | PROCEDURE STATUS
    | (GLOBAL | SESSION)? (STATUS | VARIABLES)
    ;

showFilter
    : LIKE STRING_LITERAL
    | WHERE expression
    ;

showGlobalInfoClause
    : STORAGE? ENGINES
    | MASTER STATUS
    | PLUGINS
    | PRIVILEGES
    | FULL? PROCESSLIST
    | PROFILES
    | SLAVE HOSTS
    | AUTHORS
    | CONTRIBUTORS
    ;

showSchemaEntity
    : EVENTS
    | TABLE STATUS
    | FULL? TABLES
    | TRIGGERS
    ;

showProfileType
    : ALL
    | BLOCK IO
    | CONTEXT SWITCHES
    | CPU
    | IPC
    | MEMORY
    | PAGE FAULTS
    | SOURCE
    | SWAPS
    ;

//    Other administrative statements

binlogStatement
    : BINLOG STRING_LITERAL
    ;

cacheIndexStatement
    : CACHE INDEX tableIndexes (',' tableIndexes)* (PARTITION '(' (uidList | ALL) ')')? IN schema = uid
    ;

flushStatement
    : FLUSH flushFormat = (NO_WRITE_TO_BINLOG | LOCAL)? flushOption (',' flushOption)*
    ;

killStatement
    : KILL connectionFormat = (CONNECTION | QUERY)? expression
    ;

loadIndexIntoCache
    : LOAD INDEX INTO CACHE loadedTableIndexes (',' loadedTableIndexes)*
    ;

// remark reset (maser | slave) describe in replication's
//  statements section
resetStatement
    : RESET QUERY CACHE
    ;

shutdownStatement
    : SHUTDOWN
    ;

// details

tableIndexes
    : tableReferenceName (indexFormat = (INDEX | KEY)? '(' uidList ')')?
    ;

flushOption
    : (
        DES_KEY_FILE
        | HOSTS
        | ( BINARY | ENGINE | ERROR | GENERAL | RELAY | SLOW)? LOGS
        | OPTIMIZER_COSTS
        | PRIVILEGES
        | QUERY CACHE
        | STATUS
        | USER_RESOURCES
        | TABLES (WITH READ LOCK)?
    )                                            # simpleFlushOption
    | RELAY LOGS channelOption?                  # channelFlushOption
    | (TABLE | TABLES) tables? flushTableOption? # tableFlushOption
    ;

flushTableOption
    : WITH READ LOCK
    | FOR EXPORT
    ;

loadedTableIndexes
    : tableReferenceName (PARTITION '(' (partitionList = uidList | ALL) ')')? (
        indexFormat = (INDEX | KEY)? '(' indexList = uidList ')'
    )? (IGNORE LEAVES)?
    ;

// Utility Statements

simpleDescribeStatement
    : command = (EXPLAIN | DESCRIBE | DESC) tableReferenceName (column = uid | pattern = STRING_LITERAL)?
    ;

fullDescribeStatement
    : command = (EXPLAIN | DESCRIBE | DESC) (
        formatType = (EXTENDED | PARTITIONS | FORMAT) '=' formatValue = (TRADITIONAL | JSON)
    )? describeObjectClause
    ;

helpStatement
    : HELP STRING_LITERAL
    ;

useStatement
    : USE databaseReferenceName
    ;

signalStatement
    : SIGNAL (( SQLSTATE VALUE? stringLiteral) | ID | REVERSE_QUOTE_ID) (
        SET signalConditionInformation ( ',' signalConditionInformation)*
    )?
    ;

resignalStatement
    : RESIGNAL (( SQLSTATE VALUE? stringLiteral) | ID | REVERSE_QUOTE_ID)? (
        SET signalConditionInformation ( ',' signalConditionInformation)*
    )?
    ;

signalConditionInformation
    : (
        CLASS_ORIGIN
        | SUBCLASS_ORIGIN
        | MESSAGE_TEXT
        | MYSQL_ERRNO
        | CONSTRAINT_CATALOG
        | CONSTRAINT_SCHEMA
        | CONSTRAINT_NAME
        | CATALOG_NAME
        | SCHEMA_NAME
        | TABLE_NAME
        | COLUMN_NAME
        | CURSOR_NAME
    ) '=' (stringLiteral | DECIMAL_LITERAL | mysqlVariable | simpleId)
    ;

withStatement
    : WITH RECURSIVE? commonTableExpressions
     (',' commonTableExpressions)*
    ;

tableStatement
    : TABLE tableReferenceName orderByClause? limitClause?
    ;

diagnosticsStatement
    : GET (CURRENT | STACKED)? DIAGNOSTICS (
        (variableClause '=' ( NUMBER | ROW_COUNT) ( ',' variableClause '=' ( NUMBER | ROW_COUNT))*)
        | (
            CONDITION (decimalLiteral | variableClause) variableClause '=' diagnosticsConditionInformationName (
                ',' variableClause '=' diagnosticsConditionInformationName
            )*
        )
    )
    ;

diagnosticsConditionInformationName
    : CLASS_ORIGIN
    | SUBCLASS_ORIGIN
    | RETURNED_SQLSTATE
    | MESSAGE_TEXT
    | MYSQL_ERRNO
    | CONSTRAINT_CATALOG
    | CONSTRAINT_SCHEMA
    | CONSTRAINT_NAME
    | CATALOG_NAME
    | SCHEMA_NAME
    | TABLE_NAME
    | COLUMN_NAME
    | CURSOR_NAME
    ;

// details

describeObjectClause
    : (selectStatement | deleteStatement | insertStatement | replaceStatement | updateStatement) # describeStatements
    | FOR CONNECTION uid                                                                         # describeConnection
    ;

// Common Clauses

//    DB Objects

fullId
    : uid (DOT_ID | '.' uid)?
    ;

tableName
    : fullId
    ;

tableReferenceName
    : tableName
    ;

tableDeclarationName
    : tableName
    ;

viewReferenceName
    : tableName
    ;

viewDeclarationName
    : tableName
    ;

eventReferenceName
    : fullId
    ;

eventDeclarationName
    : fullId
    ;

functionReferenceName
    : fullId
    ;

functionDeclarationName
    : fullId
    ;

procedureReferenceName
    : fullId
    ;

procedureDeclarationName
    : fullId
    ;

triggerReferenceName
    : fullId
    ;

triggerDeclarationName
    : fullId
    ;

databaseReferenceName
    : uid
    ;

databaseDeclarationName
    : uid
    ;

roleName
    : userName
    | uid
    ;

roleReferenceName
    : roleName
    ;

roleDeclarationName
    : roleName
    ;

userReferenceName
    : userName
    ;

userDeclarationName
    : userName
    ;

tablespaceReferenceName
    : uid
    ;

tablespaceDeclarationName
    : uid
    ;

fullColumnName
    : uid (dottedId dottedId?)?
    | .? dottedId dottedId?
    ;

columnReferenceName
    : fullColumnName
    ;

columnDeclarationName
    : fullColumnName
    ;

aliasDeclarationName
    : uid
    ;

indexReferenceName
    : uid
    ;

indexDeclarationName
    : uid
    ;

constraintReferenceName
    : uid
    ;

constraintDeclarationName
    : uid
    ;

indexColumnName
    : ((columnReferenceName | STRING_LITERAL) indexColumnLength? | expression) sortType = (ASC | DESC)?
    ;

indexColumnLength
    : '(' decimalLiteral ')'
    ;

simpleUserName
    : STRING_LITERAL
    | ID
    | ADMIN
    | keywordsCanBeId
    ;

hostName
    : (LOCAL_ID | HOST_IP_ADDRESS | '@')
    ;

userName
    : simpleUserName
    | simpleUserName hostName
    | currentUserExpression
    ;

mysqlVariable
    : LOCAL_ID
    | GLOBAL_ID
    ;

charsetName
    : BINARY
    | charsetNameBase
    | STRING_LITERAL
    | CHARSET_REVERSE_QOUTE_STRING
    ;

collationName
    : uid
    | STRING_LITERAL
    ;

engineName
    : engineNameBase
    | ID
    | STRING_LITERAL
    ;

engineNameBase
    : ARCHIVE
    | BLACKHOLE
    | CONNECT
    | CSV
    | FEDERATED
    | INNODB
    | MEMORY
    | MRG_MYISAM
    | MYISAM
    | NDB
    | NDBCLUSTER
    | PERFORMANCE_SCHEMA
    | TOKUDB
    ;

uuidSet
    : decimalLiteral '-' decimalLiteral '-' decimalLiteral '-' decimalLiteral '-' decimalLiteral (
        ':' decimalLiteral '-' decimalLiteral
    )+
    ;

xid
    : globalTableUid = xuidStringId (',' qualifier = xuidStringId (',' idFormat = decimalLiteral)?)?
    ;

xuidStringId
    : STRING_LITERAL
    | BIT_STRING
    | HEXADECIMAL_LITERAL+
    ;

authPlugin
    : uid
    | STRING_LITERAL
    ;

uid
    : simpleId
    //| DOUBLE_QUOTE_ID
    //| REVERSE_QUOTE_ID
    | CHARSET_REVERSE_QOUTE_STRING
    | STRING_LITERAL
    ;

simpleId
    : ID
    | charsetNameBase
    | transactionLevelBase
    | engineNameBase
    | privilegesBase
    | intervalTypeBase
    | dataTypeBase
    | keywordsCanBeId
    | scalarFunctionName
    ;

dottedId
    : DOT_ID
    | '.' uid
    ;

//    Literals

decimalLiteral
    : DECIMAL_LITERAL
    | ZERO_DECIMAL
    | ONE_DECIMAL
    | TWO_DECIMAL
    | REAL_LITERAL
    ;

fileSizeLiteral
    : FILESIZE_LITERAL
    | decimalLiteral
    ;

stringLiteral
    : (STRING_CHARSET_NAME? STRING_LITERAL | START_NATIONAL_STRING_LITERAL) STRING_LITERAL+
    | (STRING_CHARSET_NAME? STRING_LITERAL | START_NATIONAL_STRING_LITERAL) (COLLATE collationName)?
    ;

booleanLiteral
    : TRUE
    | FALSE
    ;

hexadecimalLiteral
    : STRING_CHARSET_NAME? HEXADECIMAL_LITERAL
    ;

nullNotnull
    : NOT? (NULL_LITERAL | NULL_SPEC_LITERAL)
    ;

constant
    : stringLiteral
    | decimalLiteral
    | '-' decimalLiteral
    | hexadecimalLiteral
    | booleanLiteral
    | REAL_LITERAL
    | BIT_STRING
    | NOT? nullLiteral = (NULL_LITERAL | NULL_SPEC_LITERAL)
    ;

//    Data Types

dataType
    : typeName = (
        CHAR
        | CHARACTER
        | VARCHAR
        | TINYTEXT
        | TEXT
        | MEDIUMTEXT
        | LONGTEXT
        | NCHAR
        | NVARCHAR
        | LONG
    ) VARYING? lengthOneDimension? BINARY? (charSet charsetName)? (COLLATE collationName | BINARY)? # stringDataType
    | NATIONAL typeName = (CHAR | CHARACTER) VARYING lengthOneDimension? BINARY?                    # nationalVaryingStringDataType
    | NATIONAL typeName = (VARCHAR | CHARACTER | CHAR) lengthOneDimension? BINARY?                  # nationalStringDataType
    | NCHAR typeName = VARCHAR lengthOneDimension? BINARY?                                          # nationalStringDataType
    | typeName = (
        TINYINT
        | SMALLINT
        | MEDIUMINT
        | INT
        | INTEGER
        | BIGINT
        | MIDDLEINT
        | INT1
        | INT2
        | INT3
        | INT4
        | INT8
    ) lengthOneDimension? (SIGNED | UNSIGNED | ZEROFILL)*                              # dimensionDataType
    | typeName = REAL lengthTwoDimension? (SIGNED | UNSIGNED | ZEROFILL)*              # dimensionDataType
    | typeName = DOUBLE PRECISION? lengthTwoDimension? (SIGNED | UNSIGNED | ZEROFILL)* # dimensionDataType
    | typeName = (DECIMAL | DEC | FIXED | NUMERIC | FLOAT | FLOAT4 | FLOAT8) lengthTwoOptionalDimension? (
        SIGNED
        | UNSIGNED
        | ZEROFILL
    )*                                                                                                      # dimensionDataType
    | typeName = (DATE | TINYBLOB | MEDIUMBLOB | LONGBLOB | BOOL | BOOLEAN | SERIAL)                        # simpleDataType
    | typeName = (BIT | TIME | TIMESTAMP | DATETIME | BINARY | VARBINARY | BLOB | YEAR | VECTOR) lengthOneDimension? # dimensionDataType
    | typeName = (ENUM | SET) collectionOptions BINARY? (charSet charsetName)?                              # collectionDataType
    | typeName = (
        GEOMETRYCOLLECTION
        | GEOMCOLLECTION
        | LINESTRING
        | MULTILINESTRING
        | MULTIPOINT
        | MULTIPOLYGON
        | POINT
        | POLYGON
        | JSON
        | GEOMETRY
    ) (SRID decimalLiteral)?                                                           # spatialDataType
    | typeName = LONG VARCHAR? BINARY? (charSet charsetName)? (COLLATE collationName)? # longVarcharDataType // LONG VARCHAR is the same as LONG
    | LONG VARBINARY                                                                   # longVarbinaryDataType
    ;

collectionOptions
    : '(' STRING_LITERAL (',' STRING_LITERAL)* ')'
    ;

convertedDataType
    : (
        typeName = (BINARY | NCHAR | FLOAT) lengthOneDimension?
        | typeName = CHAR lengthOneDimension? (charSet charsetName)?
        | typeName = (DATE | DATETIME | TIME | YEAR | JSON | INT | INTEGER | DOUBLE)
        | typeName = (DECIMAL | DEC) lengthTwoOptionalDimension?
        | (SIGNED | UNSIGNED) (INTEGER | INT)?
    ) ARRAY?
    ;

lengthOneDimension
    : '(' decimalLiteral ')'
    ;

lengthTwoDimension
    : '(' decimalLiteral ',' decimalLiteral ')'
    ;

lengthTwoOptionalDimension
    : '(' decimalLiteral (',' decimalLiteral)? ')'
    ;

//    Common Lists

uidList
    : uid (',' uid)*
    ;

fullColumnNameList
    : fullColumnName (',' fullColumnName)*
    ;

columnReferenceNameList
    : columnReferenceName (',' columnReferenceName)*
    ;

tables
    : tableReferenceName (',' tableReferenceName)*
    ;

indexColumnNames
    : '(' indexColumnName (',' indexColumnName)* ')'
    ;

expressions
    : expression (',' expression)*
    ;

expressionsWithDefaults
    : expressionOrDefault (',' expressionOrDefault)*
    ;

constants
    : constant (',' constant)*
    ;

simpleStrings
    : STRING_LITERAL (',' STRING_LITERAL)*
    ;

userVariables
    : LOCAL_ID (',' LOCAL_ID)*
    ;

//    Common Expressons

defaultValue
    : NULL_LITERAL
    | CAST '(' expression AS convertedDataType ')'
    | unaryOperator? constant
    | currentTimestamp (ON UPDATE currentTimestamp)?
    | '(' expression ')'
    | '(' fullId ')'
    ;

currentTimestamp
    : (
        (CURRENT_TIMESTAMP | LOCALTIME | LOCALTIMESTAMP) ('(' decimalLiteral? ')')?
        | NOW '(' decimalLiteral? ')'
    )
    ;

expressionOrDefault
    : expression
    | DEFAULT
    ;

ifExists
    : IF EXISTS
    ;

ifNotExists
    : IF NOT EXISTS
    ;

orReplace
    : OR REPLACE
    ;

waitNowaitClause
    : WAIT decimalLiteral
    | NOWAIT
    ;

//    Functions

functionCall
    : specificFunction                         # specificFunctionCall
    | aggregateWindowedFunction                # aggregateFunctionCall
    | nonAggregateWindowedFunction             # nonAggregateFunctionCall
    | scalarFunctionName '(' functionArgs? ')' # scalarFunctionCall
    | fullId '(' functionArgs? ')'             # udfFunctionCall
    | passwordFunctionClause                   # passwordFunctionCall
    ;

specificFunction
    : (CURRENT_DATE | CURRENT_TIME | CURRENT_TIMESTAMP | LOCALTIME | UTC_TIMESTAMP | SCHEMA) (
        '(' ')'
    )?                                                                       # simpleFunctionCall
    | currentUserExpression                                                  # currentUser
    | CONVERT '(' expression separator = ',' convertedDataType ')'           # dataTypeFunctionCall
    | CONVERT '(' expression USING charsetName ')'                           # dataTypeFunctionCall
    | CAST '(' expression AS convertedDataType ')'                           # dataTypeFunctionCall
    | VALUES '(' columnReferenceName ')'                                     # valuesFunctionCall
    | CASE expression caseFuncAlternative+ (ELSE elseArg = functionArg)? (END | END_SYMBOLE) # caseExpressionFunctionCall
    | CASE caseFuncAlternative+ (ELSE elseArg = functionArg)? (END | END_SYMBOLE)            # caseFunctionCall
    | CHAR '(' functionArgs (USING charsetName)? ')'                         # charFunctionCall
    | POSITION '(' (positionString = stringLiteral | positionExpression = expression) IN (
        inString = stringLiteral
        | inExpression = expression
    ) ')' # positionFunctionCall
    | (SUBSTR | SUBSTRING) '(' (sourceString = stringLiteral | sourceExpression = expression) FROM (
        fromDecimal = decimalLiteral
        | fromExpression = expression
    ) (FOR ( forDecimal = decimalLiteral | forExpression = expression))? ')' # substrFunctionCall
    | TRIM '(' positioinForm = (BOTH | LEADING | TRAILING) (
        sourceString = stringLiteral
        | sourceExpression = expression
    )? FROM (fromString = stringLiteral | fromExpression = expression) ')' # trimFunctionCall
    | TRIM '(' (sourceString = stringLiteral | sourceExpression = expression) FROM (
        fromString = stringLiteral
        | fromExpression = expression
    ) ')' # trimFunctionCall
    | WEIGHT_STRING '(' (stringLiteral | expression) (
        AS stringFormat = (CHAR | BINARY) '(' decimalLiteral ')'
    )? levelsInWeightString? ')'                                                                            # weightFunctionCall
    | EXTRACT '(' intervalType FROM (sourceString = stringLiteral | sourceExpression = expression) ')'      # extractFunctionCall
    | GET_FORMAT '(' datetimeFormat = (DATE | TIME | DATETIME) ',' stringLiteral ')'                        # getFormatFunctionCall
    | JSON_VALUE '(' expression ',' expression (RETURNING convertedDataType)? jsonOnEmpty? jsonOnError? ')' # jsonValueFunctionCall
    ;

caseFuncAlternative
    : WHEN condition = functionArg THEN consequent = functionArg
    ;

levelsInWeightString
    : LEVEL levelInWeightListElement (',' levelInWeightListElement)*   # levelWeightList
    | LEVEL firstLevel = decimalLiteral '-' lastLevel = decimalLiteral # levelWeightRange
    ;

levelInWeightListElement
    : decimalLiteral orderType = (ASC | DESC | REVERSE)?
    ;

aggregateWindowedFunction
    : (AVG | MAX | MIN | SUM) '(' aggregator = (ALL | DISTINCT)? functionArg ')' overClause?
    | COUNT '(' (
        starArg = '*'
        | aggregator = ALL? functionArg
        | aggregator = DISTINCT functionArgs
    ) ')' overClause?
    | (
        BIT_AND
        | BIT_OR
        | BIT_XOR
        | STD
        | STDDEV
        | STDDEV_POP
        | STDDEV_SAMP
        | VAR_POP
        | VAR_SAMP
        | VARIANCE
    ) '(' aggregator = ALL? functionArg ')' overClause?
    | GROUP_CONCAT '(' aggregator = DISTINCT? functionArgs (
        ORDER BY orderByExpression (',' orderByExpression)*
    )? (SEPARATOR separator = STRING_LITERAL)? ')'
    ;

nonAggregateWindowedFunction
    : (LAG | LEAD) '(' expression (',' decimalLiteral)? (',' decimalLiteral)? ')' overClause
    | (FIRST_VALUE | LAST_VALUE) '(' expression ')' overClause
    | (CUME_DIST | DENSE_RANK | PERCENT_RANK | RANK | ROW_NUMBER) '(' ')' overClause
    | NTH_VALUE '(' expression ',' decimalLiteral ')' overClause
    | NTILE '(' decimalLiteral ')' overClause
    ;

overClause
    : OVER ('(' windowSpec ')' | windowName)
    ;

windowSpec
    : windowName? partitionClause? orderByClause? frameClause?
    ;

windowName
    : uid
    ;

frameClause
    : frameUnits frameExtent
    ;

frameUnits
    : ROWS
    | RANGE
    ;

frameExtent
    : frameRange
    | frameBetween
    ;

frameBetween
    : BETWEEN frameRange AND frameRange
    ;

frameRange
    : CURRENT ROW
    | UNBOUNDED (PRECEDING | FOLLOWING)
    | expression (PRECEDING | FOLLOWING)
    ;

partitionClause
    : PARTITION BY expression (',' expression)*
    ;

scalarFunctionName
    : functionNameBase
    | ASCII
    | CURDATE
    | CURRENT_DATE
    | CURRENT_TIME
    | CURRENT_TIMESTAMP
    | CURTIME
    | DATE_ADD
    | DATE_SUB
    | IF
    | INSERT
    | LOCALTIME
    | LOCALTIMESTAMP
    | MID
    | NOW
    | REPEAT
    | REPLACE
    | SUBSTR
    | SUBSTRING
    | SYSDATE
    | TRIM
    | UTC_DATE
    | UTC_TIME
    | UTC_TIMESTAMP
    ;

passwordFunctionClause
    : functionName = (PASSWORD | OLD_PASSWORD) '(' functionArg ')'
    ;

functionArgs
    : (constant | columnReferenceName | functionCall | expression) (
        ',' (constant | columnReferenceName | functionCall | expression)
    )*
    ;

functionArg
    : constant
    | columnReferenceName
    | functionCall
    | expression
    ;

//    Expressions, predicates

// Simplified approach for expression
expression
    : notOperator = (NOT | '!') expression                   # notExpression
    | expression logicalOperator expression                  # logicalExpression
    | predicate IS NOT? testValue = (TRUE | FALSE | UNKNOWN) # isExpression
    | predicate                                              # predicateExpression
    ;

predicate
    : predicate NOT? IN '(' (selectStatement | expressions) ')'                            # inPredicate
    | predicate IS nullNotnull                                                             # isNullPredicate
    | left = predicate comparisonOperator right = predicate                                # binaryComparisonPredicate
    | predicate comparisonOperator quantifier = (ALL | ANY | SOME) '(' selectStatement ')' # subqueryComparisonPredicate
    | predicate NOT? BETWEEN predicate AND predicate                                       # betweenPredicate
    | predicate SOUNDS LIKE predicate                                                      # soundsLikePredicate
    | predicate NOT? LIKE predicate (ESCAPE STRING_LITERAL)?                               # likePredicate
    | predicate NOT? regex = (REGEXP | RLIKE) predicate                                    # regexpPredicate
    | predicate MEMBER OF '(' predicate ')'                                                # jsonMemberOfPredicate
    | expressionAtom                                                                       # expressionAtomPredicate
    ;

// Add in ASTVisitor nullNotnull in constant
expressionAtom
    : constant                                                  # constantExpressionAtom
    | columnReferenceName                                       # fullColumnNameExpressionAtom
    | functionCall                                              # functionCallExpressionAtom
    | expressionAtom COLLATE collationName                      # collateExpressionAtom
    | mysqlVariable                                             # mysqlVariableExpressionAtom
    | unaryOperator expressionAtom                              # unaryExpressionAtom
    | BINARY expressionAtom                                     # binaryExpressionAtom
    | LOCAL_ID VAR_ASSIGN expressionAtom                        # variableAssignExpressionAtom
    | '(' expression (',' expression)* ')'                      # nestedExpressionAtom
    | ROW '(' expression (',' expression)+ ')'                  # nestedRowExpressionAtom
    | EXISTS '(' selectStatement ')'                            # existsExpressionAtom
    | '(' selectStatement ')'                                   # subqueryExpressionAtom
    | INTERVAL expression intervalType                          # intervalExpressionAtom
    | left = expressionAtom bitOperator right = expressionAtom  # bitExpressionAtom
    | left = expressionAtom multOperator right = expressionAtom # mathExpressionAtom
    | left = expressionAtom addOperator right = expressionAtom  # mathExpressionAtom
    | left = expressionAtom jsonOperator right = expressionAtom # jsonExpressionAtom
    ;

unaryOperator
    : '!'
    | '~'
    | '+'
    | '-'
    | NOT
    ;

comparisonOperator
    : '='
    | '>'
    | '<'
    | '<' '='
    | '>' '='
    | '<' '>'
    | '!' '='
    | '<' '=' '>'
    ;

logicalOperator
    : AND
    | '&' '&'
    | XOR
    | OR
    | '|' '|'
    ;

bitOperator
    : '<' '<'
    | '>' '>'
    | '&'
    | '^'
    | '|'
    ;

multOperator
    : '*'
    | '/'
    | '%'
    | DIV
    | MOD
    ;

addOperator
    : '+'
    | '-'
    ;

jsonOperator
    : '-' '>'
    | '-' '>' '>'
    ;

//    Simple id sets
//     (that keyword, which can be id)

charsetNameBase
    : ARMSCII8
    | ASCII
    | BIG5
    | BINARY
    | CP1250
    | CP1251
    | CP1256
    | CP1257
    | CP850
    | CP852
    | CP866
    | CP932
    | DEC8
    | EUCJPMS
    | EUCKR
    | GB18030
    | GB2312
    | GBK
    | GEOSTD8
    | GREEK
    | HEBREW
    | HP8
    | KEYBCS2
    | KOI8R
    | KOI8U
    | LATIN1
    | LATIN2
    | LATIN5
    | LATIN7
    | MACCE
    | MACROMAN
    | SJIS
    | SWE7
    | TIS620
    | UCS2
    | UJIS
    | UTF16
    | UTF16LE
    | UTF32
    | UTF8
    | UTF8MB3
    | UTF8MB4
    ;

transactionLevelBase
    : REPEATABLE
    | COMMITTED
    | UNCOMMITTED
    | SERIALIZABLE
    ;

privilegesBase
    : TABLES
    | ROUTINE
    | EXECUTE
    | FILE
    | PROCESS
    | RELOAD
    | SHUTDOWN
    | SUPER
    | PRIVILEGES
    ;

intervalTypeBase
    : QUARTER
    | MONTH
    | DAY
    | HOUR
    | MINUTE
    | WEEK
    | SECOND
    | MICROSECOND
    ;

dataTypeBase
    : DATE
    | TIME
    | TIMESTAMP
    | DATETIME
    | YEAR
    | ENUM
    | TEXT
    ;

keywordsCanBeId
    : ACCOUNT
    | ACTION
    | ADMIN
    | AFTER
    | AGGREGATE
    | ALGORITHM
    | ANY
    | ARRAY
    | AT
    | AUDIT_ADMIN
    | AUDIT_ABORT_EXEMPT
    | AUTHORS
    | AUTOCOMMIT
    | AUTOEXTEND_SIZE
    | AUTO_INCREMENT
    | AUTHENTICATION_POLICY_ADMIN
    | AVG
    | AVG_ROW_LENGTH
    | ATTRIBUTE
    | BACKUP_ADMIN
    | BEGIN
    | BINLOG
    | BINLOG_ADMIN
    | BINLOG_ENCRYPTION_ADMIN
    | BIT
    | BIT_AND
    | BIT_OR
    | BIT_XOR
    | BLOCK
    | BOOL
    | BOOLEAN
    | BTREE
    | BUCKETS
    | CACHE
    | CASCADED
    | CHAIN
    | CHANGED
    | CHANNEL
    | CHECKSUM
    | PAGE_CHECKSUM
    | CATALOG_NAME
    | CIPHER
    | CLASS_ORIGIN
    | CLIENT
    | CLONE_ADMIN
    | CLOSE
    | CLUSTERING
    | COALESCE
    | CODE
    | COLUMNS
    | COLUMN_FORMAT
    | COLUMN_NAME
    | COMMENT
    | COMMIT
    | COMPACT
    | COMPLETION
    | COMPRESSED
    | COMPRESSION
    | CONCURRENT
    | CONDITION
    | CONNECT
    | CONNECTION
    | CONNECTION_ADMIN
    | CONSISTENT
    | CONSTRAINT_CATALOG
    | CONSTRAINT_NAME
    | CONSTRAINT_SCHEMA
    | CONTAINS
    | CONTEXT
    | CONTRIBUTORS
    | COPY
    | COUNT
    | CPU
    | CURRENT
    | CURRENT_USER
    | CURSOR_NAME
    | DATA
    | DATAFILE
    | DEALLOCATE
    | DEFAULT
    | DEFAULT_AUTH
    | DEFINER
    | DELAY_KEY_WRITE
    | DES_KEY_FILE
    | DIAGNOSTICS
    | DIRECTORY
    | DISABLE
    | DISCARD
    | DISK
    | DO
    | DUMPFILE
    | DUPLICATE
    | DYNAMIC
    | EMPTY
    | ENABLE
    | ENCRYPTION
    | ENCRYPTION_KEY_ADMIN
    | END
    | ENDS
    | ENGINE
    | ENGINE_ATTRIBUTE
    | ENGINES
    | ENFORCED
    | ERROR
    | ERRORS
    | ESCAPE
    | EUR
    | EVEN
    | EVENT
    | EVENTS
    | EVERY
    | EXCEPT
    | EXCHANGE
    | EXCLUSIVE
    | EXPIRE
    | EXPORT
    | EXTENDED
    | EXTENT_SIZE
    | FAILED_LOGIN_ATTEMPTS
    | FAST
    | FAULTS
    | FIELDS
    | FILE_BLOCK_SIZE
    | FILTER
    | FIREWALL_ADMIN
    | FIREWALL_EXEMPT
    | FIREWALL_USER
    | FIRST
    | FIXED
    | FLUSH
    | FOLLOWS
    | FOUND
    | FULL
    | FUNCTION
    | GENERAL
    | GLOBAL
    | GRANTS
    | GROUP
    | GROUP_CONCAT
    | GROUP_REPLICATION
    | GROUP_REPLICATION_ADMIN
    | HANDLER
    | HASH
    | HELP
    | HISTORY
    | HOST
    | HOSTS
    | IDENTIFIED
    | IGNORED
    | IGNORE_SERVER_IDS
    | IMPORT
    | INDEXES
    | INITIAL_SIZE
    | INNODB_REDO_LOG_ARCHIVE
    | INPLACE
    | INSERT_METHOD
    | INSTALL
    | INSTANCE
    | INSTANT
    | INTERNAL
    | INVOKE
    | INVOKER
    | IO
    | IO_THREAD
    | IPC
    | ISO
    | ISOLATION
    | ISSUER
    | JIS
    | JSON
    | KEY_BLOCK_SIZE
    | LAMBDA
    | LANGUAGE
    | LAST
    | LATERAL
    | LEAVES
    | LESS
    | LEVEL
    | LIST
    | LOCAL
    | LOGFILE
    | LOGS
    | MASTER
    | MASTER_AUTO_POSITION
    | MASTER_CONNECT_RETRY
    | MASTER_DELAY
    | MASTER_HEARTBEAT_PERIOD
    | MASTER_HOST
    | MASTER_LOG_FILE
    | MASTER_LOG_POS
    | MASTER_PASSWORD
    | MASTER_PORT
    | MASTER_RETRY_COUNT
    | MASTER_SSL
    | MASTER_SSL_CA
    | MASTER_SSL_CAPATH
    | MASTER_SSL_CERT
    | MASTER_SSL_CIPHER
    | MASTER_SSL_CRL
    | MASTER_SSL_CRLPATH
    | MASTER_SSL_KEY
    | MASTER_TLS_VERSION
    | MASTER_USER
    | MAX_CONNECTIONS_PER_HOUR
    | MAX_QUERIES_PER_HOUR
    | MAX
    | MAX_ROWS
    | MAX_SIZE
    | MAX_UPDATES_PER_HOUR
    | MAX_USER_CONNECTIONS
    | MEDIUM
    | MEMBER
    | MEMORY
    | MERGE
    | MESSAGE_TEXT
    | MID
    | MIGRATE
    | MIN
    | MIN_ROWS
    | MODE
    | MODIFY
    | MUTEX
    | MYSQL
    | MYSQL_ERRNO
    | NAME
    | NAMES
    | NCHAR
    | NDB_STORED_USER
    | NESTED
    | NEVER
    | NEXT
    | NO
    | NOCOPY
    | NODEGROUP
    | NONE
    | NOWAIT
    | NUMBER
    | ODBC
    | OFFLINE
    | OFFSET
    | OF
    | OJ
    | OLD_PASSWORD
    | ONE
    | ONLINE
    | ONLY
    | OPEN
    | OPTIMIZER_COSTS
    | OPTIONAL
    | OPTIONS
    | ORDER
    | ORDINALITY
    | OWNER
    | PACK_KEYS
    | PAGE
    | PARSER
    | PARTIAL
    | PARTITIONING
    | PARTITIONS
    | PASSWORD
    | PASSWORDLESS_USER_ADMIN
    | PASSWORD_LOCK_TIME
    | PATH
    | PERSIST_RO_VARIABLES_ADMIN
    | PHASE
    | PLUGINS
    | PLUGIN_DIR
    | PLUGIN
    | PORT
    | PRECEDES
    | PREPARE
    | PRESERVE
    | PREV
    | PRIMARY
    | PROCESSLIST
    | PROFILE
    | PROFILES
    | PROXY
    | QUERY
    | QUICK
    | REBUILD
    | RECOVER
    | RECURSIVE
    | REDO_BUFFER_SIZE
    | REDUNDANT
    | RELAY
    | RELAYLOG
    | RELAY_LOG_FILE
    | RELAY_LOG_POS
    | REMOVE
    | REORGANIZE
    | REPAIR
    | REPLICATE_DO_DB
    | REPLICATE_DO_TABLE
    | REPLICATE_IGNORE_DB
    | REPLICATE_IGNORE_TABLE
    | REPLICATE_REWRITE_DB
    | REPLICATE_WILD_DO_TABLE
    | REPLICATE_WILD_IGNORE_TABLE
    | REPLICATION
    | REPLICATION_APPLIER
    | REPLICATION_SLAVE_ADMIN
    | RESET
    | RESOURCE_GROUP_ADMIN
    | RESOURCE_GROUP_USER
    | RESUME
    | RETURNED_SQLSTATE
    | RETURNING
    | RETURNS
    | REUSE
    | ROLE
    | ROLE_ADMIN
    | ROLLBACK
    | ROLLUP
    | ROTATE
    | ROW
    | ROWS
    | ROW_FORMAT
    | RTREE
    | S3
    | SAVEPOINT
    | SCHEDULE
    | SCHEMA_NAME
    | SECURITY
    | SECONDARY_ENGINE_ATTRIBUTE
    | SERIAL
    | SERVER
    | SESSION
    | SESSION_VARIABLES_ADMIN
    | SET_USER_ID
    | SHARE
    | SHARED
    | SHOW_ROUTINE
    | SIGNED
    | SIMPLE
    | SLAVE
    | SLOW
    | SKIP_QUERY_REWRITE
    | SNAPSHOT
    | SOCKET
    | SOME
    | SONAME
    | SOUNDS
    | SOURCE
    | SQL_AFTER_GTIDS
    | SQL_AFTER_MTS_GAPS
    | SQL_BEFORE_GTIDS
    | SQL_BUFFER_RESULT
    | SQL_CACHE
    | SQL_NO_CACHE
    | SQL_THREAD
    | STACKED
    | START
    | STARTS
    | STATS_AUTO_RECALC
    | STATS_PERSISTENT
    | STATS_SAMPLE_PAGES
    | STATUS
    | STD
    | STDDEV
    | STDDEV_POP
    | STDDEV_SAMP
    | STOP
    | STORAGE
    | STRING
    | SUBCLASS_ORIGIN
    | SUBJECT
    | SUBPARTITION
    | SUBPARTITIONS
    | SUM
    | SUSPEND
    | SWAPS
    | SWITCHES
    | SYSTEM_VARIABLES_ADMIN
    | TABLE_NAME
    | TABLESPACE
    | TABLE_ENCRYPTION_ADMIN
    | TABLE_TYPE
    | TEMPORARY
    | TEMPTABLE
    | THAN
    | TP_CONNECTION_ADMIN
    | TRADITIONAL
    | TRANSACTION
    | TRANSACTIONAL
    | TRIGGERS
    | TRUNCATE
    | UNBOUNDED
    | UNDEFINED
    | UNDOFILE
    | UNDO_BUFFER_SIZE
    | UNINSTALL
    | UNKNOWN
    | UNTIL
    | UPGRADE
    | USA
    | USER
    | USE_FRM
    | USER_RESOURCES
    | VALIDATION
    | VALUE
    | VAR_POP
    | VAR_SAMP
    | VARIABLES
    | VARIANCE
    | VERSION_TOKEN_ADMIN
    | VIEW
    | VIRTUAL
    | WAIT
    | WARNINGS
    | WITHOUT
    | WORK
    | WRAPPER
    | X509
    | XA
    | XA_RECOVER_ADMIN
    | XML
    | YES
    ;

functionNameBase
    : ABS
    | ACOS
    | ADDDATE
    | ADDTIME
    | AES_DECRYPT
    | AES_ENCRYPT
    | AREA
    | ASBINARY
    | ASIN
    | ASTEXT
    | ASWKB
    | ASWKT
    | ASYMMETRIC_DECRYPT
    | ASYMMETRIC_DERIVE
    | ASYMMETRIC_ENCRYPT
    | ASYMMETRIC_SIGN
    | ASYMMETRIC_VERIFY
    | ATAN
    | ATAN2
    | BENCHMARK
    | BIN
    | BIT_COUNT
    | BIT_LENGTH
    | BUFFER
    | CEIL
    | CEILING
    | CENTROID
    | CHARACTER_LENGTH
    | CHARSET
    | CHAR_LENGTH
    | COERCIBILITY
    | COLLATION
    | COMPRESS
    | CONCAT
    | CONCAT_WS
    | CONNECTION_ID
    | CONV
    | CONVERT_TZ
    | COS
    | COT
    | COUNT
    | CRC32
    | CREATE_ASYMMETRIC_PRIV_KEY
    | CREATE_ASYMMETRIC_PUB_KEY
    | CREATE_DH_PARAMETERS
    | CREATE_DIGEST
    | CROSSES
    | CUME_DIST
    | DATABASE
    | DATE
    | DATEDIFF
    | DATE_FORMAT
    | DAY
    | DAYNAME
    | DAYOFMONTH
    | DAYOFWEEK
    | DAYOFYEAR
    | DECODE
    | DEGREES
    | DENSE_RANK
    | DES_DECRYPT
    | DES_ENCRYPT
    | DIMENSION
    | DISJOINT
    | DISTANCE
    | ELT
    | ENCODE
    | ENCRYPT
    | ENDPOINT
    | ENVELOPE
    | EQUALS
    | EXP
    | EXPORT_SET
    | EXTERIORRING
    | EXTRACTVALUE
    | FIELD
    | FIND_IN_SET
    | FIRST_VALUE
    | FLOOR
    | FORMAT
    | FOUND_ROWS
    | FROM_BASE64
    | FROM_DAYS
    | FROM_UNIXTIME
    | GEOMCOLLFROMTEXT
    | GEOMCOLLFROMWKB
    | GEOMETRYCOLLECTION
    | GEOMETRYCOLLECTIONFROMTEXT
    | GEOMETRYCOLLECTIONFROMWKB
    | GEOMETRYFROMTEXT
    | GEOMETRYFROMWKB
    | GEOMETRYN
    | GEOMETRYTYPE
    | GEOMFROMTEXT
    | GEOMFROMWKB
    | GET_FORMAT
    | GET_LOCK
    | GLENGTH
    | GREATEST
    | GTID_SUBSET
    | GTID_SUBTRACT
    | HEX
    | HOUR
    | IFNULL
    | INET6_ATON
    | INET6_NTOA
    | INET_ATON
    | INET_NTOA
    | INSTR
    | INTERIORRINGN
    | INTERSECTS
    | INVISIBLE
    | ISCLOSED
    | ISEMPTY
    | ISNULL
    | ISSIMPLE
    | IS_FREE_LOCK
    | IS_IPV4
    | IS_IPV4_COMPAT
    | IS_IPV4_MAPPED
    | IS_IPV6
    | IS_USED_LOCK
    | LAG
    | LAST_INSERT_ID
    | LAST_VALUE
    | LCASE
    | LEAD
    | LEAST
    | LEFT
    | LENGTH
    | LINEFROMTEXT
    | LINEFROMWKB
    | LINESTRING
    | LINESTRINGFROMTEXT
    | LINESTRINGFROMWKB
    | LN
    | LOAD_FILE
    | LOCATE
    | LOG
    | LOG10
    | LOG2
    | LOWER
    | LPAD
    | LTRIM
    | MAKEDATE
    | MAKETIME
    | MAKE_SET
    | MASTER_POS_WAIT
    | MBRCONTAINS
    | MBRDISJOINT
    | MBREQUAL
    | MBRINTERSECTS
    | MBROVERLAPS
    | MBRTOUCHES
    | MBRWITHIN
    | MD5
    | MICROSECOND
    | MINUTE
    | MLINEFROMTEXT
    | MLINEFROMWKB
    | MOD
    | MONTH
    | MONTHNAME
    | MPOINTFROMTEXT
    | MPOINTFROMWKB
    | MPOLYFROMTEXT
    | MPOLYFROMWKB
    | MULTILINESTRING
    | MULTILINESTRINGFROMTEXT
    | MULTILINESTRINGFROMWKB
    | MULTIPOINT
    | MULTIPOINTFROMTEXT
    | MULTIPOINTFROMWKB
    | MULTIPOLYGON
    | MULTIPOLYGONFROMTEXT
    | MULTIPOLYGONFROMWKB
    | NAME_CONST
    | NTH_VALUE
    | NTILE
    | NULLIF
    | NUMGEOMETRIES
    | NUMINTERIORRINGS
    | NUMPOINTS
    | OCT
    | OCTET_LENGTH
    | ORD
    | OVERLAPS
    | PERCENT_RANK
    | PERIOD_ADD
    | PERIOD_DIFF
    | PI
    | POINT
    | POINTFROMTEXT
    | POINTFROMWKB
    | POINTN
    | POLYFROMTEXT
    | POLYFROMWKB
    | POLYGON
    | POLYGONFROMTEXT
    | POLYGONFROMWKB
    | POSITION
    | POW
    | POWER
    | QUARTER
    | QUOTE
    | RADIANS
    | RAND
    | RANDOM
    | RANK
    | RANDOM_BYTES
    | RELEASE_LOCK
    | REVERSE
    | RIGHT
    | ROUND
    | ROW_COUNT
    | ROW_NUMBER
    | RPAD
    | RTRIM
    | SCHEMA
    | SECOND
    | SEC_TO_TIME
    | SESSION_USER
    | SESSION_VARIABLES_ADMIN
    | SHA
    | SHA1
    | SHA2
    | SIGN
    | SIN
    | SLEEP
    | SOUNDEX
    | SQL_THREAD_WAIT_AFTER_GTIDS
    | SQRT
    | SRID
    | STARTPOINT
    | STRCMP
    | STR_TO_DATE
    | ST_AREA
    | ST_ASBINARY
    | ST_ASTEXT
    | ST_ASWKB
    | ST_ASWKT
    | ST_BUFFER
    | ST_CENTROID
    | ST_CONTAINS
    | ST_CROSSES
    | ST_DIFFERENCE
    | ST_DIMENSION
    | ST_DISJOINT
    | ST_DISTANCE
    | ST_ENDPOINT
    | ST_ENVELOPE
    | ST_EQUALS
    | ST_EXTERIORRING
    | ST_GEOMCOLLFROMTEXT
    | ST_GEOMCOLLFROMTXT
    | ST_GEOMCOLLFROMWKB
    | ST_GEOMETRYCOLLECTIONFROMTEXT
    | ST_GEOMETRYCOLLECTIONFROMWKB
    | ST_GEOMETRYFROMTEXT
    | ST_GEOMETRYFROMWKB
    | ST_GEOMETRYN
    | ST_GEOMETRYTYPE
    | ST_GEOMFROMTEXT
    | ST_GEOMFROMWKB
    | ST_INTERIORRINGN
    | ST_INTERSECTION
    | ST_INTERSECTS
    | ST_ISCLOSED
    | ST_ISEMPTY
    | ST_ISSIMPLE
    | ST_LINEFROMTEXT
    | ST_LINEFROMWKB
    | ST_LINESTRINGFROMTEXT
    | ST_LINESTRINGFROMWKB
    | ST_NUMGEOMETRIES
    | ST_NUMINTERIORRING
    | ST_NUMINTERIORRINGS
    | ST_NUMPOINTS
    | ST_OVERLAPS
    | ST_POINTFROMTEXT
    | ST_POINTFROMWKB
    | ST_POINTN
    | ST_POLYFROMTEXT
    | ST_POLYFROMWKB
    | ST_POLYGONFROMTEXT
    | ST_POLYGONFROMWKB
    | ST_SRID
    | ST_STARTPOINT
    | ST_SYMDIFFERENCE
    | ST_TOUCHES
    | ST_UNION
    | ST_WITHIN
    | ST_X
    | ST_Y
    | STRING_TO_VECTOR
    | SUBDATE
    | SUBSTRING_INDEX
    | SUBTIME
    | SYSTEM_USER
    | TAN
    | TIME
    | TIMEDIFF
    | TIMESTAMP
    | TIMESTAMPADD
    | TIMESTAMPDIFF
    | TIME_FORMAT
    | TIME_TO_SEC
    | TOUCHES
    | TO_BASE64
    | TO_DAYS
    | TO_SECONDS
    | UCASE
    | UNCOMPRESS
    | UNCOMPRESSED_LENGTH
    | UNHEX
    | UNIX_TIMESTAMP
    | UPDATEXML
    | UPPER
    | UUID
    | UUID_SHORT
    | VALIDATE_PASSWORD_STRENGTH
    | VECTOR_DIM
    | VECTOR_TO_STRING
    | VERSION
    | VISIBLE
    | WAIT_UNTIL_SQL_THREAD_AFTER_GTIDS
    | WEEK
    | WEEKDAY
    | WEEKOFYEAR
    | WEIGHT_STRING
    | WITHIN
    | YEAR
    | YEARWEEK
    | Y_FUNCTION
    | X_FUNCTION
    | JSON_ARRAY
    | JSON_OBJECT
    | JSON_QUOTE
    | JSON_CONTAINS
    | JSON_CONTAINS_PATH
    | JSON_EXTRACT
    | JSON_KEYS
    | JSON_OVERLAPS
    | JSON_SEARCH
    | JSON_VALUE
    | JSON_ARRAY_APPEND
    | JSON_ARRAY_INSERT
    | JSON_INSERT
    | JSON_MERGE
    | JSON_MERGE_PATCH
    | JSON_MERGE_PRESERVE
    | JSON_REMOVE
    | JSON_REPLACE
    | JSON_SET
    | JSON_UNQUOTE
    | JSON_DEPTH
    | JSON_LENGTH
    | JSON_TYPE
    | JSON_VALID
    | JSON_TABLE
    | JSON_SCHEMA_VALID
    | JSON_SCHEMA_VALIDATION_REPORT
    | JSON_PRETTY
    | JSON_STORAGE_FREE
    | JSON_STORAGE_SIZE
    | JSON_ARRAYAGG
    | JSON_OBJECTAGG
    | STATEMENT
    ;
