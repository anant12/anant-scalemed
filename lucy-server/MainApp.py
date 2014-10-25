from flask import Flask, request, g
from flaskext.mysql import MySQL
import json


mysql = MySQL()
app = Flask(__name__)
app.config['MYSQL_DATABASE_USER'] = 'loggingresearch'
app.config['MYSQL_DATABASE_PASSWORD'] = 'loggingresearchproject'
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
#@app.route("/Authenticate")
#def authenticate():
#	username = request.args.get('UserName')
#	password = request.args.get('Password')
#	g.cursor.execute("SELECT * from User where userName = \'%s\' and password = \'%s\'" % (username, password))
#	data = g.cursor.fetchone()
#	if data is None:
#		return "Username or Password is wrong"
#	else:
#		return "Logged in successfully"

@app.route("/upload", methods=["GET"])
def receiver():
	raw_json_str = request.args.get("json", "")
	#return raw_json_str
	uuid = request.args.get("uuid", "")
	data_type = request.args.get("data_type", "")
	g.cursor.execute("SHOW TABLES LIKE \'%s\'" % data_type)
	table = g.cursor.fetchone()

	#if there is no table, don't do anything to the database
	#rather than create a corresponding table for security reason
	if table is None:
		return "possible wrong data type"
	else:
		json_array = parseJson(raw_json_str)
		#for each row, we shoud have: uuid, data1, data2...
		#since we don't know how many columns each data type may need, use another
		#helper method to convert a json_object to list of rows
		#and insert them
		for json_obj in json_array:
			#construct query, it must start with uuid
			columns = "uuid,"
			values = "'" + uuid + "'" + ","
			for item in json_obj.items():
				columns += item[0] + ","
				values += "'" + item[1] + "'" + ","
			#correct the last comma
			columns = columns[:-1]
			values = values[:-1]
			try:
				g.cursor.execute("INSERT INTO %s (%s) VALUES (%s)" % (table[0], columns, values))
				g.db.commit()
			#return "successfully write into table: %s" % table
			except:
				g.db.rollback()

	return str(json_array)

def parseJson(json_str):
	json_obj = json.loads(json_str)
	return json_obj

if __name__ == "__main__":
	app.debug = True
	app.run()
