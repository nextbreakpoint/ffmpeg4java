INSTALL_PATH := $(CURDIR)/../src/main/resources

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
all: build-lib install

.PHONY: build-image
build-image:
	docker build -t ffmpeg4java-linux .

.PHONY: build-lib
build-lib: build-image
	docker run --rm -i -v $(CURDIR):/var/native ffmpeg4java-linux make checkout configure compile
