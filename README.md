# ShuJu

This project aims to collect and implement various statistics and machine learning algorithms. So far, the following algorithms have been implemented:

	* Artificial Neural Network (back propagation)
	* K-Nearest Neighbor
	* Simple Linear Regression
	* Decision Tree
	* DataSet management with transformation framework such as normalization, standardization ...

## Project Documentation
https://romualdrousseau.github.io/ShuJu/

## User Guide
https://github.com/RomualdRousseau/ShuJu/wiki

## Install Processing examples
	* Set environement variable PROCESSING_USER_LIBRARIES to the Processing User libraries
	* mvn clean install

## Deployment

### Deploy snapshot
```bash
mvn clean deploy
```

### Deploy release
```bash
mvn clean site -Dmessage="<commit_log_here>" scm:checkin -P release
mvn scm:update
```

### Deploy documentation
```bash
mvn clean site deploy -P documentation
```
