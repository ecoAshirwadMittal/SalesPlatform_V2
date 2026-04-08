# Domain Model

## Entities

### 📦 PrivateKey
> The private key file is a .p8 file which can be decrypted with its passphrase. It is used to generate a JWT which can then be verified by a different party using a public token that has been generated based on the private key.

**Extends:** `System.FileDocument`

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Passphrase` | StringAttribute | - | - | Passphrase which is used to encode/decode the priv |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SnowflakeRESTSQL.Administrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 JWT
> This non-persistents entity is used to display a generated JWT for the administrator so they can test whether the token generation works.

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Token` | StringAttribute | - | - | Value of the JSON Web Token as a string |
| `ExpirationDate` | DateTimeAttribute | - | [%CurrentDateTime%] | Expiration date of the JSON Web Token |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SnowflakeRESTSQL.Administrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 ConnectionDetails
> This entity holds all the information which is needed to connect to snowflake. Attributes which are specific to a statement such as the database, a schema and a warehouse are not part of this. It is possible to create multiple connections and select a different one per statement. To be able to use the Role Base Authentication with Key pair authentication the Connection details need to be connected with the Account

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Name` | StringAttribute | - | - | Identifier of the connection inside of the Mendix  |
| `AccountURL` | StringAttribute | - | - | The unique account URL of the Snowflake account wi |
| `ResourcePath` | StringAttribute | - | - | Path to a resource on Snowflake API i.e. /api/v2/s |
| `AccountIdentifier` | StringAttribute | - | - | It is the unique account identifier that identifie |
| `Username` | StringAttribute | - | - | It is the username with which you sign in to your  |
| `AuthenticationType` | Enum(`SnowflakeRESTSQL.ENUM_AuthenticationType`) | - | - | Authentication type enumeration included the suppo |

#### Access Rules

| Roles | Create | Read | Write | Delete | XPath |
|---|---|---|---|---|---|
| SnowflakeRESTSQL.Administrator | ❌ | ✅ | ✅ | ❌ | - |

### 📦 Statement
> This entity holds all request-specific information such as the role which shall be used to conduct the request and the database in which the referenced tables can be found. You can find more information about the request body here: https://docs.snowflake.com/en/developer-guide/sql-api/submitting-requests#example-of-a-request

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `SQLStatement` | StringAttribute | - | - | The SQL statement to execute. |
| `Timeout` | IntegerAttribute | - | 45 | The amount of seconds after which the connection w |
| `Database` | StringAttribute | - | - | The database to use. |
| `Schema` | StringAttribute | - | - | The database schema to use, for example 'PUBLIC'. |
| `Warehouse` | StringAttribute | - | - | The warehouse to use for computations. |
| `Role` | StringAttribute | - | - | The role to use to execute the SQL statement (pref |

### 📦 ResultSet
> If the POST request to the statement API has been exceuted successfully, an http response object is being returned. This response can me imported into a ResultSet object which holds more information about the different partitions in which the results have been split and where to retrieve them. More information about his can be found here: https://docs.snowflake.com/en/developer-guide/sql-api/handling-responses

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `Code` | StringAttribute | - | - | Code that is returned from Snowflake as a response |
| `StatementHandle` | StringAttribute | - | - | Unique handle given to the statement that has been |
| `Message` | StringAttribute | - | - | Message that is returned from Snowflake as a respo |
| `NumRows` | IntegerAttribute | - | - | The amount of rows which will be returned by the e |

### 📦 PartitionInfo
> A partition is a part of the results which has been stored as its own object for performance reasons. The results are usually split into a list of partitions. This entity holds information about one particular partition. You can find more information about the partitions here: https://docs.snowflake.com/en/developer-guide/sql-api/handling-responses#getting-the-results-from-the-response

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `RowCount` | IntegerAttribute | - | - | This attribute holds information about the number  |

### 📦 ExampleObject
> Example output object of TransformHttpRequestToObjectList

#### Attributes

| Name | Type | Length | Default | Documentation |
|---|---|---|---|---|
| `ATTR_TXT` | StringAttribute | 200 | - | Example string attribute |
| `ATTR_INT` | IntegerAttribute | - | - | Example integer attribute |
| `ATTR_LONG` | LongAttribute | - | - | Example long attribute |
| `ATTR_BOOL` | BooleanAttribute | - | false | Example boolean attribute |
| `ATTR_DECI` | DecimalAttribute | - | - | Example decimal attribute |
| `ATTR_ENUM` | Enum(`SnowflakeRESTSQL.ENUM_Example`) | - | - | Example enumeration attribute |
| `ParsedDate` | DateTimeAttribute | - | - | Example parsed date attribute (after java action e |

## Associations

| Name | Parent | Child | Type | Owner | Delete |
|---|---|---|---|---|---|
| `ConnectionDetails_PrivateKey` | 56143aac-af0b-4aad-847a-b911f0c509f6 | 1f98fac7-b2ab-468a-8b0c-5856eae10284 | Reference | Both | DeleteMeButKeepReferences |
| `JWT_ConnectionDetails` | bed67bef-5169-4217-9224-fafa6087bb40 | 56143aac-af0b-4aad-847a-b911f0c509f6 | Reference | Default | DeleteMeButKeepReferences |
| `PartitionInfo_ResultSet` | 8d976b99-db5a-4b31-a605-dac75b73988d | eb5d36c8-b401-4ec9-a2c0-e62a39501eac | Reference | Default | DeleteMeButKeepReferences |
