set dotenv-load

#
# RECIPES
#

# Default is this help
default: help

# Print the available recipes
help:
    @just --justfile {{justfile()}} --list

# Initializatize maven, build, and run the tests
all: initialize test build

# Initializatize maven
initialize:
    mvn initialize

# Clean
clean:
    mvn clean

# Build
build:
    mvn -U -DskipTests package

# Run the tests
test:
    mvn -Dtest=UnitTestSuite -Dsurefire.failIfNoSpecifiedTests=false test

# Run all tests
test-full:
    mvn -Dtest=FullTestSuite -Dsurefire.failIfNoSpecifiedTests=false test

# Install in the local repository
install:
	mvn -DskipTests install

# Deploy snapshot to the maven repository
deploy-snapshot:
	mvn clean deploy -DskipTests -P snapshot -s .mvn/settings.xml

# Deploy release to the maven repository
deploy-release:
	mvn clean deploy -DskipTests -P release -s .mvn/settings.xml

# Update all plugins
update-plugins:
    mvn -DcreateChecksum=true -DprocessDependencyManagement=false versions:display-plugin-updates

# Update all dependencies
update-deps:
    mvn -DcreateChecksum=true -DprocessDependencyManagement=false versions:display-dependency-updates
