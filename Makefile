MVN ?= mvn
.DEFAULT_GOAL := help

.PHONY: build test build-skip-tests unit-test clean help

build:
	$(MVN) package

test:
	$(MVN) test

build-skip-tests:
	$(MVN) package -DskipTests

unit-test:
	test -n "$(TEST)" || (echo "TEST is not set. Use 'make unit-test TEST=ClassName'" && exit 1)
	$(MVN) -Dtest=$(TEST) test

clean:
	$(MVN) clean

help:
	@echo "Makefile for Maven build"
	@echo
	@echo "Targets:"
	@echo "  build              Build the project with tests"
	@echo "  test               Run all tests"
	@echo "  build-skip-tests   Build the project without tests"
	@echo "  unit-test TEST=<name>  Run a single unit test class"
	@echo "  clean              Remove build artifacts"
	@echo "  help               Show this help"
