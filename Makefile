MVN ?= mvn
.DEFAULT_GOAL := help

.PHONY: build test build-skip-tests unit-test clean fmt fmt-check help

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

.PHONY: fmt
fmt:
	$(MVN) $(Q) spotless:apply

.PHONY: fmt-check
fmt-check:
	$(MVN) $(Q) -Dspotless.failOnError=true -DskipTests verify

help:
	@echo "Makefile for Maven build"
	@echo
	@echo "Targets:"
	@echo "  build              Build the project with tests"
	@echo "  test               Run all tests"
	@echo "  build-skip-tests   Build the project without tests"
	@echo "  unit-test TEST=<name>  Run a single unit test class"
	@echo "  fmt                Run spotless"
	@echo "  fmt-check          Run spotless only check"
	@echo "  clean              Remove build artifacts"
	@echo "  help               Show this help"
