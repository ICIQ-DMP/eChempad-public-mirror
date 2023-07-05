# Containerization
We can run eChempad using containers. This is more comfortable than setting up the working environment in your own 
machine.  
- Dependencies are already inside the container.
- Containers are multi-platform.
- Deploy is just a command: `sudo docker-compose up -d`. 

## Container structure
We will containerize the application using two docker images: 
* The image for the eChempad, that we will build from source.
* The image for postgreSQL, that we find on the Internet (Docker Hub).

The image for the eChempad container is created using a multi-stage build. This means that the compilation of the 
project occurs in a container and the execution of the application occurs in another container, separating the concerns
of each stage and reducing the size of the final container image. 

### Steps to create and run the eChempad container image from scratch
These are the steps needed to create the repository for the container image and run it, from scratch. 
* The first image can be built by being in the root of the eChempad repository and using the `Dockerfile` with `sudo 
docker build -t aleixmt/echempad:v2 .` where `v2` is the tag that we are writing for this particular version of the 
image. A tag in Docker Hub can be understood as a branch in a git repository.
* Create docker repository for this image in DockerHub.
* Login to Docker Hub: `docker login -u YOUR-USER-NAME`
* (optional) After we have built it we may need to change the tag in order to contain our username and repository target
`docker tag echempad aleixmt/echempad`.
* Finally, upload the image to docker hub `docker push aleixmt/echempad:v2`.
* With these steps you already can use the `docker-compose.yml` file to raise the whole application without worrying 
  about dependencies. To use it open a terminal in the folder where the `docker-compose.yml` file is located and issue
  the command `docker-compose up -d`.

### Steps to update container image
These are the steps to update the container image in the repository:
* Build image locally: `sudo docker build -t aleixmt/echempad:v2 .`.
* Upload image to repository (you have to be already logged in): `sudo docker push aleixmt/echempad:v2`.
* Stop old containers: `sudo docker-compose down`.
* Pull image from Docker Hub and rerun: `sudo docker-compose up -d`.

One liner:
```bash
sudo docker build -t aleixmt/echempad:v1.3 . &&  sudo docker push aleixmt/echempad:v1.3 && sudo docker-compose down && sudo docker-compose up -d
```

###### Building java application for deployment 
It is important to understand how compilation and run phase are interrelated but at the same time should be independent
of each other. Also, it is important to understand the existence of the spring boot profiles, which implies the loading
of different data from the `.properties` files present in the project. 

Specifically, the `container` profile is active when the environment variable `spring_profiles_active` is equal to the 
value `container`. This profile overwrites the property that sets the network location of the database. Specifically, 
configures the location of the database taking into account that the application is running in a container, and as such
the network location of the database is different.  

So, the profile for this matter is a runtime dependency. To compile the project you can use:
```
mvn clean package spring-boot:repackage
```

This will need to be refined in the future because the tests are also run with this command, but obviously are not 
needed, but right now it works (with no tests). The product of this command is a `.war` file, than can be run with 
`java` with:

```shell
java -jar eChempad-1.0.war
```
