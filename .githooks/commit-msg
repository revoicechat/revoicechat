#!/bin/bash

# commit-msg hook to enforce Conventional Commits with mandatory scope
# Place in .git/hooks/commit-msg and make executable (chmod +x)

commit_msg_file=$1
commit_msg=$(cat "$commit_msg_file")

# Pattern for Conventional Commits with MANDATORY scope
# Format: type(scope): description
pattern='^(feat|fix|docs|style|refactor|perf|test|build|ci|chore|revert)(\([a-z0-9\-]+\))!?: .{1,50}'

if ! echo "$commit_msg" | grep -qE "$pattern"; then
    echo "❌ ERROR: Commit message does not follow the required format"
    echo ""
    echo "Required format:"
    echo "  type(scope): short description"
    echo ""
    echo "  optional body"
    echo ""
    echo "  optional footer"
    echo ""
    echo "Allowed types:"
    echo "  - feat: new feature"
    echo "  - fix: bug fix"
    echo "  - docs: documentation"
    echo "  - style: formatting, missing semicolons, etc."
    echo "  - refactor: code refactoring"
    echo "  - perf: performance improvement"
    echo "  - test: adding/modifying tests"
    echo "  - build: build system changes"
    echo "  - ci: CI/CD changes"
    echo "  - chore: maintenance tasks"
    echo "  - revert: revert a previous commit"
    echo ""
    echo "⚠️  Scope is MANDATORY"
    echo ""
    echo "Valid examples:"
    echo "  feat(auth)!: add OAuth2 authentication"
    echo "  fix(api): resolve timeout issue in user endpoint"
    echo "  docs(readme): update installation instructions"
    echo ""
    echo "Your message:"
    echo "  $commit_msg"
    exit 1
fi

echo "✅ Valid commit message"
exit 0