# hs-server

## How to deploy application

Set environment variables. As an example:

```bash
export HS_SERVER_DATASOURCE_DATABASE=bluster
export HS_SERVER_DATASOURCE_PORT=5432
export HS_SERVER_DATASOURCE_USERNAME=postgres
export HS_SERVER_DATASOURCE_PASSWORD=54321
export HS_SERVER_PORT=8097
export HS_SERVER_JWT_SECRET_KEY=404E6372B4B6250645367566B5970
export HS_SERVER_JWT_EXPIRATION=86400000
export HS_SERVER_JWT_REFRESH_EXPIRATION=604800000
```

* `USERNAME`, `PASSWORD` should match with `docker-compose.yml`

Start `docker-compose.yml`:

```bash
docker compose up [-d]
```

> Connection to running container could be done with `docker exec -it <container_id> bash`

Start an application with envvars set:

```bash
java -jar build/libs/hs-server-0.0.1-SNAPSHOT.jar

# or

./gradleW bootRun
```

