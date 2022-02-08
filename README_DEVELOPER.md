# Installing development environment for eChempad

Steps to obtain a functional environment to develop and test eChempad in a Linux system.

## First steps

The first thing that we got to do is clone the repository that contains the software "Linux-Auto-Customizer". This 
software consists in a set of scripts to automatically install dependencies, libraries and programs to a Linux 
Environment. It can be used in many distros, but in this guide we suppose that our environment is Ubuntu Linux. It
may be the same or similar instructions in related distros.

We can clone the repository anywhere, for example in our HOME folder:

```bash
cd $HOME
git clone https://github.com/AleixMT/Linux-Auto-Customizer
cd Linux-Auto-Customizer
bash src/core/install.sh -h
```

The previous commands will show the help of the software if everything is okay.

## Resolving dependencies

In the repository execute the next orders:
```bash
sudo bash install.sh -v -o psql
bash install.sh -v -o jdk pgadmin postman ideau  # ideac 
```

This will install:
  * **JDK8:** Java development kit. Contains the interpreter for the Java programming language. 
  * **psql:** PostGreSQL, SQL DataBase engine
  * **IntelliJ IDEA Community / IntelliJ IDEA Ultimate:** IDE with a high customization via plugins to work with Java. 
    The  ultimate edition needs a license; The community version, which is commented out, has also all the required 
    features to work with the project. 
  * **pgadmin:** Graphical viewer of the PostGreSQL DataBase using a web browser.
  * **postman:** UI used to manage API calls and requests. Useful for testing and for keeping record of interesting API 
    calls. Has cloud synchronization, environments variables, workflows, etc.

This will set up the software with some new soft links and aliases, which will be populated in your environment by 
writing to the `.bashrc` of your HOME folder.

## Setting up database connection
Log in as the `postgres` user:
```bash
sudo su - postgres
```

Then create the user that the installation will use:
```bash
createuser --interactive --pwprompt
```
Notice that there are other ways of doing this. You can also do it directly by submitting orders to the database from 
this user, but in this case it is easier if you have this binary wrapper. It will ask for a password, consider this the
database password.

Then we need to create the database for our software:
```bash
createdb eChempad
```

## Compile software
#### Terminal
In a terminal, clone the repository, enter its root directory and run the maven wrapper with the `compile` option. By
using the installation before, all the needed
software and its dependencies and environmental variables are resolved, so they can be found by maven and our editor:
```bash
git clone https://github.com/AleixMT/eChempad
cd eChempad
./mvnw compile
```
This will download all the necessary libraries to execute the project.
This script has also
been modified to accept different configuration files, kill anything that is using the port that we are going to
deploy our app and is easy to modify the certificates that it uses.

#### IDE
You can also compile the software by using the IDE. To do so, after cloning the repository `cd` into it and call the 
`ideac` or `ideau`, depending on which we installed. I will use `ideau`.
```bash
git clone https://github.com/AleixMT/eChempad
cd eChempad
ideau
```
You should wait until IntelliJ IDEA has loaded your project. There may be some additional file indexing, but you can
start using the IDE to configure it.

After that you should be able to compile the software by clicking in the hammer in the upper right part of the IDE 
window. This is the easier step.

## Run software
#### Terminal
To compile using only a terminal, clone the repository, enter it and run it using the maven wrapper. By using the 
installation before, 
all the needed 
software and its dependencies and environmental variables are resolved, so they can be found by maven and our IDE:
```bash
#git clone https://github.com/AleixMT/eChempad  # Not needed if we already cloned it
cd eChempad
./mvnw spring-boot:run
```

#### IDE
After cloning the repository `cd` into it and call the `ideac` or `ideau`, depending on which we installed. I will use
`ideau`.
```bash
git clone https://github.com/AleixMT/eChempad
cd eChempad
ideau
```
You should wait until IntelliJ IDEA has loaded your project. There may be some additional file indexing, but you can 
start using the IDE to configure it. 

