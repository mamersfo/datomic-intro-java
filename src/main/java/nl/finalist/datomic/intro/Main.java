package nl.finalist.datomic.intro;

import java.io.FileReader;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import datomic.Connection;
import datomic.Database;
import datomic.Peer;
import datomic.Util;

public class Main
{
    private static final Logger LOGGER = LoggerFactory.getLogger( Main.class );
 
    public static Connection createAndConnect( String uri )
    {
        LOGGER.info( "Creating and connecting to database at {}", uri );
        Peer.createDatabase( uri );
        return Peer.connect( uri );
    }
    
    @SuppressWarnings("rawtypes")
    public static Object loadDatomicFile( String path, Connection conn )
    {
        try
        {
            Reader reader = new FileReader( path );
            List txData = (List)Util.readAll( reader ).get(0);
            return conn.transact( txData ).get();
        }
        catch( Exception e )
        {
            throw new RuntimeException( e.getMessage(), e );
        }
    }
    
    private static Long findByName( String name, Database db )
    {
        Collection<List<Object>> results = 
            Peer.q( "[:find ?p :in $ ?n :where [?p :name ?n]]", db, name );
        
        return (Long)results.iterator().next().get( 0 );
    }
    
    @SuppressWarnings("rawtypes")
    public static void loadPlayerTeamAndSalary( String path, Connection conn )
    {
        List<Map<String,String>> data = Helper.readCsv( path );
        
        for ( Map<String,String> player : data )
        {
            Long playerId = findByName( player.get( "name" ), conn.db() );
            Long teamId = findByName( player.get( "team" ), conn.db() );
            Double salary = Double.parseDouble( player.get( "salary" ) );
            
            List txData = Util.list(
                Util.list( ":db/add", playerId, ":player/team", teamId ),
                Util.list( ":db/add", playerId, ":player/salary", salary ) );
            
            conn.transact( txData );
        }
    }
    
    @SuppressWarnings("rawtypes")
    public static void loadPlayerTwitterScreenName( String path, Connection conn)
    {
        List<Map<String,String>> data = Helper.readCsv( path );
        
        for ( Map<String,String> player : data )
        {
            Long id = findByName( player.get( "name" ), conn.db() );
            String screenName = player.get( "screenName" );
            List txData = Util.list( Util.list( ":db/add", id, ":player/twitter.screenName", screenName ));            
            conn.transact( txData );
        }
    }    
    
    public static void main( String[] input )
    {
        Collection<List<Object>> result = 
            Peer.q( "[:find ?a ?v :in $ :where [$ ?a ?v]]", System.getProperties() );
        
        for ( List<Object> list : result )
        {
            System.out.println( list );
        }
    }
}
