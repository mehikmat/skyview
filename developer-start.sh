#!/bin/sh
#
# ---------------------------------------------------------------------
# skyview startup script.
# ---------------------------------------------------------------------
#
if [ ! -z "$M2_HOME" ] && [ ! -z "$JAVA_HOME" ]; then
	echo "[INFO:] Found Maven at $M2_HOME**********************"
	echo "[INFO:] Found Java at $JAVA_HOME***********************"
	echo "[INFO:] Cleaning up**************************"
	"$M2_HOME"/bin/mvn clean
	echo "[INFO:] Compiling and packagin***********************"
	"$M2_HOME"/bin/mvn package
	echo "[INFO:] Runnign SkyView********************************"
	"$JAVA_HOME"/bin/java -jar ./target/skyview-1.0.jar
else
        echo "[ERROR:] JAVA_HOME or M2_HOME is not set!!! Please install them first"
fi
 
