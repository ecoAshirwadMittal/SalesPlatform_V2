# Constant: OverrideServiceType

> Overrides the service type to use. The applicable values are: Local, Cloud, Private or empty (default). When empty, the module will automatically determine the service type based on the operating system (Local for Windows/Mac, Cloud for other operating systems). 1) Local = Uses the local service that is packaged with the PDF Document Generation module. The local service is executed on the same machine as the Mendix runtime for every document request, and is terminated afterwards. 2) Cloud = Uses the public PDF document generation service on Mendix Public Platform 3) Private = Uses a private PDF document generation service, hosted by the customer

| Property | Value |
|---|---|
| **Type** | `String` |
| **Default Value** | `(empty)` |
| **Exposed to Client** | ❌ |
