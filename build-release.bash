#!/usr/bin/env bash

# build a release version of cadesim.
BUILDPREFIX="build"
CLIENTBUILDDIR="$BUILDPREFIX"/client
SERVERBUILDDIR="$BUILDPREFIX"/server

CLIENTZIPNAME="cadesim-client.zip"
SERVERZIPNAME="cadesim-server.zip"

# change working directory to cadesim
pushd $(dirname "$0")>/dev/null
echo "using root directory $(pwd)"

# make build structure

if [ -d "$BUILDPREFIX" ]; then
    rm -r "$BUILDPREFIX"
    echo "$BUILDPREFIX existed, removed it"
fi
if ! mkdir -p "$CLIENTBUILDDIR" "$SERVERBUILDDIR"; then
    echo "failed making $CLIENTBUILDDIR and/or $SERVERBUILDDIR".
    exit 1
else
    echo "made $CLIENTBUILDDIR and $SERVERBUILDDIR"
fi

# +-------------+
# | make client |
# +-------------+

echo "making client..."
declare -a clientfiles=(
    "bg.png"
    "cadesim-client.jar"
    "getdown.jar"
    "getdown.txt"
    "getdown-bg.png"
    "growup.ico"
    "growup.png"
    "user.config"
    "version.txt"
)
pushd client-launcher>/dev/null
for file in "${clientfiles[@]}"; do cp -r "$file" ../"$CLIENTBUILDDIR"; done
popd>/dev/null

# zip and cleanup. If using git bash, follow instructions here:
# https://ranxing.wordpress.com/2016/12/13/add-zip-into-git-bash-on-windows/
pushd "$CLIENTBUILDDIR">/dev/null
zip -r "$CLIENTZIPNAME" . -9
for f in *; do # delete original files, leave new zip
    if [ ! "$f" == "$CLIENTZIPNAME" ]; then
        if [ -d "$f" ]; then
            rm -r "$f"
        elif [ -f "$f" ]; then
            rm "$f"
        else
            # guard against glob not matching
            echo "    WARNING: directory was unexpectedly empty."
        fi
    fi
done
popd>/dev/null
echo "done making client."

# +-------------+
# | make server |
# +-------------+
echo "making server..."
declare -a serverfiles=(
    "maps"
    "bg.png"
    "cadesim-server.jar"
    "getdown.jar"
    "getdown.txt"
    "getdown-bg.png"
    "growup.ico"
    "growup.png"
)
pushd server>/dev/null
for file in "${serverfiles[@]}"; do cp -r "$file" ../"$SERVERBUILDDIR"; done
popd>/dev/null

# zip. If using git bash, follow instructions here:
# https://ranxing.wordpress.com/2016/12/13/add-zip-into-git-bash-on-windows/
pushd "$SERVERBUILDDIR">/dev/null
zip -r "$SERVERZIPNAME" . -9
for f in *; do # delete original files, leave new zip
    if [ ! "$f" == "$SERVERZIPNAME" ]; then
        if [ -d "$f" ]; then
            rm -r "$f"
        elif [ -f "$f" ]; then 
            rm "$f"
        else
            # guard against glob not matching
            echo "    WARNING: directory was unexpectedly empty."
        fi
    fi
done
popd>/dev/null
echo "done making server."

# restore working directory
echo "build release complete, see $CLIENTBUILDDIR and $SERVERBUILDDIR".
popd>/dev/null
exit 0