FROM openjdk:17
WORKDIR /app
ARG JAR_FILE=build/libs/onjeong-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} onjeong.jar
ENTRYPOINT ["java","-jar","onjeong.jar"]