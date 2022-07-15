import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Server {
    
    private static final String _IP = "192.168.1.69";
  
    private static final int _PUERTO = 1234;
    private static final int _BACKLOG = 50;

    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_CLEAR = "\033[H\033[2J";

    public static void main(String[] args) throws IOException {
        InetAddress ip = InetAddress.getByName(_IP);
        ServerSocket serverSocket = new ServerSocket(_PUERTO, _BACKLOG, ip);

        try {
            System.out.println(ANSI_CLEAR);
            System.out.println(ANSI_GREEN +
            "Servidor iniciado . . . OK" + ANSI_RESET);
            System.out.println(ANSI_CYAN +
            "información del servidor: " + ANSI_RESET + ANSI_YELLOW +
            serverSocket.getInetAddress() + ":" + serverSocket.getLocalPort() +
            ANSI_RESET);
            System.out.println("--------------------------------------------------------------------");
    
            for (;;) {
                Socket client = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(client);
                clientHandler.start();
            }
        } catch (Exception e) {
            System.out.println(e);
            } finally {
            serverSocket.close();
            }
        }
        private static class ClientHandler extends Thread {
            private Socket socket;
            private boolean flag;

            ClientHandler(Socket socket) {
                this.socket = socket;
                flag = true;
            }

            @Override
            public void run() {
               super.run();

               System.out.println("Cliente: " + socket.getInetAddress() + ":" +
socket.getPort() + ANSI_GREEN + " -> CONECTADO" + ANSI_RESET);

               try {
                    String nombreHilo = this.currentThread().getName();

                    PrintStream socketOutput = new PrintStream(socket.getOutputStream());
                    BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    while (flag) {
                        
                        String str = socketInput.readLine();
                        if (str != null) {
                            if ("bye".equalsIgnoreCase(str)) {
                                flag = false;
                                socketOutput.println(
                                "Cliente: " + ANSI_YELLOW + socket.getInetAddress() +
                                " Termino la conexion" + ANSI_RESET);
                            } else {
                               

                                DataInputStream datosEntrada = new DataInputStream( new BufferedInputStream(socket.getInputStream()));
                                DataOutputStream datosSalida = new DataOutputStream(socket.getOutputStream());
                
                                int puertoRemitente = socket.getPort();
                
                                InetAddress ipRemitente = socket.getInetAddress();
                
                                char tipoDato = datosEntrada.readChar();
                
                                int longitud = datosEntrada.readInt();               
                if (tipoDato == 's'){
                    byte[] bytesDatos = new byte[longitud];

                    boolean finData = false;

                    StringBuilder dataEnMensaje = new StringBuilder(longitud);

                    int totalBytesLeidos = 0;

                    while (!finData){
                        int bytesActualesLeidos = datosEntrada.read(bytesDatos);

                        totalBytesLeidos = bytesActualesLeidos + totalBytesLeidos;

                        if (totalBytesLeidos <= longitud){
                            dataEnMensaje.append(new String(bytesDatos, 0, bytesActualesLeidos, StandardCharsets.UTF_8));
                        } else {
                            dataEnMensaje.append(new String(bytesDatos, 0, longitud - totalBytesLeidos + bytesActualesLeidos, StandardCharsets.UTF_8));
                        }
                        if (dataEnMensaje.length() >= longitud)
                        finData = true;
                    }
                    
                String[] lstEntradaServidor = dataEnMensaje.toString().split(",");                
                double tmpCliente = Double.valueOf(lstEntradaServidor[0]);
                double hmdCliente = Double.valueOf(lstEntradaServidor[1]);
                double co2Cliente = Double.valueOf(lstEntradaServidor[2]);
                
                String resultadoTmp = "false";
                String resultadoHmd = "false";
                String resultadoCo2 = "false";
                
                if(tmpCliente<16 || tmpCliente>39){
                    resultadoTmp = "true";
                }

                if(hmdCliente>80){
                    resultadoHmd = "true";
                }

                if(co2Cliente>3000){
                    resultadoCo2 = "true";
                }           
                
                DaoRespuesta dao = new DaoRespuesta();
                
                SimpleDateFormat fecha = new SimpleDateFormat("dd-MM-yyyy");
                 SimpleDateFormat hora = new SimpleDateFormat("HH:mm:ss");
                dao.guardar(new RespuestaPojo(
                        fecha.format(new Date()), hora.format(new Date()), ipRemitente.toString(), 
                        String.valueOf(puertoRemitente) , String.valueOf(tmpCliente), 
                        String.valueOf(hmdCliente), String.valueOf(co2Cliente), String.valueOf(resultadoTmp), 
                        String.valueOf(resultadoHmd), String.valueOf(resultadoCo2))
                );
                
                String salida = resultadoTmp + "," + resultadoHmd + "," + resultadoCo2;;
                                
                datosSalida.writeUTF(salida);
                
                System.out.println("Fecha = " + fecha.format(new Date()) + "\tHora =" + hora.format(new Date()) + "\tCliente = " + ipRemitente + ":" + puertoRemitente 
                                       + "\tEntrada = " + tmpCliente + ", " + hmdCliente + ", " + co2Cliente 
                                        + "\tSalida = " + resultadoTmp + ", " + resultadoHmd + ", " + resultadoCo2);
                }                               
                                System.out.println("Servidor: "+ ANSI_GREEN + "IP: " + socket.getInetAddress() + ":" + socket.getPort() +
                                ANSI_RESET + ANSI_YELLOW + "\t data: " + str + ANSI_RESET + ANSI_CYAN +"\t hilo: " + nombreHilo);

                                socketOutput.println("Servidor: OK");
                            }
                        }
                    }
                    socketInput.close();
                    socketOutput.close();
                    socket.close();
                }
                catch(SocketException ex){
                    System.out.println(ANSI_RED + "Error: " + ex.getMessage() + ANSI_RESET);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Conexión anormal");
                    }finally {
                        try {
                            // Cerrar la conexión del socket
                            socket.close();
                            } catch (IOException e) {
                                System.out.println("Cliente: " + socket.getInetAddress() + ANSI_RED + " -> DESCONECTADO" + ANSI_RESET);
                    }
                }
            }
        }
}

    
    
    
    
   