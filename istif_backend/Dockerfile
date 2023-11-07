FROM maven:3.8.4-openjdk-17-slim
COPY . /istif_backend
WORKDIR /istif_backend
RUN mvn clean install -DskipTests
EXPOSE 8080
CMD ["java", "-jar", "target/istif_backend-0.0.1.jar"]
