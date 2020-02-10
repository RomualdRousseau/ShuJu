# ShuJu

This project aims to collect and implement various statistics and machine learning algorithms. So far, the following algorithms have been implemented:

    * Math library
        * Scalar
        * Vector
        * Natrix
        * Linear Algebra algorithms (QR, HouseGHolder, Linear Solve)
	* Artificial Neural Network
        * Full connected multiple layers
        * Gradient descent back propagation
        * Activation functions (LeakyRelu, Linaer, Relu, Sigmoid, Softmax, Tanh)
        * Loss function (Huber, MeanSquare, SoftmaxCrossEntropy)
        * Initializers (Glorot, He, Lecun)
        * Normalizers (Batch, L2)
        * Optimizers (Adam, RMSProp, Sgd)
        * SChedulers (Exponential)
    * Kmean
	* K-Nearest Neighbor
    * Naive Bayes
	* Simple Linear Regression
    * NLP
        * Tokenizers (ngram, shingle)
        * Word to vect ()
    * CV
        * Image convolution filter
        * Shape extractors (rectangle by hough transformation)
    * Genetic
        * Pool with selection
        * Mutation interface
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
