package nl.finalist.datomic.intro;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import datomic.Database;
import datomic.Entity;

public final class Helper
{
    private static final Logger LOGGER = LoggerFactory.getLogger( Helper.class );
    
    @SuppressWarnings("rawtypes")
    static void print( Collection<List<Object>> collection )
    {
        LOGGER.info( "Found: {}", collection.size() );

        for ( List list : collection )
        {
            LOGGER.info( list.toString() );
        }
    }    

    static List<Entity> entities( Collection<List<Object>> collection, Database db )
    {
        List<Entity> result = new ArrayList<Entity>();
        
        for ( List<Object> list : collection )
        {
            result.add( db.entity( list.get( 0 ) ) );
        }        
        
        return result;
    }
    
    static List<List<Object>> list( Collection<List<Object>> collection )
    {
        ArrayList<List<Object>> result = new ArrayList<List<Object>>();
        result.addAll( collection );
        return result;
    }
    
    static List<Entity> sort( List<Entity> entities, String attr, String sortOrder )
    {
        Collections.sort( entities, new EntityComparator( attr, sortOrder ) );
        return entities;
    }
    
    static List<List<Object>> sort( List<List<Object>> values, int index, String sortOrder )
    {
        Collections.sort( values, new ValuesComparator( index, sortOrder ) );
        return values;
    }
                
    static void print( List<Entity> entities )
    {
        LOGGER.info( "Found: {}", entities.size() );

        for ( Entity entity : entities )
        {
            LOGGER.info( entity.toString() );                
        }
    }
    
    public static List<Map<String,String>> readCsv( String path )
    {
        List<Map<String,String>> result = new ArrayList<Map<String,String>>();
        
        List<String> keys = null;
        
        InputStream inputStream = null;
        
        try
        {
            inputStream = new FileInputStream( new File ( path ) );
            
            BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream, "UTF8" ) );

            for ( String line = reader.readLine(); line != null; line = reader.readLine() )
            {
                List<String> values = Arrays.asList( line.split( "," ) );

                if ( keys == null )
                {
                    keys = values;
                }
                else
                {
                    Map<String,String> map = new TreeMap<String,String>();
                    
                    for ( int i=0, max = keys.size(); i < max; i++ )
                    {
                        String key = keys.get( i ).trim();
                        
                        if ( key.length() > 0 )
                        {
                            map.put( keys.get( i ), values.get( i ) );
                        }
                    }
                    
                    result.add( map );
                }
            }
        }
        catch( Exception e )
        {
            throw new RuntimeException( e.getMessage(), e );
        }
        finally
        {
            if ( inputStream != null ) try { inputStream.close(); } catch( IOException e ) {}
        }
        
        return result;
    }
    
    public static class EntityComparator implements Comparator<Entity>
    {
        private final String key;
        private String sortOrder = "ASC";
        
        public EntityComparator( String key )
        {
            this.key = key;
        }

        public EntityComparator( String key, String sortOrder )
        {
            this.key = key;
            this.sortOrder = sortOrder;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public int compare( Entity e1, Entity e2 )
        {
            Object o1 = e1.get( key );
            Object o2 = e2.get( key );
            
            int result = 0;
            
            if ( o1 instanceof Comparable )
            {
                result = ((Comparable)o1).compareTo( o2 );
                if ( sortOrder.equalsIgnoreCase( "DESC" ) ) result = -result;
            }
            
            return result;
        }
    }

    public static class ValuesComparator implements Comparator<List<Object>>
    {
        private final int index;
        private String sortOrder = "ASC";
        
        public ValuesComparator( int index )
        {
            this.index = index;
        }
        
        public ValuesComparator( int index, String sortOrder )
        {
            this.index = index;
            this.sortOrder = sortOrder;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public int compare( List<Object> l1, List<Object> l2 )
        {
            Object o1 = l1.get( index );
            Object o2 = l2.get( index );
            
            int result = 0;
            
            if ( o1 instanceof Comparable )
            {
                result = ((Comparable)o1).compareTo( o2 );
                if ( sortOrder.equalsIgnoreCase( "DESC" ) ) result = -result;
            }
            
            return result;
        }
    }
}
