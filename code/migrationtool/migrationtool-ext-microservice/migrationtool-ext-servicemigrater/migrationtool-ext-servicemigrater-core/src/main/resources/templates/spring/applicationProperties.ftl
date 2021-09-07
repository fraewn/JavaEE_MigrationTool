# serivce name
spring.application.name=<#if applicationName?has_content>${applicationName}<#else>service</#if>
# own service port
server.port=<#if serverPort?has_content>${serverPort}<#else>8080</#if>
# eureka server url
eureka.client.serviceUrl.defaultZone=<#if eurekaServiceUrl?has_content>${eurekaServiceUrl}<#else>http://registry:8761/eureka</#if>
# Database connection
SPRING.DATASOURCE.DRIVERCLASSNAME: <#if dataSourceDriver?has_content>${dataSourceDriver}<#else>org.postgresql.Driver</#if>
SPRING.DATASOURCE.URL: <#if dataSourceUrl?has_content>${dataSourceUrl}<#else>jdbc:postgresql://localhost:5432/</#if>
SPRING.DATASOURCE.USER: <#if dataSourceUser?has_content>${dataSourceUser}<#else>postgres</#if>
SPRING.DATASOURCE.USERNAME: <#if dataSourceUserName?has_content>${dataSourceUserName}<#else>postgres</#if>
SPRING.DATASOURCE.PASSWORD: <#if dataSourcePassword?has_content>${dataSourcePassword}<#else>admin</#if>  
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
