import requests
import json


test_data = [
  {
    "task_id": 1,
    "rule_id": 1,
    "source_system": "BW系统",
    "event_type": "xxx",
    "start_time": "2024-01-17T00:00:00.000Z",
    "end_time": "2024-01-31T00:00:00.000Z",
    "log_rule_id": "11343234524145136635224567746",
    "protect_objects": [
      {
        "ip": "192.168.122.11",
        "port": 1656,
        "url": "alice.bob.com",
        "protocol": "TCP"
      },
      {
        "ip": "192.168.122.114",
        "port": 514,
        "url": "alice.bob.com",
        "protocol": "UDP"
      }
    ]
  },
  {
    "task_id": 2,
    "rule_id": 2,
    "source_system": "BW系统",
    "event_type": "yyy",
    "start_time": "2024-02-17T00:00:00.000Z",
    "end_time": "2024-02-31T00:00:00.000Z",
    "log_rule_id": "22343234524145136635224567746",
    "protect_objects": [
      {
        "ip": "192.168.123.11",
        "port": 1657,
        "url": "charlie.dave.com",
        "protocol": "TCP"
      }
    ]
  }
]

url = 'http://127.0.0.1:8081/api/v1/kafkasend'


response = requests.post(url, json=test_data)

# 打印响应内容
print(f"Status Code: {response.status_code}")
print(f"Response Body: {response.text}")
