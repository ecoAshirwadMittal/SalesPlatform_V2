# Java Action: ExecuteParameterizedQuery

> For a more detailed documentation, please visit the website at https://docs.mendix.com/appstore/connectors/database-connector This Java action provides a consistent environment for Mendix projects to perform an arbitrary parameterized SELECT SQL query on external relational databases. Do not use this Java action for INSERT, UPDATE, DELETE or DDL queries. This action returns a list of Mendix objects based on the JDBC result set. The JDBC drivers for the databases you want to connect to must be placed inside the userlib directory of your project. Note: Proper security is required when manually composing the query text to avoid SQL injection. @param jdbcUrl A database JDBC URL address that points to your database. @param userName The user name for logging into the database. @param password The password for logging into the database. @param sql A string template containing the SELECT query to be performed and its query parameters. @param resultObjectType A fully qualified name for the result object type. @return Result of the query as a list of mendix objects.

**Returns:** `List`

## Parameters

| Name | Type | Required |
|---|---|---|
| `jdbcUrl` | String | ✅ |
| `userName` | String | ✅ |
| `password` | String | ✅ |
| `sql` | Unknown | ✅ |
| `resultObjectType` | Unknown | ✅ |
