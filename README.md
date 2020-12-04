
git status

git add .

git commit -m '1'

git pull

git push








xs


cd ~/XS_Tongji/ && git pull

cd ~/XS_Tongji/xs-utils/ && mvn install

cd ~/XS_Tongji/xs-server/ && mvn install


xs-op


cd ~/XS_Tongji/xs-op/ && gulp --build

cd ~/XS_Tongji/xs-op/ && mvn clean && mvn package

ps -ef | grep -e 'tomcat-xs-op' | grep -v 'grep' | awk '{print $2}' | xargs -i kill -9 {}

sh ~/tomcat-xs-op/bin/startup.sh && tail -f ~/tomcat-xs-op/logs/catalina.out


xs-web


cd ~/XS_Tongji/xs-web/ && gulp --build

cd ~/XS_Tongji/xs-web/ && mvn clean && mvn package

ps -ef | grep -e 'tomcat-xs-web' | grep -v 'grep' | awk '{print $2}' | xargs -i kill -9 {}

sh ~/tomcat-xs-web/bin/startup.sh && tail -f ~/tomcat-xs-web/logs/catalina.out


xs-tj

cd ~/XS_Tongji/xs-tj/ && mvn clean && mvn package

ps -ef | grep -e 'tomcat-xs-tj' | grep -v 'grep' | awk '{print $2}' | xargs -i kill -9 {}

sh ~/tomcat-xs-tj/bin/startup.sh && tail -f ~/tomcat-xs-tj/logs/catalina.out




