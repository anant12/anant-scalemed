from flask import Flask, request
app = Flask(__name__)

@app.route("/")
def hello():
    return "Hello World!"


@app.route("/upload", methods=["GET"])
def receiver():
	raw_json_str = request.args.get("json", "")
	uuid = request.args.get("uuid", "")
	return "(uuid: %s, json: %s)" % (uuid, raw_json_str)

if __name__ == "__main__":
    app.run()
