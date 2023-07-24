FROM adoptopenjdk/openjdk11:latest
MAINTAINER br.com.votacao
RUN mkdir /app
COPY build/libs/votacao-0.0.1-SNAPSHOT.jar /app
WORKDIR /app
ENTRYPOINT exec java -jar votacao-0.0.1-SNAPSHOT.jar