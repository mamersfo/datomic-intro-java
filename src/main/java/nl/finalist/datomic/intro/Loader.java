package nl.finalist.datomic.intro;

import static nl.finalist.datomic.intro.Helper.entities;
import static nl.finalist.datomic.intro.Helper.print;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import datomic.Connection;
import datomic.Peer;

public class Loader
{
    private static final Logger LOGGER = LoggerFactory.getLogger( Tests.class );
    
    private static final String uri = "datomic:free://localhost:4334/players";
    
    /**
     * @param args
     */
    public static void main( String[] args )
    {
        query();
    }
    
    private static void query()
    {
        Connection conn = Main.createAndConnect( uri );
        String query = Solutions.solution1;
        Collection<List<Object>> results = Peer.q( query, conn.db() );
        print( entities( results, conn.db() ) );        
    }

    private static void load()
    {
        LOGGER.info( "Creating and connecting to database at {}", uri );
        Connection conn = Main.createAndConnect( uri );
     
        LOGGER.info( "Adding schema and data with attrs: " +
            "name, country, person/born, person/height, player/position" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-1.dtm", conn );
        Main.parseDatomicFileAndRunTransaction( "data/data-1.dtm", conn );

        LOGGER.info( "Adding attributes to schema: player/team, player/salary + data" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-2.dtm", conn );
        Main.loadPlayerTeamAndSalary( "data/data-2-2011.csv", conn );
        Main.loadPlayerTeamAndSalary( "data/data-2-2012.csv", conn );
        
        LOGGER.info( "Adding Twitter user attributes to schema + data" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-3.dtm", conn );
        Main.parseDatomicFileAndRunTransaction( "data/data-3.dtm", conn );

        LOGGER.info( "Adding attributes to schema: player/twitter.screenName + data" );
        Main.parseDatomicFileAndRunTransaction( "data/schema-4.dtm", conn );
        Main.loadPlayerTwitterScreenName( "data/data-4.csv", conn );
    }
}
