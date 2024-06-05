FROM docker.io/eclipse-temurin:17.0.7_7-jre

WORKDIR /app
COPY target/SpringProject-0.0.1-SNAPSHOT.jar /app/SpringProject-0.0.1-SNAPSHOT.jar
EXPOSE 8080/tcp
CMD ["java", "-XX:+UseG1GC", "-jar", "SpringProject-0.0.1-SNAPSHOT.jar"]