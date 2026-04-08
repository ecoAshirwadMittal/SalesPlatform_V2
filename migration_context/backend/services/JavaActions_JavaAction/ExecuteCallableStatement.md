# Java Action: ExecuteCallableStatement

> For a more detailed documentation, please visit the website at https://docs.mendix.com/appstore/connectors/database-connector This Java action provides a consistent environment for Mendix projects to call an arbitrary statement on external relational databases. This action requires an instance of the Statement NPE (Non-Persistable Entity) with associated NPEs of its Parameters. Input parameters need to be filled in before calling this action and output parameters will be filled in afterwards. The JDBC drivers for the databases you want to connect to must be placed inside the userlib directory of your project. Note: Proper security is required when manually composing the statement text to avoid SQL injection. @param jdbcUrl A database JDBC URL address that points to your database. @param userName The user name for logging into the database. @param password The password for logging into the database. @param statement An instance of the Statement NPE containing both the content of the statement to be called as well as all of its parameters.

**Returns:** `Void`

## Parameters

| Name | Type | Required |
|---|---|---|
| `jdbcUrl` | String | ✅ |
| `userName` | String | ✅ |
| `password` | String | ✅ |
| `statement` | ConcreteEntity | ✅ |
