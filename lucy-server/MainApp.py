from flask import Flask, request, g, make_response, send_from_directory
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
	return "Welcome to Lucy Server"

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

#use get for testing puspose, change to post later
@app.route("/upload", methods=["POST"])
def receiver():
	raw_json_str = request.form["json"]
	#return raw_json_str
	uuid = request.form["uuid"]
	data_type = request.form["data_type"]
	g.cursor.execute("SHOW TABLES LIKE \'%s\'" % data_type)
	table = g.cursor.fetchone()
	
	#if there is no table, don't do anything to the database
	#rather than create a corresponding table for security reason
	if table is None:
		resp = make_response("there is no such table")
		resp.headers["Content-Type"] = "text/plain"
		resp.headers["charset"] = "UTF-8"
		return resp
	else:
		json_array = parseJson(raw_json_str)
		if not json_array:
			#return "data cannot be parsed as json: %s" % raw_json_str
			resp = make_response("data cannot be parsed as json: %s" % raw_json_str)
                	resp.headers["Content-Type"] = "text/plain"
                	resp.headers["charset"] = "UTF-8"
                	return resp
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
				query = "INSERT INTO %s (%s) VALUES (%s)" % (table[0], columns, values)
				g.cursor.execute(query)
				g.db.commit()
				#return "successfully write into table: %s" % table
			except:
				g.db.rollback()
				#return "fail to write into table: %s, rollback \n query: %s" % (table, query)
		                resp = make_response("fail to write into table: %s, rollback \n query: %s" % (table, query))
                		resp.headers["Content-Type"] = "text/plain"
                		resp.headers["charset"] = "UTF-8"
                		return resp
	return str(json_array)

@app.route('/<path:filename>')
def photos(filename):
    return send_from_directory("/", filename)

def parseJson(json_str):
	try:
		json_obj = json.loads(json_str)
		return json_obj
	except:
		return None

if __name__ == "__main__":
	#app.debug = True
	#app.run(host='0.0.0.0')
	app.run(debug=True)
