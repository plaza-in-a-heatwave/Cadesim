#!/usr/bin/env bash
# quick script to copy across release builds into the release branch.

# == assumes you have already run the gradle release script. ==
# == assumes you are running the repo under git.
ROOT="$(realpath "$(dirname "$0")")"

# 0. save current branch
CURRENTBRANCH="$(git branch --show-current)"

# 1. change to release branch
git checkout release --

# 2. add latest to growup dir
rm -r growup
mkdir growup
cp -r release/* growup/

# 3. apply git
git add growup/
git commit -m "release-$(date --iso-8601=s)"
git push

# 4. change back
git checkout "$CURRENTBRANCH"