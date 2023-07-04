### Containerization
We can run eChempad using containers which makes it easier to deploy since the containers already have the dependencies 
and because of that containers are multi-platform. The alternative is installing eChempad directly on our machine.

#### Container structure
We will containerize the application using two docker images: 
* The image for the eChempad, that we build using `mvn clean compile`.
* The image for postgreSQL, that we find on the Internet. 

###### Steps to createthe eChempad container
* The first image can be built by being in the root of the eChempad repository and using the `Dockerfile` with `sudo 
docker build -t aleixmt/echempad:v1 .` where `v1` is the tag that we are writing for this particular version of the 
image.
* Create docker repository for this image in DockerHub.
* Login to Docker Hub: `docker login -u YOUR-USER-NAME`
* After we have built it we may need to change the tag in order to contain our username and repository target `docker 
tag echempad aleixmt/echempad`
* Finally, upload the image to docker hub `sudo docker push aleixmt/echempad:v1`

###### Steps to create container, upload it and deploy it on localhost along with db
```bash
sudo docker build -t aleixmt/echempad:v1 . &&  sudo docker push aleixmt/echempad:v1 && sudo docker-compose down && sudo docker-compose up -d
```

###### Build WAR file for deployment
```
mvn clean  package spring-boot:repackage
```