import socket
from confluent_kafka import Producer


import socket
import struct

def convert_ipv4_address(ip_int):

    return socket.inet_ntoa(struct.pack('!I', ip_int))

def parse_and_convert_ip(data_part):

    parts = data_part.split('-')
    src_ip = convert_ipv4_address(int(parts[0]))
    dst_ip = convert_ipv4_address(int(parts[1]))
    src_port = int(parts[2])
    dst_port = int(parts[3])
    protocol = int(parts[4])
    return src_ip, dst_ip, src_port, dst_port, protocol


def send_test_data(host, port, data):
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.connect((host, port))
        s.sendall(data.encode())


def format_data_to_json(test_data):
    data_parts = test_data.split(",")

    task_id = data_parts[29]
    rule_id_value = data_parts[30]
    protect_object_is_src_dst = data_parts[31]

    src_ip_v4, dst_ip_v4, src_port_v4, dst_port_v4, protocol_v4 = parse_and_convert_ip(data_parts[1])
    print("task_id:", task_id, "rule_id:", rule_id_value, "protect_object_is_src_dst:", protect_object_is_src_dst, "src_ip_v4:", src_ip_v4, "dst_ip_v4:", dst_ip_v4, "src_port_v4:", src_port_v4, "dst_port_v4:", dst_port_v4, "protocol_v4:", protocol_v4)
    formatted_data = {
        "task_id": task_id,
        "rule_id": rule_id_value,
        "protect_object_is_src_dst": protect_object_is_src_dst,
        "five_tuple_with_mask": {
            "src_ip": src_ip_v4,
            "dst_ip": dst_ip_v4,
            "src_port": src_port_v4,
            "dst_port": dst_port_v4,
            "protocol": protocol_v4,
            "event_id": data_parts[24],
            # test_data_2.split(",")[-7],
            "mask_src_ip": data_parts[5],
            "mask_dst_ip": data_parts[6],
            "mask_src_port": int(data_parts[7]),
            "mask_dst_ip": int(data_parts[8]),
            "mask_protocol": int(data_parts[9])
        },
        "content": test_data
    }
    return formatted_data

def delivery_report(err, msg):
    if err is not None:
        print('Message delivery failed: {}'.format(err))
    else:
        print('Message delivered to {} [{}]'.format(msg.topic(), msg.partition()))

conf = {
    'bootstrap.servers': "192.168.107.49:9092",
        'client.id': 'python-server'
    }

producer = Producer(conf)
def process_data(data, topic):
    producer.produce(topic, data.encode('utf-8'), callback=delivery_report)
    producer.poll(0)
def start_server(host, port, topic):
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind((host, port))
    server_socket.listen(1)
    print(f"Listening on {host}:{port}")

    while True:
        conn, addr = server_socket.accept()
        print(f"Connected by {addr}")

        try:
            while True:
                data = conn.recv(4096)
                if not data:
                    break
                print(f"Received data: {data}")
                formatted_json = format_data_to_json(data.decode())
                print(f"Formatted JSON: {formatted_json}")
                import json
                json_data = json.dumps(formatted_json)

                process_data(json_data, topic)
        finally:
            conn.close()

if __name__ == "__main__":
    HOST = '127.0.0.1'
    PORT = 65432
    TOPIC = 'topic-alert'
    start_server(HOST, PORT, TOPIC)
