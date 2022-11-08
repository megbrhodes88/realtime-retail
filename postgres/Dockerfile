FROM debezium/example-postgres:2.0

ENV POSTGRES_DB=retail
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=rt-inv-c0nflu3nt!

RUN mkdir /data
COPY data/* /data/

RUN rm /docker-entrypoint-initdb.d/inventory.sql
COPY setup.sql /docker-entrypoint-initdb.d/
ADD setup.sql /docker-entrypoint-initdb.d
RUN chmod a+r /docker-entrypoint-initdb.d/*

EXPOSE 5432