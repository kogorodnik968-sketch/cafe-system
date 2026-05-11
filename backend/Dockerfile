FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests -Dmaven.test.skip=true -Dcheckstyle.skip=true
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/cafe_app-0.0.1-SNAPSHOT.jar"]