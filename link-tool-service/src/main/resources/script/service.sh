#!/bin/bash
if [ $# -lt 1 ];
then
  echo "USAGE: $0 classname opts"
  exit 1
fi
SERVICE_NAME="DeeplinkTool"
JAR_PATH=`ls *.jar`
if [[ ! "$?" == "0" || ! -e $JAR_PATH ]]; then
  echo "Did you forget to execute ./package.sh?"
  exit 2
fi

#选择java jdk版本1.7/1.8并确定版本号
JAVA_VERSION=1.8
BASE_DIR=$(dirname $0)
PID_FILE="$BASE_DIR/../$SERVICE_NAME.pid"
if [ "$JAVA_VERSION" == "1.8"  ]; then
	JDK_PATH=/usr/local/jdk8
else
	JDK_PATH=/usr/local/jdk
fi
KEYWORD="$JAR_PATH"


function test_java_version() {
	java_version=`$1/java -version 2>&1 |awk 'NR==1{ print $3  }'|sed 's/\"//g'`
	if [[ ! "$java_version" =~ "$JAVA_VERSION" ]];then
		return 1
	fi
	return 0
}

if [ -z "$JAVA_HOME" ]; then
  JAVA_HOME=$JDK_PATH
fi

if ! test_java_version "$JAVA_HOME/bin" ;then

	if [[ ! -d "$JDK_PATH" ]];then
		echo "$JDK_PATH not exist!"
		exit 2
	fi

	if test_java_version "$JDK_PATH/bin"; then
		export JAVA_HOME="$JDK_PATH"
	else
		echo "java version not match!"
		exit 3
	fi
fi

JAVA_OPTS="$JAVA_OPTS -server -Xms4g -Xmx4g -Xmn2048m"
JAVA_OPTS="$JAVA_OPTS -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=70 -XX:+CMSParallelRemarkEnabled -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+CMSClassUnloadingEnabled -XX:SurvivorRatio=8"
JAVA_OPTS="$JAVA_OPTS -XX:PermSize=96m -XX:MaxPermSize=256m -XX:MaxDirectMemorySize=192m"
JAVA_OPTS="$JAVA_OPTS -verbose:gc -Xloggc:${HOME}/gc.log -XX:+PrintGCDetails"
JAVA_OPTS="$JAVA_OPTS -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/admin/logs"
JAVA_OPTS="$JAVA_OPTS -XX:-OmitStackTraceInFastThrow"
JAVA_OPTS="$JAVA_OPTS -Djava.ext.dirs=${JAVA_HOME}/jre/lib/ext"
JAVA_OPTS="$JAVA_OPTS -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector"
JAVA_OPTS="$JAVA_OPTS -jar "
#JAVA_OPTS="$JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,address=9555,server=y,suspend=n"
JAVA="$JAVA_HOME/bin/java"

# Returns 0 when the service is running and sets the variable $pid to the PID.
function getServicePID {
if [ ! -f $PID_FILE ]; then return 1; fi
pid="$(<$PID_FILE)"
checkProcessIsRunning $pid || return 1
return 0;
}
# Returns 0 if the process with PID $1 is running.
function checkProcessIsRunning {
local pid="$1"
ps -ef | grep java | grep $pid | grep "$KEYWORD" | grep -q --binary -F java
if [ $? -ne 0 ]; then
echo "Process $pid is not running or could not be found.";
return 1;
fi
return 0;
}

function startService {
local server_type="$1"
getServicePID
if [ $? -eq 0 ]; then echo "$SERVICE_NAME is already running."; RETVAL=0; return 0; fi
echo -n "Starting $SERVICE_NAME..."
startServiceProcess
if [ $? -ne 0 ]; then RETVAL=1; echo "$SERVICE_NAME start failed, see nohup.log."; return 1; fi
COUNT=0
if [ "$server_type" == "provider" ]; then
# sleep 15
the_result=1
while [ $COUNT -lt 1 ]; do
for (( i=0; i<120; i=i+1)); do
startPortCheck $pid
if [ $? -ne 0 ]; then
echo -e ".\c"
sleep 1;
else
the_result=0
echo "start cost $i s"
break;
fi
done
break
done
else ## assuming standalone dubbo server,we will check if
the_result=1
while [ $COUNT -lt 1 ]; do
for (( i=0; i<120; i=i+1 )) do
STR=`grep "server started" nohup.log`
if [ ! -z "$STR" ]; then
echo "PID=$pid"
echo "Server start OK in $i seconds."
the_result=0
break;
fi
echo -e ".\c"
sleep 1
done
break
done
fi
echo $the_result
RETVAL=$the_result
if [ $the_result -eq 1 ]; then echo "The service is gone or port is unnormal, please check!"; fi
return $RETVAL;
}
function startServiceProcess {
touch $PID_FILE
rm -rf nohup.log
nohup $JAVA $JAVA_OPTS $KEYWORD >> nohup.log 2>&1 & echo $! > $PID_FILE
sleep 0.1
pid="$(<$PID_FILE)"
if checkProcessIsRunning $pid; then :; else
echo "$SERVICE_NAME start failed, see nohup.log."
return 1
fi
return 0;
}
# return 0 if port is released successfully when starting.
function startPortCheck {
if [ ! -f $PID_FILE ]; then
echo "Error:can't find $PID_FILE when starting port check!";
return 1;
fi
pid="$(<$PID_FILE)"
for m in `netstat -lntp 2> /dev/null|grep $pid |awk '{print $4}'|awk -F ':' '{print $NF}'`
do
echo "$SERVICE_NAME is ok and ports as $m"
result=`echo status | /opt/nc/bin/nc -i 1 127.0.0.1 $m | grep OK | wc -l`
if [ $result == 1 ]; then
echo "please wait for some seconds, it's checking the status of dubbo service"
return 0;
fi
done
return 1;
}

function stopService {
getServicePID
if [ $? -ne 0 ]; then echo "$SERVICE_NAME is not running."; RETVAL=0; return 0; fi
processcheck
getPortFile
if [ $? -ne 0 ]; then echo "can't find portFile."; RETVAL=1; fi
echo "Stopping $SERVICE_NAME... "
stopServiceProcess
if [ $? -ne 0 ]; then RETVAL=1; echo "failed."; return 1; fi
for ((i=0; i<10; i++)); do
portReleaseCheck
if [ $? -ne 0 ]; then
echo "the port is not released successfully."
sleep 1
else
echo "all ports are released successfully."
return 0
break;
fi
done
RETVAL=0
return 0;
}
#进程数目以及起服账号检查
function processcheck {
piduser=`ps -ef | grep $pid | awk '{print $1 } '`
pidnum=`ps -ef | grep $pid | grep java | awk '{print $2 } '`
echo -e "==========piduser=$piduser pidnum=$pidnum=======\n"
if [ "$piduser" == "root" ];then
echo -e "WARNING: process using root, please contact SCM!\n"
exit -1
fi
}
function getPortFile {
port=`netstat -lntp 2> /dev/null |egrep "$pid\/"|awk '{print $4}'|awk -F ":" '{print $NF}'`
if [ $? -ne 0 ]; then
echo "WARNING: can't find service port when getPortFile";
return 1;
fi
echo $port
return 0;
}
function stopServiceProcess {
STOP_DATE=`date +%Y%m%d%H%M%S`
kill $pid || return 1
for ((i=0; i<120; i++)); do
checkProcessIsRunning $pid
if [ $? -ne 0 ]; then
rm -f $PID_FILE
return 0
fi
sleep 1
done
echo "\n$SERVICE_NAME did not terminate within 120 seconds, sending SIGKILL..."
kill -s KILL $pid
local killWaitTime=15
for ((i=0; i<10; i++)); do
checkProcessIsRunning $pid
if [ $? -ne 0 ]; then
rm -f $PID_FILE
return 0
fi
sleep 1
done
echo "Error: $SERVICE_NAME could not be stopped within 120 + 10 seconds!"
return 1;
}

# return 0 if all ports are released successfully when stoping.
function portReleaseCheck {
sleep 3
port_check_result=0
for n in $port;do
echo "ports as $n,wait to release"
port_check=`netstat -nltup 2>/dev/null | awk '{print $4}' | awk -F ":" '{if ($NF == "'$n'")print $NF}' | sort -u`
echo "The number of unreleased ports:$port_check"
if [[ $port_check -eq 0 ]]; then
echo "this port:$n is released successfully"
else
echo "this port is not released"
port_check_result=1
return 1
fi
done
return 0
}

function main {
  RETVAL=0
  case "$1" in
    start)
      startService "$2"
      ;;
    stop)
      stopService
      ;;
    restart)
      stopService && startService "$2"
      ;;
    status)
      checkServiceStatus
      ;;
    *)
      echo "Usage: $0 {start|stop|restart|status} {provider}"
      exit 1
      ;;
  esac
  exit $RETVAL
}

main "$1" "$2"
