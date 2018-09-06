SHELL := /bin/bash
TOKEN=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI0NTgxMSIsImF6cCI6ImJhM2UyNDA4LTllZGUtNDcwZC04MDM4LTRlNjU1ZDYwZjQ2MyIsImlzcyI6Imh0dHA6Ly9kYXRhLmVwb3V0YS5jc2MuZmkiLCJleHAiOjE1Mjc4MTEyMDAsImlhdCI6MTUxMjEzMDE2NywianRpIjoiOWM5ODM1MzAtZGQ3Ni00ZjY1LTgzODYtZTdlNWM5NmU5YTYzIiwiYXV0aG9yaXRpZXMiOlsiRUdBRDAxIl19.iYBX-YXR7L6VQlm8nOcm_GYsVJHkig1WPJoIFWg6CJF0T-2GwoLhKnUM8FTp-KGWdfSLkYfyvxMSV168vocVdv__PLRBhvwvmv_KffKU8cBu-PPJJGdRj4xR2RQUl553xpG_uXqjFYeUxUJUJOrMwqn8pDkKRpycm40Kx5NfrtgaKHnT5i2rI3IFWkFcQXg5semX3eQwAelQ3DIDPe0ZFqQMJ6ah7GG2hweqkyK3tJowCm-s4TQYxsNsUD8jDfjPQEdqBe4PLKMqFHmudpZHARlKdP63cStabZFNw0OTyPZUK5zSnOAZulwuuBWhec5IYQHXy79pEvr4IOshKdH7oA
DB_PASSWORD := $(shell bash -c 'echo $$RANDOM')

bootstrap:
	# Build images 
	mvn install -DskipTests
	docker build -t ega-data-api/db:v1 db  

	# Prepare configuration
	sed -i.bak  "s/^\(POSTGRES_PASSWORD\s*=\s*\).*\$$/\1a$(DB_PASSWORD)/" db.env
	sed -i.bak  "s/^\(spring.datasource.password\s*=\s*\).*\$$/\1a$(DB_PASSWORD)/" configuration/filedatabase-docker.properties 
		
	# Start containers 
	docker-compose up -d 
	
	@echo "\n\n==========================================="
	@echo "Well done! Everything is ready for you!"
	@echo "Try it yourself:"
	@echo "export TOKEN="$(TOKEN)
	@echo "export STABLE_ID=<stable_id of the ingested file, see README for detailes>"
	@echo "curl -H \"Authorization: Bearer \$$TOKEN\" http://localhost:8051/elixir/data/files/\$$STABLE_ID"
	
clean:
	mvn clean