First thing is you got to go to the right upper part of the window in order to reach compile, run and debug menu. 
You should import the run configurations that are stored in the `.run/` folder of this project. You also may need
to add the Java interpreter that we installed. It will be located in the folder `$HOME/.customizer/java`. You can print
this direction by typing in a terminal:
```bash
echo $JAVA_HOME
```

To use the IDE to run the software there are two options:
###### Using the same maven wrapper
There are two ways of running the software from our IDE: 

First we can use the bash script called `mvnw`. This is the script used in the previous terminal section.
We can call the script as explained from inside the IDE.
We can configure a run configuration that executes the maven wrapper or import the one in the `.run/` folder by 
clicking *Add configuration...* in the upper right part of the IDE window. This will load a window where we can 
configure how this configuration works:
- *Script path*: `$HOME/eChempad/mvnw`
- *Working directory*: `$HOME/eChempad`
- *Script options*: `spring-boot:run`
- *Interpreter path*: `/bin/bash`
- *Execute in the terminal*: Yes

After that we only need to click to the green play button with this configuration selected to run the program. We will
see the output in the terminal at the bottom.

###### Using the autoconfiguration of the IDE
Go to the class `EChempadApplication` and in the line where we declare the class:
```java
public class EChempadApplication {...}
```

You can click right click and run the application.

## Debug software

Open the IDE, and perform the same steps to run the app with the autoconfiguration, but when you right-click select 
`debug EChempadApplication`. You can add breakpoints on the desired lines by clicking on the left of the line and
control the execution by using the menu at the bottom of the IDE.

To send API requests to test the application you may use `postman`, but you can proceed with `curl` if you prefer to 
only use the terminal. 

You should send the requests to your local machine `localhost`, to port `8081` and you may attack the API in the desired
subfolder, depending on the API you want to test, for example `experiment`. Notice that there are many ways of 
attacking the API at certain points, depending on the HTTP modes `GET`, `POST`, `PUT`, `DELETE` and the parameters 
submitted at the URL or the body of the message. For example, with `GET` mode and nothing in the body with this URL, 
we would get a list of all the experiments:

|  Body | HTTP mode | API | URL | Action |
|---|---|---|---|---|
| *Empty* | GET | experiment | http://localhost:8081/api/experiment | Get all experiments |
| *Empty* | GET | experiment | http://localhost:8081/api/experiment/1c9abdba-1f82-11ec-9621-0242ac130002 | Get the experiment with the ID |  
| `{ "name": "An experiment name", "description": "Example experiment." }` | POST | experiment | http://localhost:8081/api/experiment | Add a new experiment |
| `{ "name": "My journal", "description": "Contains many experiments." }` | PUT | journal | http://localhost:8081/api/journal/18a34472-57a3-46ee-8913-98eefcd4cf89 | Overwrites an existent journal |  
| *Empty* | DELETE | experiment | http://localhost:8081/api/experiment/1c9abdba-1f82-11ec-9621-0242ac130002 | Deletes the experiment with the supplied ID |

You can even visit the URLs with your browser to attack the `GET` API calls, for example visiting 
http://localhost:8081/api/researcher to trigger the `GET` API function in that URL. 
Notice that browser requests have always empty body.

#### Debug database
You can run the program installed `pgadmin` to explore and manipulate the contents of the database. Just open a terminal
and introduce the command `pgadmin`. You can also click in the desktop launcher in the desktop. 

This will open the browser in the URL where `pgadmin` is listening. You may need to reload the page. When `pgadmin` 
shows up you will need to set up a new connection to a database, so you will need to enter the password and user to open
your database. You can also set a master password, which is a password that can be used to open all the databases stored
in `pgadmin`, so you do not need to remember each credential. It is specially useful if you are planning on having more 
than one database.

#### Project spelling dictionary
You may add made up words or names, so the spelling of the IDE does not detect them as an orthographic mistake. 
