/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package payara.rest;


import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author Andrew Pielage
 */
@Path("request")
@RequestScoped
public class RestResource {
    
    @Resource
    ManagedExecutorService executor;

    public RestResource() {
    }

    @EJB
    private SingletonClusteredEJB singletonClusteredEjb;

    @EJB
    private SingletonClusteredTimeoutEJB singletonClusteredTimeoutEjb;

    @EJB
    private SingletonTimeoutEJB singletonTimeoutEjb;

    @Inject
    ClusteredApplicationBean clusteredApplicationBean;



    @GET
    @Path("clustered")
    @Produces(MediaType.TEXT_PLAIN)
    public String getEjbClustered() {
        for (int i = 0; i < 6; i++) {
            executor.submit(() -> {
                System.out.println("!!!!!!!! Starting a new task, calling hello.");
                singletonClusteredEjb.hello();
            });
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException interruptedException) {
            // Nom nom nom
        }

        return "Launched via @EJB, look at the Payara log. Sequence is: " + singletonClusteredEjb.getSequence();
    }

    @GET
    @Path("clustered-timeout")
    @Produces(MediaType.TEXT_PLAIN)
    public String getEjbClusteredTimeout() {
        for (int i = 0; i < 6; i++) {
            executor.submit(() -> {
                System.out.println("!!!!!!!! Starting a new task, calling hello.");
                singletonClusteredTimeoutEjb.hello();
            });
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException interruptedException) {
            // Nom nom nom
        }

        return "Launched via @EJB, look at the Payara log. Sequence is: " + singletonClusteredTimeoutEjb.getSequence();
    }

    @GET
    @Path("singleton-timeout")
    @Produces(MediaType.TEXT_PLAIN)
    public String getEjbSingletonTimeout() {
        for (int i = 0; i < 6; i++) {
            executor.submit(() -> {
                System.out.println("!!!!!!!! Starting a new task, calling hello.");
                singletonTimeoutEjb.hello();
            });
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException interruptedException) {
            // Nom nom nom
        }

        return "Launched via @EJB, look at the Payara log. Sequence is: " + singletonTimeoutEjb.getSequence();
    }

    @GET
    @Path("clustered-application")
    @Produces(MediaType.TEXT_PLAIN)
    public String getClusteredApplication() {
        for (int i = 0; i < 6; i++) {
            executor.submit(() -> {
                System.out.println("!!!!!!!! Starting a new task, calling hello.");
                clusteredApplicationBean.hello();
            });
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException interruptedException) {
            // Nom nom nom
        }

        return "Launched @ApplicationScoped bean, look at the Payara log. Sequence is: " + clusteredApplicationBean.getSequence();
    }

}
