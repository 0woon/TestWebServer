//package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;

public class main {

	private final String DEFAULT_HOSTNAME = "0.0.0.0";
    private final int DEFAULT_PORT = 8080;
    private final int DEFAULT_BACKLOG = 0;
    private HttpServer server = null;
    
    private final boolean SSL_ENABLE = false;
    
    /**
     * 생성자
     */
    public main() throws IOException {
        createServer(SSL_ENABLE, DEFAULT_HOSTNAME, DEFAULT_PORT);
    }
    public main(int port) throws IOException {
        createServer(SSL_ENABLE, DEFAULT_HOSTNAME, port);
    }
    public main(boolean sslEnable, String host, int port) throws IOException {
        createServer(sslEnable, host, port);
    }
    
    /**
     * 서버 생성
     */
    private void createServer(boolean sslEnable, String host, int port) throws IOException {
        // HTTP Server 생성
    	if(sslEnable) {
    		this.server = HttpsServer.create(new InetSocketAddress(host, port), DEFAULT_BACKLOG);
    		// HTTP Server Context 설정
            server.createContext("/", new RootHandler());            
    	} else {
    		this.server = HttpServer.create(new InetSocketAddress(host, port), DEFAULT_BACKLOG);
    		// HTTP Server Context 설정
            server.createContext("/", new RootHandler());
    	}
    	
    }
    
    /**
     * 서버 실행
     */
    public void start() {
        server.start();
    }
    // 여긴 테스트입니다
    /**
     * 서버 중지
     */
    public void stop(int delay) {
        server.stop(delay);
    }
    
