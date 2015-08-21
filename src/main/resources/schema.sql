--DROP TABLE FUNCTIONALMODELDATAFIELD IF EXISTS  ;
--DROP TABLE FUNCTIONALMODEL IF EXISTS  ;
--DROP TABLE DATAFIELD IF EXISTS  ;
--DROP TABLE DATAGROUP IF EXISTS  ;
--DROP TABLE FUNCTIONALPROCESS IF EXISTS  ;
--DROP TABLE FUNCTIONALSUBPROCESS IF EXISTS  ;
--
--DROP TABLE SYSTEMCONTEXT IF EXISTS  ;
DROP trigger tr_system_context_versions IF EXISTS;
DROP sequence seq_SystemContext if exists;
--
CREATE TABLE IF NOT EXISTS SystemContext  (
	id BIGINT IDENTITY NOT NULL PRIMARY KEY ,
	SystemContextId BIGINT ,
	version BIGINT ,
	name VARCHAR(100) NOT NULL,
	notes VARCHAR(200) ,
	diagram BLOB ,
	userid VARCHAR(100) NOT NULL ,
	createdtime TIMESTAMP DEFAULT NOW(),
	deleteflag BOOLEAN DEFAULT FALSE
)   ;

--CREATE TABLE DataGroup  (
--	DATAGROUPID BIGINT IDENTITY NOT NULL PRIMARY KEY ,
--	SYSTEMCONTEXTID BIGINT REFERENCES SYSTEMCONTEXT(SYSTEMCONTEXTID) ,
--	VERSION BIGINT ,
--	NAME VARCHAR(100) NOT NULL,
--	NOTES VARCHAR(200) ,
--	USERID VARCHAR(100) NOT NULL ,
--	CREATETIME TIMESTAMP DEFAULT NOW(),
--	DELETEFLAG BOOLEAN DEFAULT FALSE
--)   ;
--
--CREATE TABLE DATAFIELD  (
--	DATAFIELDID BIGINT IDENTITY NOT NULL PRIMARY KEY ,
--	DATAGROUPID BIGINT REFERENCES DATAGROUP(DATAGROUPID) ,
--	VERSION BIGINT ,
--	NAME VARCHAR(100) NOT NULL,
--	USERID VARCHAR(100) NOT NULL ,
--	CREATETIME TIMESTAMP DEFAULT NOW(),
--	DELETEFLAG BOOLEAN  DEFAULT FALSE
--)   ;
--
--CREATE TABLE FUNCTIONALPROCESS  (
--	FUNCTIONALPROCESSID BIGINT IDENTITY NOT NULL PRIMARY KEY ,
--	SYSTEMCONTEXTID BIGINT REFERENCES SYSTEMCONTEXT(SYSTEMCONTEXTID) ,
--	VERSION BIGINT ,
--	NAME VARCHAR(100) NOT NULL,
--	NOTES VARCHAR(200) ,
--	USERID VARCHAR(100) NOT NULL ,
--	CREATETIME TIMESTAMP DEFAULT NOW(),
--	DELETEFLAG BOOLEAN DEFAULT FALSE
--)   ;
--
--CREATE TABLE FUNCTIONALSUBPROCESS  (
--	FUNCTIONALSUBPROCESSID BIGINT IDENTITY NOT NULL PRIMARY KEY ,
--	FUNCTIONALPROCESSID BIGINT REFERENCES FUNCTIONALPROCESS(FUNCTIONALPROCESSID) ,
--	VERSION BIGINT ,
--	NAME VARCHAR(100) NOT NULL ,
--	USERID VARCHAR(100) NOT NULL ,
--	CREATETIME TIMESTAMP DEFAULT NOW(),
--	DELETEFLAG BOOLEAN  DEFAULT FALSE
--)   ;
--
--CREATE TABLE FUNCTIONALMODEL  (
--	FUNCTIONALMODELID BIGINT IDENTITY NOT NULL PRIMARY KEY ,
--	SYSTEMCONTEXTID BIGINT REFERENCES SYSTEMCONTEXT(SYSTEMCONTEXTID) ,
--	FUNCTIONALPROCESSID BIGINT REFERENCES FUNCTIONALPROCESS(FUNCTIONALPROCESSID) ,
--	FUNCTIONALSUBPROCESSID BIGINT REFERENCES FUNCTIONALSUBPROCESS(FUNCTIONALSUBPROCESSID) ,
--	DATAGROUPID BIGINT REFERENCES DATAGROUP(DATAGROUPID) ,
--	VERSION BIGINT ,
--	GRADE VARCHAR(10) ,
--	NOTES VARCHAR(200) ,
--	SCORE BIGINT ,
--	USERID VARCHAR(100) NOT NULL ,
--	CREATETIME TIMESTAMP DEFAULT NOW(),
--	DELETEFLAG BOOLEAN DEFAULT FALSE
--)   ;
--
--CREATE TABLE FUNCTIONALMODELDATAFIELD  (
--	FUNCTIONALMODELDATAFIELDID BIGINT IDENTITY NOT NULL PRIMARY KEY ,
--	FUNCTIONALMODELID BIGINT REFERENCES FUNCTIONALMODEL(FUNCTIONALMODELID) ,
--	DATAGROUPID BIGINT REFERENCES DATAGROUP(DATAGROUPID) ,
--	DATAFIELDID BIGINT REFERENCES DATAFIELD(DATAFIELDID) ,
--	VERSION BIGINT ,
--	USERID VARCHAR(100) NOT NULL ,
--	CREATETIME TIMESTAMP DEFAULT NOW(),
--	DELETEFLAG BOOLEAN DEFAULT FALSE
--)   ;
--
--


-- these are all commented out because

select * from dual;

CREATE TRIGGER IF NOT EXISTS tr_system_context_versions Before INSERT ON SYSTEMCONTEXT FOR EACH ROW CALL "com.fp.dao.SystemContextTrigger";

CREATE SEQUENCE IF NOT EXISTS seq_SystemContext
