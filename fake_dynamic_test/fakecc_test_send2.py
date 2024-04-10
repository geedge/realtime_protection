import socket
import struct
from flask import request, jsonify,Flask
import json

app = Flask(__name__)

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

    src_ip_v4, dst_ip_v4, src_port_v4, dst_port_v4, protocol_v4 = parse_and_convert_ip(data_parts[1])

    formatted_data = {
        "task_id": 30650,
        "rule_id": 39,
        "five_tuple_with_mask": {
            "src_ip": src_ip_v4,
            "dst_ip": dst_ip_v4,
            "src_port": src_port_v4,
            "dst_port": dst_port_v4,
            "protocol": protocol_v4,
            "mask_src_ip": data_parts[5],
            "mask_dst_ip": data_parts[6],
            "mask_src_port": int(data_parts[7]),
            "mask_dst_ip": int(data_parts[8]),
            "mask_protocol": int(data_parts[9])
        },
        "content": test_data  # Keeping the original content in the 'content' field
    }
    return formatted_data



# if __name__ == "__main__":
#     HOST = '127.0.0.1'
#     PORT = 65432
#     send_test_data(HOST, PORT, test_data)
#
#     test_data="1702017420-1-175833107,1921297587-310737541-53420-6379-6-127-0,140717936336976-140717936336992-0-0-0-131-4481,0,000,440000,1,7,107,1,111,Amazon Data Services UK,0,0,440100,0,0,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,,9,10060101189,tcp.banner=$5115,39,0"
#     test_data_2= "log_info,stream_v4,stream_v6,tunnel_v4,s_boundary,s_region,d_boundary,d_region,s_operators,s_owner,d_operators,d_owner,priority,flowid,s_city,s_district,d_city,d_district,msisdn,imsi,meid,uli,bsid,apn,event_id,return_info,file_type,file_name,file,url,cookie"
#     print(len(test_data.split(",")), len(test_data_2.split(",")))
#     print(test_data.split(",")[-7])
#     print(test_data_2.split(",")[-7])
#     print(test_data_2.split(",")[24])
#
#     # formatted_json = format_data_to_json(test_data)
#     # print(formatted_json)
#     #
#     # send_test_data(HOST, PORT, formatted_json)
#     send_test_data(HOST, PORT, test_data)


#
# @app.route('/api/v1/kafkasend', methods=['POST'])
# def kafka_send():
#     if request.method == 'POST' and request.is_json:
#         data = request.json
#         task_id = data.get('task_id', '')
#         rule_id = data.get('rule_id', '')
#
#         test_data = "1702017420-1-175833107,1921297587-310737541-53420-6379-6-127-0,140717936336976-140717936336992-0-0-0-131-4481,0,000,440000,1,7,107,1,111,Amazon Data Services UK,0,0,440100,0,0,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,,9,10060101189,tcp.banner=$5115,39,0"
#
#         modified_data = f"{test_data}, {task_id}, {rule_id},{0}"
#         HOST = '127.0.0.1'
#         PORT = 65432
#
#         send_test_data(HOST, PORT, modified_data)
#         return jsonify({'message': 'Data processed successfully'}), 200
#
#
#     return jsonify({'error': 'Invalid request'}), 400

@app.route('/api/v1/kafkasend', methods=['POST'])
def kafka_send():
    if request.method == 'POST' and request.is_json:
        data = request.get_json()

        base_data = "1702017420-1-175833107,1921297587-310737541-53420-6379-6-127-0,140717936336976-140717936336992-0-0-0-131-4481,0,000,440000,1,7,107,1,111,Amazon Data Services UK,0,0,440100,0,0,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,,9,10060101189,tcp.banner=$5115"
        for item in data:
            task_id = item.get('task_id', '')
            rule_id = item.get('rule_id', '')
            modified_data = f"{base_data}, {task_id}, {rule_id},{0}"
            HOST = '127.0.0.1'
            PORT = 65432
            send_test_data(HOST, PORT, modified_data)

        return jsonify({'message': 'Data processed successfully'}), 200

    return jsonify({'error': 'Invalid request'}), 400


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=8081, debug=True)