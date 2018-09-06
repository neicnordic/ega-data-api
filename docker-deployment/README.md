# Docker deployment for EGA-DATA-API

## Usage

1. In the parent folder, build the images by

		mvn clean install

		docker build -t ega-data-api/db:v1 db

2. In this folder, start the containers by 

		docker-compose up -d

## Stop the containers 

		docker-compose down

##TODO:

1. configurations for the database and dataedge

2. examples for testing

