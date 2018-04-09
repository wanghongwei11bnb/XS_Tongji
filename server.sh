#!/bin/bash

if [ $1 == "log" ]
then
        tail -100f ~/tomcat-xs-tj/logs/catalina.out
elif [ $1 == "stop" ]
then
        ps -ef | grep -e 'tomcat-xs-tj/bin' | grep -v 'grep' | awk '{print $2}' | xargs -i kill -9 {}
elif [ $1 == "start" ]
then
        sleep 2s
        sh ~/tomcat-xs-tj/bin/startup.sh
        tail -100f ~/tomcat-xs-tj/logs/catalina.out
elif [ $1 == "restart" ]
then
        ps -ef | grep -e 'tomcat-xs-tj/bin' | grep -v 'grep' | awk '{print $2}' | xargs -i kill -9 {}
        sleep 2s
        sh ~/tomcat-xs-tj/bin/startup.sh
        tail -100f ~/tomcat-xs-tj/logs/catalina.out
elif [ $1 == "update" ]
then
        ps -ef | grep -e 'tomcat-xs-tj/bin' | grep -v 'grep' | awk '{print $2}' | xargs -i kill -9 {}
        cd ~/XS_Tongji/
        git pull
        cd ~/XS_Tongji/xs-utils/
        mvn install
        cd ~/XS_Tongji/xs-tj/
        mvn clean package
        cd
        sleep 2s
        sh ~/tomcat-xs-tj/bin/startup.sh
        tail -100f ~/tomcat-xs-tj/logs/catalina.out
else
        echo "参数错误：log/stop|start|restart|deploy"
fi
