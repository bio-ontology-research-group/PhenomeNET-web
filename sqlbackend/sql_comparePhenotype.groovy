@Grab('mysql:mysql-connector-java:5.1.25')
@GrabConfig(systemClassLoader = true)
import groovy.sql.Sql
import java.sql.*
import groovy.json.*

if(!application) {
  application = request.getApplication(true)
}

def e1 = params.e1
def e2 = params.e2


println new JsonBuilder(search(e1,e2))

def search(def e1, def e2) {
	def url = 'jdbc:mysql://localhost:3306/phenomenet'
def user = 'root'
def driver = 'com.mysql.jdbc.Driver'
def sql = Sql.newInstance(url, user,"", driver) 
	List e1_metaData = sql.rows("Select name,metaData from entities where id=$e1")
	List e2_metaData = sql.rows("Select name,metaData from entities where id=$e2")
	sql.close()
	def rmap = []
	rmap << e1_metaData
	rmap << e2_metaData
	rmap
		
	}

	