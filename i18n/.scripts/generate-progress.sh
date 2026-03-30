#!/bin/bash

GREEN='\033[0;32m'
NC='\033[0m'

# Get dirs and langs once
dirs=$(find . -maxdepth 1 -mindepth 1 -type d -not -name '.*' | sort)
langs=$(find . -name "*.properties" -not -path "./.generated/*" | xargs -I{} basename {} .properties | sort -u)

# Build header
header="| Language |"
separator="|---|"
while read dir; do
  header+=" $(basename $dir) |"
  separator+="---|"
done <<< "$dirs"

output="${header}\n${separator}\n"

# Build one row per language
while read lang; do
  row="| \`${lang}\` |"
  while read dir; do
    file="${dir}/${lang}.properties"
    if [ ! -f "$file" ]; then
      row+=" ➖ N/A |"
      continue
    fi
    total=$(grep -c '=' "$file")
    empty=$(grep -c '^[^=]*=$' "$file" || true)
    filled=$((total - empty))
    pct=$((filled * 100 / total))

    if [ "$pct" -eq 100 ]; then row+=" ✅ 100% |"
    elif [ "$pct" -ge 50 ]; then row+=" 🟡 ${pct}% |"
    else row+=" ❌ ${pct}% |"
    fi
  done <<< "$dirs"
  output+="${row}\n"
done <<< "$langs"

# Inject into README.md
perl -i -0pe "s/(<!-- PROGRESS_START -->).*?(<!-- PROGRESS_END -->)/\$1\n${output}\n\$2/s" README.md

printf "📊 Progress table updated in ${GREEN}README.md${NC}\n"