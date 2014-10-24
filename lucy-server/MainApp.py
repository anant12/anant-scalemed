from flask import Flask, request, g
from flaskext.mysql import MySQL
import json


mysql = MySQL()
app = Flask(__name__)
app.config['MYSQL_DATABASE_USER'] = 'root'
app.config['MYSQL_DATABASE_PASSWORD'] = '56289086'
app.config['MYSQL_DATABASE_DB'] = 'EmpData'
app.config['MYSQL_DATABASE_HOST'] = 'localhost'
mysql.init_app(app)

@app.before_request
def before_request():
	g.db = mysql.get_db()
	g.cursor = mysql.get_db().cursor()

@app.route("/")
def hello():
	return "Hello World!"

#simple authenticate code to test if the database is connected with the MainApp
#!!!IMPORTANT!!!
#this is no real authentication, it is just a test code!!
@app.route("/Authenticate")
def authenticate():
	username = request.args.get('UserName')
	password = request.args.get('Password')
	g.cursor.execute("SELECT * from User where userName = \'%s\' and password = \'%s\'" % (username, password))
	data = g.cursor.fetchone()
	if data is None:
		return "Username or Password is wrong"
	else:
		return "Logged in successfully"

@app.route("/upload", methods=["GET"])
def receiver():
	raw_json_str = request.args.get("json", "")
	#return raw_json_str
	uuid = request.args.get("uuid", "")
	data_type = request.args.get("data_type", "")
	g.cursor.execute("SHOW TABLES LIKE \'%s\'" % data_type)
	#table = cursor.fetchone()
	table = "a table"
	#if there is no table, don't do anything to the database
	#rather than create a corresponding table for security reason
	if table is None:
		return "possible wrong data type"
	else:
		json_obj = parseJson(raw_json_str)
		#for each row, we shoud have: uuid, data1, data2...
		#since we don't know how many columns each data type may need, use another
		#helper method to convert a json_object to list of rows
		#and insert them
		#
		
		#try:
		#	cursor.execute("INSERT INTO %s VALUES (%s)" % (table, columns))
		#	g.db.commit()
		#except:
		#	g.db.rollback()
	return json_obj

def parseJson(json_str):
	json_obj = json.loads(json_str)
	return str(json_obj)

#def jsonObjToRowList(json_obj):
def shutdown_server():
    func = request.environ.get('werkzeug.server.shutdown')
    if func is None:
        raise RuntimeError('Not running with the Werkzeug Server')
    func()

@app.route('/shutdown', methods=['GET'])
def shutdown():
    shutdown_server()
    return 'Server shutting down...'

if __name__ == "__main__":
	app.debug = True
	app.run()
