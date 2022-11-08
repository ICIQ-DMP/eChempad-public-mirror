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
  * **JDK8:** Java development kit. Contains the interpreter for the Java programming language `java` and the tool to 
    manipulate the certificates used in the java VM `keytool`
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

#### Connect to the database manually using terminal
``` 
psql -d eChempad -h localhost -p 5432 -U amarine
```

## ACL SQL schema & JPA entities SQL schema
The application combines two different strategies to initialize the SQL schema for our database, which will use
postgresql. The first strategy is using a `schema.sql` file, which by activating some properties in
`application.properties` it can be executed every time our application goes up. We can add sql conditionals to
initialize the schema if and only if the schema is not present. The schema is mainly used to initialize the SQL tables
for the ACL initialization.

The other strategy is using the automatic schema initialization that comes with JPA data repositories / entities.
By modifying the corresponding property in `application.properties` we can choose to initialize the schema when our app
goes up, validate it, or do nothing.

The problem and the reason why I am documenting this is that there is an ACL table that also has an associated JPA
repository, and as such, the table can be initialized in both ways, which is wrong, since we need to use the ACL SQL
schema for the schema and the JPA repository to modify the tables programmatically.

#### Steps to reproduce a clean initialization
1- To ensure the proper initialization of the schema first begin by dropping all tables.
2- Deactivate the initialization of JPA schema by setting the DB policy to *none*.
3- Run application. The ACL SQL schema will be read from the `schema.sql` script and the app will fail because the
schema for the JPA entities will not be present. The app should file with an error like this:
`(...) Caused by: org.postgresql.util.PSQLException: ERROR: relation "researcher" does not exist
`. The *acl_sid* table will be created using the schema provided via a JPA entity class.
4- Reactivate the JPA initializations by setting the DB policy to *update*.
5- Rerun the application, which now should be working (even though because of admin initialization it can have an error 
of duplicated primary key, but if your rerun one more time everything should be working). The ACL tables from the schema,
and the JPA tables
(except the *acl_sid* table, which comes from IdSecurity JPAEntity JPA initialization) from the Entities are now fully 
initialized.
6- We can encounter one more error while initializing the app, it goes like: 
`org.postgresql.util.PSQLException: ERROR: duplicate key value violates unique constraint "acl_sid_pkey"` This happens
because we manipulate the acl_sid table "from behind" (not programmatically, we attack the raw SQL tables). And as such, 
the first petitions to create an ACL can cause a collision. 
7- After that error we may find another Exception! it goes like this: 
`java.lang.IllegalArgumentException: Transaction must be running`, but only happens twice the first time that a valid
ACL is requested.
In the next petitions, the ACL service will answer correctly.


## Creating the file structures
**(DEPRECATED)** The eChempad application stores files in the file system under the folder /eChempad/file_db. It also stores the 
credentials of the APIKeys under /eChempad/APIKeys. Now, for debug purposes we store in this path a file called "key" 
which contains a dummy APIKey inside a file. To create this file structure use

```bash
sudo mkdir -p /eChempad/file_db
sudo mkdir -p /eChempad/APIKeys
sudo chown eChempad:eChempad /eChempad/file_db
sudo chown eChempad:eChempad /eChempad/APIKeys
sudo nano /eChempad/APIKeys/key  # Introduce the key here. Remember to not paste it in the terminal or it will be kept in the history!
sudo chmod 400 -R /eChempad
```

You can also use another user if the eChempad is executed from another user.

