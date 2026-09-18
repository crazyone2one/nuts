#FROM mcr.microsoft.com/openjdk/jdk:25-ubuntu
FROM eclipse-temurin:25-jdk-alpine

MAINTAINER lijinglin <the2ndindec@gmail.com>

ENV TZ=Asia/Shanghai

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo "Asia/Shanghai" > /etc/timezone

WORKDIR /app

VOLUME /app/files

# Add Spring Boot app.jar to Container
COPY backend-1.x.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS=""

# Fire up our Spring Boot app by default
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app.jar" ]