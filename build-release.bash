#!/usr/bin/env bash

# build a release version of cadesim.

# project dirs
ROOT="$(realpath "$(dirname "$0")")"
CLIENT="$ROOT"/client-launcher
SERVER="$ROOT"/server

# all releases here
BUILDPREFIX="$ROOT"/release

# releases for client/server
CLIENTBUILDDIR="$BUILDPREFIX"/client
SERVERBUILDDIR="$BUILDPREFIX"/server

# http served releases. these will not be packaged.
CLIENTHTTPDIR="$CLIENTBUILDDIR"/http
SERVERHTTPDIR="$SERVERBUILDDIR"/http

# releases that can be distributed direct to users. These will be packaged.
# TODO: add MSI, deb package installers here if wanted.
CLIENTUSERDIR="$CLIENTBUILDDIR"/user
SERVERUSERDIR="$SERVERBUILDDIR"/user

# zip: one package option for user
CLIENTZIPNAME="$CLIENTUSERDIR"/cadesim-client.zip
SERVERZIPNAME="$SERVERUSERDIR"/cadesim-server.zip

# change working directory to cadesim
pushd "$ROOT">/dev/null
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

echo "***********************"
echo "* making client...    *"
echo "***********************"
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
pushd "$CLIENT">/dev/null
for file in "${clientfiles[@]}"; do
    cp -r "$file" "$CLIENTBUILDDIR"
done
popd>/dev/null

# process getdown
java -classpath getdown-core-*.jar com.threerings.getdown.tools.Digester "$CLIENTBUILDDIR"
pushd "$CLIENTBUILDDIR">/dev/null
mkdir "$(basename "$CLIENTUSERDIR")" "$(basename "$CLIENTHTTPDIR")"

# generate package for user. If using git bash, follow instructions here:
# https://ranxing.wordpress.com/2016/12/13/add-zip-into-git-bash-on-windows/
# # TODO: add MSI, deb package installers here if wanted.
zip -r "$CLIENTZIPNAME" . -9 -x "$(basename "$CLIENTUSERDIR")" -x "$(basename "$CLIENTHTTPDIR")"

# generate files for http
for f in *; do
    if [ ! "$f" == "$(basename "$CLIENTUSERDIR")" ] && [ ! "$f" == "$(basename "$CLIENTHTTPDIR")" ]; then
        if [ -e "$f" ]; then
            mv "$f" "$(basename "$CLIENTHTTPDIR")"
        else
            # guard against glob not matching
            echo "    WARNING: directory was unexpectedly empty."
        fi
    fi
done
popd>/dev/null
echo "***********************"
echo "* done making client. *"
echo "***********************"

# +-------------+
# | make server |
# +-------------+
echo "***********************"
echo "* making server...    *"
echo "***********************"
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
pushd "$SERVER">/dev/null
for file in "${serverfiles[@]}"; do cp -r "$file" "$SERVERBUILDDIR"; done
popd>/dev/null

# process getdown
java -classpath getdown-core-*.jar com.threerings.getdown.tools.Digester "$SERVERBUILDDIR"
pushd "$SERVERBUILDDIR">/dev/null
mkdir "$(basename "$SERVERUSERDIR")" "$(basename "$SERVERHTTPDIR")"

# generate package for user. If using git bash, follow instructions here:
# https://ranxing.wordpress.com/2016/12/13/add-zip-into-git-bash-on-windows/
# # TODO: add MSI, deb package installers here if wanted.
zip -r "$SERVERZIPNAME" . -9 -x "$(basename "$SERVERUSERDIR")" -x "$(basename "$SERVERHTTPDIR")"

# generate files for http
for f in *; do
    if [ ! "$f" == "$(basename "$SERVERUSERDIR")" ] && [ ! "$f" == "$(basename "$SERVERHTTPDIR")" ]; then
        if [ -e "$f" ]; then
            mv "$f" "$(basename "$SERVERHTTPDIR")"
        else
            # guard against glob not matching
            echo "    WARNING: directory was unexpectedly empty."
        fi
    fi
done
popd>/dev/null
echo "***********************"
echo "* done making server. *"
echo "***********************"

# restore working directory
echo ""
echo ""
echo "***************************************************************************"
echo "release complete, see:"
echo "    $CLIENTBUILDDIR - client releases"
echo "    $SERVERBUILDDIR - server releases".
echo ""
echo "guide to subdirectories:"
echo "    \"http/\" - release onto cdn for auto updates to pull from"
echo "    \"user/\" - release directly to users"
echo "***************************************************************************"
popd>/dev/null
exit 0