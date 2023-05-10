<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a name="readme-top"></a>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->



<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![Code quality][codacy-shield]][codacy-url]
[![Contributors][contributors-shield]][contributors-url]
[![Stargazers][stars-shield]][stars-url]
[![Top language][language-shield]][language-url]
[![GNU GPL v3 License][license-shield]][license-url]
[![Lines of code][loc-shield]][loc-url]
[![Number of commits since v0.1][commit-shield]][commit-url]
[![Commit activity][activity-shield]][activity-url]
[![Last commit on][last-shield]][last-url]
[![But me a coffee][coffee-shield]][coffee-url]
[![number of stars][stars-shield]][stars-url]
[![LinkedIn][linkedin-shield]][linkedin-url]



<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/AleixMT/eChempad">
    <img src=".github/images/logo.png" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">eChempad</h3>

  <p align="center">
    eChempad is a web application oriented to manage the entire
data life-cycle of experiments and assays from Experimental Chemistry and related Science disciplines.
    <br />
    <a href="https://github.com/AleixMT/eChempad"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://d.iciq.es">View Demo</a>
    ·
    <a href="https://github.com/AleixMT/eChempad/issues/new">Report Bug</a>
    ·
    <a href="https://github.com/AleixMT/eChempad/issues/new">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

[![Product Name Screen Shot][product-screenshot]](https://echempad.iciq.es)

The **Chem**istry **e**lectronic note**pad** (eChempad) platform is a suite of web services oriented to manage the entire
data life-cycle of experiments and assays from **Experimental Chemistry** and related Science disciplines.

The eChempad platform appears as an answer to the digitization needs of experimental chemists at [Institut Català
d'Investigació Química (ICIQ)](https://www.iciq.org/). This platform intends to be an analogous example of the
successful platform for **Computational Chemistry** developed at ICIQ,
[ioChem-BD](https://www.iochem-bd.org/index-introduction.jsp).

Currently, the eChempad platform allows researchers to extract experimentation data from the 
[PerkinElmer Signals Notebook](https://perkinelmerinformatics.com/products/research/signals-notebook-eln) in bulk, 
enrich them with metadata, and finally publish them into [CORA RDR](https://dataverse.csuc.cat/) a 
[Dataverse](https://dataverse.org/) instance maintained by the 
[Consorci de Serveis Universitaris de Catalunya (CSUC)](https://www.csuc.cat/en). 

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Built With

This section lists any major frameworks/libraries used to bootstrap the eChempad platform:


* [![Bootstrap][Bootstrap-shield]][Bootstrap-url]
* [![JQuery][JQuery-shield]][JQuery-url]
* [![PostgreSQL][postgres-shield]][postgres-url]
* [![Java][java-shield]][java-url]
* [![Spring Boot][springboot-shield]][springboot-url]
* [![ZK][zk-shield]][zk-url]
* [![Hibernate][hibernate-shield]][hibernate-url]
* [![Jackson][jackson-shield]][jackson-url]



<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- GETTING STARTED -->
## Getting Started

This is how you can get a local copy up and running follow these simple steps:

### Prerequisites

The first thing that we got to do is clone the repository that contains the software
[`Linux-Auto-Customizer`](https://github.com/AleixMT/Linux-Auto-Customizer). This
software consists in a set of scripts to automatically install dependencies, libraries and programs to a Linux
Environment. It can be used in many distros, but in this guide we suppose that our environment is Ubuntu Linux. It
may be the same or similar instructions in related distros.

We can clone the repository anywhere, for example in our HOME folder:

```bash
cd $HOME
git clone https://github.com/AleixMT/Linux-Auto-Customizer
cd Linux-Auto-Customizer
bash src/core/install.sh -v -o customizer
```

The previous commands will install the software, so it can be accessed using the link `customizer-install` and
`customizer-uninstall` software if everything is okay.

#### Resolving dependencies

In the repository execute the next orders:
```bash
sudo customizer-install -v -o psql
bash cutomizer-install -v -o jdk pgadmin postman ideau  # ideac 
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

<p align="right">(<a href="#readme-top">back to top</a>)</p>


### Installation

#### Setting up database connection
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

##### Connect to the database manually using terminal
``` 
psql -d eChempad -h localhost -p 5432 -U amarine
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## Usage

Use this space to show useful examples of how a project can be used. Additional screenshots, code examples and demos 
work well in this space. You may also link to more resources.

_For more examples, please refer to the [Documentation](https://example.com)_

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ROADMAP -->
## Roadmap
- [x] Frontend addition with ZK
- [ ] Write tests
- [ ] Customize license plugins
- [ ] Change backend to composite structure

See the [open issues](https://github.com/AleixMT/eChempad/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any 
contributions you make are **greatly appreciated**. You can start by taking a look to the 
[`README_DEVELOPER.md`](https://github.com/AleixMT/eChempad/blob/develop/doc/README_DEVELOPER.md).

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also 
simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- LICENSE -->
## License

Distributed under the GNU AFFERO GENERAL PUBLIC LICENSE, Version 3. See `LICENSE.md` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTACT -->
## Contact

Your Name - [@ioChem_BD](https://twitter.com/ioChem_BD) - [dmp@iciq.es](dmp@iciq.es)

Project Link: [https://github.com/AleixMT/eChempad](https://github.com/AleixMT/eChempad)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- ACKNOWLEDGMENTS -->
## Acknowledgments

Use this space to list resources you find helpful and would like to give credit to. I've included a few of my favorites to kick things off!

* [Choose an Open Source License](https://choosealicense.com)
* [GitHub Emoji Cheat Sheet](https://www.webpagefx.com/tools/emoji-cheat-sheet)
* [Malven's Flexbox Cheatsheet](https://flexbox.malven.co/)
* [Malven's Grid Cheatsheet](https://grid.malven.co/)
* [Img Shields](https://shields.io)
* [GitHub Pages](https://pages.github.com)
* [Font Awesome](https://fontawesome.com)
* [React Icons](https://react-icons.github.io/react-icons/search)

<p align="right">(<a href="#readme-top">back to top</a>)</p>



## Credits

Copyright 2020-2023 Institute of Chemical Research of Catalonia (ICIQ) 

###### Institutions involved in the eChempad development:

* [Institute of Chemical Research of Catalonia(ICIQ)](https://www.iciq.es/) ![img_1.png](.github/images/logo-ICIQ.png)


###### Development:
* Main developer: Aleix Mariné-Tena (2021 - now)

###### Software committee:
* Moisés Álvarez (ICIQ - URV) (main developer of [ioChem-BD](https://www.iochem-bd.org/))

###### Scientific committee:
* Carles Bo (ICIQ)

###### Scientific contributors:
* Imma Escofet (ICIQ)
* Gemma Aragay (ICIQ)

###### System administrators:
* Martin Gumbau (ICIQ)



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/AleixMT/eChempad?style=for-the-badge
[contributors-url]: https://github.com/AleixMT/eChempad/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/AleixMT/eChempad.svg?style=for-the-badge&label=Fork&maxAge=2592000  
[forks-url]: https://github.com/AleixMT/eChempad/network/members
[stars-shield]: https://img.shields.io/github/stars/AleixMT/eChempad?style=for-the-badge
[stars-url]: https://github.com/AleixMT/eChempad/stargazers
[license-shield]: https://img.shields.io/github/license/AleixMT/eChempad?style=for-the-badge
[license-url]: https://github.com/AleixMT/eChempad/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/aleix-marin%C3%A9-083672122/
[product-screenshot]: .github/images/screenshot.png

[codacy-shield]: https://app.codacy.com/project/badge/Grade/9d77f6c73bab4a11b847d131146fc243
[codacy-url]: https://app.codacy.com/gh/AleixMT/eChempad/dashboard
[language-shield]: https://img.shields.io/github/languages/top/AleixMT/eChempad?style=for-the-badge&color=blue&logo=oracle
[language-url]: https://www.gnu.org/software/bash
[loc-shield]: https://img.shields.io/tokei/lines/github/AleixMT/eChempad?style=for-the-badge&logo=github
[loc-url]: https://gitlab.com/AleixMT/eChempad
[commit-shield]: https://img.shields.io/github/last-commit/AleixMT/eChempad?style=for-the-badge
[commit-url]: https://github.com/AleixMT/eChempad/issues
[activity-shield]: https://img.shields.io/github/commit-activity/m/AleixMT/eChempad?style=for-the-badge
[activity-url]: https://github.com/AleixMT/eChempad/graphs/commit-activity
[last-shield]: https://img.shields.io/github/last-commit/AleixMT/eChempad?&style=for-the-badge&color=blue
[last-url]: https://github.com/AleixMT/eChempad/commits/master
[coffee-shield]: https://img.shields.io/badge/-buy_me_a%C2%A0coffee-gray?logo=buy-me-a-coffee&style=for-the-badge
[coffee-url]: https://www.buymeacoffee.com/VidWise
[stars-shield]: https://img.shields.io/github/stars/AleixMT/eChempad?style=for-the-badge&logo=github
[stars-url]: https://github.com/AleixMT/eChempad/stargazers

[Bootstrap-shield]: https://img.shields.io/badge/Bootstrap-1.0+-white?style=for-the-badge&logo=bootstrap&logoColor=white
[Bootstrap-url]: https://getbootstrap.com
[JQuery-shield]: https://img.shields.io/badge/jQuery-1.0+-orange?style=for-the-badge&logo=jquery&logoColor=white
[JQuery-url]: https://jquery.com
[postgres-shield]: https://img.shields.io/badge/postgres-12.14+-blue?style=for-the-badge&logo=postgresql&logoColor=white
[postgres-url]: https://www.postgresql.org/
[java-shield]: https://img.shields.io/badge/java-8.0+-yellow?style=for-the-badge&logo=oracle&logoColor=white
[java-url]: https://www.java.com/es/
[git-shield]: https://img.shields.io/badge/git-2.25.1+-black?style=for-the-badge&logo=git
[git-url]: https://git.com
[bash-shield]: https://img.shields.io/badge/bash-4.0+-black?style=for-the-badge&logo=gnubash
[bash-url]: https://www.gnu.org/software/bash/
[springboot-shield]: https://img.shields.io/badge/springboot-4.0+-green?style=for-the-badge&logo=spring
[springboot-url]: https://spring.io/
[zk-shield]: https://img.shields.io/badge/zk-4.0+-purple?style=for-the-badge&logo=zkoss
[zk-url]: https://www.zkoss.org/
[hibernate-shield]: https://img.shields.io/badge/hibernate-1.0+-red?style=for-the-badge&logo=hibernate
[hibernate-url]: https://www.zkoss.org/
[jackson-shield]: https://img.shields.io/badge/jackson-1.0+-brown?style=for-the-badge&logo=jackson
[jackson-url]: https://github.com/FasterXML/jackson