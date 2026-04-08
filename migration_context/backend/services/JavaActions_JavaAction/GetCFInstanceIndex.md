# Java Action: GetCFInstanceIndex

> Returns the Cloud Foundry Instance Index that is set during deployment of the application in a Cloud native environment. Based on the Cloud Foundry Instance Index, Mendix determines what is the leader instance (index 0 executes scheduled events, db sync, session management etc.) or slave instance. Returns 0 for the leader instance, 1 or higher for slave instances or -1 when the environment variable could not be read (when running locally or on premise). When -1 is returned, it will probably be the leader in the cluster. Make sure emulate cloud security is disabled. Otherwise, the policy restrictions will prevent the method to be executed. Action is tested in Mendix Cloud on 19-12-2018.

**Returns:** `Integer`

