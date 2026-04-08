# JSON Structure: JSON_ResultSet

## Sample JSON

```json
{
  "resultSetMetaData" : {
    "numRows" : 10000,
    "format" : "jsonv2",
    "partitionInfo" : [ {
      "rowCount" : 2968,
      "uncompressedSize" : 105511,
      "compressedSize" : 19967
    }, {
      "rowCount" : 7032,
      "uncompressedSize" : 247939,
      "compressedSize" : 45918
    } ],
    "rowType" : [ {
      "name" : "AVG_CLOUD_COVER_TOT_PCT",
      "database" : "GLOBAL_WEATHER__CLIMATE_DATA_FOR_BI",
      "schema" : "STANDARD_TILE",
      "table" : "FORECAST_DAY",
      "byteLength" : null,
      "precision" : 3,
      "type" : "fixed",
      "scale" : 0,
      "nullable" : true,
      "collation" : null,
      "length" : null
    }, {
      "name" : "AVG_WIND_SPEED_100M_MPH",
      "database" : "GLOBAL_WEATHER__CLIMATE_DATA_FOR_BI",
      "schema" : "STANDARD_TILE",
      "table" : "FORECAST_DAY",
      "byteLength" : null,
      "precision" : 4,
      "type" : "fixed",
      "scale" : 1,
      "nullable" : true,
      "collation" : null,
      "length" : null
    }, {
      "name" : "MAX_PRESSURE_2M_MB",
      "database" : "GLOBAL_WEATHER__CLIMATE_DATA_FOR_BI",
      "schema" : "STANDARD_TILE",
      "table" : "FORECAST_DAY",
      "byteLength" : null,
      "precision" : 6,
      "type" : "fixed",
      "scale" : 2,
      "nullable" : true,
      "collation" : null,
      "length" : null
    }, {
      "name" : "PROBABILITY_OF_SNOW_PCT",
      "database" : "GLOBAL_WEATHER__CLIMATE_DATA_FOR_BI",
      "schema" : "STANDARD_TILE",
      "table" : "FORECAST_DAY",
      "byteLength" : null,
      "precision" : 3,
      "type" : "fixed",
      "scale" : 0,
      "nullable" : true,
      "collation" : null,
      "length" : null
    }, {
      "name" : "TOT_SNOWFALL_IN",
      "database" : "GLOBAL_WEATHER__CLIMATE_DATA_FOR_BI",
      "schema" : "STANDARD_TILE",
      "table" : "FORECAST_DAY",
      "byteLength" : null,
      "precision" : 4,
      "type" : "fixed",
      "scale" : 2,
      "nullable" : true,
      "collation" : null,
      "length" : null
    } ]
  },
  "data" : [ ["56","9.2","719.00","null","0.00"],
["98","20.4","984.80","null","0.00"],
["46","13.4","1022.80","null","0.00"],
["16","4.7","956.00","null","0.00"],
["100","27.9","977.40","null","0.00"],
["100","24.6","991.30","null","0.00"],
["100","23.9","989.10","null","0.00"],
["96","15.7","991.60","0","0.00"],
["99","8.0","993.50","0","0.00"],
["100","8.7","1001.90","0","0.00"],
["100","9.0","1003.90","0","0.00"],
["33","8.7","1006.50","0","0.00"],
["100","8.0","982.70","0","0.00"],
["100","8.1","1013.10","0","0.00"],
["100","16.9","989.70","0","0.00"],
["95","15.8","976.80","5","0.00"],
["98","19.5","985.30","0","0.00"],
["100","14.5","989.50","0","0.00"],
["100","16.1","985.70","0","0.00"] ],
  "code" : "090001",
  "statementStatusUrl" : "/api/v2/statements/01b3b3f9-0000-dd28-0001-90ba0006d536?requestId=2a4f040a-e667-4ecb-96b0-bcc1d58e1a8b",
  "requestId" : "2a4f040a-e667-4ecb-96b0-bcc1d58e1a8b",
  "sqlState" : "00000",
  "statementHandle" : "01b3b3f9-0000-dd28-0001-90ba0006d536",
  "message" : "Statement executed successfully.",
  "createdOn" : 1713253987175
}
```
