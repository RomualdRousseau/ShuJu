# ShuJu
![Build](https://github.com/RomualdRousseau/ShuJu/workflows/Build/badge.svg)

It is an enterprise grade Java API for linear algebra, statictic and machine learning algorithms.

Strongly influenced by Numpy and Tensorflow, ShuJu aims to provide a solid mathematical and scientific library for Java. Despite the success of python and various new languages, Java is still largely used in enterprise and mobile solutions because of its ubiquity and foundations in most of professional platforms.

Each implemented algorithm is not trivial to avoid for example calculation issues as rounding. Also a great care is given to optimization and tests.

The core of the linear algebra is (like Numpy) an implementation of multi dimensional arrays with the notion of universal functions (lamda calculus). The implementation tries to avoid most of copies during tanspose, reshape and views and use packed java arrays to be friendly with CPU vectorization instructions or GPU. Fast computations is ensured by using BLAS subroutines.

So far, the following algorithms have been implemented:

    * Math library
        * Scalar
        * Tensor (Including Vector, Matrix, ...)
        * Linear Algebra algorithms (LU, QR, HouseGHolder, Hessenberg, Cholesky, Gaussian Elimination, Linear Solve, Eigens, PCA, SVD)
    * Neural Network
        * Full connected multiple layers (Dense)
        * Convolution layers
        * DropOut layers
        * Batch Normlization Layers
        * Flatten layers
        * Pooling Layers
        * Genetic Layers
        * Gradient descent back propagation
        * Activation functions (LeakyRelu, Linaer, Relu, Sigmoid, Softmax, Tanh)
        * Loss function (Huber, Hinge, MeanSquare, SoftmaxCrossEntropy)
        * Initializers (Glorot, He, Lecun)
        * Regularizers (L1, L2)
        * Optimizers (Adam, AdaDelta, RMSProp, Sgd)
        * Schedulers (Exponential)
    * Kmean
    * K-Nearest Neighbor
    * Naive Bayes
    * Simple Linear Regression
    * NLP
        * Tokenizers (ngram, shingle)
        * Word 2 vect
        * Regular Expression
        * Stop words
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
