#! /bin/bash

docker run --rm -u $(id -u) -v $(pwd):/user my-splat:latest $@

