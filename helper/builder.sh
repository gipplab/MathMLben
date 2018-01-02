#!/bin/bash

EXTENSION=".json"
for i in {301..330}
do
	name=$i$EXTENSION
	touch $name
	echo '{}' >> $name
done
exit 0

