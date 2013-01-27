package cmu.cs.distsystems.hw1;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MasterProcessManager extends ProcessManager {

    public Map<String, HostInformation> slaveInfomation;

    //public MasterProcessManager()

    public MasterProcessManager(int serverPort){
    	super("localhost", 4444, serverPort);
    	slaveInfomation = new ConcurrentHashMap<String, HostInformation>();
    }

    @Override
    public void start() {
    	
    	try {
			System.out.println("Starting Master PM on " + InetAddress.getLocalHost().getHostName()
					+ ":" + 4444);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//Start Slave ProcessManagerServer - different thread
		this.pmServerThread = startPMSlaveServer();
		this.pmServerThread.start();
		
		//Start Master Process Manager
		Thread masterService = new Thread(new ProcessManagerServer
				( this, 4444, MasterProcessRequestHandler.class) );
		masterService.start();
		
		//Start Work Balancer Server
		SimpleBalanceStrategy strategy = new SimpleBalanceStrategy(this);
		Thread loadBalancer = new Thread(new WorkloadBalanceThread(this, strategy));
		loadBalancer.start();
		
		//Start CLI
		startCLI();
    	
		
    }

}
