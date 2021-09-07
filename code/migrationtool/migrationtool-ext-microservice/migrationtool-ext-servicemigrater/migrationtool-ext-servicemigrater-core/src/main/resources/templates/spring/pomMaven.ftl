<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<groupId><#if groupId?has_content>${groupId}<#else>test</#if></groupId>
	<artifactId><#if artifactId?has_content>${artifactId}<#else>service</#if></artifactId>
	<version><#if version?has_content>${version}<#else>0.0.1-SNAPSHOT</#if></version>
	
	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<maven.compiler.target>1.8</maven.compiler.target>
		<maven.compiler.source>1.8</maven.compiler.source>
		
		<finalName>${r"${project.name}"}</finalName>
	
		<spring-boot-version>2.0.0.RELEASE</spring-boot-version>
		<spring-cloud.version>Finchley.RELEASE</spring-cloud.version>
	</properties>
	
	
	<repositories>
		<repository>
			<snapshots>
				<enabled>false</enabled>
			</snapshots>
			<id>spring-milestone</id>
			<name>Spring Milestone</name>
			<url>https://repo.spring.io/milestone</url>
		</repository>
		<repository>
			<snapshots>
				<enabled>true</enabled>
			</snapshots>
			<id>spring-snapshot</id>
			<name>Spring Snapshot</name>
			<url>https://repo.spring.io/snapshot</url>
		</repository>
		<repository>
			<snapshots>
				<enabled>false</enabled>
			</snapshots>
			<id>rabbit-milestone</id>
			<name>Rabbit Milestone</name>
			<url>https://dl.bintray.com/rabbitmq/maven-milestones</url>
		</repository>
	</repositories>
	
	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-dependencies</artifactId>
				<version>${r"${spring-boot-version}"}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>

			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>${r"${spring-cloud.version}"}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>

	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-tomcat</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
		</dependency>

		<dependency>
			<groupId>javax.xml.bind</groupId>
			<artifactId>jaxb-api</artifactId>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<executions>
					<execution>
						<id>repackage</id>
						<goals>
							<goal>repackage</goal>
						</goals>
					</execution>
				</executions>
			</plugin>

			<plugin>
				<groupId>org.codehaus.mojo</groupId>
				<artifactId>exec-maven-plugin</artifactId>
				<version>1.6.0</version>
				<executions>
					<!-- Create new docker image using Dockerfile which must be present 
						in current working directory. Tag the image using maven project version information. -->
					<execution>
						<id>docker-build</id>
						<phase>install</phase>
						<goals>
							<goal>exec</goal>
						</goals>
						<configuration>
							<executable>docker</executable>
							<workingDirectory>${r"${project.basedir}"}</workingDirectory>
							<arguments>
								<argument>build</argument>
								<argument>-t</argument>
								<argument>${r"${project.artifactId}"}:${r"${project.version}"}</argument>
								<argument>.</argument>
							</arguments>
						</configuration>
					</execution>

					<!-- Tag the image using maven project version information. -->
					<execution>
						<id>docker-tag</id>
						<phase>install</phase>
						<goals>
							<goal>exec</goal>
						</goals>
						<configuration>
							<executable>docker</executable>
							<workingDirectory>${r"${project.basedir}"}</workingDirectory>
							<arguments>
								<argument>tag</argument>
								<argument>${r"${project.artifactId}"}:${r"${project.version}"}</argument>
								<argument>${r"${project.artifactId}"}:latest</argument>
							</arguments>
						</configuration>
					</execution>

					<!-- Login and Push the image to a docker repo. -->
					<execution>
						<id>docker-login</id>
						<phase>deploy</phase>
						<goals>
							<goal>exec</goal>
						</goals>
						<configuration>
							<executable>docker</executable>
							<workingDirectory>${r"${project.basedir}"}</workingDirectory>
							<arguments>
								<argument>login</argument>
								<argument>-u</argument>
								<argument>${r"${docker.user}"}</argument>
								<argument>-p</argument>
								<argument>${r"${docker.password}"}</argument>
								<argument>${r"${docker.url}"}</argument>
							</arguments>
						</configuration>
					</execution>
					
					<execution>
						<id>docker-push</id>
						<phase>deploy</phase>
						<goals>
							<goal>exec</goal>
						</goals>
						<configuration>
							<executable>docker</executable>
							<workingDirectory>${r"${project.basedir}"}</workingDirectory>
							<arguments>
								<argument>push</argument>
								<argument>${r"${project.artifactId}"}:${r"${project.version}"}</argument>
							</arguments>
						</configuration>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>
</project>