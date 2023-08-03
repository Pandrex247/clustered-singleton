package payara.rest;

import jakarta.ejb.AccessTimeout;
import jakarta.ejb.Lock;
import jakarta.ejb.Singleton;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class SingletonTimeoutEJB implements Serializable {

    private Map<String, List<Long>> idCache = new ConcurrentHashMap<>();

    public SingletonTimeoutEJB() {
        new Exception("\n  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"
                + "  Creating new EJB\n"
                + "  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n").printStackTrace(System.out);
    }

    @Lock
    @AccessTimeout(2000)
    public void hello() {
        System.out.println("Start..." + this);
        
        List<Long> cachedIDs = idCache.get("sequence");
        if (cachedIDs == null) {
            cachedIDs = new ArrayList<>();
            idCache.put("sequence", cachedIDs);
        }
        if (cachedIDs.isEmpty()) {
            for (long i = 0; i < 20; i++) {
                cachedIDs.add(i);
            }
            System.out.println("############################################################\n"
                    + "  Repopulating sequence\n"
                    + "  ############################################################\n");
        }

        System.out.println("[ID] cached ids " + cachedIDs + ", this: " + this);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(SingletonTimeoutEJB.class.getName()).log(Level.SEVERE, null, ex);
        }

        Long removedId = cachedIDs.remove(0);
        System.out.println("[ID] removed ID " + removedId);
        System.out.println("...end.");
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(idCache);
        new Exception("\n  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n"
                + "  Serialising " + this + " \n"
                + "  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n").printStackTrace(System.out);
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        idCache = (Map) objectInputStream.readObject();
        new Exception("\n  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n"
                + "  Deserialising " + this + " \n"
                + "  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n").printStackTrace(System.out);
    }

    public String getSequence() {
        if (idCache == null) {
            return "Null!";
        }

        return Arrays.toString(idCache.get("sequence").toArray());
    }
}
