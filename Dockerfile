FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/fyn-0.0.1-SNAPSHOT.jar fynApplication-v1.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "fynApplication-v1.jar"]