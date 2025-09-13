MVN ?= mvn
.DEFAULT_GOAL := help

.PHONY: build test build-skiptests unit-test clean fmt fmt-check help

build:
	$(MVN) clean package

test:
	$(MVN) test

build-skiptests:
	$(MVN) package -DskipTests -Dmaven.test.skip=true

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
	$(MVN) $(Q) -Dspotless.failOnError=true -DskipTests -Dmaven.test.skip=true verify

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
