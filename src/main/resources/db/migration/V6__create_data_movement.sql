CREATE TABLE IF NOT EXISTS DataMovement  (
	id BIGINT IDENTITY NOT NULL PRIMARY KEY ,
	SizingContextId BIGINT  ,
	SubProcessId BIGINT  ,
	DataGroupId BIGINT  ,
	DataFieldId BIGINT  ,
  	Type VARCHAR(100),
	version BIGINT default 0,
	CreatedBy VARCHAR(100) NOT NULL ,
	CreatedTime TIMESTAMP DEFAULT NOW()
)   ;
