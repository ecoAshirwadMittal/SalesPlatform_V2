# JSON Structure: JSON_EcoATMSetting

## Sample JSON

```json
{
 "environments": [
  {
   "name": "DEV",
   "comment": "configuration use for the dev environment",
   "url": "",
   "isDefault": false
  },
  {
   "name": "LOCALHOST",
   "comment": "configuration used normally per default for the local environment (laptop dev)",
   "url": "https://localhost",
   "isDefault": true
  }
 ],
 "configurations": [
  {
   "filename": "oracleAccess.json",
   "isPerEnv": true,
   "microflow": "module.name"
  },
  {
   "filename": "oracleErrorMessages.json",
   "isPerEnv": false,
   "microflow": "module.name"
  }
 ]
}
```
