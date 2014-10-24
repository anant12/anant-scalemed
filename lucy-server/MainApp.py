from flask import Flask, request
from flaskext.mysql import MySQL


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
	return "uuid: %s, json: %s" % (uuid, raw_json_str)

if __name__ == "__main__":
	app.debug = True
	app.run()
