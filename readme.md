Some important notes:

1. See Catchpoint API, [https://io.catchpoint.com/ui/help](https://io.catchpoint.com/ui/help)
2. Read ./pom.xml for details
3. This is the java wrapper, calling catchpoint REST API, and extract JSON into entity object.

JSON Response
```
{
    "summary": {
        "fields": {
            "breakdown_2": {
                "name": "City",
                "id": 4
            },
            "synthetic_metrics": [
                {
                    "statistical": {
                        "prefix": "Avg",
                        "id": 1
                    },
                    "name": "Webpage Response (ms)",
                    "index": 0
                },
                {
                    "name": "% Availability",
                    "index": 1
                },
                {
                    "name": "# Runs",
                    "index": 2
                }
            ],
            "breakdown_1": {
                "name": "Country",
                "id": 11
            }
        },
        "items": [
            {
                "breakdown_2": {
                    "name": "Beijing",
                    "id": 29
                },
                "synthetic_metrics": [
                    6808.615384615385,
                    87.692,
                    65
                ],
                "breakdown_1": {
                    "name": "China",
                    "id": 44
                }
            },
            {
                "breakdown_2": {
                    "name": "Frankfurt",
                    "id": 17
                },
                "synthetic_metrics": [
                    3692.646153846154,
                    100,
                    65
                ],
                "breakdown_1": {
                    "name": "Germany",
                    "id": 81
                }
            },
            {
                "breakdown_2": {
                    "name": "Los Angeles",
                    "id": 4
                },
                "synthetic_metrics": [
                    2608.529411764706,
                    100,
                    68
                ],
                "breakdown_1": {
                    "name": "United States",
                    "id": 224
                }
            },
            {
                "breakdown_2": {
                    "name": "Shanghai",
                    "id": 30
                },
                "synthetic_metrics": [
                    5073.5,
                    93.939,
                    66
                ],
                "breakdown_1": {
                    "name": "China",
                    "id": 44
                }
            },
            {
                "breakdown_2": {
                    "name": "Wuhan",
                    "id": 142
                },
                "synthetic_metrics": [
                    8326.692307692309,
                    80,
                    65
                ],
                "breakdown_1": {
                    "name": "China",
                    "id": 44
                }
            }
        ]
    }
}
```

Entity Result (by reflection) 
```
Result[country=China,city=Beijing,availability=6808.615384615385,responseTime=87.692]
Result[country=Germany,city=Frankfurt,availability=3692.646153846154,responseTime=100.0]
Result[country=United States,city=Los Angeles,availability=2608.529411764706,responseTime=100.0]
Result[country=China,city=Shanghai,availability=5073.5,responseTime=93.939]
Result[country=China,city=Wuhan,availability=8326.692307692309,responseTime=80.0]
``` 
    