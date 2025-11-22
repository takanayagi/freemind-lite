#!/bin/bash
topdir=$(cd ${BASH_SOURCE[0]%/*} && pwd)
java -jar "${topdir}/lib/freemind.jar" "$@"
