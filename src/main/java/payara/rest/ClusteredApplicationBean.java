package payara.rest;

import fish.payara.cluster.Clustered;
import fish.payara.cluster.DistributedLockType;


import jakarta.enterprise.context.ApplicationScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
@Clustered(lock = DistributedLockType.LOCK)
public class ClusteredApplicationBean implements Serializable {

    private Map<String, List<Long>> idCache = new ConcurrentHashMap<>();

    public ClusteredApplicationBean() {
        new Exception("\n  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n"
                + "  Creating new CDI Bean\n"
                + "  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n").printStackTrace(System.out);
    }

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
            Logger.getLogger(ClusteredApplicationBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        Long removedId = cachedIDs.remove(0);
        System.out.println("[ID] removed ID " + removedId);
        System.out.println("...end.");
    }

    public String getSequence() {
        if (idCache == null) {
            return "Null!";
        }

        return Arrays.toString(idCache.get("sequence").toArray());
    }
}
