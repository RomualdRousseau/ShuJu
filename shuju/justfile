#
# RECIPES
#

# Default is this help
default: help

# Print the available recipes
help:
    @just --justfile {{justfile()}} --list

# Initializatize maven, build docker image and launch test
all: initialize build

# Initializatize maven
initialize:
    mvn initialize

# Clean project
clean:
    mvn clean

# Build docker
build:
    mvn -DskipTests package

# Test
test:
    mvn test

# Install
install:
	mvn -DskipTests install
