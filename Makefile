VERSION=1.4.1-1.11
MODS_DIR=~/Library/Application\ Support/minecraft/mods/

build_mod: build_lang
	./gradlew build

install: build_mod
	rm -f ${MODS_DIR}/blockbuster-${VERSION}.jar 2> /dev/null
	cp ./build/libs/blockbuster-${VERSION}.jar ${MODS_DIR}

build_lang:
	mkdir -p src/main/resources/assets/blockbuster/lang
	php php/help.php

check: build_lang
	php php/language.php
