#!/bin/sh

##### BEFORE LAUNCHING THIS SCRIPT, CHECK IF CONFIGURATION FILES DOES NOT CHANGE! #####

if [ $# = 0 ]
then
    echo "Invalid number of parameters"
    echo "Usage: ./compile.sh TAG"
    echo "TAG is the tag name of the build to process."
    exit
fi

# Constants
root=$(pwd)
server=${root}/vepserver
resources=${server}/src/main/resources
web=${root}/vepweb/web
package=${resources}/packages/vepweb

### First of all, we get the given tag branch ###

# path= /
git pull origin master
git pull
git checkout tags/$1


### We compile all development files to production file ###
# path= /${root}/vepweb
cd ${root}/vepweb
pub get
dart2js -m -o ${web}/main.js ${web}/main.dart

# path= /vepweb/web/public/styles
cd ${web}/public/styles
sass -t compressed sass/main.scss styles.css


### Then we move all sources from dart project to server resources ###

# path= /vepweb/web
cd ${web}
find . -type f -exec cp --parents {} ${resources} \;

# path= /vepweb/lib
cd ${root}/vepweb/lib
mkdir -p ${package}
find component -name *.html -type f -exec cp --parents {} ${package} \;


### Onced moved, we can remove development files which have been copied ###

# path=/vepserver/src/main/resources/public/styles
cd ${resources}/public/styles
rm -rf .sass-cache
rm -rf sass
rm styles.css.map

# path=/vepserver/src/main/resources
cd ${resources}
mv index.production.html index.html
rm index.*.html

# We use main.js file, main.dart file is no longer needed.
rm main.dart

### We set the build value to sbt file ###

sed 's/v0.0.0/'${1}'/g' a.txt > a.txt

### We can compile and launch the server ###

cd ${root}/vepserver
sbt assembly run