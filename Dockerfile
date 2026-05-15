FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/*.jar ms-pedidos.jar

EXPOSE 7082

ENTRYPOINT ["java","-jar","ms-pedidos.jar"]