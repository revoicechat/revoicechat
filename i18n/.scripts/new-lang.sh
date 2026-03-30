#!/bin/bash

RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

lang=$1

if [ -z "$lang" ]; then
  printf "${RED}Usage: ./new-i18n.sh <lang>${NC}\n"
  exit 1
fi

count=0
while read dir; do
  source="${dir}/en.properties"
  target="${dir}/${lang}.properties"

  if [ ! -f "$source" ]; then
    continue
  fi

  if [ -f "$target" ]; then
    printf "${RED}Skipping${NC} ${target} — file already exists\n"
    continue
  fi

  sed 's/=.*/=/' "$source" > "$target"
  printf "Created ${GREEN}${target}${NC}\n"
  count=$((count + 1))
done <<< "$(find . -maxdepth 1 -mindepth 1 -type d -not -name '.*' | sort)"

printf "🎉 Created ${count} files for language ${GREEN}${lang}${NC}\n"