Also remember that the API key can be generated from [here](https://iciq.signalsnotebook.perkinelmercloud.eu/snconfig/settings/apikey)

## Certificates of the JVM
The certificates of java are stored in the `cacerts` file, which can be located in different places of the system. We 
have our own cacerts file uploaded to the git repository, which is located in ./eChempad/src/main/resources/CA_certificates/cacerts
and is the one that we are using. 

To check the presence of certificates inside this cacerts file we can use the following command:

```
keytool -list -keystore ~/Desktop/eChempad/src/main/resources/CA_certificates/cacerts -storepass changeit
```

If you can not see any certificate you must download another cacerts file or update the JDK you are using, since from 
time to time the certificates expire and will not work.


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

A thing to notice is that there is an external tool that sends a SIGKILL signals to the eChempad process in order to 
shut down other instances of the process. To configure it within our run configuration go to the configuration of this 
run task and add a new external tool before launch by setting the script eChempad/tools/killAppOnPort.sh and passing
8081 (the port that eChempad is using). The tool will be executed before the run, so any other instances will be killed.

###### Using the autoconfiguration of the IDE
Go to the class `EChempadApplication` and in the line where we declare the class:
```java
public class EChempadApplication {
    // ...
}
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

## Continuous Integration
The project has continuous integration features:
#### Auto-generation of license
Using the Maven licenses plugin we can autogenerate the copyright headers for each file of the project and also collect 
all the third party libraries used by listing the dependencies in pom.xml.

The next instruction will generate all licenses and attach it to the source files.
Warning: It appends more than one time if executed in sequence.
```
mvn -Dthird.party.licenses=true -Dattach.license.header=true generate-resources 
```

There is a small problem and is that in Java the headers for the copyright are added as a JavaDoc, which converts the 
headers to a dangling JavaDoc. Is preferred a normal comment. Issuing this command from the same folder as the project 
will change them to a normal comment:
```bash
IFS=$'\n'; for j in $(grep -r -I -l .); do sed '1 s|/\*\*|/\*|' -i "$j"; done
```

## Project file structure
Here is a list of the folders and files contained inside the root of this project:
- `.idea/`: Not present, but appears when using a Jetbrains editor such as IntelliJ IDEA to open the project. Contains 
            internal files used by the IDE to keep the local configuration. Can be removed safely at any time; does not
            contain any project related information, only user settings. 
- `.mvn/wrapper`: Contains the Maven downloader and the downloaded Maven binaries. The binaries (*.jar files) are 
                  ignored and can be safely deleted. The source files are used to retrieve the Maven binaries if not 
                  present. This Maven is multi-platform.
- `.run/`: Contains IntelliJ IDEA run configurations in XML format. Used by the IDE to retrieve execution options, so we
           do not have to configure each IDE that we are using. 
- `src/`: Contains the code and resources used to build the project.
- `src/main/java`: Contains the Java code used in the application. This is the main source code folder.
- `src/main/resources`: Contains configuration code and other resources needed to set the development environment and
                        to project execution. 
- `src/main/resources/CA_certificates`: CA certificates usually give troubles when using the Maven wrapper. The CA 
                                        certificates in this directory are
                                        used as fallbacks if the ones provided by the system can not be used or are corrupted
- `src/main/resources/META-INF`: Used to define our own schemas and properties inside the `.properties` files used by Spring,
                                 which can be used inside the Java code to define configurations.
- `src/main/resources/Signals-API-Scratcher`: Contains the code to massively download the data from a user of the Signals Notebook 
                                    from ParkinElmerCloud into the file system using `bash` and `curl`. This is kept as
                                    reference since it will be integrated into this application.
- `src/main/resources/static`: Created by default. Contains static content such as images or sounds usually used in the creation of web pages. 
- `src/main/resources/templates`: Created by default. Contains templates in marking languages such as HTML or XML that are used to create the actual web pages to serve.
- `src/main/resources/*.properties`: Files that define properties that can be used inside the Java code to change code behaviour 
                                     without adding any code. The main one is `application.properties`, which is always parsed. Inside it 
                                     there is the property `spring.profiles.active` who tells which is the active profile and is used by the 
                                     Maven wrapper to also import the corresponding `.properties` file of the active profile.
- `src/main/rsources/*.sh`: Files that define or modify system variables depending on the profile that we are on. The file 
                            `application.sh` is always executed when using the Maven wrapper to execute the code. The other `.sh` files
                            are executed when its corresponding profile is active. 
- `src/test/java`: Contains the code of the test of the Java code. 
- `src/test/SQL`: Contains examples of SQL queries that are used in key points of the application. 
- `target/`: Contains the autogenerated binaries of the project. Is not included in the repository because it contains binary files that cannot be merged.
- `tools/`: Contains utilities to support repetitive tasks during the development. 
- `.gitattributes`: Used to mark some files as "large files" which are files that are uploaded to version control but not merged.
- `.gitignore`: Used to ignore certain files from version control.
- `.spelling_dictionary.dic`: You may add made up words or names, so the spelling corrector of the IDE does not detect 
                              them as an orthographic mistake.
- `COPYING`: Current license file (GNU AFFERO GENERAL PUBLIC LICENSE, Version 3)
- `CREDITS.md`: List of the people, organizations and institutions involved in the project development.
- `LICENSE.md`: Additional license definitions not covered in the *license* folder
- `mvnw`: Initialization wrapper script of maven, to check the place where it is residing, download the binary if 
          missing, etc. Written in `BASH`. It has been modified to have more flexibility when using CA_certificates or when there is another
          process trying to use the same port as the deployed app.
- `pom.xml`: Configuration file for Maven. It contains the different dependencies of the project and which packages 
             the interpreter needs to download.
- `README.md`: Contains a description of the project and a wide review of its important elements and goals.
- `README_DEVEL.md`: Contains a more technical description of the project in order to obtain a valid deployment of the application.

## JavaDoc
* All methods, fields and classes must have its corresponding `javadoc`. Some exceptions in the methods apply:
  * Getter / setter methods. 
  * Trivial constructor, specially no-args constructor.
  * Trivial toString method.
  * Trivial hashCode method.
  * The methods that override other methods must inherit the `javadoc` from the overridden methods.

There are certain convention that must be followed in order to produce good `javadoc` documentations:
* Each line above is indented to align with the code below the comment.
* The first line contains the begin-comment delimiter ( /**).
* Starting with Javadoc 1.4, the leading asterisks are optional.
* Write the first sentence as a short summary of the method, as Javadoc automatically places it in the method summary table (and index).
* Notice the inline tag {@link URL}, which converts to an HTML hyperlink pointing to the documentation for the URL class. This inline tag can be used anywhere that a comment can be written, such as in the text following block tags.
* If you have more than one paragraph in the doc comment, separate the paragraphs with a \<p> paragraph tag, as shown.
* Insert a blank comment line between the description and the list of tags, as shown.
* The first line that begins with an "@" character ends the description. There is only one description block per doc comment; you cannot continue the description following block tags.
* The last line contains the end-comment delimiter ( */) Note that unlike the begin-comment delimiter, the end-comment contains only a single asterisk.

