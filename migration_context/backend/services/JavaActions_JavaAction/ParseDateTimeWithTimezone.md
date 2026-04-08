# Java Action: ParseDateTimeWithTimezone

> This method parses a date from a string with a given pattern according to a specific timezone. The timezone has to be a valid timezone id http://docs.oracle.com/javase/7/docs/api/java/util/TimeZone.html (e.g. one of https://garygregory.wordpress.com/2013/06/18/what-are-the-java-timezone-ids/)

**Returns:** `DateTime`

## Parameters

| Name | Type | Required |
|---|---|---|
| `date` | String | ✅ |
| `pattern` | String | ✅ |
| `timeZone` | String | ✅ |
| `defaultValue` | DateTime | ✅ |
