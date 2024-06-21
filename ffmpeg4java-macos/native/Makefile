INSTALL_PATH := $(PWD)/../src/main/resources

.PHONY: checkout
checkout:
	./scripts/checkout.sh

.PHONY: configure
configure:
	./scripts/configure.sh

.PHONY: compile
compile:
	./scripts/compile.sh

.PHONY: clean
clean:
	./scripts/clean.sh

.PHONY: install
install:
	./scripts/copy.sh $(INSTALL_PATH)

.PHONY: all
all: checkout configure compile install
