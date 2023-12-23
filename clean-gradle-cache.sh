find .gradle -type f -name "*.lock" | while read f; do rm $f; done
find ~/.gradle -type f -name "*.lock" | while read f; do rm $f; done
