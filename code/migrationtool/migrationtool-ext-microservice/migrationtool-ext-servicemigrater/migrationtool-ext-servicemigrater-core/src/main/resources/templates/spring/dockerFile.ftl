FROM openjdk:8-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${r"${JAR_FILE}"} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE <#if port?has_content>${port}<#else>8080</#if>