FROM postgres:latest

COPY ./src/main/resources/SQL/schema-postgresql.sql /docker-entrypoint-initdb.d/init.sql