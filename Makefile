VERSION=1.4.6-1.11
MODS_DIR=~/Library/Application\ Support/minecraft/mods/

build_mod: copy_assets build_lang
	./gradlew build

copy_assets:
	rm -f ./src/main/resources/*.md
	cp ./README.md ./src/main/resources/README.md
	cp ./CHANGELOG.md ./src/main/resources/CHANGELOG.md
	cp ./LICENSE.md ./src/main/resources/LICENSE.md

install: build_mod
	rm -f ${MODS_DIR}/blockbuster-${VERSION}.jar 2> /dev/null
	cp ./build/libs/blockbuster-${VERSION}.jar ${MODS_DIR}

build_lang:
	mkdir -p src/main/resources/assets/blockbuster/lang
	php php/help.php

check: build_lang
	php php/language.php
