#!/usr/bin/env bash
# export to jar in . after project built in bin

# constants
NAME="cadesim-server.jar"

# extract deps into bin temporarily
while read line; do
cd bin
cp "$line" .
jar xf "$line"
rm "$(basename "$line")"
cd ..
done < "jar.deps"

# zip jar
jar cvfm "$NAME" ./src/META-INF/MANIFEST.MF -C ./bin/ .

# cleanup bin
cd bin
for file in ./*; do
    if [[ "$file" == "./com" ]] || [[ "$file" == "./META-INF" ]]; then
        continue
    else
        rm -r "$file"
    fi
done
cd ..

echo "jar exported to $(pwd)/$NAME"