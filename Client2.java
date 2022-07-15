import java.io.IOException;
import java.io.*;
import java.net.*;

import java.text.DecimalFormat;

public class Client2 {
    private static final String _IP_SERVIDOR = "192.168.100.9";
    private static final int _PUERTO = 1234;
    private static final int _TIMEOUT = 3000;
    private static final int _PAQUETES = 120;

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket();
        socket.setSoTimeout(_TIMEOUT);
        socket.connect(new InetSocketAddress(InetAddress.getByName(_IP_SERVIDOR), _PUERTO), _TIMEOUT);
        
        System.out.println("Se ha iniciado la conexion al servidor");
        System.out.println("Cliente IP: "+ socket.getLocalAddress() + " Puerto: " + socket.getLocalPort());
        System.out.println("Servidor IP: "+ socket.getInetAddress() + " Puerto: " + socket.getPort());

        try {
            send(socket);
            }
            catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error de operacion");
            }
            socket.close();
            System.out.println("Clliente: -> DESCONECTADO");
            }
    private static void send(Socket client) throws IOException, InterruptedException {
        OutputStream outputStream = client.getOutputStream();
        PrintStream socketPrintStream = new PrintStream(outputStream);
        String data ="";   
  
        InputStream inputStream = client.getInputStream();
        BufferedReader socketBufferReader = new BufferedReader(new InputStreamReader(inputStream)); 
        
        for (int i = 0; i < _PAQUETES; i++) {
            
            double temperatura = ((-15) + Math.random() * (45 - (-15)));
          

            double humedad = (50 + Math.random() * (100 - 50));
            

            double Co2 = (700 + Math.random() * (5000 - 700));
           

            char tipo = 's';
          
          
           
            
            DecimalFormat df = new DecimalFormat("#####.##");
  
            data = "$Temp|"+ df.format(temperatura) + "#Hum|" + df.format(humedad) +"%#Co2|"+ df.format(Co2) +"$";
            socketPrintStream.println(data);
            String echo = socketBufferReader.readLine();
            System.out.println("Cliente: " + data);
            System.out.println(echo);
            Thread.sleep(400);
        }
        System.out.println("Cliente: TOKEN 'bye'");
        socketPrintStream.println("bye");
        System.out.println("Cliente: PROCESO FINALIZADO");
}
}
