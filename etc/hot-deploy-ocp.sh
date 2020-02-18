mvn -f `dirname $0`/.. -DskipTests package
oc cp `dirname $0`/../target/incident-priority-service*.jar `oc get pods -l app=emergency-console -o template --template='{{range .items}}{{.metadata.name}}{{end}}'`:/opt/app-root/src/tmp
oc delete pods -l app=incident-priority-service