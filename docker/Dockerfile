FROM jboss/wildfly:10.0.0.Final

RUN  mkdir -p dojot/alarms/metamodel
WORKDIR dojot

ADD docker/entrypoint.sh .
ADD ear/target/alarm-manager.ear /opt/jboss/wildfly/standalone/deployments/

EXPOSE 8080

CMD ./entrypoint.sh
