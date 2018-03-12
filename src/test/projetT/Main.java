package projetT;
import EchoEndpointAnnotated.java

public class Main {
	
	public void runServer() {
	    Server server = new Server("localhost", 8025, "/websockets", null, EchoEndpoint.class);

	    try {
	        server.start();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	        System.out.print("Please press a key to stop the server.");
	        reader.readLine();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        server.stop();
	    }
	}

}