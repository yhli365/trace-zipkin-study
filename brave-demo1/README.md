zipkin为分布式链路调用监控系统，聚合各业务系统调用延迟数据，达到链路调用监控跟踪。

mvn clean
mvn eclipse:clean
mvn eclipse:eclipse
mvn package

--启动zipkin
java -jar zipkin-server-1.13.1-exec.jar
WebUI: http://127.0.0.1:9411/

--启动测试服务
java -jar target/zipkin-brave-demo1-*.jar
java -jar target/zipkin-brave-demo1-*.jar --spring.profiles.active=service2
java -jar target/zipkin-brave-demo1-*.jar --spring.profiles.active=service3
java -jar target/zipkin-brave-demo1-*.jar --spring.profiles.active=service4

--测试
访问以下URL后查看zipkin的WebUI
http://localhost:8080/start
http://localhost:8080/foo
http://localhost:8080/bar
http://localhost:8080/tar

