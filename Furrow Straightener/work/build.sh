#!/bin/bash
javac -d . ../src/devoworm/portegys_image_transform/*.java
cp ../res/images/eye.png devoworm/portegys_image_transform
jar cvfm ../bin/portegys_image_transform.jar portegys_image_transform.mf devoworm