## Conventions
The names of the columns in the DB will be the same as the name of the corresponding variables in the Java code. 

## Access Control List permissions
We use the default permission evaluator of Spring Security, which does not do bitwise operations to check the 
permissions against something. So the default with this code is the following:
- 1 means "READ"
- 2 means "Write"
- 4 means "Create"
- 8 means "Delete" 
- 16 means "Administer"

## Spring Boot general concepts

#### Inversion of control
Spring-boot particularly is focused on using a pattern design called inversion of control, in which the programming is less
imperative and explicit, and becomes declarative and aspect-oriented.

Basically one of the most used features is the automatic dependency injection. When annotating a class with `@Component`
we are telling Spring-Boot that a class can be auto-injected in places where the `@Autowired` annotation is present.

This is useful because we do not need to initialize or instantiate the classes of our program.

### Annotations

#### `@Converter`
A converter is a class that defines two methods to interchange the format between two classes A and B. It is
particularly used transparently when in need to convert a memory object to a serializable type when introducing it into
the DB and vice-versa.

The `@Converter` annotation can receive a parameter `autoApply = bool` so it performs automatic translation between
types implicitly, there is no need to call directly the methods. Spring will call it for you when needed.

In the following example the converter is used to convert Paths to String (Serializable) and vice-versa:

```
@Converter(autoApply = true)  
public class PathConverter implements AttributeConverter<Path, String>, Serializable {

    @Override
    public String convertToDatabaseColumn(Path path) {
        return path == null ? null : path.toString();
    }

    @Override
    public Path convertToEntityAttribute(String s) {
        return s == null ? null : Paths.get(s);
    }
} 
```


#### `@Component` and its specialized `@Service`, `@Controller`, `@RestController`, `@Repository`
All classes marked with these annotations are converted automatically into components that Spring Boot is able to inject
in runtime. The specializations provide some useful effects if we are actually using a specialized component annotation:
- `@Repository`: Marks all method as transactional and handles the session automatically inside them. Repository is used in
  classes that are responsible for communicating with a DB.
- `@Service`: The class provides a service inside the application.
- `@Controller`: Handles the serialization and deserialization of data coming in and out of the methods of the class.
  Controller provides endpoints that our clients are connected to, and as such they are the first and last piece of code
  executed when handling a request.
- `@RestController`: The same as a controller, but provides support for the typical CRUD operations.

#### `@Autowired`
This annotation is used to annotate a property of a class, setter of a property or constructor. This tells Spring Boot
that a field needs to be automatically injected. Then, we do not need to initialize or call the constructor of that
class.

## Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.5.4/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.5.4/maven-plugin/reference/html/#build-image)
* [Field injection is not recommended](https://blog.marcnuri.com/inyeccion-de-campos-desaconsejada-field-injection-not-recommended-spring-ioc)
* [Should JavaDoc be added to implementation](https://stackoverflow.com/questions/3061387/should-javadoc-comments-be-added-to-the-implementation)
* [Java Google StyleSheet](https://google.github.io/styleguide/javaguide.html)
* [UUID keys 1](https://stackoverflow.com/questions/45086957/how-to-generate-an-auto-uuid-using-hibernate-on-spring-boot/45087148)
* [UUID keys 2](https://thorben-janssen.com/generate-uuids-primary-keys-hibernate/)
* [UUID keys 3](https://stackoverflow.com/questions/43056220/store-uuid-v4-in-mysql)
* [Disable CSRF depending on application.properties](https://www.yawintutor.com/how-to-enable-and-disable-csrf/)
* [PostGreSQL + ACL with UUID SQL schema](https://docs.spring.io/spring-security/site/docs/4.2.x/reference/html/appendix-schema.html)
* [Cascade entity removal](https://stackoverflow.com/questions/16898085/jpa-hibernate-remove-entity-sometimes-not-working)
* [How to use JavaDoc](https://www.dummies.com/article/technology/programming-web-design/java/how-to-use-javadoc-to-documentWrapper-your-classes-153265/)
* [hibernate-mapping-exception-could-not-determine-type-for-java-nio-file-path](https://stackoverflow.com/questions/53199558/hibernate-mapping-exception-could-not-determine-type-for-java-nio-file-path)
* [Field injection is not recommended and injection types in Spring Boot](https://blog.marcnuri.com/field-injection-is-not-recommended)
* [How to ignore Null fields in Jackson](https://stackoverflow.com/questions/11757487/how-to-tell-jackson-to-ignore-a-field-during-serialization-if-its-value-is-null)