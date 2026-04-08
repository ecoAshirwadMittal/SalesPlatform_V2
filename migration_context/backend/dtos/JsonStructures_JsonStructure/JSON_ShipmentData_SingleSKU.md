# JSON Structure: JSON_ShipmentData_SingleSKU

## Sample JSON

```json
{
  "shipment": {
    "id": "281",
    "businessUnit": "ECOATM",
    "tradingPartner": "22959",
    "type": "Outbound Shipment",
    "facility": "Louisville Facility",
    "createdBy": "tasneem.amina",
    "shipmentNumber": "281",
    "shipmentStatus": "Shipped",
    "shipVia": "FedEx Ground",
    "shipVendor": "FEDEX2",
    "actualShipDate": "2025-07-28T18:22:51-05:00",
    "billToAddress": {
      "name": "",
      "addressLine1": "",
      "addressLine2": "",
      "addressLine3": "",
      "addressLine4": "",
      "city": "",
      "stateProvinceCode": "",
      "postalCode": "",
      "countryCode": ""
    },
    "shipToAddress": {
      "name": "Receiving",
      "contactName": "Manager",
      "email": "hamzahmousa04@gmail.com",
      "phone": "0000000000",
      "addressLine1": "8514 Major Avenue",
      "addressLine2": "  ",
      "addressLine3": "",
      "addressLine4": "",
      "city": "Morton Grove",
      "stateProvinceCode": "IL",
      "postalCode": "60053",
      "countryCode": "US"
    },
    "shipFromAddress": {
      "attention": "Halea Bailey",
      "phone": "5555555555",
      "addressLine1": "7213 Global Dr",
      "city": "Louisville",
      "stateProvinceCode": "KY",
      "postalCode": "40258",
      "countryCode": "US"
    },
    "notes": null,
    "orders": {
      "order": {
        "orderNumber": "5002395",
        "customerOrderNumber": "PWS-2295925062",
        "orderType": "Sales Order",
        "orderStatus": "Complete",
        "shippingStatus": "20",
        "orderSource": "PWS"
      }
    },
    "lines": {
      "line": {
        "lineNumber": "5002395-1",
        "lineStatus": "Complete",
        "customerLineNumber": "1",
        "shipmentLineNumber": "281--1",
        "itemNumber": "PWS10013473",
        "productCategory": "CellPhone",
        "orderPackQuantity": "1.0",
        "shippedPackQuantity": "1.0",
        "serialNumber": "350686384483675",
        "trackingNumber": "391515107176",
        "pack": {
          "type": "Each",
          "quantity": "1",
          "dimension": {
            "@nil": "true"
          },
          "upcs": {
            "@nil": "true"
          },
          "customMappings": {
            "@nil": "true"
          }
        },
"line": [
                {
                    "lineNumber": "5002407-1",
                    "lineStatus": "Complete",
                    "customerLineNumber": "1",
                    "shipmentLineNumber": "296--1",
                    "itemNumber": "PWS000330855471-WMS1",
                    "productCategory": "CellPhone",
                    "orderPackQuantity": "2.0",
                    "shippedPackQuantity": "1.0",
                    "serialNumber": "98765432103215",
                    "trackingNumber": "391596590792",
                    "pack": {
                        "type": "Each",
                        "quantity": "1",
                        "dimension": {
                            "@nil": "true"
                        },
                        "upcs": {
                            "@nil": "true"
                        },
                        "customMappings": {
                            "@nil": "true"
                        }
                    },
                    "container": {
                        "lpn": "PWS_Order-250729_10",
                        "number": "PWS_Order-250729_10",
                        "trackingNumber": "391596590792",
                        "status": "Shipped",
                        "description": "PWS_Order-250729_10 is packed into PWS_Order-250729_10",
                        "dimension": {
                            "length": "5.0",
                            "width": "7.0",
                            "height": "3.0",
                            "volume": "0.0",
                            "units": "IN"
                        },
                        "weight": {
                            "weight": "1.96",
                            "units": "LB"
                        },
                        "totalCharges": "0.0",
                        "referenceNumber1": "250730-1501-92",
                        "depthUOM": "Inch",
                        "type": "Shipping"
                    },
                    "inventoryAttribute1": "98765432103215"
                },
                {
                    "lineNumber": "5002407-1",
                    "lineStatus": "Complete",
                    "customerLineNumber": "1",
                    "shipmentLineNumber": "296--2",
                    "itemNumber": "PWS000330855471-WMS1",
                    "productCategory": "CellPhone",
                    "orderPackQuantity": "2.0",
                    "shippedPackQuantity": "1.0",
                    "serialNumber": "98765432103216",
                    "trackingNumber": "391596590792",
                    "pack": {
                        "type": "Each",
                        "quantity": "1",
                        "dimension": {
                            "@nil": "true"
                        },
                        "upcs": {
                            "@nil": "true"
                        },
                        "customMappings": {
                            "@nil": "true"
                        }
                    },
                    "container": {
                        "lpn": "PWS_Order-250729_10",
                        "number": "PWS_Order-250729_10",
                        "trackingNumber": "391596590792",
                        "status": "Shipped",
                        "description": "PWS_Order-250729_10 is packed into PWS_Order-250729_10",
                        "dimension": {
                            "length": "5.0",
                            "width": "7.0",
                            "height": "3.0",
                            "volume": "0.0",
                            "units": "IN"
                        },
                        "weight": {
                            "weight": "1.96",
                            "units": "LB"
                        },
                        "totalCharges": "0.0",
                        "referenceNumber1": "250730-1501-92",
                        "depthUOM": "Inch",
                        "type": "Shipping"
                    },
                    "inventoryAttribute1": "89568596996689"
                },
                {
                    "lineNumber": "5002407-2",
                    "lineStatus": "Complete",
                    "customerLineNumber": "2",
                    "shipmentLineNumber": "296--3",
                    "itemNumber": "PWS014844808083",
                    "productCategory": "CellPhone",
                    "orderPackQuantity": "2.0",
                    "shippedPackQuantity": "1.0",
                    "serialNumber": "354872888261744",
                    "trackingNumber": "391596590792",
                    "pack": {
                        "type": "Each",
                        "quantity": "1",
                        "dimension": {
                            "@nil": "true"
                        },
                        "upcs": {
                            "@nil": "true"
                        },
                        "customMappings": {
                            "@nil": "true"
                        }
                    },
                    "container": {
                        "lpn": "PWS_Order-250729_10",
                        "number": "PWS_Order-250729_10",
                        "trackingNumber": "391596590792",
                        "status": "Shipped",
                        "description": "PWS_Order-250729_10 is packed into PWS_Order-250729_10",
                        "dimension": {
                            "length": "5.0",
                            "width": "7.0",
                            "height": "3.0",
                            "volume": "0.0",
                            "units": "IN"
                        },
                        "weight": {
                            "weight": "1.96",
                            "units": "LB"
                        },
                        "totalCharges": "0.0",
                        "referenceNumber1": "250730-1501-92",
                        "depthUOM": "Inch",
                        "type": "Shipping"
                    },
                    "inventoryAttribute1": "354872888261744",
                    "inventoryAttribute2": "pass"
                },
                {
                    "lineNumber": "5002407-2",
                    "lineStatus": "Complete",
                    "customerLineNumber": "2",
                    "shipmentLineNumber": "296--4",
                    "itemNumber": "PWS014844808083",
                    "productCategory": "CellPhone",
                    "orderPackQuantity": "2.0",
                    "shippedPackQuantity": "1.0",
                    "serialNumber": "354029288914329",
                    "trackingNumber": "391596590792",
                    "pack": {
                        "type": "Each",
                        "quantity": "1",
                        "dimension": {
                            "@nil": "true"
                        },
                        "upcs": {
                            "@nil": "true"
                        },
                        "customMappings": {
                            "@nil": "true"
                        }
                    },
                    "container": {
                        "lpn": "PWS_Order-250729_10",
                        "number": "PWS_Order-250729_10",
                        "trackingNumber": "391596590792",
                        "status": "Shipped",
                        "description": "PWS_Order-250729_10 is packed into PWS_Order-250729_10",
                        "dimension": {
                            "length": "5.0",
                            "width": "7.0",
                            "height": "3.0",
                            "volume": "0.0",
                            "units": "IN"
                        },
                        "weight": {
                            "weight": "1.96",
                            "units": "LB"
                        },
                        "totalCharges": "0.0",
                        "referenceNumber1": "250730-1501-92",
                        "depthUOM": "Inch",
                        "type": "Shipping"
                    },
                    "inventoryAttribute1": "354029288914329",
                    "inventoryAttribute2": "pass"
                }
            ],
        "container": {
          "lpn": "5002395_Box",
          "number": "5002395_Box",
          "trackingNumber": "391515107176",
          "status": "Shipped",
          "description": "5002395_Box is packed into 5002395_Box",
          "dimension": {
            "length": "5.0",
            "width": "7.0",
            "height": "3.0",
            "volume": "0.0",
            "units": "IN"
          },
          "weight": {
            "weight": "0.49",
            "units": "LB"
          },
          "totalCharges": "7.71",
          "referenceNumber1": "250728-1822-84",
          "depthUOM": "Inch",
          "type": "Shipping"
        },
        "inventoryAttribute1": "350686384483675"
      }
    },
    "trackingNumber": "391515107176",
    "trackingUrl": "https://www.fedex.com/fedextrack/?trknbr=391515107176",
    "shippingCost": "7.71",
    "publishedRate": "14.35",
    "extendedShippingCost": {
      "@nil": "true"
    },
    "weight": {
      "weight": "0.49",
      "units": "LB"
    },
    "rateZone": "3",
    "saturdayDelivery": "false",
    "deliveryConfirmation": "0",
    "dropShip": "false",
    "residential": "false",
    "insuranceRequired": "false",
    "taxCost": "0.0",
    "codAmount": "0.0"
  }
}
```
