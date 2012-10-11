package nl.finalist.datomic.intro;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import datomic.Connection;
import datomic.Peer;

public class Tests
{
    private static final Logger LOGGER = LoggerFactory.getLogger( Tests.class );
    
    private final String uri = "datomic:mem://players";
        
    @Test
    public void findAllEntitiesAndPersons()
    {
        LOGGER.info( "Exercise 1: find all entities" );

        LOGGER.info( "Creating and connecting to database at {}", uri );
        Connection conn = Main.createAndConnect( uri );
        LOGGER.info( "Adding schema and data with attrs: name, country, person/born, person/height, player/position" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-1.dtm", conn );
        Main.parseDatomicFileAndRunTransaction( "data/data-1.dtm", conn );
        
        // Task: define query1
        String query1 = Solutions.query1;
        assertTrue( query1 != null && query1.length() > 0 );
        Collection<List<Object>> results = Peer.q( query1, conn.db() );
        Helper.printEntities( Helper.entities( results, conn.db() ) );
        assertEquals( 153, results.size() );

        // Task: define query2
        String query2 = Solutions.query2;
        assertTrue( query2 != null && query1.length() > 0 );
        results = Peer.q( query2, conn.db() );
        Helper.printEntities( Helper.entities( results, conn.db() ) );
        assertEquals( 85, results.size() );
    }
    
    @Test
    public void findTeamAndSalaryForZlatan()
    {
        LOGGER.info( "Exercise 3: find team name and salary for Zlatan" );
        
        LOGGER.info( "Creating and connecting to database at {}", uri );
        Connection conn = Main.createAndConnect( uri );
        LOGGER.info( "Adding schema and data with attrs: name, country, person/born, person/height, player/position" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-1.dtm", conn );
        Main.parseDatomicFileAndRunTransaction( "data/data-1.dtm", conn );
        LOGGER.info( "Adding attributes to schema: player/team, player/salary + data" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-2.dtm", conn );
        Main.loadPlayerTeamAndSalary( "data/data-2-2011.csv", conn );
        
        // Task: define the query
        String query3 = Solutions.query3;
        assertTrue( query3 != null && query3.length() > 0 );
        Collection<List<Object>> results = Peer.q( query3, conn.db(), "Zlatan Ibrahimovic" );
        List<Object> tuple = results.iterator().next();        
        assertEquals( 2, tuple.size() );
        assertEquals( "AC Milan", tuple.get( 0 ) );
        assertEquals( 9.0, tuple.get( 1 ) );
        LOGGER.info( "Added data for Zlatan Ibrahimovic, team: {}, salary: {} million per annum", tuple.toArray() );
    }
    
    @Test
    public void findTopEarnersForSubsequentYears()
    {
        LOGGER.info( "Exercise 4: find top earners for subsequent years" );
        
        LOGGER.info( "Creating and connecting to database at {}", uri );
        Connection conn = Main.createAndConnect( uri );
        LOGGER.info( "Adding schema and data with attrs: name, country, person/born, person/height, player/position" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-1.dtm", conn );
        Main.parseDatomicFileAndRunTransaction( "data/data-1.dtm", conn );
        LOGGER.info( "Adding attributes to schema: player/team, player/salary + data" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-2.dtm", conn );
        Main.loadPlayerTeamAndSalary( "data/data-2-2011.csv", conn );
        
        LOGGER.info( "Find instant when salaries were first recorded" );
        // Task: define the query
        String query4 = Solutions.query4;
        assertTrue( query4 != null && query4.length() > 0 );
        Collection<List<Object>> results = Peer.q( query4, conn.db(), "Zlatan Ibrahimovic" );
        Date year2011 = (Date)results.iterator().next().get( 0 );
        LOGGER.info( "Salary data added on {}", year2011 );

        // Pause in order to discriminate between transactions
        try { Thread.sleep( 1000 ); } catch( InterruptedException e ) {}
        LOGGER.info( "Loading player team and salary data for 2012" );
        Main.loadPlayerTeamAndSalary( "data/data-2-2012.csv", conn );

        LOGGER.info( "List name and salary, ordered by salary as of now (2012)" );
        String query = "[:find ?name ?salary :in $ :where [?player :name ?name][?player :player/salary ?salary]]";
        results = Peer.q( query, conn.db() );
        List<List<Object>> values = Helper.sort( Helper.list( results ), 1, "DESC" ); 
        assertEquals( "Samuel Eto'o", values.get( 0 ).get( 0 ) );
        Helper.printValues( values );
        
        LOGGER.info( "List name and salary, ordered by salary as of last year (2011)" );
        // Task: change database argument in order to get the facts for last year
        results = Peer.q( query, conn.db().asOf( year2011 ) );
        values = Helper.sort( Helper.list( results ), 1, "DESC" ); 
        assertEquals( "Cristiano Ronaldo", values.get( 0 ).get( 0 ) );
        Helper.printValues( values );
    }
    
    @Test
    public void findTwitterersWithOverOneMillionFollowers()
    {
        LOGGER.info( "Exercise 5: find a Twitter user's screenName and followersCount " +
                     "where followersCount is over one million followers" );
        
        LOGGER.info( "Creating and connecting to database at {}", uri );
        Connection conn = Main.createAndConnect( uri );
        LOGGER.info( "Adding schema and data with attrs: name, country, person/born, person/height, player/position" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-1.dtm", conn );
        Main.parseDatomicFileAndRunTransaction( "data/data-1.dtm", conn );
        LOGGER.info( "Adding Twitter user attributes to schema + data" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-3.dtm", conn );
        Main.parseDatomicFileAndRunTransaction( "data/data-3.dtm", conn );
        
        // Task: define the query
        String query5 = Solutions.query5;
        assertTrue( query5 != null && query5.length() > 0 );
        Collection<List<Object>> results = Peer.q( query5, conn.db() );
        assertEquals( 21, results.size() );
        List<List<Object>> values = Helper.sort( Helper.list( results ), 1, "DESC" ); 
        Helper.printValues( values );
    }
    
    @Test
    public void findNamesOfPlayersWhoAreFollowingRobinVanPersieOnTwitter()
    {        
        LOGGER.info( "Exercise 6: find names of players who are following Robin van Persie on Twitter" );
        
        LOGGER.info( "Creating and connecting to database at {}", uri );
        Connection conn = Main.createAndConnect( uri );
        LOGGER.info( "Adding schema and data with attrs: name, country, person/born, person/height, player/position" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-1.dtm", conn );
        Main.parseDatomicFileAndRunTransaction( "data/data-1.dtm", conn );
        LOGGER.info( "Adding Twitter user attributes to schema + data" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-3.dtm", conn );
        Main.parseDatomicFileAndRunTransaction( "data/data-3.dtm", conn );
        LOGGER.info( "Adding attributes to schema: player/twitter.screenName + data" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-4.dtm", conn );
        Main.loadPlayerTwitterScreenName( "data/data-4.csv", conn );

        // Task: define the query
        String query6 = Solutions.query6;
        assertTrue( query6 != null && query6.length() > 0 );
        Collection<List<Object>> results = Peer.q( query6, conn.db(), "Robin van Persie" );
        assertEquals( 23, results.size() );
        List<List<Object>> values = Helper.sort( Helper.list( results ), 0, "ASC" );
        assertEquals( "Andrei Arshavin", values.get( 0 ).get( 0 ) );
        Helper.printValues( values );
    }    
}
