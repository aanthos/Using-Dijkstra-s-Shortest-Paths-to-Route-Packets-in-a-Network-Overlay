Currently not implemented:
* start number-of-rounds
* print-shortest-path

Notes:
* Messaging nodes and registry use a Scanner(System.in) to receive commands

Description of each file:

cs455.overlay.dijkstra
* Vertex.java: Represents messaging nodes registered in the registry
* Edge.java: Represents links to each node after the overlay is set up
* Graph.java: Grabs list of active messaging nodes from registry, randomly connects the nodes with the required amount of connections, and assigns those connections random link weights

cs455.overlay.node
* Node.java: interface with the onEvent(event e) method
* MessagingNode.java: implements Node.java. Runs a foreground process that listens for user input for commands such as exiting the overlay. Sends requests and receives responses from the Registry
* Registry.java: implements Node.java. Runs a foreground process that listens for user input for commands such as setting up the overlay. Sends responses and receives requests from the messaging nodes. Stores and sends overlay information here.


cs455.overlay.transport
* TCPReceiverThread: Processes received data. Started by messaging nodes and registry
* TCPSender: Sends data on a socket between messaging nodes and registry
* TCPServerThread: Waits and listens for connections on messaging ndoes and registry

cs455.overlay.util
* OverlayCreator: Currently unused. Intention was to place overlay functions in Registry into this class

cs455.overlay.wireformats
* Deregister.java: Message type that contains deregister request/response information
* Event.java: Interface with methods implemented by all message types
* EventFactory.java: Singleton instance. It sets itself to the current node and determines the message type received by the current node and activates that specific node's onEvent method.
* LinkWeights.java: Message type sent from the registry to each active node that contains information on linked nodes and their weights
* MessagingNodesList.java: Message type sent from the registry to each active node that contains information on the messaging nodes each node is connected to.
* Protocol.java: Labels each message type
* Register.java: Message type that contains register request/response information. 
