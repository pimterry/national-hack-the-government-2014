#!/bin/bash

if [ "$IS_DATA_SERVER" == "true" ]; then
  java $JVM_OPTS -cp target/helloworld-standalone.jar clojure.main -m hello.world $PORT
else
  cd static-server
  node server.js
fi
