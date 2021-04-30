package com.mobile.monitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

   
public class Inicio {  
	
    
    
    //"TYPE","TIME","LAT","LON","ALT","BEARING","ACCURACY","SPEED","NAME","DESCRIPTION","SEGMENT"
    private static String msgTemplate = "P,2011-10-1715:41:50,-25.362574,-49.184717,946.5999755859375,0.0,11,0,0";
    private static String msgTemplateMEI = "5880,robo1,1";
    
    public Inicio(){
      
    }  
   
    
    
    public static void iniciar(){
    	
    	new Thread(new Runnable()   
        {
   		
    		private String formatLine(String line,double latitude,double longitude,float altitude,float speed) throws Exception {
    			String ret = "";
    			line = line.replace('\"', ' ');
    			String i[] = split(line,',');
    			String data = getDataHora();
    			
    				if(line.startsWith("5880")){
    					ret = "5880,"+data+","+i[1].trim()+",1";
    				}else{
    					for (int j = 0; j < i.length; j++) {
    						if(j==i.length-1){
    							ret+=i[j].trim();
    						}else{
    							if(j==1){
    								String dataArquivo = getDataHora();
    								ret+=dataArquivo+",";
    							}else if(j == 2){
    								ret+=String.valueOf(latitude)+",";
    							}else if(j == 3){
    								ret+=String.valueOf(longitude)+",";
    							}else if(j == 4){
    								ret+=String.valueOf(altitude)+",";
    							}else if(j == 7){
    								ret+=String.valueOf(speed)+",";
    							}else{
    								ret+=i[j].trim()+",";
    							}
    						}
    					}
    				}
    			return ret;
    		}
    		
    	     public final String[] split( String texto, char separador ) {

    	         if ( texto == null ) { return null;    }
    	         int tamanhoTexto = texto.length();
    	         if ( tamanhoTexto == 0 ) {
    	            return null;
    	         }
    	         Vector    lista   = new Vector();
    	         int       i      = 0;
    	         int       start  = 0;
    	         boolean   permite  = false;
    	         while ( i < tamanhoTexto ) {
    	            if ( texto.charAt( i ) == separador ) {
    	               if ( permite ) {
    	                  lista.addElement( texto.substring( start, i ).trim() );
    	                  permite = false;
    	               }
    	               start = ++i;
    	               continue;
    	            }
    	            permite = true;
    	            i++;
    	         }
    	         if ( permite ) {
    	            lista.addElement( texto.substring( start, i ).trim() );
    	         }
    	         String[]  listaElementos    = new String[lista.size()];
    	         lista.copyInto( listaElementos );
    	         return listaElementos;
    	      }

    		public String getDataHora() {
    			//recupera data e hora atual do sistema
    			Calendar cal = Calendar.getInstance();
    			Date date = new Date();
    			cal.setTime(date);
    			String mes = String.valueOf(cal.get(Calendar.MONTH) + 1);
    			String dia = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
    			String ano = String.valueOf(cal.get(Calendar.YEAR));
    			String horas = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
    			String minutos = String.valueOf(cal.get(Calendar.MINUTE));
    			String segundos = String.valueOf(cal.get(Calendar.SECOND));
    			cal = null;	date = null;
    			//formata a data de modo que o tamanho do resultado seja sempre fixo dia
    			if (dia.length() < 2) { dia = "0" + dia; } //mes
    			if (mes.length() < 2) { mes = "0" + mes; } //horas
    			if (horas.length() < 2) { horas = "0" + horas; } //minutos
    			if (minutos.length() < 2) { minutos = "0" + minutos; } //segundos
    			if (segundos.length() < 2) { segundos = "0" + segundos; } //
    			//"yyyy-MM-ddHH:mm:ss"
    			return ano + "-" + mes + "-" + dia  + horas + ":" + minutos + ":" + segundos;
    		}
    		
    		public void run()   
            {
    			Socket sc = null;;
		    	InputStream is = null;
		    	OutputStream os = null;
	    	
		    	//TextBox tb = new TextBox("Send  Localization", null, 1024, TextField.ANY | TextField.UNEDITABLE);
		    	
	            //tb.addCommand(exit);  
	            //tb.setCommandListener((CommandListener)inMIDlet); 
	            
				try {
					sc = new Socket("bancodenotas.crisnello.com",3334); 
			    	//sc.setSocketOption(SocketConnection.LINGER, 5);  
			    	is = sc.getInputStream();
			    	os = sc.getOutputStream();  
			    	
			    	//ANTES DE COME�AR devo enviar a linha com 5880
			    	boolean primeiro = true;
			    	while(true){
			    		boolean sendPosicao = false;
			    		double lat = 0.0, lon = 0.0; //zerando LOCATION para mostrar o ponto zero no MAPA
			    		float alt = 0;
			    		float speed = 0;
			    			sendPosicao = true;			    		
			    		String strEnviar = null;
						try {
							if(primeiro){
								strEnviar = formatLine(msgTemplateMEI,lat,lon,alt,speed);
								primeiro = false;
							}else
							strEnviar = formatLine(msgTemplate,lat,lon,alt,speed);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
			    		System.out.println("Enviando->"+strEnviar);
			    		os.write(strEnviar.getBytes());
			    		os.flush();
			    		//Display.getDisplay(inMIDlet).setCurrent(tb);
			    		try {
							Thread.sleep(20000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
			    	}
			    	
				} catch (IOException e) {e.printStackTrace();}
				finally{
			    	try {
						is.close();
				    	os.close();  
				    	sc.close();
					} catch (IOException e) {e.printStackTrace();}  
				}
            }
        }).start();
    }
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        try{  
            iniciar();  
        }   
        catch (SecurityException e){System.out.println(e);}   
        catch (Exception e) {System.out.println(e);}
	}
    
    public void startApp(){
    	boolean isAPIAvailable = isLocationApiSupported();
        if (isAPIAvailable){  
            try{  
                iniciar();  
            }   
            catch (SecurityException e){System.out.println(e);}   
            catch (Exception e) {System.out.println(e);}  
        }  
        else  
        {  
        	StringBuffer splashText = new StringBuffer("Information").
            append("\n").append("N�o foi poss�vel iniciar a aplica��o").  
            append(isAPIAvailable?"":"\nDispositivo n�o suporta a API Location");  
            
            System.out.println(splashText.toString());
                        
        }  
      
    }  
   
    public static boolean isLocationApiSupported()  
    {  
        String version = System.getProperty("microedition.location.version"); 
        if(version != null && !version.equals(""))
        	System.out.println("Version of API Location:"+version);
        return (version != null && !version.equals("")) ? true : false;  
    }
    


 
  
}   
