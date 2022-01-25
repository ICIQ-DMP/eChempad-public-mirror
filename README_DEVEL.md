# Installing development environment for eChempad

Steps to obtain a functional environment to develop and test eChempad in a Linux system.

#### First steps

The first thing that we got to do is clone the repository that contains the software "Linux-Auto-Customizer". This 
software consists in a set of scripts to automatically install dependencies, libraries and programs to a Linux 
Environment. It can be used in many distros, but in this guide we are supposing that our environment is Ubuntu Linux.

We can clone the repository anywhere, for example to our HOME folder:

```
cd ~
git clone https://github.com/AleixMT/Linux-Auto-Customizer
cd Linux-Auto-Customizer
bash install.sh -h
```

The previous commands will show the help of the software if everything is okay.

#### Resolving dependencies

In the repository execute the next orders:
```
sudo bash install.sh -v -o psql
bash install.sh -v -o jdk ideac ideau pgadmin postman
```

This will install:
  * JDK8: Java development kit. Contains the interpreter for the Java programming language. 
  * psql: PostGreSQL, SQL DataBase engine
  * IntelliJ IDEA Community / IntelliJ IDEA Ultimate: IDE with a high customization via plugins to work with Java. 
  * pgadmin: Graphical viewer of the PostGreSQL DataBase
  * postman: UI used to manage API calls and requests. Useful for testing and for keeping record of intersting API calls.
  
  Missing for now:
  * gitlab: GitLab platform, serving repositories locally from your computer. Used to integrate developing of the code with automatic building, deployment and testing 
  * gitlab runners: Integration with the GitLab platform to perform CD and CI.

This will set up the next things in the environment by writing to the `.bashrc` of the running user of the `install.sh` script.
#### Setting up database connection
Log in as the postgres user:
```
sudo su - postgres
```

Then create the user that the installation will use:
```
createuser --interactive --pwprompt
```
Notice that there are other ways of doing this. You can also do it directly by submitting orders to the database from 
this user, but in this case it is easier if you have this binary wrapper.

Then we need to create the database for our software:
```
createdb eChempad
```

#### Run software
Clone the repository, enter it and run it using the maven wrapper:
```
git clone https://github.com/USER/eChempad
cd eChempad
./mvnw spring-boot:run
```

#### Notes
To test the software you can visit using your browser
http://localhost:8080/api/researcher

A v1 UUID for testing: 1c9abdba-1f82-11ec-9621-0242ac130002


SQL statement to introduce values in our DB
INSERT INTO researcher(email, full_name, signalsAPIKey, id) VALUES ('patat', 'pat', 'yes638363ndsydeinumbers666', '1c9abdba-1f82-11ec-9621-0242ac130002');
