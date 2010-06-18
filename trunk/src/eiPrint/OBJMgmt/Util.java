/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eiPrint.OBJMgmt;


import com.db4o.*;

/**
 *
 * @author dave
 */
public class Util {

    public static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }

    public static void listResult(java.util.List result){
    	System.out.println(result.size());
    	for(int x = 0; x < result.size(); x++)
    		System.out.println(result.get(x));
    }

    public static void listRefreshedResult(ObjectContainer container,ObjectSet result,int depth) {
        System.out.println(result.size());
        while(result.hasNext()) {
            Object obj = result.next();
            container.ext().refresh(obj, depth);
            System.out.println(obj);
        }
    }

    public static void retrieveAll(ObjectContainer db){
        ObjectSet result=db.queryByExample(new Object());
        listResult(result);
    }

    public static void deleteAll(ObjectContainer db) {
        ObjectSet result=db.queryByExample(new Object());
        while(result.hasNext()) {
            db.delete(result.next());
        }
    }

}
