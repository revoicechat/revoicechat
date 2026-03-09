#!/bin/bash

GREEN='\033[0;32m'
NC='\033[0m'

rm -rf .generated
mkdir -p .generated

count=0
while read file; do
  dir=$(basename $(dirname "$file"))
  filename=$(basename "$file")
  grep -v '^[^=]*=$' "$file" > ".generated/${dir}_${filename}"
  count=$((count + 1))
done < <(find . -name "*.properties" -not -path "./.generated/*")

printf "🎉 Successfully generated ${count} i18n files onto ${GREEN}.generated${NC} directory\n"