# URL: /api/message

from Message import *
from flask import Flask, jsonify, request

app = Flask(__name__)

@app.route('/api/message', methods=['POST'])
def receive():
	m = Message(request.json['request-id'], request.json['instruction'], request.json['sudoku'], request.json['sender'])
	print(m.json())
	return m.json(), 201

if __name__ == '__main__':
	app.run(debug=True,port=80,host='0.0.0.0')