    public static void main(String[] args) {
        
    	main httpServerManager = null;
        
        try {
        	
        	if( 3 != args.length) {
        		System.err.println("Please enter Webserver IP, WebServer Port, SSL Enable");        		
        		return;
        	}
        	String validIp = "^([1-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(\\.([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3}$";
    		if ( !Pattern.matches(validIp, args[0] )) {
    			System.err.println("WebServer IP is incorrect. (" + args[0] + ")");        		
        		return;
			 }
    		String webServerIp = args[0];
        	int webServerPort = 0;
        	
        	try {
        		webServerPort = Integer.valueOf(args[1]);
        	} catch (Exception e) {
        		System.err.println("WebServer Port is incorrect. (" + args[1] + ")");        		
        		return;
        	}
        	
            // 시작 로그
            System.out.println(
                String.format(
                    "[%s][HTTP SERVER][%s:%s][START]\n",
                    new SimpleDateFormat("yyyy-MM-dd H:mm:ss").format(new Date()),
                    webServerIp,
                    webServerPort
                )
            );
            
            boolean sslEnable = Boolean.valueOf( args[2] );
            
            // 서버 생성
            httpServerManager = new main(sslEnable, webServerIp, webServerPort);
            httpServerManager.start();
            // Shutdown Hook
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    // 종료 로그
                    System.out.println(
                        String.format(
                            "[%s][HTTP SERVER][STOP]",
                            new SimpleDateFormat("yyyy-MM-dd H:mm:ss").format(new Date())
                        )
                    );
                }
            }));
            
            // Enter를 입력하면 종료
            System.out.print("Please press 'Enter' to stop the server.");
            System.in.read();
            
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            // 종료
            // 0초 대기후  종료
        	if( null != httpServerManager ) {
        		httpServerManager.stop(0);        		
        	}
        }
    }
 
    /**
     * Sub Class
     */
    class RootHandler implements HttpHandler {    
        
        @SuppressWarnings("unchecked")
		@Override
        public void handle(HttpExchange exchange) throws IOException {
            
            // Initialize Response Body
            OutputStream respBody = exchange.getResponseBody();
            
            try {
            	
            	// RequestBody 획득
            	String requestBody = "";
            	InputStreamReader isr = null;
            	BufferedReader br = null;
            	try {
            		isr =  new InputStreamReader(exchange.getRequestBody(),"utf-8");
                	br = new BufferedReader(isr);

                	// From now on, the right way of moving from bytes to utf-8 characters:

                	int b;
                	StringBuilder buf = new StringBuilder(512);
                	while ((b = br.read()) != -1) {
                	    buf.append((char) b);
                	}
                	requestBody = buf.toString();
            	} catch ( Exception e) {
            		
            	} finally {
            		
            		if ( null != br ) br.close();
            		if ( null != isr ) isr.close();
            	}
                // Write Response Body
            	JSONObject responseBody = new JSONObject();
            	
            	// 테스트를 위한 특정 URL에 대한 리턴
            	if(exchange.getRequestURI().toString().contains("/api/user/pwdInfo.do")) {

            		JSONArray testDataObject = new JSONArray();
            		JSONObject dataObject1 = new JSONObject();
            		if(exchange.getRequestURI().toString().contains("root")) {
            			dataObject1.put("ip", "192.168.7.157");
            			dataObject1.put("id", "root");
            			dataObject1.put("id_pwd", "Netand141)");
            		} else if (exchange.getRequestURI().toString().contains("gksduddns93")) {
            			dataObject1.put("ip", "192.168.7.157");
            			dataObject1.put("id", "gksduddns93");
            			dataObject1.put("id_pwd", "Netand141)");
            		} else {
            			dataObject1.put("ip", "192.168.7.157");
            			dataObject1.put("id", "abc");
            			dataObject1.put("id_pwd", "abc");
            		}
        			testDataObject.add(dataObject1);
            		
            		responseBody.put("resultcode", "결과코드");
            		responseBody.put("message", "메세지");
            		responseBody.put("data", testDataObject);
            		
            	} else if(exchange.getRequestURI().toString().contains("/ers/config/networkdevice")) {
            		
            		JSONObject eachEqmt = new JSONObject();
            		eachEqmt.put("id", "79aac430-7cc8-11eb-ad58-005056926583");
            		eachEqmt.put("name", "ISE_EST_Local_Host");
            		eachEqmt.put("description", "example nd");
            		
            		JSONObject eachEqmtLink = new JSONObject();            		
            		eachEqmtLink.put("rel", "self");
            		eachEqmtLink.put("href", "https://<ise-ip>:9060/ers/config/ers/config/networkdevice/79aac430-7cc8-11eb-ad58-005056926583");
            		eachEqmtLink.put("type", "application/json");
            		
            		eachEqmt.put("link", eachEqmtLink);
            		
            		List<JSONObject> eqmtList = new ArrayList<>();
            		eqmtList.add(eachEqmt);
            		
            		JSONObject jsonSearchResult = new JSONObject();
            		jsonSearchResult.put("total", 1);
            		jsonSearchResult.put("resources", eqmtList);
            		
            		responseBody.put("SearchResult", jsonSearchResult);
            	} else if(exchange.getRequestURI().toString().contains("in/jx/nexus")) {
            		
            		JSONObject samsungFireRoutingHeader = new JSONObject();
            		JSONObject samsungFireRoutingBody = new JSONObject();
            		
            		samsungFireRoutingHeader.put("providerTranId", "NRSTINS31001");
            		samsungFireRoutingBody.put("routingPath", "M");
            		
            		
            		responseBody.put("header", samsungFireRoutingHeader);
            		responseBody.put("body", samsungFireRoutingBody);
            		
            	} else if(exchange.getRequestURI().toString().contains("um/jx/nexus")) {
            		
            		JSONObject samsungFireUmsHeader = new JSONObject();
            		JSONObject samsungFireUmsBody = new JSONObject();
            		
            		samsungFireUmsHeader.put("providerTranId", "NRSTUMS00001");
            		samsungFireUmsBody.put("resultCode", "00");
            		
            		
            		responseBody.put("header", samsungFireUmsHeader);
            		responseBody.put("body", samsungFireUmsBody);
            		
            	} else if(exchange.getRequestURI().toString().contains("eai/EaiProxyServlet")) {
            		
            		JSONObject samsungPop = new JSONObject();
            		
            		samsungPop.put("msg", "samsungPop success");
            		
            		
            		responseBody.put("body", samsungPop);
            		
            	} else if(exchange.getRequestURI().toString().contains("/detail")) {
            		
            		responseBody.put("result", "success");
            		
            		JSONObject hotelShillaData = new JSONObject();
            		hotelShillaData.put("contentsType", "TEXT");
            		hotelShillaData.put("sbmDt", "20221022003011");
            		hotelShillaData.put("sbmLang", "ko");
            		hotelShillaData.put("apInfId", "HIWAPV20230103160526000000000238");
            		hotelShillaData.put("systemId", "");
            		hotelShillaData.put("notifyOption", "0");
            		hotelShillaData.put("urgYn", "N");
            		hotelShillaData.put("docSecuType", "PERSONAL");
            		hotelShillaData.put("status", "1");		// 0 : 보류 | 1 : 진행중 | 2 : 완결 | 3 : 반려
            		hotelShillaData.put("timeZone", "GMT+9");
            		hotelShillaData.put("subject", "배치 테스트를 위해 데이터 구성");
            		
            		List<JSONObject> aplnsList = new ArrayList<>();
            		JSONObject apln = new JSONObject();
            		apln.put("actPrssDt", "");
            		apln.put("docOpenDt", null);
            		apln.put("docArvDt", "");
            		apln.put("opinion", "");
            		apln.put("companyCode", "");
            		apln.put("companyName", "");
            		apln.put("departmentCode", "");
            		apln.put("departmentName", "");
            		apln.put("epId", "");
            		apln.put("emailAddress", "jww012334@naver.com");
            		apln.put("name", "");
            		apln.put("jobPositionCode", "");
            		apln.put("jobPosition", "");
            		apln.put("subOrgCode", "");
            		apln.put("subOrgName", "");
            		apln.put("seq", "1");				// 결재 라인 순서
            		apln.put("role", "2");				// 기안(0), 결재(1), 합의(2), 후결(3), 통보(9)
            		apln.put("aplnStatsCode", "1");		// 미결(0), 결재(1), 반려(2) -> apvApltRstCode 내부 DB 반영
            		apln.put("arbPmtYn", "");
            		apln.put("arbActYn", "");
            		apln.put("dlgAprEpid", "");
            		apln.put("docDelCd", "");
            		apln.put("aplnMdfyPmtYn", "N");
            		apln.put("prxAprYn", "");
            		apln.put("contentsMdfyPmtYn", "N");
            		apln.put("contenstsMdfyActYn", "");
            		apln.put("aplnMdfyActYn", "");
            		
            		aplnsList.add(apln);
            		
            		hotelShillaData.put("aplns", aplnsList);
            		
//            		List<JSONObject> attachmentList = new ArrayList<>();
//            		JSONObject attachment = new JSONObject();
//            		attachment.put("attachfileName", "test.txt");
//            		attachment.put("attachfileSize", "140");
//            		attachment.put("seq", "0");
//            		attachmentList.add(attachment);
//            		
//            		hotelShillaData.put("attachment", attachmentList);
            		
            		responseBody.put("data", hotelShillaData);
            	}
            	
            	System.out.println("===================================================================");
            	System.out.println(
            			String.format(
            					"[%s] Request Method : %s",
            					new SimpleDateFormat("yyyy-MM-dd H:mm:ss").format(new Date()),
            					exchange.getRequestMethod()
            					)
            			);
            	System.out.println(
            			String.format(
            					"[%s] Request URI : %s",
            					new SimpleDateFormat("yyyy-MM-dd H:mm:ss").format(new Date()),
            					exchange.getRequestURI()
            					)
            			);
            	System.out.println(
            			String.format(
            					"[%s] Request PATH : %s",
            					new SimpleDateFormat("yyyy-MM-dd H:mm:ss").format(new Date()),
            					exchange.getRequestURI().getPath()
            					)
            			);
            	System.out.println(
            			String.format(
            					"[%s] Request QueryString : %s",
            					new SimpleDateFormat("yyyy-MM-dd H:mm:ss").format(new Date()),
            					exchange.getRequestURI().getQuery()
            					)
            			);
            	System.out.println(
            			String.format(
            					"[%s] Request RequestBody : %s",
            					new SimpleDateFormat("yyyy-MM-dd H:mm:ss").format(new Date()),
            					requestBody
            					)
            			);
            	if(responseBody.containsKey("content")) {
            		System.out.println(
                			String.format(
                					"[%s] Response content : %s",
                					new SimpleDateFormat("yyyy-MM-dd H:mm:ss").format(new Date()),
                					responseBody.get("content")
                					)
                			);
            	}
            	
                // Encoding to UTF-8
//                ByteBuffer bb = Charset.forName("UTF-8").encode(gson.toJson(jsonBody));
            	ByteBuffer bb = Charset.forName("UTF-8").encode(responseBody.toString());
                int contentLength = bb.limit();
                byte[] content = new byte[contentLength];
                bb.get(content, 0, contentLength);
                
                // Set Response Headers
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/json;charset=UTF-8");
                headers.add("Content-Length", String.valueOf(contentLength));
                
                // Send Response Headers
                exchange.sendResponseHeaders(200, contentLength);
                
                respBody.write(content);
                
                // Close Stream
                // 반드시, Response Header를 보낸 후에 닫아야함
                respBody.close();
                
            } catch ( IOException e ) {
                e.printStackTrace();
                
                if( respBody != null ) {
                    respBody.close();
                }
            } finally {
                exchange.close();
            }
        }
    }
    
}
