# ShuJu

![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)
![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.romualdrousseau/shuju/badge.svg)
![Snyk security score](https://snyk-widget.herokuapp.com/badge/mvn/com.github.romualdrousseau/shuju/badge.svg)
![Snyk Known Vulnerabilities](https://snyk.io/test/github/com.github.romualdrousseau/shuju/badge.svg)
![Test](https://github.com/RomualdRousseau/ShuJu/actions/workflows/build-and-test.yml/badge.svg)
![Build](https://github.com/RomualdRousseau/ShuJu/actions/workflows/build-and-deploy.yml/badge.svg)
![Servier Inspired](https://raw.githubusercontent.com/servierhub/.github/main/badges/inspired.svg)

A collection of various algorithms implemented in Java covering domains such as mathematics and data science.

## Description

### Mathematics

* Geometry Algebra: a package to build and use geomatry algebra in Java.
* CV: a package to extract shapes from image.

### Data Science

* BigData: a Java list implementation with chunks and backed memory mapped.
* Core: a Java implementation of multi-dimentional array (ala ndarray).
* Op: various mathematical algorithms on multi-dimentional array.
* Preprocessing: a Java implemetation of some keras text preprocessing functions.
* Strings: a Java implemenation of some algorithms (fuzzy search) on String.

### Others

* Json: an agnostic wrapper to json frameworks.
* Commons: A Java-Python binder managing virutal environamnt and package installation.
* Redux: A min Java implementation of the Redux pattern.

## Getting Started

### Dependencies

* The Java Developer Kit, version 17.
* Apache Maven, version 3.0 or above.

### Apache Maven Installation

For more details, see the [Installation Guide](https://maven.apache.org/install.html).

#### Update dependencies

Run the following command line:

```bash
mvn -DcreateChecksum=true versions:display-dependency-updates
```

#### Update pom.xml plugins

Run the following command line:

```bash
mvn -DcreateChecksum=true versions:display-plugin-updates
```

### Build and install locally

Run the following command line:

```bash
mvn clean install
```

### Build and deploy a snapshot to the Maven repository

Run the following command line:

```bash
mvn -P snapshot clean deploy
```

### Build and deploy a release to the Maven repository

Run the following command line:

```bash
mvn -P release clean deploy
```

### Build and deploy the javadoc documentation

Run the following command line:

```bash
mvn -P documentation clean site site-deploy
```

Do not forget to configure the GitHub authentication in ***~/.m2/settings.xml*** as follow:

```xml
<server>
    <id>github</id>
    <password>PERSONAL_TOKEN_CLASSIC</password>
</server>
```

## Contribute

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## Authors

* Romuald Rousseau, romuald.rousseau@servier.com

## Version History

* 1.23
* ...
* Initial Release
