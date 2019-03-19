@Grab('mysql:mysql-connector-java:5.1.25')
@GrabConfig(systemClassLoader = true)
import groovy.sql.Sql
import java.sql.*
import groovy.json.*

	def lno=1
	def url = 'jdbc:mysql://localhost:3306/phenomenet'
	def user = 'root'
	def driver = 'com.mysql.jdbc.Driver'
	def sql = Sql.newInstance(url, user,"", driver)
	def q='a'
	//List entities = sql.rows("SELECT name FROM entities where name LIKE '$q%'")
	//println entities
	new File('./data/new_mouse_allel_reduced_matrix.txt').eachLine { line ->
		//println line.split("\\t",-1)[0] +"------"+ line.split("\\t",-1)[1]+"-----"+line.split("\\t",-1)[2]+"-----"+line.split("\\t",-1)[-1]
		println "INSERT INTO simMatrix (entity1_id,entity2_id,score) VALUES ((SELECT id FROM entities WHERE entity_id = ${line.split("\\t",-1)[0]} and type ='Mouse Allel' ),(SELECT id FROM entities WHERE entity_id = ${line.split("\\t",-1)[1]} and type = ${line.split("\\t",-1)[-1]}),${line.split("\\t",-1)[2]}) "
		println lno
		println "*********************************************************************************************"
		sql.execute "INSERT INTO simMatrix (entity1_id,entity2_id,score) VALUES ((SELECT id FROM entities WHERE entity_id = ${line.split("\\t",-1)[0]} and type ='Mouse Allel'),(SELECT id FROM entities WHERE entity_id = ${line.split("\\t",-1)[1]} and type = ${line.split("\\t",-1)[-1]}),${line.split("\\t",-1)[2]}) "
		lno++
	}
	sql.close()