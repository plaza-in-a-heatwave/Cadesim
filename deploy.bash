#!/usr/bin/env bash
# quick script to copy across release builds into the release branch.

# == assumes you have already run the gradle release script. ==
# == assumes you are running the repo under git.

# {}:   prevents file disappearing on checkout: https://stackoverflow.com/a/2358491
# () &: without these windows refuses to checkout original branch (as deploy.bash is locked)
({
    BUILDFLAVOR="$1"

    if [ "$BUILDFLAVOR" == "" ]; then
        echo "usage: $(basename "$0") buildflavor"
        exit 0
    fi

    ROOT="$(realpath "$(dirname "$0")")"

    # 0. save current branch
    CURRENTBRANCH="$(git branch --show-current)"

    # 1. change to release branch
    if ! git checkout release --; then
        echo "FATAL ERROR can't checkout release. quitting."
        exit 2
    fi

    # 2. add latest to BUILDFLAVOR dir
    rm -r "$ROOT"/"$BUILDFLAVOR"
    mkdir "$ROOT"/"$BUILDFLAVOR"
    cp -r release/* "$ROOT"/"$BUILDFLAVOR"/

    # 3. apply git
    git pull
    git add "$ROOT"/"$BUILDFLAVOR"/
    git commit -m "automatically generated release for $BUILDFLAVOR on $(date --iso-8601=s)"
    git push

    # 4. change back
    git checkout "$CURRENTBRANCH"

    exit 0
}) &