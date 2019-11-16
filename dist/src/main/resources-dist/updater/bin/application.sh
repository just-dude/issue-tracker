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
SERVER_DIR=`cd "$SCRIPT_HOME/../server" >/dev/null; pwd`

java -jar -Dapp.dir=$SCRIPT_HOME $SCRIPT_HOME/lib/update.jar --server.config=$SERVER_DIR/config/application.yml --db.root.username=postgres --db.root.password=postgres