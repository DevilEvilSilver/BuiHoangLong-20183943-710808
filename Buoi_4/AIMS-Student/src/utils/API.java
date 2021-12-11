package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import entity.payment.CreditCard;
import entity.payment.PaymentTransaction;
/**
 * Class cung cap phuong thuc giup gui request len server va nhan du lieu tra ve
 * Date: 07/12/2021
 * @author BuiHoangLong_20183943
 * @version 1.0
 */
public class API {

	/**
	 * Thuoc tinh giup format ngay thang theo dinh dang
	 */
	public static DateFormat DATE_FORMATER = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	/**
	 * Thuoc tinh giup log thong tin ra console
	 */
	private static Logger LOGGER = Utils.getLogger(Utils.class.getName());

	/**
	 * BuiHoangLong_20183943
	 * Thiet lap connection toi server
	 * @param url: duong dan toi server can request
	 * @param method: giao thuc api
	 * @param token: doan ma can cung cap de xac thuc nguoi dung
	 * @return connection
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ProtocolException
	 */
	private static HttpURLConnection setupConnection(String url, String method, String token)
			throws MalformedURLException, IOException, ProtocolException {
		LOGGER.info("Request URL: " + url + "\n");
		URL line_api_url = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) line_api_url.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod(method);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Authorization", "Bearer " + token);
		return conn;
	}

	/**
	 * BuiHoangLong_20183943
	 * Phuong thuc doc du lieu tra ve tu server
	 * @param conn: connection to server
	 * @return response: phan hoi tra ve tu server (dang String)
	 * @throws IOException
	 */
	private static String readReponse(HttpURLConnection conn) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String inputLine;
		StringBuilder respone = new StringBuilder(); // ising StringBuilder for the sake of memory and performance
		while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);
		respone.append(inputLine + "\n");
		in.close();
		LOGGER.info("Respone Info: " + respone.substring(0, respone.length() - 1).toString());
		return respone.substring(0, respone.length() - 1).toString();
	}
	
	/**
	 * BuiHoangLong_20183943
	 * Phuong thuc giup goi cac api dang GET
	 * @param url: duong dan toi server can request
	 * @param token: doan ma ban can cung cap de xac thuc nguoi dung
	 * @return response: phan hoi tu server (dang string)
	 * @throws Exception
	 */
	public static String get(String url, String token) throws Exception {
		
		// phan 1: set up
		HttpURLConnection conn = setupConnection(url, "GET", token);
		
		// phan 2: doc du lieu tra ve tu server
		return readReponse(conn);
	}

	int var;

	/**
	 * BuiHoangLong_20183943
	 * Phuong thuc giup goi cac api dang POST (thanh toan,...)
	 * @param url: duong dan toi server can request
	 * @param data: du lieu dua len server de xu ly (dang JSON)
	 * @param token: doan ma ban can cung cap de xac thuc nguoi dung
	 * @return response tu server (dang string)
	 * @throws IOException
	 */
	public static String post(String url, String data, String token) throws IOException {
		// cho phep PATCH protocol
		allowMethods("PATCH");
		
		// phan 1:set up
		HttpURLConnection conn = setupConnection(url, "GET", token);
		
		// phan 2: gui du lieu
		Writer writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
		writer.write(data);
		writer.close();
		
		// phan 3: doc du lieu tra ve tu server
		return readReponse(conn);
	}

	/**
	 * BuiHoangLong_20183943
	 * Phuong thuc cho phep goi cac loai giao thuc API khac nhau nhu PATCH, PUT,... (chi hoat dong voi Java 11)
	 * @deprecated chi hoat dong voi Java <= 11
	 * @param methods: giao thuc can cho cho phep (PATCH, PUT,...)
	 */
	private static void allowMethods(String... methods) {
		try {
			Field methodsField = HttpURLConnection.class.getDeclaredField("methods");
			methodsField.setAccessible(true);

			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);

			String[] oldMethods = (String[]) methodsField.get(null);
			Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));
			methodsSet.addAll(Arrays.asList(methods));
			String[] newMethods = methodsSet.toArray(new String[0]);

			methodsField.set(null/* static field */, newMethods);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

}
