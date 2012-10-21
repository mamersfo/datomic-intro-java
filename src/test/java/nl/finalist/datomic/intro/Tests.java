package nl.finalist.datomic.intro;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static nl.finalist.datomic.intro.Helper.*;

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
    public void entitiesAndAttributes()
    {
        LOGGER.info( "Exercise 1: find all entities" );

        LOGGER.info( "Creating and connecting to database at {}", uri );
        Connection conn = Main.createAndConnect( uri );
        LOGGER.info( "Adding schema and data with attrs: " +
                "name, country, person/born, person/height, player/position" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-1.dtm", conn );
        Main.parseDatomicFileAndRunTransaction( "data/data-1.dtm", conn );
        
        // Task: define query
        String query = Solutions.solution1;
        assertTrue( query != null && query.length() > 0 );
        Collection<List<Object>> results = Peer.q( query, conn.db() );
        print( entities( results, conn.db() ) );
        assertEquals( 153, results.size() );

        Peer.deleteDatabase( uri );
    }
    
    @Test
    public void specificEntities()
    {
        LOGGER.info( "Exercise 2: find all persons" );

        LOGGER.info( "Creating and connecting to database at {}", uri );
        Connection conn = Main.createAndConnect( uri );
        LOGGER.info( "Adding schema and data with attrs: " +
                "name, country, person/born, person/height, player/position" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-1.dtm", conn );
        Main.parseDatomicFileAndRunTransaction( "data/data-1.dtm", conn );
        
        // Task: define query
        String query = Solutions.solution2;
        assertTrue( query != null && query.length() > 0 );
        Collection<List<Object>> results = Peer.q( query, conn.db() );
        print( entities( results, conn.db() ) );
        assertEquals( 85, results.size() );

        Peer.deleteDatabase( uri );
    }

    @Test
    public void aggregateExpressions()
    {
        LOGGER.info( "Exercise 3: find for each country the number of players and their average height" );

        LOGGER.info( "Creating and connecting to database at {}", uri );
        Connection conn = Main.createAndConnect( uri );
        LOGGER.info( "Adding schema and data with attrs: " +
                "name, country, person/born, person/height, player/position" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-1.dtm", conn );
        Main.parseDatomicFileAndRunTransaction( "data/data-1.dtm", conn );
        
        // Task: define query
        String query = Solutions.solution3;
        assertTrue( query != null && query.length() > 0 );
        Collection<List<Object>> results = Peer.q( query, conn.db() );
        List<List<Object>> list = sort( list ( results ), 2, "DESC" );
        print( list );
        assertEquals( 19, list.size() );
        assertEquals( "Sweden", list.get( 0 ).get( 0 ) );
        assertEquals( 1, list.get( 0 ).get( 1 ) );
        assertEquals( 195.0, list.get( 0 ).get( 2 ) );

        Peer.deleteDatabase( uri );
    }

    @Test
    public void performJoins()
    {
        LOGGER.info( "Exercise 4: find team name and salary for Zlatan" );
        
        LOGGER.info( "Creating and connecting to database at {}", uri );
        Connection conn = Main.createAndConnect( uri );
        LOGGER.info( "Adding schema and data with attrs: " +
                "name, country, person/born, person/height, player/position" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-1.dtm", conn );
        Main.parseDatomicFileAndRunTransaction( "data/data-1.dtm", conn );
        LOGGER.info( "Adding attributes to schema: player/team, player/salary + data" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-2.dtm", conn );
        Main.loadPlayerTeamAndSalary( "data/data-2-2011.csv", conn );
        
        // Task: define the query
        String query = Solutions.solution4;
        assertTrue( query != null && query.length() > 0 );
        Collection<List<Object>> results = Peer.q( query, conn.db(), "Zlatan Ibrahimovic" );
        List<Object> tuple = results.iterator().next();        
        assertEquals( 2, tuple.size() );
        assertEquals( "AC Milan", tuple.get( 0 ) );
        assertEquals( 9.0, tuple.get( 1 ) );
        LOGGER.info( "Added data for Zlatan Ibrahimovic, team: {}, salary: {} million per annum", 
                tuple.toArray() );

        Peer.deleteDatabase( uri );
    }
    
    @Test
    public void timeTravel()
    {
        LOGGER.info( "Exercise 5: find top earners for subsequent years" );
        
        LOGGER.info( "Creating and connecting to database at {}", uri );
        Connection conn = Main.createAndConnect( uri );
        LOGGER.info( "Adding schema and data with attrs: " +
                "name, country, person/born, person/height, player/position" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-1.dtm", conn );
        Main.parseDatomicFileAndRunTransaction( "data/data-1.dtm", conn );
        LOGGER.info( "Adding attributes to schema: player/team, player/salary + data" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-2.dtm", conn );
        Main.loadPlayerTeamAndSalary( "data/data-2-2011.csv", conn );
        
        LOGGER.info( "Find instant when salaries were first recorded" );
        // Task: define the query
        String query = Solutions.solution5;
        assertTrue( query != null && query.length() > 0 );
        Collection<List<Object>> results = Peer.q( query, conn.db(), "Zlatan Ibrahimovic" );
        Date year2011 = (Date)results.iterator().next().get( 0 );
        LOGGER.info( "Salary data added on {}", year2011 );

        // Pause in order to discriminate between transactions
        try { Thread.sleep( 1000 ); } catch( InterruptedException e ) {}
        LOGGER.info( "Loading player team and salary data for 2012" );
        Main.loadPlayerTeamAndSalary( "data/data-2-2012.csv", conn );

        LOGGER.info( "List name and salary, ordered by salary as of now (2012)" );
        query = "[:find ?name ?salary :in $ :where [?player :name ?name][?player :player/salary ?salary]]";
        results = Peer.q( query, conn.db() );
        List<List<Object>> values = sort( list( results ), 1, "DESC" ); 
        assertEquals( "Samuel Eto'o", values.get( 0 ).get( 0 ) );
        print( values );
        
        LOGGER.info( "List name and salary, ordered by salary as of last year (2011)" );
        // Task: uncomment stuff below and change database argument 
        // in order to get the facts for last year so that 
        // Cristiano Ronaldo turns out to become the top earner
        
        /*
        results = Peer.q( query, conn.db() );
        values = sort( list( results ), 1, "DESC" ); 
        assertEquals( "Cristiano Ronaldo", values.get( 0 ).get( 0 ) );
        printValues( values );
        */
        
        Peer.deleteDatabase( uri );
    }
    
    @Test
    public void predicateFunctions()
    {
        LOGGER.info( "Exercise 6: find a Twitter user's screenName and followersCount " +
                     "where followersCount is over one million followers" );
        
        LOGGER.info( "Creating and connecting to database at {}", uri );
        Connection conn = Main.createAndConnect( uri );
        LOGGER.info( "Adding schema and data with attrs: " +
                "name, country, person/born, person/height, player/position" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-1.dtm", conn );
        Main.parseDatomicFileAndRunTransaction( "data/data-1.dtm", conn );
        LOGGER.info( "Adding Twitter user attributes to schema + data" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-3.dtm", conn );
        Main.parseDatomicFileAndRunTransaction( "data/data-3.dtm", conn );
        
        // Task: define the query
        String query = Solutions.solution6;
        assertTrue( query != null && query.length() > 0 );
        Collection<List<Object>> results = Peer.q( query, conn.db() );
        assertEquals( 21, results.size() );
        List<List<Object>> values = sort( list( results ), 1 ); 
        print( values );

        Peer.deleteDatabase( uri );
    }
    
    @Test
    public void multipleJoins()
    {        
        LOGGER.info( "Exercise 7: find names of players who are following Robin van Persie on Twitter" );
        
        LOGGER.info( "Creating and connecting to database at {}", uri );
        Connection conn = Main.createAndConnect( uri );
        LOGGER.info( "Adding schema and data with attrs: " +
                "name, country, person/born, person/height, player/position" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-1.dtm", conn );
        Main.parseDatomicFileAndRunTransaction( "data/data-1.dtm", conn );
        LOGGER.info( "Adding Twitter user attributes to schema + data" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-3.dtm", conn );
        Main.parseDatomicFileAndRunTransaction( "data/data-3.dtm", conn );
        LOGGER.info( "Adding attributes to schema: player/twitter.screenName + data" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-4.dtm", conn );
        Main.loadPlayerTwitterScreenName( "data/data-4.csv", conn );

        // Task: define the query
        String query = Solutions.solution7;
        assertTrue( query != null && query.length() > 0 );
        Collection<List<Object>> results = Peer.q( query, conn.db(), "Robin van Persie" );
        assertEquals( 23, results.size() );
        List<List<Object>> values = sort( list( results ), 0 );
        assertEquals( "Andrei Arshavin", values.get( 0 ).get( 0 ) );
        print( values );

        Peer.deleteDatabase( uri );
    }    
    
    @Test
    public void usingRules()
    {
        LOGGER.info( "Exercise 8: find names of goalkeepers or defenders, using rules" );

        LOGGER.info( "Creating and connecting to database at {}", uri );
        Connection conn = Main.createAndConnect( uri );
        LOGGER.info( "Adding schema and data with attrs: " +
                "name, country, person/born, person/height, player/position" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-1.dtm", conn );
        Main.parseDatomicFileAndRunTransaction( "data/data-1.dtm", conn );
        
        // Task: define query
        String query = "[:find ?n :in $ % :where [?e :name ?n](goalkeepers_or_defenders ?e)]";
        String rules = Solutions.solution8;
        Collection<List<Object>> results = Peer.q( query, conn.db(), rules );
        print( sort( list( results ), 0 ) );
        assertEquals( 24, results.size() );

        Peer.deleteDatabase( uri );
    }

}
