<h1 align="center">
    <img src="http://www.jfoenix.com/img/logo-JFX.png">
</h1>
<p align="center">
<sup>
<b>The Migrationtool is an Java library, that implements different possibilites to transform a monolith</b>
</sup>
</p>

The Migrationtool suggests a structured way to analyze and interpret the structure of a monolith. Different processes within a pipeline are supported, which can fundamentally change the monolith. A listing of all supported transformation is given in the Overview section.

This readme focuses on installation and development aspects. A comprehensive documentation of the configuration can be found in the wiki of this repository.

The Migrationtool is based on the master tesis by Franziska Küsters and the master thesis by René Odenell.

# Overview

The Migrationtool consists in its current state the following components:

* **Microservices** <br/> A process for automated system analysis of a monolith followed by a transition to a microservice architecture. This new architecture is only a suggestion and not a complete functional migration.
* **MEAN-Stack** <br/> TODO (currently under development)

# Build and Run

All source code is released under the terms of the Apache 2.0 license.

> :warning: **This project is still under development.** 

Prerequisite: JDK 9+ and Maven is installed and added to the path.

You can get your own deployment of the Migrationtool by running the Maven command:

```
clean install
```
It is important to add the correct profile based on the planned useage:

* **microservice-build** <=> Microservice process
* **nodejs-build** <=> MEAN-Stack process

Once this process has been performed, a resulting zip file is obtained in one of the ''delpoyable'' modules. This file enumerates all components and represents the deployment of the migration tool.

Then run in a command prompt / shell:
```
java -cp "migrationtool.jar;plugins/*" core.Main [optional arguments]
```
After a short time the execution of the programm should be started.
