package nl.finalist.datomic.intro;

import static nl.finalist.datomic.intro.Helper.entities;
import static nl.finalist.datomic.intro.Helper.list;
import static nl.finalist.datomic.intro.Helper.print;
import static nl.finalist.datomic.intro.Helper.sort;
import static nl.finalist.datomic.intro.Main.createAndConnect;
import static nl.finalist.datomic.intro.Main.loadDatomicFile;
import static nl.finalist.datomic.intro.Main.loadPlayerTeamAndSalary;
import static nl.finalist.datomic.intro.Main.loadPlayerTwitterScreenName;
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
    public void entitiesAndAttributes()
    {
        LOGGER.info( "Exercise 1: find all entities" );

        Connection conn = createAndConnect( uri );
        loadDatomicFile( "data/schema-1.dtm", conn );
        loadDatomicFile( "data/data-1.dtm", conn );
        
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

        Connection conn = createAndConnect( uri );
        loadDatomicFile( "data/schema-1.dtm", conn );
        loadDatomicFile( "data/data-1.dtm", conn );
        
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

        Connection conn = createAndConnect( uri );
        loadDatomicFile( "data/schema-1.dtm", conn );
        loadDatomicFile( "data/data-1.dtm", conn );
        
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
        
        Connection conn = createAndConnect( uri );
        loadDatomicFile( "data/schema-1.dtm", conn );
        loadDatomicFile( "data/data-1.dtm", conn );
        LOGGER.info( "Adding attributes to schema: player/team, player/salary + data" );
        loadDatomicFile( "data/schema-2.dtm", conn );
        loadPlayerTeamAndSalary( "data/data-2-2011.csv", conn );
        
        // Task: define the query
        String query = Solutions.solution4;
        assertTrue( query != null && query.length() > 0 );
        Collection<List<Object>> results = Peer.q( query, conn.db(), "Zlatan Ibrahimovic" );
        List<Object> tuple = results.iterator().next();        
        assertEquals( 2, tuple.size() );
        assertEquals( "AC Milan", tuple.get( 0 ) );
        assertEquals( 9.0, tuple.get( 1 ) );

        Peer.deleteDatabase( uri );
    }
    
    @Test
    public void timeTravel()
    {
        LOGGER.info( "Exercise 5: find top earners for subsequent years" );
        
        Connection conn = createAndConnect( uri );
        loadDatomicFile( "data/schema-1.dtm", conn );
        loadDatomicFile( "data/data-1.dtm", conn );
        loadDatomicFile( "data/schema-2.dtm", conn );
        // Loading player team and salary data for 2011
        loadPlayerTeamAndSalary( "data/data-2-2011.csv", conn );
        
        LOGGER.info( "Find instant when salaries were first recorded" );
        String query = "[:find ?instant :in $ :where " + 
                           "[?p :player/salary _ ?tx]" +
                           "[?tx :db/txInstant ?instant]]";
        Collection<List<Object>> results = Peer.q( query, conn.db() );
        Date year2011 = (Date)results.iterator().next().get( 0 );
        LOGGER.info( "Salary data added on {}", year2011 );

        // Pause in order to discriminate between transactions
        try { Thread.sleep( 1000 ); } catch( InterruptedException e ) {}
        // Loading player team and salary data for 2012
        loadPlayerTeamAndSalary( "data/data-2-2012.csv", conn );

        // List name and salary, ordered by salary as of now (2012)
        query = "[:find ?name ?salary :in $ :where [?player :name ?name][?player :player/salary ?salary]]";
        results = Peer.q( query, conn.db() );
        List<List<Object>> values = sort( list( results ), 1, "DESC" ); 
        assertEquals( "Samuel Eto'o", values.get( 0 ).get( 0 ) );
        
        // Task: change argument to get the facts for last year,
        // so that Cristiano Ronaldo turns out to be the top earner 
        results = Peer.q( query, conn.db() );
        values = sort( list( results ), 1, "DESC" ); 
        assertEquals( "Cristiano Ronaldo", values.get( 0 ).get( 0 ) );
        
        Peer.deleteDatabase( uri );
    }
    
    @Test
    public void predicateFunctions()
    {
        LOGGER.info( "Exercise 6: find a Twitter user's screenName and followersCount " +
                     "where followersCount is over one million followers" );
        
        Connection conn = createAndConnect( uri );
        loadDatomicFile( "data/schema-1.dtm", conn );
        loadDatomicFile( "data/data-1.dtm", conn );
        loadDatomicFile( "data/schema-3.dtm", conn );
        loadDatomicFile( "data/data-3.dtm", conn );
        
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
        
        Connection conn = createAndConnect( uri );
        loadDatomicFile( "data/schema-1.dtm", conn );
        loadDatomicFile( "data/data-1.dtm", conn );
        loadDatomicFile( "data/schema-3.dtm", conn );
        loadDatomicFile( "data/data-3.dtm", conn );
        loadDatomicFile( "data/schema-4.dtm", conn );
        loadPlayerTwitterScreenName( "data/data-4.csv", conn );

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

        Connection conn = createAndConnect( uri );
        loadDatomicFile( "data/schema-1.dtm", conn );
        loadDatomicFile( "data/data-1.dtm", conn );
        
        String query = "[:find ?n :in $ % :where [?e :name ?n](goalkeepers_or_defenders ?e)]";
        // Task: define the rules
        String rules = Solutions.solution8;
        Collection<List<Object>> results = Peer.q( query, conn.db(), rules );
        print( sort( list( results ), 0 ) );
        assertEquals( 24, results.size() );

        Peer.deleteDatabase( uri );
    }

}
