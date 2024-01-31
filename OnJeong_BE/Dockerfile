FROM openjdk:17
ARG JAR_FILE=build/libs/onjeong.jar
COPY ${JAR_FILE} /app/onjeong.jar
ENTRYPOINT ["java","-jar","onjeong.jar"]