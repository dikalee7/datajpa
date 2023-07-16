# Docker study

> Postgresql DB 도커 생성 및 데이터 유지되도록 설정
- docker compose 및 volume 이용
- docker-compose.yml 작성
  
```
version: "1"
services:
  db:
    image: postgres:latest
    container_name: myone-postgres
    restart: always
    ports:
      - "5432:5432"
    environment:
      POSTGRES_USER: "postgres"
      POSTGRES_PASSWORD: "club2046!"
    volumes:
      - db_data:/var/lib/postgresql/data
volumes:
  db_data:
```

> Postgresql Database with user

```
CREATE DATABASE {dbname} 
WITH 
   ENCODING = 'UTF8'
   OWNER = {username}
   CONNECTION LIMIT = 100;
```
