from flask import Flask, request
from flaskext.mysql import MySQL
import json


mysql = MySQL()
app = Flask(__name__)
app.config['MYSQL_DATABASE_USER'] = 'root'
app.config['MYSQL_DATABASE_PASSWORD'] = '56289086'
app.config['MYSQL_DATABASE_DB'] = 'EmpData'
app.config['MYSQL_DATABASE_HOST'] = 'localhost'
mysql.init_app(app)

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
	cursor = mysql.connect().cursor()
	cursor.execute("SELECT * from User where userName = \'%s\' and password = \'%s\'" % (username, password))
	data = cursor.fetchone()
	if data is None:
		return "Username or Password is wrong"
	else:
		return "Logged in successfully"

@app.route("/upload", methods=["GET"])
def receiver():
	raw_json_str = request.args.get("json", "")
	uuid = request.args.get("uuid", "")
	data_type = request.args.get("data_type", "")
	cursor = mysql.connect().cursor()
	cursor.execute("SHOW TABLES LIKE %s" % data_type)
	table = cursor.fetchone()

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
		try:
			cursor.execute("INSERT INTO %s VALUES (%s)" % (table, columns))
			mysql.commit()
		except:
			mysql.rollback()

	return "uuid: %s, json: %s" % (uuid, raw_json_str)

def parseJson(json_str):
	json_obj = json.load(json_str)
	return json_obj

def jsonObjToRowList(json_obj):


if __name__ == "__main__":
	app.debug = True
	app.run()
