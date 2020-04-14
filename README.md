# Deploy

Running PRD: https://career-java-api.herokuapp.com/

Running Branch: https://career-java-api-branch.herokuapp.com/

# Valid Commands

## Artifact Version
`mvn -Dexec.executable='echo' -Dexec.args='${project.version}' --non-recursive exec:exec -q`

```bash
mvn clean package && \
docker build -t allanweber/career-app . && \
docker tag allanweber/career-app allanweber/career-app:latest
```

## PRD

docker run -p 8080:8080 --name career -e MONGO_USER=<user> -e MONGO_PASS=<pass> allanweber/career-app:latest

## DEV

docker run -p 8080:8080 --name career -e SPRING_PROFILES_ACTIVE=dev allanweber/career-app:latest

# Stop and remove

docker stop career && docker rm career && docker rmi allanweber/career-app -f

# Docker compose

docker-compose up --build

# Old docker configurations
ARG ENV_ARG=default
ARG MONGO_USER_ARG=a
ARG MONGO_PASS_ARG=1
ENV ENV_PROFILE $ENV_ARG
ENV MONGO_USER $MONGO_USER_ARG
ENV MONGO_PASS $MONGO_PASS_ARG

# Docker Image alternatives

## Commands

docker build --build-arg ENV_ARG=dev -t allanweber/career-app .

docker build --build-arg ENV_ARG=prod -t allanweber/career-app .

docker build -t allanweber/career-app .

docker tag allanweber/career-app allanweber/career-app:latest

docker run -p 8080:8080 -d --name career allanweber/career-app

docker run -p 8080:8080 --name career -e MONGO_USER=<user> -e MONGO_PASS=<pass> allanweber/career-app

docker run -p 8080:8080 --name career allanweber/career-app

docker stop career && docker rm career && docker rmi allanweber/career-app

-e "SPRING_PROFILES_ACTIVE=dev"
