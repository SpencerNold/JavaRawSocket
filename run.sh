#! /bin/bash
./gradlew prepare
sudo java -Djava.library.path=prepared/natives -cp prepared/JavaRawSocket.jar me.spencernold.jrs.Main