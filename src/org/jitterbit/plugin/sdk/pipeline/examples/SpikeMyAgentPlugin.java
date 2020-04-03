/*
 * Copyright (c) 1998-2007 Jitterbit, Inc.
 *
 */
package org.jitterbit.plugin.sdk.pipeline.examples;

import org.jitterbit.plugin.sdk.DataElement;
import org.jitterbit.plugin.sdk.DataElementFactory;
import org.jitterbit.plugin.sdk.DataElements;
import org.jitterbit.plugin.sdk.DataType;
import org.jitterbit.plugin.sdk.InputFile;
import org.jitterbit.plugin.sdk.InputFiles;
import org.jitterbit.plugin.sdk.pipeline.PipelinePlugin;
import org.jitterbit.plugin.sdk.pipeline.PipelinePluginContext;
import org.jitterbit.plugin.sdk.pipeline.PipelinePluginInput;
import org.jitterbit.plugin.sdk.pipeline.PipelinePluginOutput;
import org.jitterbit.plugin.sdk.pipeline.PluginResult;

/**
 * Example of a simple pipeline plugin that does essentially nothing. It can be used as a template
 * to implement your own plugins.
 * 
 */
public class SpikeMyAgentPlugin extends PipelinePlugin {

    /**
     * This method provides the implementation of the plugin.
     * 
     */
    @Override
    public PluginResult run(PipelinePluginInput input, 
                            PipelinePluginOutput output, 
                            PipelinePluginContext context) throws Exception {
    	spikeCPU();
    	System.out.println("done...");
        return PluginResult.SUCCESS;
        
        /*
         * If you don't set any output files the Jitterbit engine will assume that 
         * you did not touch them and will use the input files as-is.
         */
        
        /*
         * If something goes wrong, there are two ways of communicating this
         * back to the Jitterbit plugin framework:
         * 
         * 1. Throw an exception from this method. The exception's message 
         * should preferrably contain a clear message describing what went wrong.
         * This message will end up in the operation log:
         * 
         * throw new Exception("The data element 'Count' had an illegal value (25)");
         * 
         * 
         * 2. Return PluginResult.FAILURE. The reason of the failure should be written
         * to the output's log:
         * 
         * LogMessage msg = LogMessageFactory.error("The data element 'Count' had an illegal value (25)");
         * output.setLogMessage(msg);
         * return PluginResult.FAILURE;
         */
    }
    
    private void spikeCPU() {
    	int numCore = Runtime.getRuntime().availableProcessors();
    	int numThreadsPerCore = 2;
    	
    	System.out.println("available processors:" + numCore);
    	System.out.println("maxThreads:" + (numCore * numThreadsPerCore));
    	
    	double load = 0.8;
    	final long duration = 300000;
    	
    	// loop for 5 minutes
    	long startTime = System.currentTimeMillis();
    	int threadCount = 0;
    	while(System.currentTimeMillis() - startTime < duration) {
    		threadCount++;
    		new BusyThread("Thread: " + threadCount, load, duration).start();
    	}
    }
    
    
    /**
     * This is the entry point to the plugin. This method is called by the
     * Jitterbit plugin framework.
     * 
     */
    public static void main(String[] args) {
        // Note that the run() method with no arguments should be called here.
        new SpikeMyAgentPlugin().run();
    }
    
    /**
     * Thread that generates the load
     */
    private static class BusyThread extends Thread {
        private double load;
        private long duration;

        /**
         * Constructor which creates the thread
         * @param name Name of this thread
         * @param load Load % that this thread should generate
         * @param duration Duration that this thread should generate the load for
         */
        public BusyThread(String name, double load, long duration) {
            super(name);
        	System.out.println("starting thread " + name);

            this.load = load;
            this.duration = duration;
        }

        /**
         * Generates the load when run
         */
        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            
            try {
                // Loop for the given duration            	
                while (System.currentTimeMillis() - startTime < this.duration) {   
                	// every 100ms, sleep
                    if (System.currentTimeMillis() % 100 > 0) {
                    	System.out.println("waiting for " + this.getName());
                    	
                    	startTime = System.currentTimeMillis();                    	
                    	Thread.sleep((long) Math.floor((1 - this.load) * 10000));                     
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
