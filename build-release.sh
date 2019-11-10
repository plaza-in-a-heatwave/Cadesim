#!/usr/bin/env bash
d=cadesim-server_"$1"
mkdir "$d"
./export-to-jar.sh
cp "cadesim-server.jar" "$d"
cp -r maps "$d"
cp -r logs "$d"
cp LICENSE "$d"
tar -cjvf "$d".tar.bz2 "$d"
rm -r "$d"