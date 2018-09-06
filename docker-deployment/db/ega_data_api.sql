CREATE SCHEMA archive;

CREATE TABLE archive.file_index_file (
	file_id varchar(128) NULL,
	index_file_id varchar(128) NULL
)
WITH (
	OIDS=FALSE
);


CREATE SEQUENCE archive.event_event_id_seq;
CREATE TABLE archive.event (
	event_id int8 NOT NULL DEFAULT nextval('archive.event_event_id_seq'::regclass),
	client_ip varchar(45) NOT NULL,
	event varchar(256) NOT NULL,
	download_ticket varchar(256) NOT NULL,
	event_type varchar(256) NOT NULL,
	email varchar(256) NOT NULL,
	created timestamp NOT NULL DEFAULT now(),
	CONSTRAINT event_pkey PRIMARY KEY (event_id)
)
WITH (
	OIDS=FALSE
);

CREATE TABLE archive.file_dataset (
	dataset_id varchar(128) NULL,
	file_id varchar(128) NULL
)
WITH (
	OIDS=FALSE
);
CREATE UNIQUE INDEX dataset_id_idx ON archive.file_dataset (dataset_id, file_id);
CREATE INDEX file_id_idx ON archive.file_dataset (file_id);


CREATE TABLE archive.file (
	file_id  varchar(128) NULL,
	file_name varchar(256) NULL,
	file_size int8 NULL,
        checksum varchar(128) NULL,
        checksum_type varchar(12) NULL,
	file_status varchar(13) NULL
)
WITH (
	OIDS=FALSE
);


CREATE SEQUENCE archive.download_log_new_download_log_id_seq ; 
CREATE TABLE archive.download_log (
	download_log_id int8 NOT NULL DEFAULT nextval('archive.download_log_new_download_log_id_seq'::regclass),
	client_ip varchar(45) NOT NULL,
	server varchar(45) NOT NULL,
	email varchar(256) NOT NULL,
	file_id varchar(15) NOT NULL,
	download_speed float8 NOT NULL DEFAULT '-1'::integer,
	download_status varchar(256) NOT NULL DEFAULT 'success'::character varying,
	download_protocol varchar(256) NOT NULL,
	encryption_type varchar(256) NOT NULL,
	start_coordinate int8 NOT NULL DEFAULT 0,
	end_coordinate int8 NOT NULL DEFAULT 0,
	bytes int8 NOT NULL DEFAULT 0,
	created timestamp NOT NULL DEFAULT now()
)
WITH (
	OIDS=FALSE
);

/*?? where is download_ticket_status_cv defined*/
CREATE TABLE archive.download_ticket_status_cv (
	ticket_status varchar(28) NOT NULL,
	CONSTRAINT download_ticket_status_cv_pkey PRIMARY KEY (ticket_status)
)
WITH (
	OIDS=FALSE
);

insert into archive.download_ticket_status_cv (ticket_status) values ('ready');


CREATE SEQUENCE archive.download_ticket_download_ticket_id_seq ; 
/*where is download_ticket defined? */
CREATE TABLE archive.download_ticket (
	download_ticket_id int8 NOT NULL DEFAULT nextval('archive.download_ticket_download_ticket_id_seq'::regclass),
	email varchar(256) NOT NULL,
	download_ticket varchar(256) NOT NULL,
	client_ip varchar(45) NOT NULL,
	file_id varchar(15) NOT NULL,
	encryption_key text NOT NULL,
	encryption_type varchar(28) NOT NULL,
	ticket_status varchar(28) NOT NULL,
	label varchar(256) NOT NULL,
	created timestamp NOT NULL DEFAULT now(),
	start_coordinate int8 NOT NULL DEFAULT 0,
	end_coordinate int8 NOT NULL DEFAULT 0,
	CONSTRAINT download_ticket_pkey PRIMARY KEY (download_ticket_id),
	CONSTRAINT download_ticket_ticket_status_fkey FOREIGN KEY (ticket_status) REFERENCES archive.download_ticket_status_cv(ticket_status)
)
WITH (
	OIDS=FALSE
);

INSERT INTO archive.file VALUES ('EGAF01', '/data/EGAF01.txt.cip', 678, 'x', 'md5', 'available');
INSERT INTO archive.file_dataset VALUES ('EGAD01', 'EGAF01');
