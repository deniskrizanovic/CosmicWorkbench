CREATE TABLE IF NOT EXISTS FUNCTIONALPROCESS  (
	id BIGINT IDENTITY NOT NULL PRIMARY KEY ,
	FUNCTIONALPROCESSID BIGINT  ,
	SYSTEMCONTEXTID BIGINT  ,
	VERSION BIGINT default 0,
	NAME VARCHAR(100) NOT NULL,
	NOTES VARCHAR(200) ,
	createdby VARCHAR(100) NOT NULL ,
	createdtime TIMESTAMP DEFAULT NOW()
)   ;


--DROP sequence seq_FunctionalProcess if exists;
CREATE SEQUENCE IF NOT EXISTS seq_FunctionalProcess;
