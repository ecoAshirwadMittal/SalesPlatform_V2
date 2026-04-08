# Java Action: ExecuteParameterizedStatement

> For a more detailed documentation, please visit the website at https://docs.mendix.com/appstore/connectors/database-connector This Java action provides a consistent environment for Mendix projects to perform arbitrary parameterized SQL statements on external relational databases. The statement text may contain placeholders to be filled in with values directly from Mendix. Do not use this Java action for SELECT queries. This action returns the number of affected rows. The JDBC drivers for the databases you want to connect to must be placed inside the userlib directory of your project. Note: While the text parameters are properly escaped, proper security is still required when manually composing the parameterized template text to avoid SQL injection. @param jdbcUrl A database JDBC URL address that points to your database. @param userName The user name for logging into the database. @param password The password for logging into the database. @param sql A string template containing the SQL statement to be performed and the statement parameters. @return Number of affected rows.

**Returns:** `Integer`

## Parameters

| Name | Type | Required |
|---|---|---|
| `jdbcUrl` | String | ✅ |
| `userName` | String | ✅ |
| `password` | String | ✅ |
| `sql` | Unknown | ✅ |
