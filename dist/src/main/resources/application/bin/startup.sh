# resolve links - $0 may be a softlink
SCRIPT_PATH="$0"

while [ -h "$SCRIPT_PATH" ]; do
  ls=`ls -ld "$SCRIPT_PATH"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    SCRIPT_PATH="$link"
  else
    SCRIPT_PATH=`dirname "$SCRIPT_PATH"`/"$link"
  fi
done

# Get standard environment variables
SCRIPT_DIR=`dirname "$SCRIPT_PATH"`
SCRIPT_HOME=`cd "$SCRIPT_DIR/.." >/dev/null; pwd`
PID_PATH=$SCRIPT_HOME/bin/application.pid

java -jar -Dapp.dir=$SCRIPT_HOME -Dpid.path=$PID_PATH \
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 \
-Dlog4j.configurationFile=$SCRIPT_HOME/config/log4j2.xml $SCRIPT_HOME/lib/application.jar --spring.config.location=$SCRIPT_HOME/config/application.yml