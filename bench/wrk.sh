#!/bin/bash

./wrk/wrk -t12 -c500 -d60s $1
