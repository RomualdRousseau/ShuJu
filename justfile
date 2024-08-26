set positional-arguments

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
    mvn -DskipTests package

# Run the tests
test:
    mvn -Dtest=UnitTestSuite -Dsurefire.failIfNoSpecifiedTests=false test

# Run all tests
test-full:
    mvn -Dtest=UnitFullTestSuite -Dsurefire.failIfNoSpecifiedTests=false test

# Install in the local repository
install:
	mvn -DskipTests install

# Deploy snapshot
deploy-snapshot:
	mvn -U -B clean deploy -DskipTests -P snapshot --also-make --batch-mode

# Deploy release
deploy-release:
	mvn -U -B clean deploy -DskipTests -P release --also-make --batch-mode

# Prepape a new version:
prepare-version *args='':
    mvn versions:set -DnewVersion={{args}}
    mvn versions:commit

build-doc:
    mvn -P documentation clean site site:stage

# Update all plugins and dependencies
update:-DnewVersion=
    mvn versions:use-latest-release
