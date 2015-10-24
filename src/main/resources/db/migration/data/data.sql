/*INSERT INTO SYSTEMCONTEXT (VERSION,NAME,NOTES) VALUES (1,'SYSTEM A','NOTES A');
INSERT INTO SYSTEMCONTEXT (VERSION,NAME,NOTES) VALUES (1,'SYSTEM B','NOTES B');
INSERT INTO SYSTEMCONTEXT (VERSION,NAME,NOTES) VALUES (1,'SYSTEM C','NOTES C');

insert into datagroup (name, version, notes, systemcontextid) select 'data 1',1,'data 1 note', systemcontextid from systemcontext where name = 'SYSTEM A';
insert into datagroup (name, version, notes, systemcontextid) select 'data 2',1,'data 2', systemcontextid from systemcontext where name = 'SYSTEM A';
insert into functionalprocess (name, version, notes, systemcontextid) select 'data 1',1,'data 1 note', systemcontextid from systemcontext where name = 'SYSTEM A';
insert into functionalprocess (name, version, notes, systemcontextid) select 'data 2',1,'data 2 note', systemcontextid from systemcontext where name = 'SYSTEM A';

insert into datafield (name, datagroupid ) select 'test', datagroupid from datagroup where name = 'data 1';
insert into datafield (name, datagroupid ) select 'test1', datagroupid from datagroup where name = 'data 1';
insert into datafield (name, datagroupid ) select 'test2', datagroupid from datagroup where name = 'data 2';
insert into functionalsubprocess (name, functionalprocessid ) select 'test', functionalprocessid from functionalprocess where name = 'data 1';
insert into functionalsubprocess (name, functionalprocessid ) select 'test as', functionalprocessid from functionalprocess where name = 'data 1';
insert into functionalsubprocess (name, functionalprocessid ) select 'test1', functionalprocessid from functionalprocess where name = 'data 2';
insert into functionalsubprocess (name, functionalprocessid ) select 'test', functionalprocessid from functionalprocess where name = 'data 2';    */

