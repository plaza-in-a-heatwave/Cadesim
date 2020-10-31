#!/usr/bin/env bash
# quick script to copy across release builds into the release branch.

# == assumes you have already run the gradle release script. ==
# == assumes you are running the repo under git.

# {}:   prevents file disappearing on checkout: https://stackoverflow.com/a/2358491
# () &: without these windows refuses to checkout original branch (as deploy-growup.bash is locked)
({
    ROOT="$(realpath "$(dirname "$0")")"

    # 0. save current branch
    CURRENTBRANCH="$(git branch --show-current)"

    # 1. change to release branch
    if ! git checkout release --; then
        echo "FATAL ERROR can't checkout release. quitting."
        exit
    fi

    # 2. add latest to growup dir
    rm -r "$ROOT"/growup
    mkdir "$ROOT"/growup
    cp -r release/* "$ROOT"/growup/

    # 3. apply git
    git add "$ROOT"/growup/
    git commit -m "release-$(date --iso-8601=s)"
    git push

    # 4. change back
    git checkout "$CURRENTBRANCH"

    exit
}) &