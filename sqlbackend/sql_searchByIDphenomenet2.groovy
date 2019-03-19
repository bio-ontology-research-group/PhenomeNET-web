@Grab('mysql:mysql-connector-java:5.1.25')
@GrabConfig(systemClassLoader = true)
import groovy.sql.Sql
import java.sql.*
import groovy.json.*

if(!application) {
  application = request.getApplication(true)
}

def q = params.term


println new JsonBuilder(search(q))

def search(def q) {
	def url = 'jdbc:mysql://localhost:3306/phenomenet'
def user = 'root'
def driver = 'com.mysql.jdbc.Driver'
def sql = Sql.newInstance(url, user,"", driver) 
	List entities = sql.rows("SELECT * FROM entities where id=$q")
	//println new JsonBuilder(entities).toPrettyString()

	sql.close()
	return entities
		
	}

	