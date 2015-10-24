CREATE TABLE IF NOT EXISTS SystemContext  (
	id BIGINT IDENTITY NOT NULL PRIMARY KEY ,
	SystemContextId BIGINT ,
	version BIGINT DEFAULT 0,
	name VARCHAR(100) NOT NULL,
	notes VARCHAR(200) ,
	diagram BLOB ,
	createdby VARCHAR(100) NOT NULL ,
	createdtime TIMESTAMP DEFAULT NOW()
)   ;

--DROP sequence seq_SystemContext if exists;
CREATE SEQUENCE IF NOT EXISTS seq_SystemContext;
