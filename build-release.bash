#!/usr/bin/env bash
echo "this script will create a release version of cadesim."
echo ""
echo ""
echo "*********************************"
echo "* build-release.bash running... *"
echo "*                               *"
echo "* make sure environment has:    *"
echo "*    grep, cut, zip, java       *"
echo "*                               *"
echo "* please exit the release dir   *"
echo "* before creating the release.  *"
echo "*********************************"

# jars can have different names when released for compatibility
CLIENTJARNAME="cadesim.jar"        # also remember to change this in getdown.txt
SERVERJARNAME="cadesim-server.jar"

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
echo "using build prefix: $BUILDPREFIX"
if [ -d "$BUILDPREFIX" ]; then
    rm -r "$BUILDPREFIX"
    echo "  build prefix already exists, removing it"
fi
if ! mkdir -p "$CLIENTBUILDDIR" "$SERVERBUILDDIR"; then
    echo "failed making $CLIENTBUILDDIR and/or $SERVERBUILDDIR".
    exit 1
else
    echo "making client in: $CLIENTBUILDDIR"
    echo "making server in: $SERVERBUILDDIR"
fi

# +-------------+
# | make client |
# +-------------+
echo ""
echo ""
echo "***********************"
echo "* making client...    *"
echo "***********************"

pushd "$CLIENT">/dev/null
grep "version = " "getdown.txt" | cut -d" " -f 3 >"version.txt" # update version.txt to match getdown.txt

declare -a clientfiles=(
    "bg.png"
    "getdown.jar"
    "getdown.txt"
    "getdown-bg.png"
    "growup.ico"
    "growup.png"
    "user.config"
    "version.txt"
)
for file in "${clientfiles[@]}"; do
    cp -r "$file" "$CLIENTBUILDDIR"
done
# move this separately, it's huge. from build task.
mv build/libs/client-launcher*.jar "$CLIENTBUILDDIR"/"$CLIENTJARNAME"
rm "version.txt" # temporary build file
popd>/dev/null

# process getdown
java -classpath getdown-core-*.jar com.threerings.getdown.tools.Digester "$CLIENTBUILDDIR"
pushd "$CLIENTBUILDDIR">/dev/null
mkdir "$(basename "$CLIENTUSERDIR")" "$(basename "$CLIENTHTTPDIR")"

# generate package for user. If using git bash, follow instructions here:
# https://ranxing.wordpress.com/2016/12/13/add-zip-into-git-bash-on-windows/
# # TODO: add MSI, deb package installers here if wanted.
zip -r "$CLIENTZIPNAME" . -9 --exclude "/$(basename "$CLIENTUSERDIR")/*" --exclude "/$(basename "$CLIENTHTTPDIR")/*" --exclude "digest.txt" --exclude "digest2.txt" --exclude "version.txt">/dev/null

# generate files for http
for f in *; do
    if [ ! "$f" == "$(basename "$CLIENTUSERDIR")" ] && [ ! "$f" == "$(basename "$CLIENTHTTPDIR")" ]; then
        if [ -e "$f" ]; then
            mv "$f" "$(basename "$CLIENTHTTPDIR")"
        else
            # guard against glob not matching
            echo "    ERROR: directory was unexpectedly empty."
            exit 2
        fi
    fi
done
popd>/dev/null
echo "done making client."

# +-------------+
# | make server |
# +-------------+
echo ""
echo ""
echo "***********************"
echo "* making server...    *"
echo "***********************"

pushd "$SERVER">/dev/null

declare -a serverfiles=(
    "maps"
    "bg.png"
    "getdown.jar"
    "getdown.txt"
    "getdown-bg.png"
    "growup.ico"
    "growup.png"
    "start_servers.py"
    "stop_servers.py"
)
for file in "${serverfiles[@]}"; do
    cp -r "$file" "$SERVERBUILDDIR";
done
# move this separately, it's huge. from build task.
mv build/libs/server*.jar "$SERVERBUILDDIR"/"$SERVERJARNAME"
popd>/dev/null

# process getdown
java -classpath getdown-core-*.jar com.threerings.getdown.tools.Digester "$SERVERBUILDDIR"
pushd "$SERVERBUILDDIR">/dev/null
mkdir "$(basename "$SERVERUSERDIR")" "$(basename "$SERVERHTTPDIR")"

# generate package for user. If using git bash, follow instructions here:
# https://ranxing.wordpress.com/2016/12/13/add-zip-into-git-bash-on-windows/
# # TODO: add MSI, deb package installers here if wanted.
zip -r "$SERVERZIPNAME" . -9 --exclude "/$(basename "$SERVERUSERDIR")/*" --exclude "/$(basename "$SERVERHTTPDIR")/*" --exclude "digest.txt" --exclude "digest2.txt">/dev/null

# generate files for http
for f in *; do
    if [ ! "$f" == "$(basename "$SERVERUSERDIR")" ] && [ ! "$f" == "$(basename "$SERVERHTTPDIR")" ]; then
        if [ -e "$f" ]; then
            mv "$f" "$(basename "$SERVERHTTPDIR")"
        else
            # guard against glob not matching
            echo "    ERROR: directory was unexpectedly empty."
            exit 2
        fi
    fi
done
popd>/dev/null
echo "done making server."

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